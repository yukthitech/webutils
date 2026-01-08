package com.webutils.common.repo;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.RepositoryFactory;

public class RepositoryFactoryBean<T extends ICrudRepository<?>> implements FactoryBean<T>
{
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
		return repositoryFactory.getRepository(repositoryInterface);
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
