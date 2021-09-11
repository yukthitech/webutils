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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.persistence.ITransaction;
import com.yukthitech.persistence.PersistenceException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.IWebUtilsInternalConstants;
import com.yukthitech.webutils.cache.WebutilsCacheEvict;
import com.yukthitech.webutils.cache.WebutilsCacheable;
import com.yukthitech.webutils.common.IExtendableModel;
import com.yukthitech.webutils.repository.ITenantBasedRepository;
import com.yukthitech.webutils.repository.ITenantSpaceBased;
import com.yukthitech.webutils.repository.IWebutilsRepository;
import com.yukthitech.webutils.repository.WebutilsBaseEntity;
import com.yukthitech.webutils.repository.WebutilsExtendableEntity;
import com.yukthitech.webutils.security.ISecurityService;
import com.yukthitech.webutils.utils.WebUtils;

/**
 * Abstracts basic functionality that is required for all entities.
 * @author akiran
 * @param <E> Entity type
 * @param <R> Repository type
 */
public abstract class BaseCrudService<E extends WebutilsBaseEntity, R extends IWebutilsRepository<E>>
{
	private static Logger logger = LogManager.getLogger(BaseCrudService.class);

	@Autowired
	private WebutilsRepositoryFactory webutilsRepositoryFactory;
	
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
	 * Used to fetch repository from autowired factory. 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostConstruct
	private void init()
	{
		Map<TypeVariable<?>, Type> genericMap = TypeUtils.getTypeArguments(getClass(), BaseCrudService.class);
		TypeVariable<?> typeVars[] = BaseCrudService.class.getTypeParameters();
		
		entityType = (Class) genericMap.get(typeVars[0]);
		repositoryType = (Class) genericMap.get(typeVars[1]);

		repository = webutilsRepositoryFactory.getRepository(repositoryType);
	}
	
	/**
	 * Converts the specified model into entity and saves the converted entity.
	 * @param model Model to be converted and saved
	 * @return Converted and saved entity
	 */
	@WebutilsCacheEvict(groups = IWebUtilsInternalConstants.CACHE_GROUP_GROUPED)
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
	@WebutilsCacheEvict(groups = IWebUtilsInternalConstants.CACHE_GROUP_GROUPED)
	public void save(E entity, Object model)
	{
		try(ITransaction transaction = repository.newOrExistingTransaction())
		{
			logger.trace("Trying to save entity - {}", entity);

			//populate tracked fields
			userService.populateTrackingFieldForCreate(entity);
			
			//set the default version
			entity.setVersion(1);
			
			if(entity instanceof ITenantSpaceBased)
			{
				((ITenantSpaceBased) entity).setSpaceIdentity(getUserSpace(entity, model));
			}

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
			if((ex instanceof UndeclaredThrowableException) && (ex.getCause() instanceof Exception))
			{
				ex = (Exception) ex.getCause();
			}
			
			if((ex instanceof InvocationTargetException) && (ex.getCause() instanceof Exception))
			{
				ex = (Exception) ex.getCause();
			}
			
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
	@WebutilsCacheEvict(groups = {IWebUtilsInternalConstants.CACHE_GROUP_GROUPED, "#p0.id"})
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@WebutilsCacheEvict(groups = {IWebUtilsInternalConstants.CACHE_GROUP_GROUPED, "#p0.id"})
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

			boolean res = false;
			
			if(repository instanceof ITenantBasedRepository)
			{
				res = ((ITenantBasedRepository) repository).updateByUserSpace(entity, getUserSpace(entity, model));
			}
			else
			{
				res = repository.update(entity);
			}
			
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
			if((ex instanceof UndeclaredThrowableException) && (ex.getCause() instanceof Exception))
			{
				ex = (Exception) ex.getCause();
			}
			
			if((ex instanceof InvocationTargetException) && (ex.getCause() instanceof Exception))
			{
				ex = (Exception) ex.getCause();
			}

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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@WebutilsCacheable(groups = "#p0")
	public E fetch(long id)
	{
		E entity = null;
		
		if(repository instanceof ITenantBasedRepository)
		{
			entity = (E) ((ITenantBasedRepository) repository).findByIdAndUserSpace(id, securityService.getUserSpaceIdentity());
		}
		else
		{
			entity = repository.findById(id);
		}
		
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
	@WebutilsCacheable(groups = "#p0")
	public <M> M fetchFullModel(long id, Class<M> modelType)
	{
		E entity = fetch(id);
		
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
	@WebutilsCacheEvict(groups = IWebUtilsInternalConstants.CACHE_GROUP_GROUPED)
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
	@SuppressWarnings("rawtypes")
	@WebutilsCacheEvict(groups = {IWebUtilsInternalConstants.CACHE_GROUP_GROUPED, "#p0"})
	public boolean deleteById(long id)
	{
		try(ITransaction transaction = repository.newOrExistingTransaction())
		{
			boolean res = false;
			
			if(repository instanceof ITenantBasedRepository)
			{
				res = ((ITenantBasedRepository) repository).deleteByIdAndUserSpace(id, securityService.getUserSpaceIdentity());
			}
			else
			{
				res = repository.deleteById(id);
			}
			
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
