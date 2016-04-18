package com.yukthi.webutils.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthi.persistence.ITransaction;
import com.yukthi.persistence.PersistenceException;
import com.yukthi.utils.exceptions.InvalidArgumentException;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.validation.annotations.Required;
import com.yukthi.webutils.common.models.SearchSettingsModel;
import com.yukthi.webutils.common.models.def.FieldDef;
import com.yukthi.webutils.common.models.def.ModelDef;
import com.yukthi.webutils.common.models.search.SearchField;
import com.yukthi.webutils.common.models.search.SearchSettingsColumn;
import com.yukthi.webutils.repository.ExtensionFieldEntity;
import com.yukthi.webutils.repository.UserEntity;
import com.yukthi.webutils.repository.search.ISearchSettingsRespository;
import com.yukthi.webutils.repository.search.SearchSettingsEntity;
import com.yukthi.webutils.utils.WebUtils;

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
	private static final int DEFAULT_PAGE_SIZE = 20;
	
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
	 * Instantiates a search settings service.
	 */
	public SearchSettingsService()
	{
		super(SearchSettingsEntity.class, ISearchSettingsRespository.class);
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
		
		for(FieldDef field : searchResultDef.getFields())
		{
			if(!field.isDisplayable())
			{
				continue;
			}
			
			searchSettingsColumn = new SearchSettingsColumn(field.getLabel(), true, false, new SearchField(null, field.getName()));
			searchSettingsColumn.setRequired(field.getField().getAnnotation(Required.class) != null);
			
			searchColumns.put(searchSettingsColumn, searchSettingsColumn);
		}
		
		for(ExtensionFieldEntity extensionField : extensionFields)
		{
			searchSettingsColumn = new SearchSettingsColumn(extensionField.getLabel(), true, true, 
					new SearchField(extensionField.getExtension().getName(), "extendedFields." + extensionField.getName()));

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
		}

		return searchColumns;
	}
	
	/**
	 * Fetches default settings of a search query.
	 * @param searchQuery Search query name.
	 * @return Default settings
	 */
	private SearchSettingsEntity defaultSettings(String searchQuery)
	{
		LinkedHashMap<SearchSettingsColumn, SearchSettingsColumn> searchColumns = getSettingsColumns(searchQuery);
		SearchSettingsEntity settings = new SearchSettingsEntity();
		
		settings.setSearchColumns(new ArrayList<>(searchColumns.keySet()));
		settings.setPageSize(DEFAULT_PAGE_SIZE);
		
		return settings;
	}
	
	/**
	 * Ensures all fields mentioned in settings are valid and adds required fields which are missing in settings.
	 * @param searchQuery Search query name.
	 * @param settings Setting being persisted.
	 */
	private void validateAndSetRequired(String searchQuery, SearchSettingsEntity settings)
	{
		LinkedHashMap<SearchSettingsColumn, SearchSettingsColumn> allSearchColumns = getSettingsColumns(searchQuery);
		
		List<SearchSettingsColumn> searchColumns = settings.getSearchColumns();
		SearchSettingsColumn existingColumn = null;

		for(SearchSettingsColumn column : searchColumns)
		{
			existingColumn = allSearchColumns.get(column);
			
			if(existingColumn == null)
			{
				throw new InvalidArgumentException("Specified field '{}' does not exist in search query - {}", column.getName(), searchQuery);
			}
			
			column.setFields(existingColumn.getFields());
		}
	}

	/**
	 * Filters fields which are not valid any more (deleted or updated) and adds fields
	 * which were not part of existing settings (new extension fields).
	 * @param searchQuery Search query name
	 * @param settings Existing settings.
	 */
	private void filterInvalidFields(String searchQuery, SearchSettingsEntity settings)
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
				
				filteredColumns.add(column);
			}
			
			//ignore fields which is not part all search columns
		}
		
		filteredColumns.addAll(allSearchColumns.keySet());

		settings.setSearchColumns(filteredColumns);
	}

	@Override
	public void save(SearchSettingsEntity entity, Object model)
	{
		long currentUserId = currentUserService.getCurrentUserDetails().getUserId();
		
		//ensure the settings is saved for current user.
		entity.setUser(new UserEntity(currentUserId));
		
		validateAndSetRequired(entity.getSearchQueryName(), entity);
		
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

			validateAndSetRequired(entity.getSearchQueryName(), entity);
			
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
		long currentUserId = currentUserService.getCurrentUserDetails().getUserId();
		
		SearchSettingsEntity entity = super.repository.fetchByName(currentUserId, searchQueryName);
		
		if(entity == null)
		{
			entity = defaultSettings(searchQueryName);
		}
		else
		{
			filterInvalidFields(searchQueryName, entity);
		}

		return entity;
	}

	/**
	 * Deletes all settings.
	 */
	public void deleteAll()
	{
		repository.deleteAll();
	}
}
