package com.yukthitech.webutils.bootstrap;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.UniqueConstraintViolationException;
import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.persistence.repository.search.SearchCondition;
import com.yukthitech.persistence.repository.search.SearchQuery;
import com.yukthitech.utils.ReflectionUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.common.UserDetails;
import com.yukthitech.webutils.repository.ITenantSpaceBased;
import com.yukthitech.webutils.repository.ITrackedEntity;
import com.yukthitech.webutils.repository.WebutilsBaseEntity;
import com.yukthitech.webutils.security.IAuthenticationService;
import com.yukthitech.webutils.services.CurrentUserService;
import com.yukthitech.webutils.services.freemarker.FreeMarkerService;
import com.yukthitech.webutils.user.UserEntity;
import com.yukthitech.webutils.user.UserService;
import com.yukthitech.webutils.utils.WebUtils;

/**
 * Bootstrap manage to load bootstrap data into db. This service load method does the following:
 * 	1) Gets the files defined by application property "app.bootstrap.files" which should be comma separated file list. The files will be loaded in same order.
 *  2) A context map will be maintained, which would contain all loaded beans grouped by their entity group name.
 *  	This will help in getting properties from old files.
 *  	Also helps in creating multiple entities using loops.
 *  3) Each file will be processed using below steps.
 *  3) The file will be processed first time using context map. This will be first time parse.
 *  4) Then the result json will be loaded using jackson.
 *  5) Then each entity group will be loaded in same order. For each entity properties the freemarker expressions will be reparsed. This will help in getting properties from previous entity groups.
 *  
 * @author akiran
 */
public class BootstrapManager
{
	private static Logger logger = LogManager.getLogger(BootstrapManager.class);
	
	/**
	 * The Class ServiceMethod.
	 */
	private static class ServiceMethod
	{
		/**
		 * Service class on which method needs to be invoked.
		 */
		private Object service;
		
		/**
		 * Service method to be invoked for save.
		 */
		private Method method;

		/**
		 * Instantiates a new service method.
		 *
		 * @param service the service
		 * @param method the method
		 */
		public ServiceMethod(Object service, Method method)
		{
			this.service = service;
			this.method = method;
		}
		
