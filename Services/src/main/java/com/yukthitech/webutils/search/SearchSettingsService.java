package com.yukthitech.webutils.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.persistence.ITransaction;
import com.yukthitech.persistence.PersistenceException;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.validation.annotations.Required;
import com.yukthitech.webutils.common.annotations.SearchFieldInfo;
import com.yukthitech.webutils.common.models.def.FieldDef;
import com.yukthitech.webutils.common.models.def.ModelDef;
import com.yukthitech.webutils.common.search.SearchField;
import com.yukthitech.webutils.common.search.SearchSettingsColumn;
import com.yukthitech.webutils.common.search.SearchSettingsModel;
import com.yukthitech.webutils.repository.ExtensionFieldEntity;
import com.yukthitech.webutils.repository.UserEntity;
import com.yukthitech.webutils.repository.search.ISearchSettingsRespository;
import com.yukthitech.webutils.repository.search.SearchSettingsEntity;
import com.yukthitech.webutils.services.BaseCrudService;
import com.yukthitech.webutils.services.CurrentUserService;
import com.yukthitech.webutils.services.ExtensionService;
import com.yukthitech.webutils.services.NoRepositoryFoundException;
import com.yukthitech.webutils.utils.WebUtils;

/**
 * Service to search settings.
 * @author akiran
 */
@Service
public class SearchSettingsService extends BaseCrudService<SearchSettingsEntity, ISearchSettingsRespository>
{
	private static Logger logger = LogManager.getLogger(SearchSettingsService.class);
	
	/**
	 * Default page size.
	 */
	private static final int DEFAULT_PAGE_SIZE = 1000;
	
	/**
	 * Current user service used to fetch current user id.
	 */
	@Autowired
	private CurrentUserService currentUserService;
	
	/**
	 * Used to fetch extension of an entity.
	 */
	@Autowired
	private ExtensionService extensionService;
	
	/**
	 * Fetches search query details.
	 */
	@Autowired
	private SearchService searchService;
	
	/**
	 * Cache to maintain default settings.
	 */
	private Map<String, SearchSettingsEntity> nameToDefaultSettings = new HashMap<String, SearchSettingsEntity>();
	
	/**
	 * Instantiates a search settings service.
	 */
	public SearchSettingsService()
	{
		//super(SearchSettingsEntity.class, ISearchSettingsRespository.class);
	}
	
	/**
	 * Fetches all possible settings columns for specified search query.
	 * @param searchQuery Search query name
	 * @return Matching columns
	 */
	private LinkedHashMap<SearchSettingsColumn, SearchSettingsColumn> getSettingsColumns(String searchQuery)
	{
		Class<?> entityType = searchService.getEntityTypeOf(searchQuery);
		
		//search query result def
		ModelDef searchResultDef = searchService.getSearhResultDefinition(searchQuery);
		
		//get search query's entity extension fields
		List<ExtensionFieldEntity> extensionFields = extensionService.getExtensionFieldsForEntity(entityType.getName());
		
		LinkedHashMap<SearchSettingsColumn, SearchSettingsColumn> searchColumns = new LinkedHashMap<>();
		SearchSettingsColumn searchSettingsColumn = null, existingSettingsColumn = null;
		String fieldName = null;
		Field fieldAnnot = null;
		SearchFieldInfo searchFieldInfo = null;
		
		int orderNo = 0;
		
		for(FieldDef field : searchResultDef.getFields())
		{
			fieldAnnot = field.getField().getAnnotation(Field.class);
			searchFieldInfo = field.getField().getAnnotation(SearchFieldInfo.class);
			
			fieldName = (fieldAnnot != null) ? fieldAnnot.value() : field.getName();
			
			searchSettingsColumn = new SearchSettingsColumn(field.getLabel(), field.isDisplayable(), field.isBackend(), 
					false, new SearchField(null, fieldName, field.getField().getName()));
			
			if(searchFieldInfo != null && searchFieldInfo.order() >= 0)
			{
				searchSettingsColumn.setOrder(searchFieldInfo.order());
			}
			else
			{
				searchSettingsColumn.setOrder(orderNo);
				orderNo++;
			}
			
			if(searchFieldInfo != null)
			{
				searchSettingsColumn.setSearchResultType(searchFieldInfo.resultType());
			}
			
			if("id".equals(field.getName()) || field.getField().getAnnotation(Required.class) != null)
			{
				searchSettingsColumn.setRequired(true);
			}
			
			searchSettingsColumn.setFieldDef(field);
			searchColumns.put(searchSettingsColumn, searchSettingsColumn);
		}
		
		for(ExtensionFieldEntity extensionField : extensionFields)
		{
			searchSettingsColumn = new SearchSettingsColumn(extensionField.getLabel(), false, false, true, 
					new SearchField(extensionField.getExtension().getName(), "extendedFields." + extensionField.getColumnName(), "extendedFields." + extensionField.getColumnName()));

		 	existingSettingsColumn = searchColumns.get(searchSettingsColumn);
		 	
		 	if(existingSettingsColumn != null)
		 	{
		 		existingSettingsColumn.getFields().addAll(searchSettingsColumn.getFields());
		 		searchSettingsColumn = existingSettingsColumn;
		 	}
		 	else
		 	{
		 		searchColumns.put(searchSettingsColumn, searchSettingsColumn);
		 	}
		 	
		 	searchSettingsColumn.setOrder(orderNo);
		 	orderNo++;
		}

		return searchColumns;
	}
	
