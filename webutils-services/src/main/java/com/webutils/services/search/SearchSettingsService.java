package com.webutils.services.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webutils.common.form.annotations.SearchFieldInfo;
import com.webutils.common.form.model.FieldDef;
import com.webutils.common.form.model.ModelDef;
import com.webutils.common.repo.IMissingTableRepository;
import com.webutils.common.search.SearchField;
import com.webutils.common.search.SearchSettingsColumn;
import com.webutils.common.search.SearchSettingsModel;
import com.webutils.services.auth.UserContext;
import com.webutils.services.user.UserEntity;
import com.yukthitech.persistence.ITransaction;
import com.yukthitech.persistence.PersistenceException;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.validation.annotations.Required;

/**
 * Manages per-user search column and pagination settings.
 */
@Service
public class SearchSettingsService
{
	private static final Logger logger = LogManager.getLogger(SearchSettingsService.class);
	private static final int DEFAULT_PAGE_SIZE = 1000;

	@Autowired
	private ISearchSettingsRepository repository;

	@Autowired
	private SearchService searchService;

	private final Map<String, SearchSettingsEntity> nameToDefaultSettings = new HashMap<>();

	private boolean isRepositoryAvailable()
	{
		return !(repository instanceof IMissingTableRepository);
	}

	private LinkedHashMap<SearchSettingsColumn, SearchSettingsColumn> getSettingsColumns(String searchQuery)
	{
		ModelDef searchResultDef = searchService.getSearhResultDefinition(searchQuery);
		LinkedHashMap<SearchSettingsColumn, SearchSettingsColumn> searchColumns = new LinkedHashMap<>();
		int orderNo = 0;

		for(FieldDef field : searchResultDef.getFields())
		{
			Field fieldAnnot = field.getField().getAnnotation(Field.class);
			SearchFieldInfo searchFieldInfo = field.getField().getAnnotation(SearchFieldInfo.class);
			String fieldName = (fieldAnnot != null) ? fieldAnnot.value() : field.getName();

			SearchSettingsColumn searchSettingsColumn = new SearchSettingsColumn(
					field.getLabel(),
					field.isDisplayable(),
					field.isBackend(),
					new SearchField(fieldName, field.getField().getName()));

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

		return searchColumns;
	}

	private synchronized SearchSettingsEntity defaultSettings(String searchQuery)
	{
		SearchSettingsEntity settings = nameToDefaultSettings.get(searchQuery);
		if(settings != null)
		{
			return settings;
		}

		List<SearchSettingsColumn> searchColumnsLst = new ArrayList<>(getSettingsColumns(searchQuery).keySet());
		Collections.sort(searchColumnsLst, Comparator.comparingInt(SearchSettingsColumn::getOrder));

		settings = new SearchSettingsEntity();
		settings.setSearchColumns(searchColumnsLst);
		settings.setPageSize(DEFAULT_PAGE_SIZE);
		settings.setSearchQueryName(searchQuery);
		nameToDefaultSettings.put(searchQuery, settings);
		return settings;
	}

	private void validateColumns(String searchQuery, SearchSettingsEntity settings)
	{
		LinkedHashMap<SearchSettingsColumn, SearchSettingsColumn> allSearchColumns = getSettingsColumns(searchQuery);
		for(SearchSettingsColumn column : settings.getSearchColumns())
		{
			if(allSearchColumns.get(column) == null)
			{
				throw new InvalidArgumentException("Specified field '{}' does not exist in search query - {}", column.getLabel(), searchQuery);
			}
		}
	}

	private void filterDisabledFields(String searchQuery, SearchSettingsEntity settings)
	{
		LinkedHashMap<SearchSettingsColumn, SearchSettingsColumn> allSearchColumns = new LinkedHashMap<>(getSettingsColumns(searchQuery));
		List<SearchSettingsColumn> filteredColumns = new ArrayList<>();

		for(SearchSettingsColumn column : settings.getSearchColumns())
		{
			SearchSettingsColumn actualColumn = allSearchColumns.remove(column);
			if(actualColumn != null)
			{
				column.setFields(actualColumn.getFields());
				column.setRequired(actualColumn.isRequired());
				filteredColumns.add(column);
			}
		}

		for(SearchSettingsColumn column : allSearchColumns.keySet())
		{
			filteredColumns.add(new SearchSettingsColumn(column.getLabel(), false, false));
		}

		settings.setSearchColumns(filteredColumns);
	}

	public SearchSettingsModel fetch(String searchQueryName)
	{
		return toModel(fetchSettings(searchQueryName));
	}

	public SearchSettingsEntity fetchSettings(String searchQueryName)
	{
		if(!isRepositoryAvailable())
		{
			return defaultSettings(searchQueryName);
		}

		Long userId = UserContext.getCurrentUserId();
		if(userId == null)
		{
			return defaultSettings(searchQueryName);
		}

		SearchSettingsEntity entity = repository.fetchByName(userId, searchQueryName);
		if(entity == null)
		{
			return defaultSettings(searchQueryName);
		}

		filterDisabledFields(searchQueryName, entity);
		return entity;
	}

	public SearchSettingsEntity save(SearchSettingsModel model)
	{
		if(!isRepositoryAvailable())
		{
			throw new InvalidStateException("Search settings repository is not available");
		}

		SearchSettingsEntity entity = toEntity(model);
		Long userId = UserContext.getCurrentUserId();
		entity.setUser(new UserEntity(userId));
		entity.setCreatedBy(new UserEntity(userId));
		entity.setUpdatedBy(new UserEntity(userId));
		entity.setCreatedOn(new Date());
		entity.setUpdatedOn(new Date());
		validateColumns(entity.getSearchQueryName(), entity);

		try(ITransaction transaction = repository.newOrExistingTransaction())
		{
			repository.save(entity);
			transaction.commit();
		}
		catch(Exception ex)
		{
			logger.error("Failed to save search settings - {}", entity, ex);
			if(ex instanceof PersistenceException pe)
			{
				throw pe;
			}
			throw new InvalidStateException("Failed to save search settings", ex);
		}
		return entity;
	}

	public void update(SearchSettingsModel model)
	{
		if(!isRepositoryAvailable())
		{
			throw new InvalidStateException("Search settings repository is not available");
		}

		if(model.getId() == null)
		{
			throw new InvalidArgumentException("Id is required for update");
		}

		Long userId = UserContext.getCurrentUserId();
		SearchSettingsEntity entity = toEntity(model);
		entity.setUpdatedBy(new UserEntity(userId));
		entity.setUpdatedOn(new Date());
		validateColumns(entity.getSearchQueryName(), entity);

		try(ITransaction transaction = repository.newOrExistingTransaction())
		{
			boolean updated = repository.updateForUser(entity, userId);
			if(!updated)
			{
				throw new InvalidStateException("Failed to update search settings");
			}
			transaction.commit();
		}
		catch(Exception ex)
		{
			logger.error("Failed to update search settings - {}", entity, ex);
			if(ex instanceof PersistenceException pe)
			{
				throw pe;
			}
			throw new InvalidStateException("Failed to update search settings", ex);
		}
	}

	public void deleteByName(String queryName)
	{
		if(!isRepositoryAvailable())
		{
			return;
		}
		repository.deleteByName(UserContext.getCurrentUserId(), queryName);
	}

	public void deleteAll()
	{
		if(!isRepositoryAvailable())
		{
			return;
		}
		repository.deleteAll();
	}

	private SearchSettingsEntity toEntity(SearchSettingsModel model)
	{
		SearchSettingsEntity entity = new SearchSettingsEntity();
		entity.setId(model.getId());
		entity.setVersion(model.getVersion());
		entity.setSearchQueryName(model.getSearchQueryName());
		entity.setSearchColumns(model.getSearchColumns());
		entity.setPageSize(model.getPageSize());
		return entity;
	}

	private SearchSettingsModel toModel(SearchSettingsEntity entity)
	{
		SearchSettingsModel model = new SearchSettingsModel();
		model.setId(entity.getId());
		model.setVersion(entity.getVersion());
		model.setSearchQueryName(entity.getSearchQueryName());
		model.setSearchColumns(entity.getSearchColumns());
		model.setPageSize(entity.getPageSize());
		return model;
	}
}
