/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.webutils.lov;

import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.InvalidRequestException;
import com.yukthitech.webutils.common.annotations.LOV;
import com.yukthitech.webutils.common.lov.EditableLovValue;
import com.yukthitech.webutils.common.lov.ValueLabel;
import com.yukthitech.webutils.security.ISecurityService;
import com.yukthitech.webutils.security.SecurityInvocationContext;
import com.yukthitech.webutils.security.UnauthorizedException;
import com.yukthitech.webutils.security.WebutilsSecurityService;
import com.yukthitech.webutils.services.BaseCrudService;
import com.yukthitech.webutils.services.ClassScannerService;
import com.yukthitech.webutils.services.CurrentUserService;
import com.yukthitech.webutils.services.RequestCache;
import com.yukthitech.webutils.services.dynamic.DynamicMethod;
import com.yukthitech.webutils.services.dynamic.DynamicMethodException;
import com.yukthitech.webutils.services.dynamic.DynamicMethodFactory;
import com.yukthitech.webutils.services.prop.CopyContext;
import com.yukthitech.webutils.services.prop.ValueConverter;
import com.yukthitech.webutils.user.UserEntity;

import jakarta.annotation.PostConstruct;

/**
 * The Class StoredLovService.
 */
@Service
public class StoredLovService extends BaseCrudService<StoredLovEntity, IStoredLovRepository>
{
	private static Logger logger = LogManager.getLogger(StoredLovService.class);
	
	/**
	 * Default lov name, to be used for lov without parent.  
	 */
	@Value("${webutils.defaultLov.name:DUMMY}")
	private String defaultLovName;
	
	/**
	 * Default lov option, to be used for lov without parent.  
	 */
	@Value("${webutils.defaultLov.option:DUMMY}")
	private String defaultLovOption;
	
	/**
	 * Store lov cache time in minutes.
	 */
	@Value("${webutils.stroedLov.cacheItemCount:50}")
	private int cacheItemCount;

	/**
	 * Store lov cache time in minutes.
	 */
	@Value("${webutils.stroedLov.cacheTimeMin:60}")
	private int cacheTimeMin;

	/**
	 * The security service.
	 */
	@Autowired
	private ISecurityService securityService;
	
	/**
	 * Provides current user details.
	 */
	@Autowired
	private CurrentUserService currentUserService;
	
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ClassScannerService classScannerService;
	
	/**
	 * Security service.
	 */
	@Lazy
	@Autowired
	private WebutilsSecurityService webutilsSecurityService;
	
	@Autowired
	private RequestCache requestCache;

	/**
	 * Default lov option to be used as parent.
	 */
	private Long defaultLovOptionId;
	
	/**
	 * LOV provider method details cache. These methods can provide dynamic lov option id, which will be
	 * used as parent for new options being created.
	 */
	private Map<String, DynamicMethod> nameToProviderMet = new HashMap<>();

	/**
	 * The lov val repository.
	 */
	private IStoredLovOptionRepository lovValRepository;
	
	/**
	 * Lov cache.
	 */
	private Cache<String, StoredLovEntity> cache;
	
	private DynamicMethodFactory dynamicMethodFactory = new DynamicMethodFactory();

	/**
	 * Inits the.
	 */
	@PostConstruct
	private void init()
	{
		this.lovValRepository = super.webutilsRepositoryFactory.getRepository(IStoredLovOptionRepository.class);
		
		logger.debug("Fetching default LOV option id (to act as default parent) with [LOV: {}, Option: {}]", defaultLovName, defaultLovOption);
		
		this.defaultLovOptionId = this.lovValRepository.fetchIdByParentAndLabel(defaultLovName, defaultLovOption);
		
		if(defaultLovOptionId == null)
		{
			throw new InvalidStateException("Could not find default LOV option id (to act as default parent) with [LOV: {}, Option: {}]", 
					defaultLovName, defaultLovOption);
		}
		
		cache = Caffeine.newBuilder()
	        .maximumSize(cacheItemCount)
	        .expireAfterWrite(cacheTimeMin, TimeUnit.MINUTES)
	        .build();
		
		loadParentProviderMethods();
	}
	