	/**
	 * Fetches default settings of a search query.
	 * @param searchQuery Search query name.
	 * @return Default settings
	 */
	private synchronized SearchSettingsEntity defaultSettings(String searchQuery)
	{
		SearchSettingsEntity settings = nameToDefaultSettings.get(searchQuery);
		
		if(settings != null)
		{
			return settings;
		}
		
		LinkedHashMap<SearchSettingsColumn, SearchSettingsColumn> searchColumns = getSettingsColumns(searchQuery);
		settings = new SearchSettingsEntity();
		
		List<SearchSettingsColumn> searchColumnsLst = new ArrayList<>(searchColumns.keySet());
		
		//sort columns by order
		Collections.sort(searchColumnsLst, new Comparator<SearchSettingsColumn>()
		{
			@Override
			public int compare(SearchSettingsColumn o1, SearchSettingsColumn o2)
			{
				int orderDiff = o1.getOrder() - o2.getOrder();
				return orderDiff > 0 ? orderDiff : 1;
			}
		});
		
		settings.setSearchColumns(searchColumnsLst);
		settings.setPageSize(DEFAULT_PAGE_SIZE);
		settings.setSearchQueryName(searchQuery);
		
		nameToDefaultSettings.put(searchQuery, settings);
		return settings;
	}
	
	/**
	 * Ensures all fields mentioned in settings are valid.
	 * @param searchQuery Search query name.
	 * @param settings Setting being persisted.
	 */
	private void validateColumns(String searchQuery, SearchSettingsEntity settings)
	{
		LinkedHashMap<SearchSettingsColumn, SearchSettingsColumn> allSearchColumns = getSettingsColumns(searchQuery);
		
		List<SearchSettingsColumn> searchColumns = settings.getSearchColumns();
		SearchSettingsColumn existingColumn = null;

		for(SearchSettingsColumn column : searchColumns)
		{
			existingColumn = allSearchColumns.get(column);
			
			if(existingColumn == null)
			{
				throw new InvalidArgumentException("Specified field '{}' does not exist in search query - {}", column.getLabel(), searchQuery);
			}
		}
	}

