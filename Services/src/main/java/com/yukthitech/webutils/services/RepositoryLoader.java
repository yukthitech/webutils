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
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.webutils.IRepositoryMethodRegistry;
import com.yukthitech.webutils.IWebUtilsInternalConstants;
import com.yukthitech.webutils.WebutilsConfiguration;
import com.yukthitech.webutils.annotations.RegistryMethod;
import com.yukthitech.webutils.repository.IWebutilsRepository;
import com.yukthitech.webutils.repository.RepositoryContext;
import com.yukthitech.webutils.repository.WebutilsEntity;
import com.yukthitech.webutils.security.ISessionRepository;
import com.yukthitech.webutils.services.dynamic.DynamicMethod;
import com.yukthitech.webutils.services.dynamic.DynamicMethodFactory;

/**
 * This loader is capable of initializing all the repositories in the classpath.
 * 
 * Whether to load extensions repositories can be controlled using {@link WebutilsConfiguration#setExtensionsRequired(boolean)}. Similarly
 * the packages to scan can be controlled using {@link WebutilsConfiguration#setBasePackages(java.util.List)}
 * 
 * @author akiran
 */
@Service
public class RepositoryLoader
{
	private static Logger logger = LogManager.getLogger(RepositoryLoader.class);
	
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
	
	/**
	 * Factory to create dynamic methods
	 */
	private DynamicMethodFactory dynamicMethodFactory = new DynamicMethodFactory();
	
	/**
	 * Scans and loads all repositories from base packages
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostConstruct
	private void init()
	{
		//scan and fetch all repository classes
		Set<Class<?>> repos = classScannerService.getClassesOfType(ICrudRepository.class, IWebutilsRepository.class);
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
			
			logger.debug("Loading repository: " + type.getName());
			
			if( !IWebutilsRepository.class.isAssignableFrom(type) )
			{
				if(ISessionRepository.class.equals(type))
				{
					repository = repositoryFactory.getRepository((Class) type);
					repository.setExecutionContext(repositoryContext);
					continue;
				}
				
				logger.warn("Found repository which is of non webutils repository type - {}", type.getName());
				continue;
			}
			
			repository = repositoryFactory.getRepository((Class) type);
			repository.setExecutionContext(repositoryContext);
			
			if( !WebutilsEntity.class.isAssignableFrom(repository.getEntityDetails().getEntityType()) )
			{
				logger.warn("Found entity which is of non webutils entity type - {}", type.getName());
				continue;
			}
			
			registerDynamicMethods(type, repository, dynAnnotLst);
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
}
