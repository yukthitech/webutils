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

package com.yukthi.webutils.services;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.ITransaction;
import com.yukthi.persistence.repository.RepositoryFactory;
import com.yukthi.webutils.IEntity;
import com.yukthi.webutils.common.IExtendableModel;
import com.yukthi.webutils.utils.WebUtils;

/**
 * Abstracts basic functionality that is required for all entities
 * @author akiran
 */
public abstract class BaseCrudService<E extends IEntity, R extends ICrudRepository<E>>
{
	/** The logger. */
	private static Logger logger = LogManager.getLogger(BaseCrudService.class);
	
	/**
	 * Autowired repository factory, used to fetch repository
	 */
	@Autowired
	protected RepositoryFactory repositoryFactory;

	/**
	 * Extension service to update/read extension fields
	 */
	@Autowired
	private ExtensionService extensionService;

	/**
	 * Repository type
	 */
	private Class<R> repositoryType;
	
	/**
	 * CRUD repository obtained from factory in init()
	 */
	protected R repository;
	
	/**
	 * Instantiates a new base crud service.
	 *
	 * @param repositoryType the repository type
	 */
	public BaseCrudService(Class<R> repositoryType)
	{
		this.repositoryType = repositoryType;
	}
	
	/**
	 * Used to fetch repository from autowired factory 
	 */
	@PostConstruct
	private void init()
	{
		repository = repositoryFactory.getRepository(repositoryType);
	}
	
	/**
	 * Saves specified entity "entity" and saves extension fields from "extendedFieldsModel"
	 * @param entity Entity field to save
	 * @param extendedFieldsModel Extension fields to save. Optional, can be null
	 */
	public void save(E entity, IExtendableModel extendedFieldsModel)
	{
		try(ITransaction transaction = repository.newOrExistingTransaction())
		{
			logger.trace("Trying to save entity - {}", entity);
			
			repository.save(entity);
			
			if(extendedFieldsModel != null)
			{
				logger.trace("Trying to save extended fields of entity - {}", entity);
				extensionService.saveExtendedFields(entity.getId(), extendedFieldsModel);
			}
			
			transaction.commit();
		}catch(Exception ex)
		{
			logger.error("An error occurred while saving entity - " + entity, ex);
			throw new IllegalStateException("An error occurred while saving entity - " + entity, ex);
		}
	}
	
	/**
	 * Updates specified entity "entity" and saves extension fields from "extendedFieldsModel"
	 * @param entity Entity field to update
	 * @param extendedFieldsModel Extension fields to save. Optional, can be null
	 */
	public void update(E entity, IExtendableModel extendedFieldsModel)
	{
		try(ITransaction transaction = repository.newOrExistingTransaction())
		{
			logger.trace("Trying to update entity - {}", entity);
			
			repository.update(entity);
			
			if(extendedFieldsModel != null)
			{
				logger.trace("Trying to update extended fields of entity - {}", entity);
				extensionService.saveExtendedFields(entity.getId(), extendedFieldsModel);
			}
			
			transaction.commit();
		}catch(Exception ex)
		{
			logger.error("An error occurred while updating entity - " + entity, ex);
			throw new IllegalStateException("An error occurred while updating entity - " + entity, ex);
		}
	}
	
	/**
	 * Fetches entity with specified id
	 * @param id Entity id
	 * @return Matching entity
	 */
	public E fetch(long id)
	{
		E entity = repository.findById(id);
		
		logger.trace("Entity fetch with id '{}' resulted in  - {}", id, entity);
		return entity;
	}
	
	/**
	 * To be used when fetching needs to be done with extensions
	 * @param id Entity id to be fetched
	 * @param extendableModelType Corresponding entity's model type which can hold extension fields
	 * @return Converted model with extension fields
	 */
	public <M extends IExtendableModel> M fetchWithExtensions(long id, Class<M> extendableModelType)
	{
		E entity = repository.findById(id);
		
		M model = WebUtils.convertBean(entity, extendableModelType);
		extensionService.fetchExtendedValues(model);
		
		logger.trace("Entity fetch-with-extensions for id '{}' resulted in  - {}", id, entity);
		return model;
	}
 
	/**
	 * Fetches number of entities in DB
	 * @return entity count
	 */
	public long getCount()
	{
		long count = repository.getCount();
		
		logger.trace("Got count as - {}", count);
		return count;
	}
	
	/**
	 * Deletes entity with specified id
	 * @param id Entity id to delete
	 */
	public boolean deleteById(long id)
	{
		try(ITransaction transaction = repository.newOrExistingTransaction())
		{
			logger.trace("Trying to delete entity with id - {}", id);
			
			int extDelCount = extensionService.deleteExtensionValues(id);
			
			logger.debug("Deleted {} extension field values of entity {}", extDelCount, id);
			
			boolean res = repository.deleteById(id);
			
			logger.trace("Deletion of entity with id '{}' resulted in - {}", id, res);
			
			transaction.commit();
			
			return res;
		}catch(Exception ex)
		{
			logger.error("An error occurred while deleting entity with id - " + id, ex);
			throw new IllegalStateException("An error occurred while deleting entity with id - " + id, ex);
		}
	}
}
