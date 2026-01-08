package com.webutils.common.repo;

import java.util.Arrays;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;

import com.yukthitech.persistence.ICrudRepository;

public class RepositoryRegistrar implements ImportBeanDefinitionRegistrar
{
	private static Logger logger = LogManager.getLogger(RepositoryRegistrar.class);
	
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry)
	{
		Map<String, Object> attrs = importingClassMetadata
                .getAnnotationAttributes(EnableRepositories.class.getName());
		
		// Scan a base package for interfaces
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) 
		{
			@Override
		    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) 
			{
		        // Allow interfaces that extend ICrudRepository
		        return true;
		    }
		};
		
		String[] basePackages = (String[]) attrs.get("basePackages");
		
		logger.debug("Loading repositories from package: {}", Arrays.toString(basePackages));

		scanner.addIncludeFilter(new AssignableTypeFilter(ICrudRepository.class));
		
		for(String basePackage : basePackages)
		{
			for(BeanDefinition candidate : scanner.findCandidateComponents(basePackage))
			{
				try
				{
					String className = candidate.getBeanClassName();
					
					logger.debug("Registering repository of type: {}", className);
					
					Class<?> repoInterface = Class.forName(className);
					
					BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RepositoryFactoryBean.class);
					builder.addConstructorArgValue(repoInterface);
	
					registry.registerBeanDefinition(repoInterface.getSimpleName(), builder.getBeanDefinition());
	
				} catch(ClassNotFoundException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
	}
}
