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

package com.yukthitech.webutils.search;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.OrderByField;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Conditions;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.persistence.repository.annotations.OrderBy;
import com.yukthitech.persistence.repository.annotations.OrderByType;
import com.yukthitech.persistence.repository.search.SearchCondition;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.IRepositoryMethodRegistry;
import com.yukthitech.webutils.InvalidRequestException;
import com.yukthitech.webutils.WebutilsConfiguration;
import com.yukthitech.webutils.WebutilsContext;
import com.yukthitech.webutils.annotations.SearchCustomizer;
import com.yukthitech.webutils.annotations.SearchQueryMethod;
import com.yukthitech.webutils.common.IExtendedSearchResult;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.SearchExecutionModel;
import com.yukthitech.webutils.common.annotations.ContextAttribute;
import com.yukthitech.webutils.common.annotations.Model;
import com.yukthitech.webutils.common.models.def.FieldType;
import com.yukthitech.webutils.common.models.def.ModelDef;
import com.yukthitech.webutils.common.search.ExecuteSearchResponse;
import com.yukthitech.webutils.common.search.SearchColumn;
import com.yukthitech.webutils.common.search.SearchField;
import com.yukthitech.webutils.common.search.SearchRow;
import com.yukthitech.webutils.common.search.SearchSettingsColumn;
import com.yukthitech.webutils.controllers.IExtensionContextProvider;
import com.yukthitech.webutils.repository.WebutilsExtendableEntity;
import com.yukthitech.webutils.repository.search.ISearchResultCustomizer;
import com.yukthitech.webutils.repository.search.SearchSettingsEntity;
import com.yukthitech.webutils.security.ISecurityService;
import com.yukthitech.webutils.security.SecurityInvocationContext;
import com.yukthitech.webutils.security.UnauthorizedException;
import com.yukthitech.webutils.security.WebutilsSecurityService;
import com.yukthitech.webutils.services.ClassScannerService;
import com.yukthitech.webutils.services.ModelDetailsService;
import com.yukthitech.webutils.services.dynamic.DynamicMethod;
import com.yukthitech.webutils.utils.WebUtils;

import jakarta.annotation.PostConstruct;

