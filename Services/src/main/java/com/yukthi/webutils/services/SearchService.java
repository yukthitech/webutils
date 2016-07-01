/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthi.webutils.services;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.OrderByField;
import com.yukthi.persistence.repository.annotations.Condition;
import com.yukthi.persistence.repository.annotations.Operator;
import com.yukthi.persistence.repository.annotations.OrderBy;
import com.yukthi.persistence.repository.annotations.OrderByType;
import com.yukthi.persistence.repository.search.SearchCondition;
import com.yukthi.utils.exceptions.InvalidConfigurationException;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.IRepositoryMethodRegistry;
import com.yukthi.webutils.InvalidRequestParameterException;
import com.yukthi.webutils.WebutilsConfiguration;
import com.yukthi.webutils.WebutilsContext;
import com.yukthi.webutils.annotations.SearchQueryMethod;
import com.yukthi.webutils.common.IExtendedSearchResult;
import com.yukthi.webutils.common.SearchExecutionModel;
import com.yukthi.webutils.common.annotations.ContextAttribute;
import com.yukthi.webutils.common.annotations.Model;
import com.yukthi.webutils.common.models.def.FieldType;
import com.yukthi.webutils.common.models.def.ModelDef;
import com.yukthi.webutils.common.models.search.ExecuteSearchResponse;
import com.yukthi.webutils.common.models.search.SearchColumn;
import com.yukthi.webutils.common.models.search.SearchField;
import com.yukthi.webutils.common.models.search.SearchRow;
import com.yukthi.webutils.common.models.search.SearchSettingsColumn;
import com.yukthi.webutils.controllers.IExtensionContextProvider;
import com.yukthi.webutils.repository.WebutilsExtendableEntity;
import com.yukthi.webutils.repository.search.SearchSettingsEntity;
import com.yukthi.webutils.security.ISecurityService;
import com.yukthi.webutils.security.UnauthorizedException;
import com.yukthi.webutils.services.dynamic.DynamicMethod;
import com.yukthi.webutils.utils.WebUtils;

/**
 * Service to fetch search query details and execute search queries.
 * @author akiran
 */
@Service
public class SearchService implements IRepositoryMethodRegistry<SearchQueryMethod>
{
	private static Logger logger = LogManager.getLogger(SearchService.class);
	
	private class SearchQueryDetails
	{
		private Method method;
		private ICrudRepository<?> repository;
		
		private Class<?> queryType;
		
		private String resultTypeModelName;
		private String queryTypeModelName;
		
		private OrderByField orderByFields[];

		public SearchQueryDetails(Method method, ICrudRepository<?> repository, Class<?> resultType, Class<?> queryType, OrderByField orderByFields[])
		{
			this.method = method;
			this.repository = repository;
			
			this.queryType = queryType;
			
			this.resultTypeModelName = resultType.getAnnotation(Model.class).name();
			this.queryTypeModelName = queryType.getAnnotation(Model.class).name();
			
			this.orderByFields = orderByFields;
			
			if(StringUtils.isBlank(this.resultTypeModelName))
			{
				this.resultTypeModelName = resultType.getSimpleName();
			}

			if(StringUtils.isBlank(this.queryTypeModelName))
			{
				this.queryTypeModelName = queryType.getSimpleName();
			}
		}
	}
	
	/**
	 * Search method details cache.
	 */
	private Map<String, SearchQueryDetails> nameToSearchMet = new HashMap<>();
	
	/**
	 * Model details service to fetch model details of query and result types.
	 */
	@Autowired
	private ModelDetailsService modelDetailsService;
	
	/**
	 * Security service to check authorization of target search method.
	 */
	@Autowired(required = false)
	private ISecurityService securityService;

	/**
	 * Used to fetch date format.
	 */
	@Autowired
	private WebutilsConfiguration webutilsConfiguration;
	
	/**
	 * Used to fetch search settings.
	 */
	@Autowired
	private SearchSettingsService searchSettingsService;
	