		/**
		 * Invokes the service method with specified model.
		 * @param model Model to save
		 */
		public void invoke(Object model) throws Exception
		{
			try
			{
				method.invoke(service, model);
			} catch(InvocationTargetException ex)
			{
				throw (Exception) ex.getCause();
			} catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while invoking saving model using service method - {}", model);
			}
		}
	}
	
	/**
	 * Service method pattern.
	 */
	private static final Pattern SERVICE_METHOD_PATTERN = Pattern.compile("([\\w\\.]+)\\.(\\w+)\\(([\\w\\.]+)\\)");

	/**
	 * Class name index.
	 */
	private static final int IDX_CLASS_NAME = 1;
	
	/**
	 * Method name index.
	 */
	private static final int IDX_METHOD_NAME = 2;
	
	/**
	 * Param type index.
	 */
	private static final int IDX_PARAM_TYPE = 3;

	/**
	 * File bootstrap data can be found.
	 */
	@Value("${app.bootstrap.files}")
	private String bootstrapDataFiles;
	
	/**
	 * Directory to keep working files.
	 */
	@Value("${app.work.dir}")
	private String workDir;

	/**
	 * Repository factories to save and fetch entities.
	 */
	@Autowired
	private RepositoryFactory repositoryFactory;

	/**
	 * Object mapper to parse json data.
	 */
	private ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * User service to fetch default users.
	 */
	@Autowired
	private UserService userService;
	
	/**
	 * Application context used to fetch service instances.
	 */
	@Autowired
	private ApplicationContext applicationContext;
	
	/**
	 * Current user service to manage default active user.
	 */
	@Autowired
	private CurrentUserService currentUserService;
	
	/**
	 * Used to fetch user details from entity.
	 */
	@Autowired
	private IAuthenticationService<?> authenticationService;
	
	/**
	 * Free marker service to process templates.
	 */
	@Autowired
	private FreeMarkerService freeMarkerService;

	/**
	 * Setter for setting bootstrap file.
	 * @param bootstrapDataFile File to load.
	 */
	public void setBootstrapDataFile(String bootstrapDataFile)
	{
		this.bootstrapDataFiles = bootstrapDataFile;
	}

	/**
	 * Parse service method string into object that can be used for invocation of save method. 
	 * @param modelType Model type for which service method was specified.
	 * @param serviceMethod Service method string.
	 * @return Parsed service method object.
	 */
	private ServiceMethod parseServiceMethod(Class<?> modelType, String serviceMethod)
	{
		Matcher matcher = SERVICE_METHOD_PATTERN.matcher(serviceMethod);
		
		if(!matcher.matches())
		{
			throw new InvalidArgumentException("Invalid service method format encountered - '{}'. Expected format - <class-name>.<method-name>(param-type)", serviceMethod);
		}
		
		Class<?> serviceClass = null;
		
		try
		{
			serviceClass = Class.forName(matcher.group(IDX_CLASS_NAME));
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("Invalid service class name specified - '{}' in service method string - {}", matcher.group(IDX_CLASS_NAME), serviceMethod);
		}
		
		Object service = applicationContext.getBean(serviceClass);
		
		if(service == null)
		{
			throw new InvalidStateException("No service registered with spring of type - {}", matcher.group(IDX_CLASS_NAME));
		}
		
		Class<?> paramType = null;
		
		try
		{
			paramType = Class.forName(matcher.group(IDX_PARAM_TYPE));
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("Invalid param type specified - '{}' in service method string - {}", matcher.group(IDX_PARAM_TYPE), serviceMethod);
		}
		
		if(!modelType.equals(paramType))
		{
			throw new InvalidArgumentException("Model type {} is not matching specified service method argument type - {}", modelType.getName(), paramType.getName());
		}
		
		Method method = null;
		
		try
		{
			method = serviceClass.getMethod(matcher.group(IDX_METHOD_NAME), paramType);
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("No method found with specified signature - {}", serviceMethod);
		}
		
		return new ServiceMethod(service, method);
	}
	
	/**
	 * Fetches the entity from specified repository using specified id field.
	 * @param repository Repository to use for fetching.
	 * @param model Model to use for id field value.
	 * @param idField Id field name.
	 * @param entityType Entity type expected to fetch.
	 * @return Resulted entity object.
	 */
	private Object fetchByIdField(ICrudRepository<Object> repository, Object model, String idField, Class<?> entityType)
	{
		SearchQuery searchQuery = new SearchQuery(
				new SearchCondition(idField, Operator.EQ, ReflectionUtils.getFieldValue(model, idField))
		);
			
		List<Object> resLst = repository.search(searchQuery);
		
		if(CollectionUtils.isNotEmpty(resLst))
		{
			return resLst.get(0);
		}
		else
		{
			throw new InvalidStateException(
					"An unique constraint exception occurred while saving entity {}. Fetch also failed with identity field '{}' with value - {}", 
					entityType.getName(), idField, ReflectionUtils.getFieldValue(model, idField));
		}
	}
	
	/**
	 * Saves the specified model object using specified service method.
	 * @param model Model to be saved.
	 * @param serviceMethod Service method to be used for saving.
	 * @param repository Repository to be used for fetching saved object.
	 * @param idField Id field name to be used for fetching.
	 * @param entityType Expected entity type.
	 * @return Saved entity object if any.
	 */
	private Object saveByServiceMethod(Object model, ServiceMethod serviceMethod, ICrudRepository<Object> repository, String idField, Class<?> entityType) throws Exception
	{
		serviceMethod.invoke(model);
		
		return fetchByIdField(repository, model, idField, entityType);
	}
	
	/**
	 * Saves the model with specified repository.
	 * @param model Model to be saved.
	 * @param repository Repository to be used for saving.
	 * @param entityType Entity type expected to be fetched.
	 * @param spaceIdentity Space identity to be set on entity.
	 * @param defaultUser Default user to be used for tracking.
	 * @return Saved entity.
	 */
	private Object saveByRepository(Object model, ICrudRepository<Object> repository, Class<?> entityType, String spaceIdentity, UserEntity defaultUser)
	{
		Object entity = WebUtils.convertBean(model, entityType);
		
		//set default fields
		if(entity instanceof WebutilsBaseEntity)
		{
			WebutilsBaseEntity webutilsEntity = (WebutilsBaseEntity) entity;
			
			webutilsEntity.setVersion(1);
			
			if(webutilsEntity instanceof ITenantSpaceBased)
			{
				((ITenantSpaceBased) webutilsEntity).setSpaceIdentity(spaceIdentity);
			}

			//set date fields
			if(webutilsEntity instanceof ITrackedEntity)
			{
				ITrackedEntity trackedEntity = (ITrackedEntity) webutilsEntity;
				
				Date now = new Date();
				trackedEntity.setCreatedOn(now);
				trackedEntity.setUpdatedOn(now);
	
				//set user fields
				if(trackedEntity.getCreatedBy() == null && defaultUser != null)
				{
					trackedEntity.setCreatedBy(defaultUser);
					trackedEntity.setUpdatedBy(defaultUser);
				}
			}
		}
		
		boolean res = repository.save(entity);
		
		if(!res)
		{
			throw new InvalidStateException("Failed to save entity by repository - {}", entity);
		}
		
		return entity;
	}

	/**
	 * Loads entity groups and saves entities specified in it.
	 * @param fileName Name of file from which data is getting loaded.
	 * @param entityGroup Entity group to load.
	 * @param defaultUserName Default user to be used for tracking fields.
	 * @param contextMap Context map to be used for parsing expressions.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadEntityGroup(String fileName, BootstrapData.EntityGroup entityGroup, String defaultUserName, Map<String, Object> contextMap) throws Exception
	{
		logger.debug("Loading entity group - {} with indentity field - {}. Default user - {}", entityGroup.getName(), entityGroup.getIdentityField(), defaultUserName);

		Class<?> entityType = Class.forName(entityGroup.getEntityType());
		Class<?> modelType = Class.forName(entityGroup.getModelType());
		Object entity = null, model = null;
		String entityJson = null;
		String identity = null;
		String identityField = entityGroup.getIdentityField();

		ICrudRepository<Object> crudRepository = (ICrudRepository) repositoryFactory.getRepositoryForEntity(entityType);
		
		UserEntity defaultUser = null;
		
		//if default user is specified, fetch it and set it as active user
		if(defaultUserName != null)
		{
			defaultUser = userService.getUser(defaultUserName, null);
			
			if(defaultUser == null)
			{
				throw new InvalidStateException("Failed to fetch default user with specified name - {}", defaultUserName);
			}
			
			UserDetails userDetails = authenticationService.toUserDetails(defaultUser);
			currentUserService.setInternalCurrentUser(userDetails);
		}
		
		String serviceMethodStr = entityGroup.getServiceMethod();
		ServiceMethod serviceMethod = null;
		
		if(serviceMethodStr != null)
		{
			serviceMethod = parseServiceMethod(modelType, serviceMethodStr);
		}
		
		for(Map<String, Object> entityMap : entityGroup.getEntities())
		{
			logger.debug("Saving entity {} via model {} with properties - {}", entityType.getName(), modelType.getName(), entityMap);

			identity = (String) entityMap.remove("#identity");

			entityJson = objectMapper.writeValueAsString(entityMap);
			entityJson = freeMarkerService.processTemplate(fileName + "::" + entityGroup.getName(), entityJson, contextMap);

			logger.debug("Saving with model json - {}", entityJson);

			// create the entity and populate the properties
			model = objectMapper.readValue(entityJson, modelType);

			try
			{
				if(serviceMethod != null)
				{
					if(identityField == null)
					{
						throw new InvalidArgumentException("No indentity field specified for entity group '{}'. Identity field is mandatory for service method based groups.", entityGroup.getName());
					}
					
					entity = saveByServiceMethod(model, serviceMethod, crudRepository, identityField, entityType);
				}
				else
				{
					entity = saveByRepository(model, crudRepository, entityType, entityGroup.getSpaceIdentity(), defaultUser);
				}

				logger.debug("Saved entity {} successfully", entity);
			} catch(UniqueConstraintViolationException ex)
			{
				if(identityField != null)
				{
					SearchQuery searchQuery = new SearchQuery(
						new SearchCondition(identityField, Operator.EQ, ReflectionUtils.getFieldValue(model, identityField))
					);
					
					List<Object> resLst = crudRepository.search(searchQuery);
					
					if(CollectionUtils.isNotEmpty(resLst))
					{
						entity = resLst.get(0);
					}
					else
					{
						throw new InvalidStateException(
								"An unique constraint exception occurred while saving entity {}. Fetch also failed with identity field '{}' with value - {}", 
								entity, identityField, ReflectionUtils.getFieldValue(entity, identityField));
					}
				}
				
				logger.warn("An unique constrain exception occurred while saving entity. Assuming entity already exist. Entity: {} \n\tError: {}", entity, "" + ex);
			}

			//if no direct identity is specified
			if(identity == null)
			{
				//check if any identity field is specified on entity and use it
				if(entityGroup.getIdentityField() != null)
				{
					addEntityToContext(entityGroup.getName(), "" + PropertyUtils.getProperty(entity, entityGroup.getIdentityField()), entity, contextMap);
				}
			}
			//if static id value is specified, use it directly
			else
			{
				addEntityToContext(entityGroup.getName(), "" + identity, entity, contextMap);
			}
		}
		
		//reset default user, in case it is set
		currentUserService.setInternalCurrentUser(null);
	}
	
	/**
	 * Adds specified entity to specified context map under specified group.
	 * @param groupName Group name under which entity should be added.
	 * @param identity Entity identity string.
	 * @param entity Entity to add.
	 * @param contextMap Context map on which entity should be added.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addEntityToContext(String groupName, String identity, Object entity, Map<String, Object> contextMap)
	{
		Map<String, Object> entityMap = (Map) contextMap.get("entity");
		
		if(entityMap == null)
		{
			entityMap = new HashMap<>();
			contextMap.put("entity", entityMap);
		}
		
		Map<String, Object> groupMap = (Map) entityMap.get(groupName);
		
		if(groupMap == null)
		{
			groupMap = new HashMap<>();
			entityMap.put(groupName, groupMap);
		}

		groupMap.put(identity, entity);
	}

	/**
	 * Loads the specified bootstrap file.
	 * @param bootstrapDataFile Bootstrap file to load
	 * @param contextMap Context map for free marker expression evaluation.
	 * @return true if bootstrap file is loaded.
	 */
	private boolean loadBootstrapData(String bootstrapDataFile, Map<String, Object> contextMap) throws Exception
	{
		//load the input resource
		Resource resource = applicationContext.getResource(bootstrapDataFile);
		
		if(!resource.exists())
		{
			logger.warn("Configured bootstrap data file does not exist - " + bootstrapDataFile);
			return false;
		}
		
		//load and create work directory
		File workDirFile = new File(workDir);
		
		if(!workDirFile.exists())
		{
			workDirFile.mkdirs();
		}
		
		File loadedFile = new File(workDirFile, resource.getFilename() + ".loaded");

		// check if the data file is modified from last load
		if(loadedFile.exists())
		{
			logger.warn("Found bootstrap file '{}' is already loaded. Hence skipping data load.", bootstrapDataFile);
			return false;
		}

		// load the data file
		logger.debug("Loading data file - {}", bootstrapDataFile);
		
		//load the file and process it as free marker template (first level)
		String fileContent = null;
		
		try
		{
			InputStream is = resource.getInputStream();
			fileContent = IOUtils.toString(is);
			is.close();
		}catch(Exception ex)
		{
			throw new InvalidArgumentException(ex, "An error occurred while loading bootstrap resource - {}", bootstrapDataFile);
		}
		
		fileContent = freeMarkerService.processTemplate(bootstrapDataFile, fileContent, contextMap);

		ObjectMapper objectMapper = new ObjectMapper();
		BootstrapData bootstrapData = objectMapper.readValue(fileContent, BootstrapData.class);

		for(BootstrapData.EntityGroup entityGroup : bootstrapData.getEntityGroups())
		{
			loadEntityGroup(bootstrapDataFile, entityGroup, bootstrapData.getDefaultUserName(), contextMap);
		}
		
		loadedFile.createNewFile();
		
		return true;
	}

	/**
	 * Post construct method to load bootstrap file.
	 */
	public void load()
	{
		// check if the data file is configured and available
		if(StringUtils.isBlank(bootstrapDataFiles))
		{
			logger.debug("No bootstrap file(s) configured. Skipping bootstrap data load.");
			return;
		}
		
		String files[] = bootstrapDataFiles.split("\\s*\\,\\s*");
		
		logger.debug("Loading bootstrap files - {}", Arrays.toString(files));
		
		Map<String, Object> contextMap = new HashMap<>();

		for(String file : files)
		{
			try
			{
				loadBootstrapData(file, contextMap);
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while loading the bootstrap files", ex);
			}
		}

		logger.debug("Bootstrap data loaded successfully..");
	}
}
