package com.webutils.services.search;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webutils.common.UserDetails;
import com.webutils.common.form.annotations.ContextAttribute;
import com.webutils.common.form.annotations.Model;
import com.webutils.common.form.model.FieldType;
import com.webutils.common.form.model.ModelDef;
import com.webutils.common.search.ExecuteSearchResponse;
import com.webutils.common.search.SearchColumn;
import com.webutils.common.search.SearchExecutionModel;
import com.webutils.common.search.SearchRow;
import com.webutils.common.search.SearchSettingsColumn;
import com.webutils.services.auth.UserContext;
import com.webutils.services.common.ClassScannerService;
import com.webutils.services.common.FreeMarkerService;
import com.webutils.services.common.InvalidRequestException;
import com.webutils.services.common.SecurityService;
import com.webutils.services.common.WebutilsFormatConfiguration;
import com.webutils.services.form.model.ModelService;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.OrderByField;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Conditions;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.persistence.repository.annotations.OrderBy;
import com.yukthitech.persistence.repository.annotations.OrderByType;
import com.yukthitech.persistence.repository.search.SearchCondition;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.PropertyAccessor;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Loads repository search methods and executes search queries.
 */
@Service
public class SearchService
{
	private static final Logger logger = LogManager.getLogger(SearchService.class);

	private static class SearchQueryDetails
	{
		private final Class<? extends ICrudRepository<?>> repositoryType;
		private final Method method;
		private final ICrudRepository<?> repository;
		private final Class<?> queryType;
		private final String resultTypeModelName;
		private final String queryTypeModelName;
		private final OrderByField[] orderByFields;
		private final ISearchQueryCustomizer queryCustomizer;
		private final ISearchResultCustomizer resultCustomizer;

		private SearchQueryDetails(Method method, Class<? extends ICrudRepository<?>> repositoryType, ICrudRepository<?> repository, Class<?> resultType, Class<?> queryType,
				OrderByField[] orderByFields, ISearchQueryCustomizer queryCustomizer, ISearchResultCustomizer resultCustomizer)
		{
			this.method = method;
			this.repositoryType = repositoryType;
			this.repository = repository;
			this.queryType = queryType;
			this.resultTypeModelName = resolveModelName(resultType);
			this.queryTypeModelName = resolveModelName(queryType);
			this.orderByFields = orderByFields;
			this.queryCustomizer = queryCustomizer;
			this.resultCustomizer = resultCustomizer;
		}

		private static String resolveModelName(Class<?> modelType)
		{
			Model model = modelType.getAnnotation(Model.class);
			String name = model.name();
			return StringUtils.isBlank(name) ? modelType.getSimpleName() : name;
		}
	}

	private final Map<String, SearchQueryDetails> nameToSearchMet = new HashMap<>();

	@Lazy
	@Autowired
	private ModelService modelService;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private WebutilsFormatConfiguration formatConfiguration;

	@Lazy
	@Autowired
	private SearchSettingsService searchSettingsService;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ClassScannerService classScannerService;

	@Autowired
	private FreeMarkerService freeMarkerService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private boolean initialized = false;

	@EventListener
	public void loadSearchMethods(ContextStartedEvent event)
	{
		if(initialized)
		{
			return;
		}
		initialized = true;

		Set<Method> methods = classScannerService.getMethodsAnnotatedWith(SearchQueryMethod.class);
		for(Method method : methods)
		{
			if(!ICrudRepository.class.isAssignableFrom(method.getDeclaringClass()))
			{
				logger.info("Ignoring search method {}.{}() as declaring type is not a repository",
						method.getDeclaringClass().getName(), method.getName());
				continue;
			}

			@SuppressWarnings("unchecked")
			Class<? extends ICrudRepository<?>> repoType = (Class<? extends ICrudRepository<?>>) method.getDeclaringClass();
			ICrudRepository<?> repository = applicationContext.getBean(repoType);
			if(repository == null)
			{
				logger.info("Ignoring search method {}.{}() as repository bean is not available",
						method.getDeclaringClass().getName(), method.getName());
				continue;
			}

			registerRepositoryMethod(method, method.getAnnotation(SearchQueryMethod.class), repoType, repository);
		}
	}