	/**
	 * Filters fields which are not valid any more (deleted or updated) and adds fields
	 * which were not part of existing settings (new extension fields).
	 * @param searchQuery Search query name
	 * @param settings Existing settings.
	 */
	private void filterDisabledFields(String searchQuery, SearchSettingsEntity settings)
	{
		LinkedHashMap<SearchSettingsColumn, SearchSettingsColumn> allSearchColumns = new LinkedHashMap<>(getSettingsColumns(searchQuery));
		
		List<SearchSettingsColumn> searchColumns = settings.getSearchColumns();
		List<SearchSettingsColumn> filteredColumns = new ArrayList<>();
		
		SearchSettingsColumn actualColumn = null;
		
		for(SearchSettingsColumn column : searchColumns)
		{
			actualColumn = allSearchColumns.remove(column);
			
			//if column is valid
			if(actualColumn != null)
			{
				//populate valid fields
				column.setFields(actualColumn.getFields());
				column.setRequired(actualColumn.isRequired());
				column.setExtended(actualColumn.isExtended());
				
				filteredColumns.add(column);
			}
			
			//ignore fields which is not part all search columns
		}
		
		for(SearchSettingsColumn column : allSearchColumns.keySet())
		{
			filteredColumns.add(new SearchSettingsColumn(column.getLabel(), false, false, column.isExtended()));
		}
		
		settings.setSearchColumns(filteredColumns);
	}

	@Override
	public void save(SearchSettingsEntity entity, Object model)
	{
		long currentUserId = currentUserService.getCurrentUserDetails().getUserId();
		
		//ensure the settings is saved for current user.
		entity.setUser(new UserEntity(currentUserId));
		
		validateColumns(entity.getSearchQueryName(), entity);
		
		super.save(entity, model);
	}

	@Override
	public void update(SearchSettingsEntity entity, Object model)
	{
		long currentUserId = currentUserService.getCurrentUserDetails().getUserId();
		
		WebUtils.validateEntityForUpdate(entity);
		
		try(ITransaction transaction = repository.newOrExistingTransaction())
		{
			logger.trace("Trying to update entity - {}", entity);
			
			userService.populateTrackingFieldForUpdate(entity);

			validateColumns(entity.getSearchQueryName(), entity);
			
			boolean res = repository.updateForUser(entity, currentUserId);
			
			if(!res)
			{
				logger.error("Failed to update entity - {}", entity);
				throw new InvalidStateException("Failed to update entity");
			}
			
			transaction.commit();
		}catch(Exception ex)
		{
			logger.error("An error occurred while updating entity - " + entity, ex);

			if(ex instanceof PersistenceException)
			{
				throw (PersistenceException) ex;
			}
			
			throw new IllegalStateException("An error occurred while updating entity - " + entity, ex);
		}
	}

	/**
	 * Fetches search settings for current user for specified search query.
	 * @param searchQueryName Search query name.
	 * @return Matching search settings of current user.
	 */
	public SearchSettingsModel fetch(String searchQueryName)
	{
		return toModel(fetchSettings(searchQueryName), SearchSettingsModel.class);
	}
	
	/**
	 * Fetches search settings for current user for specified search query.
	 * @param searchQueryName Search query name.
	 * @return Matching search settings entity of current user.
	 */
	public SearchSettingsEntity fetchSettings(String searchQueryName)
	{
		try
		{
			long currentUserId = currentUserService.getCurrentUserDetails().getUserId();
			SearchSettingsEntity entity = super.repository.fetchByName(currentUserId, searchQueryName);
			
			if(entity == null)
			{
				entity = defaultSettings(searchQueryName);
			}
			else
			{
				filterDisabledFields(searchQueryName, entity);
			}
	
			return entity;
		}catch(NoRepositoryFoundException ex)
		{
			return defaultSettings(searchQueryName);
		}
	}
	
	/**
	 * Deletes search query settings of specified query.
	 * @param queryName Query settings to be deleted.
	 */
	public void deleteByName(String queryName)
	{
		long currentUserId = currentUserService.getCurrentUserDetails().getUserId();
		super.repository.deleteByName(currentUserId, queryName);		
	}

	/**
	 * Deletes all settings.
	 */
	public void deleteAll()
	{
		repository.deleteAll();
	}
}