	/**
	 * Loads the provider Lov option methods into cache.
	 */
	private void loadParentProviderMethods()
	{
		Set<Method> providerMethods = classScannerService.getMethodsAnnotatedWith(ParentLovProvider.class);
		ParentLovProvider providerMethod = null;
		DynamicMethod dynamicMethod = null;
		String name = null;
		Object springComponent = null;
		
		//loop through all registered services
		for(Method method : providerMethods)
		{
			springComponent = applicationContext.getBean(method.getDeclaringClass());
			
			if(springComponent == null)
			{
				logger.info("Ignoring provider-method {}.{}() as the declaring class instance is not present in spring scope", method.getDeclaringClass().getName(), method.getName());
				continue;
			}
			
			if(method.getParameterCount() != 0)
			{
				throw new InvalidConfigurationException("Invalid arguments (expected zero params) specified for provider method - {}.{}()", method.getDeclaringClass().getName(), method.getName());
			}
			
			if(!long.class.equals(method.getReturnType()) && !Long.class.equals(method.getReturnType()))
			{
				throw new InvalidConfigurationException("Invalid return type (expected long) specified for provider method - {}.{}()", method.getDeclaringClass().getName(), method.getName());
			}

			providerMethod = method.getAnnotation(ParentLovProvider.class);
			name = providerMethod.name();

			try
			{
				dynamicMethod = dynamicMethodFactory.buildDynamicMethod(method.getDeclaringClass(), method);
			}catch(DynamicMethodException ex)
			{
				throw new InvalidConfigurationException("Invalid arguments specified for provider method - {}.{}()", method.getDeclaringClass().getName(), method.getName(), ex);
			}
			
			dynamicMethod.setDefaultObject(springComponent);
			
			if(nameToProviderMet.containsKey(name))
			{
				throw new InvalidConfigurationException("Duplicate provider-method found with same name - {}, {}", nameToProviderMet.get(name), dynamicMethod);
			}
			
			nameToProviderMet.put(name, dynamicMethod);
		}
	}
	
	/**
	 * Invokes the provider-method with specified name and fetches the parent lov that
	 * can be used.
	 * @param providerName
	 * @return Fetches lov option parent id for current provider name
	 */
	private Long getParentLovId(String providerName)
	{
		DynamicMethod method = nameToProviderMet.get(providerName);
		
		if(method == null)
		{
			throw new InvalidParameterException("Invalid provider-method name specified - " + providerName);
		}
		
		//if security service is specified, check user authorization for target search method
		if(securityService != null)
		{
			SecurityInvocationContext context = webutilsSecurityService.newSecurityInvocationContext(method.getType(), method.getMethod(), null);
			
			if(!securityService.isAuthorized(context))
			{
				throw new UnauthorizedException("Current user is not authorized to execute lov query - {}", providerName);
			}
		}

		return (Long) method.invoke();
	}

	
	/**
	 * Fetches provider id for specified lov entity. Based on provider name 
	 * configured for lov.
	 * 
	 * @param lovEntity
	 * @return
	 */
	private Long fetchParentLovOptionId(StoredLovEntity lovEntity)
	{
		String providerName = lovEntity.getParentOptIdProvider();
		
		// if provider name is not specified, used default lov option id
		if(StringUtils.isBlank(providerName))
		{
			return defaultLovOptionId;
		}
		
		// id provider name is configured, fetch the parent lov id based
		//  on given provider name
		Long res = getParentLovId(providerName);
		
		if(res == null)
		{
			throw new InvalidStateException("Could not fetch dynamic lov parent option id using provider: {}", providerName);
		}
		
		return res;
	}
	