	@SuppressWarnings("rawtypes")
	private void registerRepositoryMethod(Method method, SearchQueryMethod annotation,
			Class<? extends ICrudRepository<?>> repoType, ICrudRepository<?> repository)
	{
		Type returnType = method.getGenericReturnType();
		if(!(returnType instanceof ParameterizedType parameterizedType))
		{
			throw new IllegalStateException("Invalid return type specified for search method - " + method);
		}

		if(!Collection.class.isAssignableFrom((Class) parameterizedType.getRawType())
				|| !(parameterizedType.getActualTypeArguments()[0] instanceof Class<?> returnModelType))
		{
			throw new IllegalStateException("Invalid return type specified for search method - " + method);
		}

		Class<?> queryModelType = annotation.queryModel();
		if(queryModelType.getAnnotation(Model.class) == null)
		{
			throw new InvalidStateException("For search-method {}.{} non-model {} is defined as query-model-type",
					repository.getClass().getName(), method.getName(), queryModelType.getName());
		}
		if(returnModelType.getAnnotation(Model.class) == null)
		{
			throw new InvalidStateException("For search-method {}.{} non-model {} is defined as query-return-type",
					repository.getClass().getName(), method.getName(), returnModelType.getName());
		}

		String queryName = annotation.name();
		if(nameToSearchMet.containsKey(queryName))
		{
			throw new InvalidConfigurationException("Duplicate search configuration encountered. Same name '{}' is used by two search-query methods - {}, {}",
					queryName, nameToSearchMet.get(queryName).method, method);
		}

		logger.info("Loading search method - {}.{}", method.getDeclaringClass().getName(), method.getName());

		List<OrderByField> orderByFields = new ArrayList<>();
		OrderBy orderByAnnot = method.getAnnotation(OrderBy.class);
		if(orderByAnnot != null)
		{
			com.yukthitech.persistence.repository.annotations.OrderByField[] fieldsAnnot = orderByAnnot.fields();
			if(fieldsAnnot != null)
			{
				for(com.yukthitech.persistence.repository.annotations.OrderByField annot : fieldsAnnot)
				{
					orderByFields.add(new OrderByField(annot.name(), annot.type()));
				}
			}
			String[] fieldNames = orderByAnnot.value();
			if(fieldNames != null)
			{
				for(String field : fieldNames)
				{
					orderByFields.add(new OrderByField(field, OrderByType.ASC));
				}
			}
		}
		else
		{
			orderByFields.add(new OrderByField("id", OrderByType.ASC));
		}

		ISearchQueryCustomizer queryCustomizer = resolveCustomizer(annotation.queryCustomizer(), ISearchQueryCustomizer.class);
		ISearchResultCustomizer resultCustomizer = resolveCustomizer(annotation.resultCustomizer(), ISearchResultCustomizer.class);

		nameToSearchMet.put(queryName, new SearchQueryDetails(method, repoType, repository, returnModelType, queryModelType,
				orderByFields.toArray(new OrderByField[0]), queryCustomizer, resultCustomizer));
	}

	@SuppressWarnings("unchecked")
	private <T> T resolveCustomizer(Class<?> customizerType, Class<T> markerInterface)
	{
		if(markerInterface.equals(customizerType))
		{
			return null;
		}
		return (T) applicationContext.getBean(customizerType);
	}

	public ModelDef getSearhQueryDefinition(String searchQueryName)
	{
		SearchQueryDetails details = getSearchQueryDetails(searchQueryName);
		return modelService.getModelDef(details.queryTypeModelName);
	}

	public Class<?> getSearchQueryType(String searchQueryName)
	{
		return getSearchQueryDetails(searchQueryName).queryType;
	}

	public ModelDef getSearhResultDefinition(String searchQueryName)
	{
		SearchQueryDetails details = getSearchQueryDetails(searchQueryName);
		return modelService.getModelDef(details.resultTypeModelName);
	}

	public Class<?> getEntityTypeOf(String searchQueryName)
	{
		return getSearchQueryDetails(searchQueryName).repository.getEntityDetails().getEntityType();
	}

	public boolean isValidSearchQuery(String searchQueryName)
	{
		return nameToSearchMet.containsKey(searchQueryName);
	}

	private SearchQueryDetails getSearchQueryDetails(String searchQueryName)
	{
		SearchQueryDetails details = nameToSearchMet.get(searchQueryName);
		if(details == null)
		{
			throw new InvalidRequestException("Invalid search query name specified - " + searchQueryName);
		}
		return details;
	}

