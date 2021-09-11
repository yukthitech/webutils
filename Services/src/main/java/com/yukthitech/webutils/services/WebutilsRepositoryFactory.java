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

package com.yukthitech.webutils.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.NoTableExistsException;
import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.webutils.IRepositoryMethodRegistry;
import com.yukthitech.webutils.IWebUtilsInternalConstants;
import com.yukthitech.webutils.WebutilsConfiguration;
import com.yukthitech.webutils.WebutilsPropertyPlaceholder;
import com.yukthitech.webutils.annotations.RegistryMethod;
import com.yukthitech.webutils.common.annotations.Conditional;
import com.yukthitech.webutils.common.annotations.Optional;
import com.yukthitech.webutils.repository.IWebutilsRepository;
import com.yukthitech.webutils.repository.RepositoryContext;
import com.yukthitech.webutils.repository.WebutilsBaseEntity;
import com.yukthitech.webutils.security.ISessionRepository;
import com.yukthitech.webutils.services.dynamic.DynamicMethod;
import com.yukthitech.webutils.services.dynamic.DynamicMethodFactory;
import com.yukthitech.webutils.services.freemarker.FreeMarkerService;

/**
 * This loader is capable of initializing all the repositories in the classpath.
 * 
 * Whether to load extensions repositories can be controlled using {@link WebutilsConfiguration#setExtensionsRequired(boolean)}. Similarly
 * the packages to scan can be controlled using {@link WebutilsConfiguration#setBasePackages(java.util.List)}
 * 
 * @author akiran
 */
@Service
public class WebutilsRepositoryFactory
{
	private static Logger logger = LogManager.getLogger(WebutilsRepositoryFactory.class);
	
	/**
	 * Used to access spring environment.
	 */
	@Autowired
	private WebutilsPropertyPlaceholder propertyPlaceholder;
	
	/**
	 * Class scan service used to find repositories
	 */
	@Autowired
	private ClassScannerService classScannerService;
	
	/**
	 * Repository factory which is used to load repositories
	 */
	@Autowired
	private RepositoryFactory repositoryFactory;
	
	/**
	 * Configuration used to control extension repository load
	 */
	@Autowired
	private WebutilsConfiguration configuration;

	/**
	 * Application context to fetch registry instances
	 */
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private RepositoryContext repositoryContext;
	
	@Autowired
	private FreeMarkerService freeMarkerService;
	
	/**
	 * Factory to create dynamic methods
	 */
	private DynamicMethodFactory dynamicMethodFactory = new DynamicMethodFactory();
	
	private List<ICrudRepository<?>> repositories = new ArrayList<>();
	