	/**
	 * Used to fetch extension name of the search result.
	 */
	@Autowired
	private IExtensionContextProvider extensionContextProvider;
	
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.IRepositoryMethodRegistry#registerRepositoryMethod(java.lang.reflect.Method, java.lang.annotation.Annotation)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void registerRepositoryMethod(Method method, SearchQueryMethod annotation, ICrudRepository<?> repository)
	{
		//Ensure return type is collection
		Type returnType = method.getGenericReturnType();
		
		if(!(returnType instanceof ParameterizedType))
		{
			throw new IllegalStateException("Invalid return type specified for search method - " + method);
		}
		
		ParameterizedType parameterizedType = (ParameterizedType) returnType;
		
		if( !Collection.class.isAssignableFrom((Class) parameterizedType.getRawType()) || !(parameterizedType.getActualTypeArguments()[0] instanceof Class))
		{
			throw new IllegalStateException("Invalid return type specified for search method - " + method);
		}
		
		//ensure return type bean and query input are models
		Class<?> returnModelType = (Class) parameterizedType.getActualTypeArguments()[0];
		Class<?> queryModelType = annotation.queryModel();
		
		if(queryModelType.getAnnotation(Model.class) == null)
		{
			throw new InvalidStateException("For search-method {}.{} non-model {} is defined as query-model-type", repository.getClass().getName(), method.getName(), queryModelType.getName());
		}
		
		if(returnModelType.getAnnotation(Model.class) == null)
		{
			throw new InvalidStateException("For search-method {}.{} non-model {} is defined as query-return-type", repository.getClass().getName(), method.getName(), returnModelType.getName());
		}
		
		//for extendable entities ensure search results is also extendable
		if(WebutilsExtendableEntity.class.isAssignableFrom(repository.getEntityDetails().getEntityType()))
		{
			if(!IExtendedSearchResult.class.isAssignableFrom(returnModelType))
			{
				throw new InvalidStateException("For extendable entity {} search result type {} defined for search method {}.{} is not extendable", 
						repository.getEntityDetails().getEntityType().getName(), returnModelType.getName(), repository.getClass().getName(), method.getName());
			}
		}
		
		String queryName = annotation.name();
		
		//if duplicate lov name is encountered throw error
		if(nameToSearchMet.containsKey(queryName))
		{
			throw new InvalidConfigurationException("Duplicate search configuration encountered. Same name '{}' is used by two search-query methods - {}, {}", 
					WebUtils.toString( nameToSearchMet.get(queryName).method ), 
					WebUtils.toString( method ) 
			);
		}
	
		logger.info("Loading search method - {}.{}", method.getDeclaringClass().getName(), method.getName());
		
		OrderBy orderByAnnot = method.getAnnotation(OrderBy.class);
		OrderByField orderByFields[] = null;
		
		if(orderByAnnot != null)
		{
			com.yukthi.persistence.repository.annotations.OrderByField fieldsAnnot[] = orderByAnnot.fields();
			
			if(fieldsAnnot != null)
			{
				orderByFields = new OrderByField[fieldsAnnot.length];
				int idx = 0;
				
				for(com.yukthi.persistence.repository.annotations.OrderByField annot : fieldsAnnot)
				{
					orderByFields[idx] = new OrderByField(annot.name(), annot.type());
					idx++;
				}
			}
			else
			{
				orderByFields = new OrderByField[orderByAnnot.value().length];
				int idx = 0;
				String fieldNames[] = orderByAnnot.value();
				
				for(String field : fieldNames)
				{
					orderByFields[idx] = new OrderByField(field, OrderByType.ASC);
					idx++;
				}
			}
		}
		else
		{
			orderByFields = new OrderByField[]{new OrderByField("id", OrderByType.ASC)};
		}
		
		//register the model
		nameToSearchMet.put(annotation.name(), new SearchQueryDetails(method, repository, returnModelType, queryModelType, orderByFields));
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.IDynamicRepositoryMethodRegistry#registerDynamicRepositoryMethod(com.yukthi.webutils.services.dynamic.DynamicMethod, java.lang.annotation.Annotation)
	 */
	@Override
	public void registerDynamicMethod(DynamicMethod method, SearchQueryMethod annotation)
	{
		throw new InvalidStateException("This method is not expected to be invoked");
	}

	/**
	 * Returns model definition of search query model for specified query.
	 * @param searchQueryName Query name for which query model details needs to be fetched
	 * @return Search query model details
	 */
	public ModelDef getSearhQueryDefinition(String searchQueryName)
	{
		SearchQueryDetails searchQueryDetails = nameToSearchMet.get(searchQueryName);
		
		if(searchQueryDetails == null)
		{
			throw new InvalidRequestParameterException("Invalid search query name specified - " + searchQueryName);
		}
		
		return modelDetailsService.getModelDef(searchQueryDetails.queryTypeModelName);
	}
	
	/**
	 * Fetches query java type for specified search query.
	 * @param searchQueryName Search query for which query type needs to be fetched
	 * @return Search query bean type
	 */
	public Class<?> getSearchQueryType(String searchQueryName)
	{
		SearchQueryDetails searchQueryDetails = nameToSearchMet.get(searchQueryName);
		
		if(searchQueryDetails == null)
		{
			throw new InvalidRequestParameterException("Invalid search query name specified - " + searchQueryName);
		}
		
		return searchQueryDetails.queryType;
	}

	/**
	 * Returns model definition of search result model for specified query.
	 * @param searchQueryName Query name for which query model details needs to be fetched
	 * @return Search result model details
	 */
	public ModelDef getSearhResultDefinition(String searchQueryName)
	{
		SearchQueryDetails searchQueryDetails = nameToSearchMet.get(searchQueryName);
		
		if(searchQueryDetails == null)
		{
			throw new InvalidRequestParameterException("Invalid search query name specified - " + searchQueryName);
		}
		
		return modelDetailsService.getModelDef(searchQueryDetails.resultTypeModelName);
	}
	
	/**
	 * Executes search query method with name specified by "searchQueryName" by passing query-object "query". The number of results
	 * will be limited to "resultLimit".
	 * @param searchQueryName Search query name to execute
	 * @param query Query object containing conditions
	 * @param searchExecutionModel Search execution params
	 * @return Results of search query execution
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ExecuteSearchResponse executeSearch(String searchQueryName, Object query, SearchExecutionModel searchExecutionModel)
	{
		//validate inputs
		SearchQueryDetails searchQueryDetails = nameToSearchMet.get(searchQueryName);
		
		if(searchQueryDetails == null)
		{
			throw new InvalidRequestParameterException("Invalid search query name specified - " + searchQueryName);
		}
		
		if(!searchQueryDetails.queryType.isAssignableFrom(query.getClass()))
		{
			throw new InvalidRequestParameterException("Invalid search query bean type {} specified for search query {}. Expected type - {}" + searchQueryName);
		}

		//if security service is specified, check user authorization for target search method
		if(securityService != null)
		{
			if(!securityService.isAuthorized(searchQueryDetails.method))
			{
				throw new UnauthorizedException("Current user is not authorized to execute search query - {}", searchQueryName);
			}
		}
		
		com.yukthi.persistence.repository.search.SearchQuery repoSearchQuery = new com.yukthi.persistence.repository.search.SearchQuery();
		Field queryFields[] = searchQueryDetails.queryType.getDeclaredFields();
		Condition condition = null;
		Object value = null;
		String strValue = null;
		
		SearchCondition searchCondition = null;
		ContextAttribute contextAttribute = null;
		WebutilsContext context = WebutilsContext.getContext();
				
		//loop through query object fields and extract conditions and add it repo search query
		for(Field field : queryFields)
		{
			condition = field.getAnnotation(Condition.class);
			
			if(condition == null)
			{
				continue;
			}
			
			field.setAccessible(true);
			contextAttribute = field.getAnnotation(ContextAttribute.class);
			
			if(contextAttribute == null)
			{
				try
				{
					value = field.get(query);
				}catch(Exception ex)
				{
					throw new InvalidStateException("An error occurred while fetching field value - {}", field.getName());
				}
			}
			else
			{
				try
				{
					value = PropertyUtils.getProperty(context.getAttributeMap(), contextAttribute.value());
				}catch(Exception ex)
				{
					throw new InvalidStateException(ex, "An error occurred while fetching context attribute - {}", contextAttribute.value());
				}
			}
			
			//ignore nulls
			if(value == null)
			{
				continue;
			}
			
			//ignore blank value
			if(value instanceof String)
			{
				strValue = (String) value;
				
				if(strValue.trim().length() == 0)
				{
					continue;
				}
				
				strValue = strValue.replace("*", "%");
				value = strValue;
			}
			
			if(value instanceof Enum)
			{
				value = value.toString();
			}
			
			searchCondition = new SearchCondition(condition.value(), condition.op(), value);
			searchCondition.setIgnoreCase(condition.ignoreCase());
			
			repoSearchQuery.addCondition(searchCondition);
		}
		
		repoSearchQuery.addCondition(new SearchCondition("spaceIdentity", Operator.EQ, securityService.getUserSpaceIdentity()));
		
		//set ordering
		repoSearchQuery.setOrderByFields(Arrays.asList(searchQueryDetails.orderByFields));
		
		///////////////////////////////////////////////////////////
		//Add result fields
		SearchSettingsEntity searchSettings = searchSettingsService.fetchSettings(searchQueryName);
		
		for(SearchSettingsColumn column : searchSettings.getSearchColumns())
		{
			//if the field is required or displayable
			if(column.isRequired() || column.isDisplayed())
			{
				//if the column is extended field add it the search query extended list
				if(column.isExtended())
				{
					for(SearchField field : column.getFields())
					{
						repoSearchQuery.addAdditionEntityField(field.getField());
					}
				}
			}
			//if the column is not needed
			else
			{
				//and if it is fixed field
				if(!column.isExtended())
				{
					//add it to exclusion list
					repoSearchQuery.addExcludedField(column.getFieldName());
				}
			}
		}
		
		//set limit on repo search query
		int pageSize = searchExecutionModel.isFetchAll() ? -1 : searchSettings.getPageSize();
		int pageNo = searchExecutionModel.getPageNumber();
		
		repoSearchQuery.setResultsOffset((pageNo - 1) * pageSize);
		repoSearchQuery.setResultsLimit(pageSize);

		//execute search and return results
		try
		{
			List<Object> results = (List) searchQueryDetails.method.invoke(searchQueryDetails.repository, repoSearchQuery);
			long count = 0;
			
			//if fetch count is enabled
			if(searchExecutionModel.isFetchCount())
			{
				count = searchQueryDetails.repository.searchCount(repoSearchQuery);
			}

			return toResponse(searchQueryName, results, searchSettings, searchExecutionModel, count);
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while executing search query - {}", searchQueryName);
		}
	}
	
	/**
	 * Converts specified results into response.
	 * @param searchQueryName Search query for which conversion should be done.
	 * @param results Search results to be converted.
	 * @param searchSettings Search settings to be used
	 * @param searchExecutionModel Search execution params
	 * @return Converted response. 
	 */
	private ExecuteSearchResponse toResponse(String searchQueryName, List<Object> results, SearchSettingsEntity searchSettings, SearchExecutionModel searchExecutionModel, long count) throws Exception
	{
		ExecuteSearchResponse response = new ExecuteSearchResponse();
		response.setPageNumber(searchExecutionModel.getPageNumber());
		
		response.setTotalCount(count);

		//add search result headers
		for(SearchSettingsColumn column : searchSettings.getSearchColumns())
		{
			//if the field is required or displayable
			if(column.isRequired() || column.isDisplayed())
			{
				if(column.isExtended())
				{
					response.addSearchColumn(new SearchColumn("Ext_" + column.getLabel().replaceAll("\\s+", "_"), column.getLabel(), column.isDisplayed(), FieldType.STRING));
				}
				else
				{
					response.addSearchColumn(new SearchColumn(column.getPropertyName(), column.getLabel(), column.isDisplayed(), FieldType.STRING));
				}
			}
		}
		
		if(results == null || results.isEmpty())
		{
			response.setSearchResults(new ArrayList<>());
			return response;
		}

		//Build the rows
		SearchRow searchRow = null;
		Object value = null;
		String extensionName = null;
		
		for(Object result : results)
		{
			searchRow = new SearchRow();
			
			for(SearchSettingsColumn column : searchSettings.getSearchColumns())
			{
				//ignore fields which will not be part of results
				if(!column.isRequired() && !column.isDisplayed())
				{
					continue;
				}
				
				//if column belong to extended field
				if(column.isExtended())
				{
					//if it is simple extension field
					if(!column.isMixedField())
					{
						value = ((IExtendedSearchResult) result).getDynamicFieldValue(column.getFieldName());
					}
					//if multiple extension fields (of different extensions) point to same column
					else
					{
						extensionName = extensionContextProvider.getExtensionName(result);
						
						if(extensionName != null)
						{
							value = ((IExtendedSearchResult) result).getDynamicFieldValue(column.getMixedFieldName(extensionName));
						}
						else
						{
							value = ((IExtendedSearchResult) result).getDynamicFieldValue(column.getFieldName());
						}
					}
				}
				//if column belong to static field
				else
				{
					value = PropertyUtils.getProperty(result, column.getPropertyName());
				}

				if(value == null)
				{
					searchRow.addValue(null);
					continue;
				}
				
				if(value instanceof Date)
				{
					value = webutilsConfiguration.getDateFormat().format(value);
				}
				
				searchRow.addValue(value.toString());
			}

			response.addSearchResult(searchRow);
		}
		
		return response;
	}
	
	/**
	 * Fetches entity type of a search query.
	 * @param searchQueryName Search query name.
	 * @return Search query entity type.
	 */
	public Class<?> getEntityTypeOf(String searchQueryName)
	{
		//validate inputs
		SearchQueryDetails searchQueryDetails = nameToSearchMet.get(searchQueryName);
		
		if(searchQueryDetails == null)
		{
			throw new InvalidRequestParameterException("Invalid search query name specified - " + searchQueryName);
		}

		return searchQueryDetails.repository.getEntityDetails().getEntityType();
	}
}
