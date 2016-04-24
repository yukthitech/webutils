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

import com.yukthi.persistence.ITransaction;
import com.yukthi.persistence.PersistenceException;
import com.yukthi.persistence.repository.RepositoryFactory;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.common.IExtendableModel;
import com.yukthi.webutils.repository.IWebutilsRepository;
import com.yukthi.webutils.repository.WebutilsEntity;
import com.yukthi.webutils.repository.WebutilsExtendableEntity;
import com.yukthi.webutils.security.ISecurityService;
import com.yukthi.webutils.utils.WebUtils;

/**
 * Abstracts basic functionality that is required for all entities.
 * @author akiran
 * @param <E> Entity type
 * @param <R> Repository type
 */
public abstract class BaseCrudService<E extends WebutilsEntity, R extends IWebutilsRepository<E>>
{
	private static Logger logger = LogManager.getLogger(BaseCrudService.class);
	
	/**
	 * Autowired repository factory, used to fetch repository.
	 */
	@Autowired
	protected RepositoryFactory repositoryFactory;

	/**
	 * Extension service to update/read extension fields.
	 */
	@Autowired
	protected ExtensionService extensionService;

	/**
	 * Used to populate tracked fields.
	 */
	@Autowired
	protected CurrentUserService userService;
	
	/**
	 * Service to store files specified as part of model.
	 */
	@Autowired
	protected FileService fileService;
	
	/**
	 * Security service used to fetch user space identity.
	 */
	@Autowired
	protected ISecurityService securityService;
	
	/**
	 * Service to maintain images of the model.
	 */
	@Autowired
	protected ImageService imageService;
	
	/**
	 * Repository type.
	 */
	protected Class<R> repositoryType;
	
	/**
	 * Entity type.
	 */
	protected Class<E> entityType;
	
	/**
	 * CRUD repository obtained from factory in init().
	 */
	protected R repository;
	
	/**
	 * Instantiates a new base crud service.
	 *
	 * @param entityType Entity type for which this service is being created.
	 * @param repositoryType the repository type
	 */
	public BaseCrudService(Class<E> entityType, Class<R> repositoryType)
	{
		this.entityType = entityType;
		this.repositoryType = repositoryType;
	}
	
	/**
	 * Used to fetch repository from autowired factory. 
	 */
	@PostConstruct
	private void init()
	{
		repository = repositoryFactory.getRepository(repositoryType);
	}
	
	/**
	 * Converts the specified model into entity and saves the converted entity.
	 * @param model Model to be converted and saved
	 * @return Converted and saved entity
	 */
	public E save(Object model)
	{
		//convert to entity
		E entity = WebUtils.convertBean(model, entityType);
		
		//save entity
		save(entity, model);
		
		return entity;
	}
	
	/**
	 * Saves specified entity "entity" and saves extension fields from "extendedFieldsModel".
	 * @param entity Entity field to save
	 * @param model Model with extension fields and file informations to save. Optional, can be null
	 */
	public void save(E entity, Object model)
	{
		try(ITransaction transaction = repository.newOrExistingTransaction())
		{
			logger.trace("Trying to save entity - {}", entity);

			//populate tracked fields
			userService.populateTrackingFieldForCreate(entity);
			
			//set the default version
			entity.setVersion(1);
			entity.setSpaceIdentity(getUserSpace(entity, model));

			//copy extended fields to entity
			if(model != null && (model instanceof IExtendableModel))
			{
				logger.trace("Mapping extended fields entity - {}", entity);
				extensionService.mapExtendedFieldsToEntity( (IExtendableModel) model, (WebutilsExtendableEntity) entity );
			}

			boolean res = repository.save(entity);
			
			if(!res)
			{
				logger.error("Failed to save entity - {}", entity);
				throw new InvalidStateException("Failed to save entity");
			}
			
			//save files specified on model
			if(model != null)
			{
				fileService.saveFilesFromModel(model, entityType, entity.getId());
				imageService.saveImagesFromModel(model, entityType, entity.getId());
			}
			
			transaction.commit();
		}catch(Exception ex)
		{
			logger.error("An error occurred while saving entity - " + entity, ex);
			
			if(ex instanceof PersistenceException)
			{
				throw (PersistenceException) ex;
			}
			
			throw new IllegalStateException("An error occurred while saving entity - " + entity, ex);
		}
	}
	
	/**
	 * Converts the specified model into entity and updates the converted entity.
	 * @param model Model to be converted and updated
	 * @return Converted entity
	 */
	public E update(Object model)
	{
		//convert to entity
		E entity = WebUtils.convertBean(model, entityType);
		
		//update entity
		update(entity, model);
		
		return entity;
	}
	