	private boolean isLikeOperator(Condition condition, Conditions conditions)
	{
		if(condition != null)
		{
			return condition.op() == Operator.LIKE;
		}
		if(conditions != null)
		{
			for(Condition cond : conditions.value())
			{
				if(cond.op() != Operator.LIKE)
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private List<Object> searchObjects(String searchQueryName, Object query, SearchExecutionModel searchExecutionModel,
			ObjectWrapper<SearchSettingsEntity> searchSettingsWrapper,
			ObjectWrapper<com.yukthitech.persistence.repository.search.SearchQuery> queryWrapper)
	{
		SearchQueryDetails searchQueryDetails = getSearchQueryDetails(searchQueryName);

		if(query != null && !searchQueryDetails.queryType.isAssignableFrom(query.getClass()))
		{
			throw new InvalidRequestException("Invalid search query bean type {} specified for search query {}. Expected type - {}",
					query.getClass().getName(), searchQueryName, searchQueryDetails.queryType.getName());
		}

		securityService.checkAuthorization(searchQueryDetails.method);

		if(query != null && searchQueryDetails.queryCustomizer != null)
		{
			searchQueryDetails.queryCustomizer.customizeQuery(new SearchCustomizationContext()
					.setRepositoryType(searchQueryDetails.repositoryType)
					.setMethod(searchQueryDetails.method)
					.setSearchQueryName(searchQueryName)
					.setQuery(query));
		}

		com.yukthitech.persistence.repository.search.SearchQuery repoSearchQuery = new com.yukthitech.persistence.repository.search.SearchQuery();
		queryWrapper.setValue(repoSearchQuery);

		if(query != null)
		{
			buildConditions(searchQueryDetails, query, buildExpressionContext(query), repoSearchQuery);
		}

		UserDetails userDetails = UserContext.getCurrentUser();
		if(userDetails != null && StringUtils.isNotBlank(userDetails.getCustomSpace())
				&& searchQueryDetails.repository.getEntityDetails().hasField("spaceIdentity"))
		{
			repoSearchQuery.addCondition(new SearchCondition("spaceIdentity", Operator.EQ, userDetails.getCustomSpace()));
		}

		repoSearchQuery.setOrderByFields(Arrays.asList(searchQueryDetails.orderByFields));

		SearchSettingsEntity searchSettings = searchSettingsService.fetchSettings(searchQueryName);
		searchSettingsWrapper.setValue(searchSettings);

		for(SearchSettingsColumn column : searchSettings.getSearchColumns())
		{
			if(column.isRequired() || column.isDisplayed() || column.isBackend())
			{
				continue;
			}
			repoSearchQuery.addExcludedField(column.getFieldName());
		}

		int pageSize = -1;
		if(!searchExecutionModel.isFetchAll())
		{
			pageSize = searchExecutionModel.getPageSize() > 1
					? searchExecutionModel.getPageSize()
					: searchSettings.getPageSize();
		}

		int pageNo = searchExecutionModel.getPageNumber();
		repoSearchQuery.setResultsOffset((pageNo - 1) * pageSize);
		repoSearchQuery.setResultsLimit(pageSize);

		try
		{
			return (List) searchQueryDetails.method.invoke(searchQueryDetails.repository, repoSearchQuery);
		}
		catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while executing search query - {}", searchQueryName, ex);
		}
	}

	private Map<String, Object> buildExpressionContext(Object query)
	{
		Map<String, Object> context = new HashMap<>();
		UserDetails currentUser = UserContext.getCurrentUser();
		
		context.put("query", query);
		context.put("currentUser", currentUser);
		
		if(currentUser != null)
		{
			context.put("userId", currentUser.getId());
			context.put("customSpace", currentUser.getCustomSpace());
		}
		
		return context;
	}

	private void buildConditions(SearchQueryDetails searchQueryDetails, Object query, Map<String, Object> expressionContext,
			com.yukthitech.persistence.repository.search.SearchQuery repoSearchQuery)
	{
		for(Field field : searchQueryDetails.queryType.getDeclaredFields())
		{
			Condition condition = field.getAnnotation(Condition.class);
			Conditions conditions = field.getAnnotation(Conditions.class);
			if(condition == null && conditions == null)
			{
				continue;
			}

			field.setAccessible(true);
			Object value = readFieldValue(field, query, expressionContext);
			if(value == null)
			{
				continue;
			}

			if(value instanceof String strValue)
			{
				if(strValue.trim().isEmpty())
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

			if(value instanceof Enum<?> enumValue)
			{
				value = enumValue.toString();
			}

			SearchCondition searchCondition;
			if(condition != null)
			{
				searchCondition = new SearchCondition(condition.value(), condition.op(), value);
				searchCondition.setIgnoreCase(condition.ignoreCase());
			}
			else
			{
				searchCondition = null;
				for(Condition cond : conditions.value())
				{
					SearchCondition subcond = new SearchCondition(cond.value(), cond.op(), value);
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
	}

	private Object readFieldValue(Field field, Object query, Map<String, Object> expressionContext)
	{
		ContextAttribute contextAttribute = field.getAnnotation(ContextAttribute.class);
		if(contextAttribute == null)
		{
			try
			{
				return field.get(query);
			}
			catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while fetching field value - {}", field.getName(), ex);
			}
		}

		try
		{
			return freeMarkerService.fetchValue("search-context-attribute-" + field.getName(), contextAttribute.value(), expressionContext);
		}
		catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while fetching context attribute - {}", contextAttribute.value(), ex);
		}
	}

	@SuppressWarnings("unchecked")
	public ExecuteSearchResponse executeSearch(String searchQueryName, Object query, SearchExecutionModel searchExecutionModel)
	{
		ObjectWrapper<SearchSettingsEntity> searchSettingsWrapper = new ObjectWrapper<>();
		ObjectWrapper<com.yukthitech.persistence.repository.search.SearchQuery> queryWrapper = new ObjectWrapper<>();

		List<Object> results = searchObjects(searchQueryName, query, searchExecutionModel, searchSettingsWrapper, queryWrapper);
		SearchQueryDetails searchQueryDetails = getSearchQueryDetails(searchQueryName);

		long count = 0;
		if(searchExecutionModel.isFetchCount())
		{
			count = searchQueryDetails.repository.searchCount(queryWrapper.getValue());
		}

		if(searchQueryDetails.resultCustomizer != null)
		{
			results = (List<Object>) searchQueryDetails.resultCustomizer.customize(new SearchCustomizationContext()
					.setRepositoryType(searchQueryDetails.repositoryType)
					.setMethod(searchQueryDetails.method)
					.setSearchQueryName(searchQueryName)
					.setQuery(query), results);
		}

		try
		{
			return toResponse(results, searchSettingsWrapper.getValue(), searchExecutionModel, count);
		}
		catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting to responses", ex);
		}
	}

	private ExecuteSearchResponse toResponse(List<Object> results, SearchSettingsEntity searchSettings,
			SearchExecutionModel searchExecutionModel, long count) throws Exception
	{
		ExecuteSearchResponse response = new ExecuteSearchResponse(results);
		response.setPageNumber(searchExecutionModel.getPageNumber());
		response.setTotalCount(count);

		for(SearchSettingsColumn column : searchSettings.getSearchColumns())
		{
			if(column.isRequired() || column.isDisplayed() || column.isBackend())
			{
				response.addSearchColumn(new SearchColumn(column.getPropertyName(), column.getLabel(), column.isDisplayed(),
						FieldType.STRING, column.getSearchResultType()));
			}
		}

		if(results == null || results.isEmpty())
		{
			response.setSearchResults(new ArrayList<>());
			return response;
		}

		Map<String, SimpleDateFormat> dateFormats = new HashMap<>();
		SimpleDateFormat defaultDateFormat = formatConfiguration.newDateFormat();
		DecimalFormat defaultNumberFormat = formatConfiguration.newNumberFormat();

		for(Object result : results)
		{
			SearchRow searchRow = new SearchRow();
			for(SearchSettingsColumn column : searchSettings.getSearchColumns())
			{
				if(!column.isRequired() && !column.isDisplayed() && !column.isBackend())
				{
					continue;
				}

				Object value = PropertyAccessor.getProperty(result, column.getPropertyName());
				if(value == null)
				{
					searchRow.addValue(null);
					continue;
				}

				if(value instanceof Date dateValue)
				{
					if(column.getFieldDef() != null && column.getFieldDef().getFormat() != null)
					{
						SimpleDateFormat dateFormat = dateFormats.computeIfAbsent(column.getFieldDef().getFormat(),
								SimpleDateFormat::new);
						value = dateFormat.format(dateValue);
					}
					else
					{
						value = defaultDateFormat.format(dateValue);
					}
				}

				if(value instanceof Number numberValue)
				{
					if(column.getFieldDef() != null && column.getFieldDef().getFormat() != null)
					{
						value = new DecimalFormat(column.getFieldDef().getFormat()).format(numberValue);
					}
					else
					{
						value = defaultNumberFormat.format(numberValue);
					}
				}

				if(column.getFieldDef() != null && column.getFieldDef().getFieldType() == FieldType.CUSTOM_TYPE)
				{
					searchRow.addValue(objectMapper.writeValueAsString(value));
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
}
