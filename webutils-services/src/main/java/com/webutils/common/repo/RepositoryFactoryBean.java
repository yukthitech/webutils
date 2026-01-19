package com.webutils.common.repo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.webutils.common.Optional;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.NoTableExistsException;
import com.yukthitech.persistence.repository.RepositoryFactory;

public class RepositoryFactoryBean<T extends ICrudRepository<?>> implements FactoryBean<T>
{
	private static Logger logger = LogManager.getLogger(RepositoryFactoryBean.class);
	
	private Class<T> repositoryInterface;
	
	@Autowired
	private RepositoryFactory repositoryFactory;

	public RepositoryFactoryBean(Class<T> repositoryInterface) throws ClassNotFoundException
	{
		this.repositoryInterface = repositoryInterface;
	}

	@Override
	public T getObject()
	{
		try
		{
			return repositoryFactory.getRepository(repositoryInterface);
		} catch(NoTableExistsException ex)
		{
			Optional optional = repositoryInterface.getAnnotation(Optional.class);
			
			if(optional != null)
			{
				logger.warn("Skipping optional repository {} as corresponding table is not found in the db", repositoryFactory.getName());
				return null;
			}
			
			throw ex;
		}
	}

	@Override
	public Class<?> getObjectType()
	{
		return repositoryInterface;
	}

	@Override
	public boolean isSingleton()
	{
		return true;
	}
}
