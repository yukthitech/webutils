package com.yukthitech.webutils.services;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.PersistenceException;

/**
 * Proxy for repository used for lazy loading of repositories.
 * @author akiran
 */
public class WebutilsRepositoryProxy implements InvocationHandler
{
	private WebutilsRepositoryFactory repositoryFactory;
	
	private Class<? extends ICrudRepository<?>> type;
	
	private Object repository;
	
	public WebutilsRepositoryProxy(WebutilsRepositoryFactory repositoryFactory, Class<? extends ICrudRepository<?>> type)
	{
		this.repositoryFactory = repositoryFactory;
		this.type = type;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		if(repository == null)
		{
			repository = repositoryFactory.getActualRepository(type);
		}
		
		if(repository == null)
		{
			throw new NoRepositoryFoundException("Specified repository is not loaded: " + type.getName());
		}

		try
		{
			return method.invoke(repository, args);
		}catch(InvocationTargetException ex)
		{
			if(ex.getCause() instanceof PersistenceException)
			{
				throw ex.getCause();
			}
			
			throw ex;
		}
	}
}