	private Map<Class<?>, ICrudRepository<?>> repositoryProxies = new HashMap<Class<?>, ICrudRepository<?>>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostConstruct
	private void init()
	{
		//scan and fetch all repository classes
		Set<Class<?>> actRepos = classScannerService.getClassesOfType(ICrudRepository.class, IWebutilsRepository.class);
		
		//sort the repos, so that the errors, if any, comes in same order
		Set<Class<?>> repos = new TreeSet<Class<?>>(new Comparator<Class<?>>()
		{
			@Override
			public int compare(Class<?> o1, Class<?> o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		repos.addAll(actRepos);
		
		boolean loadExtensions = configuration.isExtensionsRequired();

		Set<Class<? extends Annotation>> dynAnnotLst = (Set) classScannerService.getClassesWithAnnotation(RegistryMethod.class);
		
		ICrudRepository<?> repository = null;
		
		logger.debug("*******************************************************************");
		logger.debug("Repository loading started..............");
		logger.debug("*******************************************************************");
		
		for(Class<?> type : repos)
		{
			if(IWebutilsRepository.class.equals(type))
			{
				continue;
			}
			
			if(!loadExtensions && type.getName().startsWith(IWebUtilsInternalConstants.EXTENSIONS_REPO_BASE_PACKAGE))
			{
				logger.debug("Skipping extensions repository as extensions are disabled: " + type.getName());
				continue;
			}
			
			Conditional conditional = type.getAnnotation(Conditional.class);
			
			if(conditional != null)
			{
				boolean condRes = freeMarkerService.processConditionTemplate("repo-condition-" + type.getName(), conditional.value(), propertyPlaceholder);
				
				if(!condRes)
				{
					logger.warn("Ignoring repository, as the condition on repository is evaluated to false: " + type.getName());
					continue;
				}
			}
			
			logger.debug("Loading repository: " + type.getName());
			
			if( !IWebutilsRepository.class.isAssignableFrom(type) )
			{
				if(ISessionRepository.class.equals(type))
				{
					repository = repositoryFactory.getRepository((Class) type);
					repository.setExecutionContext(repositoryContext);

					logger.debug("Non-webutils repository loaded: {}", type.getName());
					repositories.add(repository);
					continue;
				}
				
				logger.warn("Found repository which is of non webutils repository type - {}", type.getName());
				continue;
			}
			
			// Handle no table exists exception, and check if repo is optional or not and throw approp exception
			try
			{
				repository = repositoryFactory.getRepository((Class) type);
			} catch(NoTableExistsException ex)
			{
				Optional optional = type.getAnnotation(Optional.class);
				
				if(optional != null)
				{
					logger.warn("Skipping optional repository {} as corresponding table is not found in the db", type.getName());
					continue;
				}
				
				throw ex;
			}
			
			repository.setExecutionContext(repositoryContext);
			
			if( !WebutilsBaseEntity.class.isAssignableFrom(repository.getEntityDetails().getEntityType()) )
			{
				logger.warn("Found entity which is of non webutils entity type - {}", type.getName());
				continue;
			}
			
			registerDynamicMethods(type, repository, dynAnnotLst);
			
			logger.debug("Repository loaded: {}", type.getName());
			repositories.add(repository);
		}
		
		logger.debug("*******************************************************************");
		logger.debug("Repository loading completed");
		logger.debug("*******************************************************************");
	}
	
	/**
	 * Registers all the dynamic of specified repository with corresponding registries.
	 * @param repository Repository in which dynamic methods has to be scanned
	 * @param dynAnnotLst Dynamic annotation list to be scanned
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void registerDynamicMethods(Class<?> repoCls, ICrudRepository<?> repository, Set<Class<? extends Annotation>> dynAnnotLst)
	{
		logger.debug("Scanning {} repository for dynamic methods", repoCls.getName());
		
		Method methods[] = repoCls.getMethods();
		RegistryMethod dynamicRepositoryMethod = null;
		IRepositoryMethodRegistry registry = null;
		Annotation annotation = null;
		DynamicMethod dynamicMethod = null;
		
		//loop through repository methods
		for(Method method : methods)
		{
			//check and loop through dynamic annotation list
			for(Class<? extends Annotation> annotType : dynAnnotLst)
			{
				annotation = method.getAnnotation(annotType);
				
				//if method is not having dynamic annotation
				if(annotation == null)
				{
					continue;
				}
				
				logger.debug("Repository method {}.{}() found with dynamic method annotation - {}", 
									repoCls.getName(), method.getName(), annotType.getName());
				
				dynamicRepositoryMethod = annotType.getAnnotation(RegistryMethod.class);
				registry = applicationContext.getBean(dynamicRepositoryMethod.registryType());
				
				if(registry == null)
				{
					throw new IllegalStateException(String.format("No registry of type %s found on annotation - %s on spring context", 
							dynamicRepositoryMethod.registryType().getName(), annotType.getName()));
				}
				
				//if the target method is expected to be fully dynamic
				if(dynamicRepositoryMethod.dynamic())
				{
					//register the repository method
					dynamicMethod = dynamicMethodFactory.buildDynamicMethod(repoCls, method);
					applicationContext.getAutowireCapableBeanFactory().autowireBean(dynamicMethod);
					dynamicMethod.setDefaultObject(repository);
					
					registry.registerDynamicMethod(dynamicMethod, annotation);
				}
				else
				{
					registry.registerRepositoryMethod(method, annotation, repository);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	<R extends ICrudRepository<?>> R getActualRepository(Class<R> repoType)
	{
		for(ICrudRepository<?> repo : this.repositories)
		{
			if(repoType.isAssignableFrom(repo.getClass()))
			{
				return (R) repo;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns lazy loading repository.
	 * @param <R>
	 * @param repoType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized <R extends ICrudRepository<?>> R getRepository(Class<R> repoType)
	{
		R repo = (R) this.repositoryProxies.get(repoType);
		
		if(repo != null)
		{
			return repo;
		}
		
		repo = (R) Proxy.newProxyInstance(RepositoryFactory.class.getClassLoader(), new Class<?>[] {repoType}, new WebutilsRepositoryProxy(this, repoType));
		this.repositoryProxies.put(repoType, repo);
		
		return repo;
	}

}