	/**
	 * A converter method that converts {@link EditableLovValue} into {@link StoredLovOptionEntity} which
	 * can be persisted with relations. This method will take care of persisting new lov options.
	 * 
	 * This will be used during write operations.
	 * 
	 * @param editable
	 * @param context
	 * @return
	 */
	@ValueConverter(sourceType = EditableLovValue.class, targetType = StoredLovOptionEntity.class)
	public StoredLovOptionEntity editableToLov(EditableLovValue editable, CopyContext context)
	{
		LOV lov = context.getSourceField().getAnnotation(LOV.class);
		StoredLovEntity lovEntity = getLov(lov.name());
		
		// Fetch parent option id (based on configured parent provider name with lov)
		Long parentOptionId = fetchParentLovOptionId(lovEntity);
		
		// Fetch the lov option list (indexed with label and id)
		//    the list will be cached on request. So that on same request db will not hit again
		LovOptionListWrapper optLst = requestCache.get(String.format("lov-opt-list", lovEntity.getId(), parentOptionId), key -> 
		{
			List<StoredLovOptionEntity> options = lovValRepository.fetchByLov(lovEntity.getId(), parentOptionId);
			return new LovOptionListWrapper(options);
		});
		
		// if new option is being provided
		if(editable.getId() == null || editable.getId() <= 0)
		{
			// check in case insensitive way, if option is already present 
			StoredLovOptionEntity lovOpt = optLst.getLovOptionByLabel(editable.getNewValue()); 

			// if label is already present reuse it
			if(lovOpt != null)
			{
				return lovOpt;
			}
			
			// if not present, create new one
			lovOpt = new StoredLovOptionEntity()
					.setParentLov(lovEntity)
					.setLabel(editable.getNewValue())
					.setCreatedBy(new UserEntity(currentUserService.getCurrentUserDetails().getUserId()))
					.setApproved(false)
					.setApprovedOn(new Date())
					.setParentLovOption((StoredLovOptionEntity) new StoredLovOptionEntity().setId(defaultLovOptionId))
					;
			
			lovValRepository.save(lovOpt);
			
			// update cached list with new option
			optLst.addLovOption(lovOpt);
			return lovOpt;
		}
		
		// ensure existing id provided is part of current lov list
		boolean isValid = (optLst.getById(editable.getId()) != null);
				
		// if not valid throw exception
		if(!isValid)
		{
			throw new InvalidArgumentException("Invalid option-id '{}' specified for LOV: {}", editable.getId(), lov.name());
		}
		
		return (StoredLovOptionEntity) new StoredLovOptionEntity().setId(editable.getId());
	}
	
	/**
	 * Converter method which converts entity to EditableLovValue. This will be helpful during read operation.
	 * @param entity
	 * @param context
	 * @return
	 */
	@ValueConverter(sourceType = StoredLovOptionEntity.class, targetType = EditableLovValue.class)
	public EditableLovValue lovToEditable(StoredLovOptionEntity entity, CopyContext context)
	{
		return new EditableLovValue().setId(entity.getId());
	}
	
	private StoredLovEntity getLov(String name)
	{
		return cache.get(name, lovName -> 
		{
			return super.repository.fetchByName(lovName);
		});
	}
	
	/**
	 * Gets the lov values.
	 *
	 * @param name the name
	 * @param dependencyValue the dependency value
	 * @param locale the locale
	 * 
	 * @return the lov values
	 */
	public List<ValueLabel> getLovValues(String name, String dependencyValue, Locale locale)
	{
		Long dependencyId = null;
		
		if(StringUtils.isNotBlank(dependencyValue))
		{
			try
			{
				dependencyId = Long.parseLong(dependencyValue);
			}catch(Exception ex)
			{
				throw new InvalidRequestException("Invalid dependency-value specified (should be numerical value): {}", dependencyValue);
			}
		}
		
		StoredLovEntity lovEntity = super.repository.fetchByName(name);
		
		if(lovEntity == null)
		{
			throw new InvalidRequestException("Invalid stored-lov name specified: " + name);
		}
		
		if(!securityService.isAuthorized(lovEntity))
		{
			throw new UnauthorizedException("Current user is not authorized to access stored-lov - {}", name);
		}
		
		List<StoredLovOptionEntity> options = lovValRepository.fetchByLov(lovEntity.getId(), dependencyId);
		
		if(CollectionUtils.isEmpty(options))
		{
			return Collections.emptyList();
		}
		
		return options.stream()
				.map(opt -> new ValueLabel("" + opt.getId(), opt.getLabel()))
				.collect(Collectors.toList());
	}

	/**
	 * Gets the lov values.
	 *
	 * @param name the name
	 * @param locale the locale
	 * 
	 * @return the lov values
	 */
	public List<ValueLabel> getLovValues(String name, Locale locale)
	{
		return getLovValues(name, null, locale);
	}
	
	/**
	 * Checks if is valid lov.
	 *
	 * @param name the name
	 * 
	 * @return true, if is valid lov
	 */
	public boolean isValidLov(String name)
	{
		return repository.isValid(name);
	}
}