/**
 * Service to fetch search query details and execute search queries.
 * 
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

		private Class<? extends ISearchResultCustomizer<?>> customizerType;

		public SearchQueryDetails(Method method, ICrudRepository<?> repository, Class<?> resultType, Class<?> queryType, OrderByField orderByFields[], Class<? extends ISearchResultCustomizer<?>> customizerType)
		{
			this.method = method;
			this.repository = repository;

			this.queryType = queryType;

			this.resultTypeModelName = resultType.getAnnotation(Model.class).name();
			this.queryTypeModelName = queryType.getAnnotation(Model.class).name();

			this.orderByFields = orderByFields;
			this.customizerType = customizerType;

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
	
	private static class SearchCustomizerMethod
	{
		private Object service;
		
		private Method method;

		public SearchCustomizerMethod(Object service, Method method)
		{
			this.service = service;
			this.method = method;
		}
	}

	/**
	 * Search method details cache.
	 */
	private Map<String, SearchQueryDetails> nameToSearchMet = new HashMap<>();

	/**
	 * Model details service to fetch model details of query and result types.
	 */
	@Lazy
	@Autowired
	private ModelDetailsService modelDetailsService;

	/**
	 * Security service to check authorization of target search method.
	 */
	@Autowired(required = false)
	private ObjectProvider<ISecurityService> securityService;

	/**
	 * Used to fetch date format.
	 */
	@Autowired
	private WebutilsConfiguration webutilsConfiguration;

	/**
	 * Used to fetch search settings.
	 */
	@Lazy
	@Autowired
	private SearchSettingsService searchSettingsService;

	/**
	 * Used to fetch extension name of the search result.
	 */
	@Autowired(required = false)
	private IExtensionContextProvider extensionContextProvider;

	@Autowired(required = false)
	private ISearchCustomizer queryCustomizer;
	
	/**
	 * Security service.
	 */
	@Lazy
	@Autowired
	private WebutilsSecurityService webutilsSecurityService;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private ClassScannerService classScannerService;
	
	private Map<String, SearchCustomizerMethod> customizerMethods = new HashMap<>();

	/**
	 * Post construct method used to validate autowired services.
	 */
	@PostConstruct
	private void init()
	{
		if(webutilsConfiguration.isExtensionsRequired() && extensionContextProvider == null)
		{
			throw new InvalidStateException("Though extensions are enabled no implmentation provided for {}", IExtensionContextProvider.class.getName());
		}
	}
	
	/**
	 * To avoid cross dependency issues, customizer method loading
	 * is done as part of application start (by this time all dependencies would be loaded).
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void loadCustoizerMethods()
	{
		Set<Method> methods = classScannerService.getMethodsAnnotatedWith(SearchCustomizer.class);
		
		for(Method method : methods)
		{
			SearchCustomizer customizer = method.getAnnotation(SearchCustomizer.class);
			Object service = applicationContext.getBean(method.getDeclaringClass());
			
			if(service == null)
			{
				throw new InvalidStateException("Search-customizer method is defined in non-service method: {}.{}()", method.getDeclaringClass().getName(), method.getName());
			}
			
			if(method.getParameterCount() != 1 && SearchCustomizationContext.class.equals(method.getParameterTypes()[0]))
			{
				throw new InvalidStateException("Search-customizer method should have single parameter accepting of type SearchCustomizationContext: {}.{}()", method.getDeclaringClass().getName(), method.getName());
			}
			
			this.customizerMethods.put(customizer.name(), new SearchCustomizerMethod(service, method));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void registerRepositoryMethod(Method method, SearchQueryMethod annotation, ICrudRepository<?> repository)
	{
		// Ensure return type is collection
		Type returnType = method.getGenericReturnType();

		if(!(returnType instanceof ParameterizedType))
		{
			throw new IllegalStateException("Invalid return  type specified for search method - " + method);
		}

		ParameterizedType parameterizedType = (ParameterizedType) returnType;

		if(!Collection.class.isAssignableFrom((Class) parameterizedType.getRawType()) || !(parameterizedType.getActualTypeArguments()[0] instanceof Class))
		{
			throw new IllegalStateException("Invalid return type specified for search method - " + method);
		}

		// ensure return type bean and query input are models
		Class<?> returnModelType = (Class) parameterizedType.getActualTypeArguments()[0];
		Class<?> queryModelType = annotation.queryModel();

		if(queryModelType.getAnnotation(Model.class) == null)
		{
			throw new InvalidStateException("For search-method {}.{} non-model {} is defined as query-model-type", repository.getType().getName(), method.getName(), queryModelType.getName());
		}

		if(returnModelType.getAnnotation(Model.class) == null)
		{
			throw new InvalidStateException("For search-method {}.{} non-model {} is defined as query-return-type", repository.getType().getName(), method.getName(), returnModelType.getName());
		}

		// for extendable entities ensure search results is also extendable
		if(WebutilsExtendableEntity.class.isAssignableFrom(repository.getEntityDetails().getEntityType()))
		{
			if(!IExtendedSearchResult.class.isAssignableFrom(returnModelType))
			{
				throw new InvalidStateException("For extendable entity {} search result type {} defined for search method {}.{} is not extendable", repository.getEntityDetails().getEntityType().getName(), returnModelType.getName(), repository.getType().getName(), method.getName());
			}
		}

		String queryName = annotation.name();

		// if duplicate lov name is encountered throw error
		if(nameToSearchMet.containsKey(queryName))
		{
			throw new InvalidConfigurationException("Duplicate search configuration encountered. Same name '{}' is used by two search-query methods - {}, {}", 
					queryName, WebUtils.toString(nameToSearchMet.get(queryName).method), WebUtils.toString(method));
		}

		logger.info("Loading search method - {}.{}", method.getDeclaringClass().getName(), method.getName());

		OrderBy orderByAnnot = method.getAnnotation(OrderBy.class);
		List<OrderByField> orderByFields = new ArrayList<>();

		if(orderByAnnot != null)
		{
			com.yukthitech.persistence.repository.annotations.OrderByField fieldsAnnot[] = orderByAnnot.fields();

			if(fieldsAnnot != null)
			{
				for(com.yukthitech.persistence.repository.annotations.OrderByField annot : fieldsAnnot)
				{
					orderByFields.add( new OrderByField(annot.name(), annot.type()) );
				}
			}
			
			String fieldNames[] = orderByAnnot.value();
			
			if(fieldNames != null)
			{
				for(String field : fieldNames)
				{
					orderByFields.add( new OrderByField(field, OrderByType.ASC) );
				}
			}
		}
		else
		{
			orderByFields.add( new OrderByField("id", OrderByType.ASC) );
		}

		// customizer

		Class<? extends ISearchResultCustomizer> customizer = annotation.customizer();

		// register the annotation
		nameToSearchMet.put(annotation.name(), new SearchQueryDetails(method, repository, 
				returnModelType, queryModelType, 
				orderByFields.toArray(new OrderByField[0]), (Class) customizer));
	}

	@Override
	public void registerDynamicMethod(DynamicMethod method, SearchQueryMethod annotation)
	{
		throw new InvalidStateException("This method is not expected to be invoked");
	}

	/**
	 * Returns model definition of search query model for specified query.
	 * 
	 * @param searchQueryName
	 *            Query name for which query model details needs to be fetched
	 * @return Search query model details
	 */
	public ModelDef getSearhQueryDefinition(String searchQueryName)
	{
		SearchQueryDetails searchQueryDetails = nameToSearchMet.get(searchQueryName);

		if(searchQueryDetails == null)
		{
			throw new InvalidRequestException("Invalid search query  name specified - " + searchQueryName);
		}

		return modelDetailsService.getModelDef(searchQueryDetails.queryTypeModelName);
	}

	/**
	 * Fetches query java type for specified search query.
	 * 
	 * @param searchQueryName
	 *            Search query for which query type needs to be fetched
	 * @return Search query bean type
	 */
	public Class<?> getSearchQueryType(String searchQueryName)
	{
		SearchQueryDetails searchQueryDetails = nameToSearchMet.get(searchQueryName);

		if(searchQueryDetails == null)
		{
			throw new InvalidRequestException("Invalid search query name specified - " + searchQueryName);
		}

		return searchQueryDetails.queryType;
	}

	/**
	 * Returns model definition of search result model for specified query.
	 * 
	 * @param searchQueryName
	 *            Query name for which query model details needs to be fetched
	 * @return Search result model details
	 */
	public ModelDef getSearhResultDefinition(String searchQueryName)
	{
		SearchQueryDetails searchQueryDetails = nameToSearchMet.get(searchQueryName);

		if(searchQueryDetails == null)
		{
			throw new InvalidRequestException("Invalid search query name specified - " + searchQueryName);
		}

		return modelDetailsService.getModelDef(searchQueryDetails.resultTypeModelName);
	}
	
	private boolean isLikeOperator(Condition condition, Conditions conditions)
	{
		if(condition != null)
		{
			return (condition.op() == Operator.LIKE);
		}
		
		if(conditions != null)
		{
			boolean isLike = true;
					
			for(Condition cond : conditions.value())
			{
				if(cond.op() != Operator.LIKE)
				{
					isLike = false;
					break;
				}
			}
			
			return isLike;
		}
		
		return false;
	}

	/**
	 * Executes search with specified criteria and returns the matching objects.
	 *
	 * @param searchQueryName the search query name
	 * @param query the query
	 * @param searchExecutionModel the search execution model
	 * @param searchSettingsWrapper the search settings wrapper
	 * @param queryWrapper the query wrapper
	 * @return the list
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Object> searchObjects(String searchQueryName, Object query, SearchExecutionModel searchExecutionModel, 
			ObjectWrapper<SearchSettingsEntity> searchSettingsWrapper,
			ObjectWrapper<com.yukthitech.persistence.repository.search.SearchQuery> queryWrapper)
	{
		// validate inputs
		SearchQueryDetails searchQueryDetails = nameToSearchMet.get(searchQueryName);

		if(searchQueryDetails == null)
		{
			throw new InvalidRequestException("Invalid search query name specified - " + searchQueryName);
		}

		if(!searchQueryDetails.queryType.isAssignableFrom(query.getClass()))
		{
			throw new InvalidRequestException("Invalid search query bean type {} specified for search query {}. Expected type - {}" + searchQueryName);
		}
		
		// if security service is specified, check user authorization for target
		// search method
		if(securityService.getIfAvailable() != null)
		{
			SecurityInvocationContext context = webutilsSecurityService.newSecurityInvocationContext(searchQueryDetails.repository.getType(), searchQueryDetails.method, query);
			
			if(!securityService.getIfAvailable().isAuthorized(context))
			{
				throw new UnauthorizedException("Current user is not authorized to execute search query - {}", searchQueryName);
			}
		}
		
		//if customizer is available
		if(queryCustomizer != null)
		{
			SearchCustomizationContext custContext = new SearchCustomizationContext()
					.setRepositoryType(searchQueryDetails.repository.getType())
					.setMethod(searchQueryDetails.method)
					.setSearchQueryName(searchQueryName)
					.setQuery(query);
					
			queryCustomizer.customizeQuery(custContext);
		}
		
		SearchCustomizerMethod customizerMethod = this.customizerMethods.get(searchQueryName);
		
		if(customizerMethod != null)
		{
			try
			{
				SearchCustomizationContext custContext = new SearchCustomizationContext()
						.setRepositoryType(searchQueryDetails.repository.getType())
						.setMethod(searchQueryDetails.method)
						.setSearchQueryName(searchQueryName)
						.setQuery(query);

				customizerMethod.method.invoke(customizerMethod.service, custContext);
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while invoking customization-method for search query: {}", searchQueryName, ex);
			}
		}

		com.yukthitech.persistence.repository.search.SearchQuery repoSearchQuery = new com.yukthitech.persistence.repository.search.SearchQuery();
		queryWrapper.setValue(repoSearchQuery);
		
		Field queryFields[] = searchQueryDetails.queryType.getDeclaredFields();
		Condition condition = null;
		Conditions conditions = null;
		Object value = null;
		String strValue = null;

		SearchCondition searchCondition = null;
		ContextAttribute contextAttribute = null;
		WebutilsContext context = WebutilsContext.getContext();

		// loop through query object fields and extract conditions and add it
		// repo search query
		for(Field field : queryFields)
		{
			condition = field.getAnnotation(Condition.class);
			conditions = field.getAnnotation(Conditions.class);

			if(condition == null && conditions == null)
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
				} catch(Exception ex)
				{
					throw new InvalidStateException("An error occurred while fetching field value - {}", field.getName());
				}
			}
			else
			{
				try
				{
					value = PropertyUtils.getProperty(context.getAttributeMap(), contextAttribute.value());
				} catch(Exception ex)
				{
					throw new InvalidStateException("An error occurred while fetching context attribute - {}", contextAttribute.value(), ex);
				}
			}

			// ignore nulls
			if(value == null)
			{
				continue;
			}

			// ignore blank value
			if(value instanceof String)
			{
				strValue = (String) value;

				if(strValue.trim().length() == 0)
				{
					continue;
				}

				strValue = strValue.replace("*", "%");
				
				if(isLikeOperator(condition, conditions) && !strValue.contains("%"))
				{
					strValue = "%" + strValue + "%";
				}

				value = strValue;
			}

			if(value instanceof Enum)
			{
				value = value.toString();
			}

			if(condition != null)
			{
				searchCondition = new SearchCondition(condition.value(), condition.op(), value);
				searchCondition.setIgnoreCase(condition.ignoreCase());
			}
			else
			{
				searchCondition = null;
				SearchCondition subcond = null;
				
				for(Condition cond : conditions.value())
				{
					subcond = new SearchCondition(cond.value(), cond.op(), value);
					subcond.setIgnoreCase(cond.ignoreCase());
					
					if(searchCondition == null)
					{
						searchCondition = subcond;
					}
					else
					{
						subcond.setJoinOperator(cond.joinWith());
						searchCondition.addCondition(subcond);
					}
				}
			}

			repoSearchQuery.addCondition(searchCondition);
		}
		
		String userSpace = securityService.getIfAvailable().getUserSpaceIdentity();

		if(StringUtils.isNotBlank(userSpace))
		{
			repoSearchQuery.addCondition(new SearchCondition("spaceIdentity", Operator.EQ, userSpace));
		}

		// set ordering
		repoSearchQuery.setOrderByFields(Arrays.asList(searchQueryDetails.orderByFields));

		///////////////////////////////////////////////////////////
		// Add result fields
		SearchSettingsEntity searchSettings = searchSettingsService.fetchSettings(searchQueryName);
		searchSettingsWrapper.setValue(searchSettings);

		for(SearchSettingsColumn column : searchSettings.getSearchColumns())
		{
			// if the field is required or displayable
			if(column.isRequired() || column.isDisplayed() || column.isBackend())
			{
				// if the column is extended field add it the search query
				// extended list
				if(column.isExtended())
				{
					for(SearchField field : column.getFields())
					{
						repoSearchQuery.addAdditionEntityField(field.getField());
					}
				}
			}
			// if the column is not needed
			else
			{
				// and if it is fixed field
				if(!column.isExtended())
				{
					// add it to exclusion list
					repoSearchQuery.addExcludedField(column.getFieldName());
				}
			}
		}

		// set limit on repo search query, by default fetch all records
		int pageSize = -1;
		
		if(!searchExecutionModel.isFetchAll())
		{
			//if page size is specified by input query give that higher preference then search settings
			pageSize = searchExecutionModel.getPageSize() > 1 ? searchExecutionModel.getPageSize() : searchSettings.getPageSize();
		}
		
		int pageNo = searchExecutionModel.getPageNumber();

		repoSearchQuery.setResultsOffset((pageNo - 1) * pageSize);
		repoSearchQuery.setResultsLimit(pageSize);

		// execute search and return results
		try
		{
			return (List) searchQueryDetails.method.invoke(searchQueryDetails.repository, repoSearchQuery);
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while executing search query - {}", searchQueryName, ex);
		}
	}

	/**
	 * Search objects with given criteria.
	 *
	 * @param searchQueryName the search query name
	 * @param query the query
	 * @param searchExecutionModel the search execution model
	 * @return the list
	 */
	public List<Object> searchObjects(String searchQueryName, Object query, SearchExecutionModel searchExecutionModel)
	{
		ObjectWrapper<SearchSettingsEntity> searchSettingsWrapper = new ObjectWrapper<>();
		ObjectWrapper<com.yukthitech.persistence.repository.search.SearchQuery> queryWrapper = new ObjectWrapper<>();
		
		return searchObjects(searchQueryName, query, searchExecutionModel, searchSettingsWrapper, queryWrapper);
	}
	
	/**
	 * Executes search query method with name specified by "searchQueryName" by
	 * passing query-object "query". The number of results will be limited to
	 * "resultLimit".
	 * 
	 * @param searchQueryName
	 *            Search query name to execute
	 * @param query
	 *            Query object containing conditions
	 * @param searchExecutionModel
	 *            Search execution params
	 * @return Results of search query execution
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ExecuteSearchResponse executeSearch(String searchQueryName, Object query, SearchExecutionModel searchExecutionModel)
	{
		ObjectWrapper<SearchSettingsEntity> searchSettingsWrapper = new ObjectWrapper<>();
		ObjectWrapper<com.yukthitech.persistence.repository.search.SearchQuery> queryWrapper = new ObjectWrapper<>();
		
		List<Object> results = searchObjects(searchQueryName, query, searchExecutionModel, searchSettingsWrapper, queryWrapper);

		try
		{
			long count = 0;
			SearchQueryDetails searchQueryDetails = nameToSearchMet.get(searchQueryName);

			// if fetch count is enabled
			if(searchExecutionModel.isFetchCount())
			{
				count = searchQueryDetails.repository.searchCount(queryWrapper.getValue());
			}
			
			// instance of customizer
			if(!ISearchResultCustomizer.class.equals(searchQueryDetails.customizerType))
			{
				SearchCustomizationContext custContext = new SearchCustomizationContext()
						.setRepositoryType(searchQueryDetails.repository.getType())
						.setMethod(searchQueryDetails.method)
						.setSearchQueryName(searchQueryName)
						.setQuery(query);

				ISearchResultCustomizer customizerResult = searchQueryDetails.customizerType.getConstructor().newInstance();
				results = customizerResult.customize(custContext, results);
			}
			
			return toResponse(searchQueryName, results, searchSettingsWrapper.getValue(), searchExecutionModel, count);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while coverting to responses", ex);
		}
	}

	/**
	 * Converts specified results into response.
	 * 
	 * @param searchQueryName
	 *            Search query for which conversion should be done.
	 * @param results
	 *            Search results to be converted.
	 * @param searchSettings
	 *            Search settings to be used
	 * @param searchExecutionModel
	 *            Search execution params
	 * @param count total number rows resulted in search
	 * @return Converted response.
	 */
	private ExecuteSearchResponse toResponse(String searchQueryName, List<Object> results, SearchSettingsEntity searchSettings, SearchExecutionModel searchExecutionModel, long count) throws Exception
	{
		ExecuteSearchResponse response = new ExecuteSearchResponse(results);
		response.setPageNumber(searchExecutionModel.getPageNumber());

		response.setTotalCount(count);

		// add search result headers
		for(SearchSettingsColumn column : searchSettings.getSearchColumns())
		{
			// if the field is required or displayable
			if(column.isRequired() || column.isDisplayed() || column.isBackend())
			{
				if(column.isExtended())
				{
					response.addSearchColumn(new SearchColumn("Ext_" + column.getLabel().replaceAll("\\s+", "_"), column.getLabel(), 
							column.isDisplayed(), FieldType.STRING, column.getSearchResultType()));
				}
				else
				{
					response.addSearchColumn(new SearchColumn(column.getPropertyName(), column.getLabel(), column.isDisplayed(), 
							FieldType.STRING, column.getSearchResultType()));
				}
			}
		}

		if(results == null || results.isEmpty())
		{
			response.setSearchResults(new ArrayList<>());
			return response;
		}

		// Build the rows
		SearchRow searchRow = null;
		Object value = null;
		String extensionName = null;
		Map<String, SimpleDateFormat> dateFormats = new HashMap<>();

		for(Object result : results)
		{
			searchRow = new SearchRow();

			for(SearchSettingsColumn column : searchSettings.getSearchColumns())
			{
				// ignore fields which will not be part of results
				if(!column.isRequired() && !column.isDisplayed() && !column.isBackend())
				{
					continue;
				}

				// if column belong to extended field
				if(column.isExtended())
				{
					// if it is simple extension field
					if(!column.isMixedField())
					{
						value = ((IExtendedSearchResult) result).getDynamicFieldValue(column.getFieldName());
					}
					// if multiple extension fields (of different extensions)
					// point to same column
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
				// if column belong to static field
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
					if(column.getFieldDef() != null && column.getFieldDef().getFormat() != null)
					{
						SimpleDateFormat dateFormat = dateFormats.get(column.getFieldDef().getFormat());
						
						if(dateFormat == null)
						{
							dateFormat = new SimpleDateFormat(column.getFieldDef().getFormat());
							dateFormats.put(column.getFieldDef().getFormat(), dateFormat);
						}
						
						value = dateFormat.format(value);
					}
					else
					{
						value = webutilsConfiguration.getDateFormat().format(value);
					}
				}

				if(value instanceof Number)
				{
					if(column.getFieldDef() != null && column.getFieldDef().getFormat() != null)
					{
						DecimalFormat decimalFormat = new DecimalFormat(column.getFieldDef().getFormat());
						value = decimalFormat.format(value);
					}
					else
					{
						value = webutilsConfiguration.getNumberFormat().format(value);
					}
				}

				if(column.getFieldDef() != null && column.getFieldDef().getFieldType() == FieldType.CUSTOM_TYPE)
				{
					searchRow.addValue( IWebUtilsCommonConstants.OBJECT_MAPPER.writeValueAsString(value) );
				}
				else
				{
					searchRow.addValue(value.toString());
				}
			}

			response.addSearchResult(searchRow);
		}

		return response;
	}

	/**
	 * Fetches entity type of a search query.
	 * 
	 * @param searchQueryName
	 *            Search query name.
	 * @return Search query entity type.
	 */
	public Class<?> getEntityTypeOf(String searchQueryName)
	{
		// validate inputs
		SearchQueryDetails searchQueryDetails = nameToSearchMet.get(searchQueryName);

		if(searchQueryDetails == null)
		{
			throw new InvalidRequestException("Invalid search query name specified - " + searchQueryName);
		}

		return searchQueryDetails.repository.getEntityDetails().getEntityType();
	}
}