	/**
	 * Updates specified entity "entity" and saves extension fields from "extendedFieldsModel".
	 * @param entity Entity field to update
	 * @param model Model with extension field values and files to save. Optional, can be null
	 */
	public void update(E entity, Object model)
	{
		WebUtils.validateEntityForUpdate(entity);
		
		try(ITransaction transaction = repository.newOrExistingTransaction())
		{
			logger.trace("Trying to update entity - {}", entity);
			
			userService.populateTrackingFieldForUpdate(entity);

			//copy extended fields to entity
			if(model != null && (model instanceof IExtendableModel))
			{
				logger.trace("Mapping extended fields entity - {}", entity);
				extensionService.mapExtendedFieldsToEntity( (IExtendableModel) model, (WebutilsExtendableEntity) entity );
			}

			boolean res = repository.updateByUserSpace(entity, getUserSpace(entity, model));
			
			if(!res)
			{
				logger.error("Failed to update entity - {}", entity);
				throw new InvalidStateException("Failed to update entity");
			}
			
			//save files specified on model
			if(model != null)
			{
				fileService.saveFilesFromModel(model, entityType, entity.getId());
				imageService.saveImagesFromModel(model, entityType, entity.getId());
			}

			transaction.commit();
		}catch(Exception ex)
		{
			logger.error("An error occurred while updating entity - " + entity, ex);

			if(ex instanceof PersistenceException)
			{
				throw (PersistenceException) ex;
			}
			
			throw new IllegalStateException("An error occurred while updating entity - " + entity, ex);
		}
	}
	
	/**
	 * Fetches entity with specified id.
	 * @param id Entity id
	 * @return Matching entity
	 */
	public E fetch(long id)
	{
		E entity = repository.findByIdAndUserSpace(id, securityService.getUserSpaceIdentity());
		
		logger.trace("Entity fetch with id '{}' resulted in  - {}", id, entity);
		return entity;
	}
	
	/**
	 * To be used for fetching model with full information like - extensions, files, images etc.
	 * @param id Entity id to be fetched
	 * @param modelType Corresponding entity's model type which can hold extension fields
	 * @param <M> Model type
	 * @return Converted model with extension fields
	 */
	public <M> M fetchFullModel(long id, Class<M> modelType)
	{
		E entity = repository.findByIdAndUserSpace(id, securityService.getUserSpaceIdentity());
		
		if(entity == null)
		{
			logger.trace("No entity found with id - {}", id);
			return null;
		}
		
		M model = toModel(entity, modelType);

		logger.trace("Entity fetch full mode for id '{}' resulted in  - {}", id, entity);
		return model;
	}
	
	/**
	 * Converts the specified entity into specified model-type's model and populates.
	 * the extension fields and file, image fields as required.
	 * @param entity Entity to be converted
	 * @param modelType Model type
	 * @param <M> Model type
	 * @return Converted model object
	 */
	protected <M> M toModel(E entity, Class<M> modelType)
	{
		if(entity == null)
		{
			return null;
		}
		
		M model = WebUtils.convertBean(entity, modelType);
		
		if(model instanceof IExtendableModel)
		{
			extensionService.mapExtendedFieldsToModel( repository, entity.getId(), (IExtendableModel) model); 
		}

		//fetch file information
		fileService.readFilesForModel(model, entityType, entity.getId());
		
		//fetch image information
		imageService.readImagesForModel(model, entityType, entity.getId());
		
		return model;
	}

	/**
	 * Fetches number of entities in DB.
	 * @return entity count
	 */
	public long getCount()
	{
		long count = repository.getCount();
		
		logger.trace("Got count as - {}", count);
		return count;
	}
	
	/**
	 * Deletes entity with specified id.
	 * @param id Entity id to delete
	 * @return returns true if delete was successful.
	 */
	public boolean deleteById(long id)
	{
		try(ITransaction transaction = repository.newOrExistingTransaction())
		{
			boolean res = repository.deleteByIdAndUserSpace(id, securityService.getUserSpaceIdentity());
			
			logger.trace("Deletion of entity with id '{}' resulted in - {}", id, res);
			
			//Delete files
			fileService.delete(entityType, null, id);
			
			transaction.commit();
			
			return res;
		}catch(Exception ex)
		{
			logger.error("An error occurred while deleting entity with id - " + id, ex);
			
			if(ex instanceof PersistenceException)
			{
				throw (PersistenceException) ex;
			}
			
			throw new IllegalStateException("An error occurred while deleting entity with id - " + id, ex);
		}
	}
	
	/**
	 * This method is used in save while setting user space of an entity. By default,
	 * this method returns current user's user-space. This method can be overridden to give custom
	 * way of setting user space.
	 * @param entity Entity for which space identity is required.
	 * @param model Model for which space identity is required.
	 * @return target user space.
	 */
	protected String getUserSpace(E entity, Object model)
	{
		return securityService.getUserSpaceIdentity();
	}
}
