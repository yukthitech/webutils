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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.yukthi.persistence.ITransaction;
import com.yukthi.persistence.repository.RepositoryFactory;
import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.exceptions.InvalidArgumentException;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.ServiceException;
import com.yukthi.webutils.annotations.ExtendableEntity;
import com.yukthi.webutils.annotations.ExtensionOwner;
import com.yukthi.webutils.common.IExtendableModel;
import com.yukthi.webutils.controllers.ExtensionUtil;
import com.yukthi.webutils.extensions.ExtensionPointDetails;
import com.yukthi.webutils.repository.ExtensionEntity;
import com.yukthi.webutils.repository.ExtensionFieldEntity;
import com.yukthi.webutils.repository.ExtensionFieldValueEntity;
import com.yukthi.webutils.repository.IExtensionFieldRepository;
import com.yukthi.webutils.repository.IExtensionFieldValueRepository;
import com.yukthi.webutils.repository.IExtensionRepository;

/**
 * Service related to extensions, fields and values
 * @author akiran
 */
@Service
public class ExtensionService
{
	private static Logger logger = LogManager.getLogger(ExtensionService.class);
	
	/**
	 * Used to fetch repository instances
	 */
	@Autowired
	private RepositoryFactory repositoryFactory;
	
	@Autowired
	private ClassScannerService classScannerService;

	@Autowired
	private ExtensionUtil extensionUtil;

	private Map<String, ExtensionPointDetails> nameToExtension = new HashMap<>();
	
	private IExtensionRepository extensionRepository;
	
	private IExtensionFieldRepository extensionFieldRepository;
	
	private IExtensionFieldValueRepository extensionFieldValueRepository;
	
	/**
	 * Fetches repositories from autowired repository factory
	 */
	@PostConstruct
	private void init()
	{
		this.extensionRepository = repositoryFactory.getRepository(IExtensionRepository.class);
		this.extensionFieldRepository = repositoryFactory.getRepository(IExtensionFieldRepository.class);
		this.extensionFieldValueRepository = repositoryFactory.getRepository(IExtensionFieldValueRepository.class);
		
		Set<Class<?>> extendableTypes = classScannerService.getClassesWithAnnotation(ExtendableEntity.class);
		ExtendableEntity extendableEntity = null;
		
		for(Class<?> type : extendableTypes)
		{
			extendableEntity = type.getAnnotation(ExtendableEntity.class);
			nameToExtension.put(extendableEntity.name(), new ExtensionPointDetails(extendableEntity.name(), type));
		}
	}
	
	/**
	 * Fetches extension point with specified name
	 * @param name Name of the extension point
	 * @return
	 */
	public ExtensionPointDetails getExtensionPoint(String name)
	{
		return nameToExtension.get(name);
	}
	
	/**
	 * Fetches extension with specified criteria. 
	 * @param entityType Entity type for which extension is being fetched
	 * @param ownerEntityType Owner entity type, this is optional
	 * @param ownerId Owner entity id, this is optional
	 * @return Matching extension entity
	 */
	@Cacheable("default")
	public ExtensionEntity getExtensionEntity(Class<?> entityType, Class<?> ownerEntityType, long ownerId)
	{
		logger.trace("Fetching extension entity - [Entity: {}, Owner type: {}, Owner Id: {}]", entityType.getName(), 
				(ownerEntityType != null)? ownerEntityType.getName() : null, ownerId);
		
		//if owner entity type is not specified use Object class
		if(ownerEntityType == null)
		{
			ownerEntityType = Object.class;
		}
		//if owner type is specified
		else
		{
			//if owner entity does not have required annotation
			if(ownerEntityType.getAnnotation(ExtensionOwner.class) == null)
			{
				throw new InvalidArgumentException("Invalid extension owner specified - {}. Owner entity should be marked with @{}", ownerEntityType.getName(), ExtensionOwner.class.getName());
			}
		}
		
		//try to find extension with specified details
		return extensionRepository.findEntity(entityType.getName(), ownerEntityType.getName(), ownerId);
	}
	
	/**
	 * Creates new extension with specified details
	 * @param extension Extension to be saved
	 */
	public void saveExtensionEntity(ExtensionEntity extension)
	{
		//if owner entity type is not specified use Object class
		if(extension.getOwnerEntityType() == null)
		{
			extension.setOwnerEntityType(Object.class.getName());
			extension.setOwnerId(0L);
		}
		//if owner type is specified
		else
		{
			Class<?> ownerEntityType = null;
			
			try
			{
				ownerEntityType = Class.forName(extension.getOwnerEntityType());
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "Invalid owner entity type specified", extension.getOwnerEntityType());
			}
			
			//if owner entity does not have required annotation
			if(ownerEntityType.getAnnotation(ExtensionOwner.class) == null)
			{
				throw new InvalidArgumentException("Invalid extension owner specified - {}. Owner entity should be marked with @{}", ownerEntityType.getName(), ExtensionOwner.class);
			}

			//if owner entity does not have @Table annotation
			if(ownerEntityType.getAnnotation(ExtensionOwner.class) == null)
			{
				throw new InvalidArgumentException("Non-entity is specified as extension owner - {}", ownerEntityType.getName());
			}
		}

		if(!extensionRepository.save(extension))
		{
			throw new ServiceException("Failed to save extension - {}", extension);
		}
	}
	
	/**
	 * Fetches extension fields for specified extension id
	 * @param extensionId Specified extension id
	 * @return List of extension fields
	 */
	@Cacheable(value = "extensionFields", key = "#root.args[0]")
	public List<ExtensionFieldEntity> getExtensionFields(long extensionId)
	{
		logger.trace("Fetching extension fields for extension - {}", extensionId);
		
		return extensionFieldRepository.findExtensionFields(extensionId);
	}
	
	/**
	 * Saves specified extension field for specified extension id
	 * @param extensionId Extension id under which field should be save
	 * @param extensionFieldEntity Extension field to be saved
	 */
	@CacheEvict(value = "extensionFields", key = "#root.args[0]")
	public void saveExtensionField(long extensionId, ExtensionFieldEntity extensionFieldEntity)
	{
		logger.trace("Saving new extension field for extension - {}", extensionId);
		
		extensionFieldEntity.setExtension(new ExtensionEntity(extensionId));
		
		if(!extensionFieldRepository.save(extensionFieldEntity))
		{
			throw new ServiceException("Failed to save extension field entity.");
		}
	}
	
	/**
	 * updates specified extension field under specified extension
	 * @param extensionId
	 * @param extensionFieldEntity
	 */
	@CacheEvict(value = "extensionFields", key = "#root.args[0]")
	public void updateExtensionField(long extensionId, ExtensionFieldEntity extensionFieldEntity)
	{
		logger.trace("Updating extension field for extension - {}", extensionId);
		
		extensionFieldEntity.setExtension(new ExtensionEntity(extensionId));

		if(!extensionFieldRepository.update(extensionFieldEntity))
		{
			throw new ServiceException("Failed to update extension field details");
		}
	}
	
	/**
	 * Fetches extension id for specified field id
	 * @param extensionFieldId Field id for which extension id needs to be fetched
	 * @return Extension id
	 */
	public long getExtensionIdForField(long extensionFieldId)
	{
		return extensionFieldRepository.fetchExtensionIdById(extensionFieldId);
	}
	
	/**
	 * Deletes extension values for specified entity
	 * @param entityId Entity id for which fields needs to be deleted
	 * @return Number of deleted records
	 */
	public int deleteExtensionValues(long entityId)
	{
		logger.trace("Deleting extension values for entity - {}", entityId);
		
		return extensionFieldValueRepository.deleteByEntityId(entityId);
	}
	
	/**
	 * Deletes extension field
	 * @param extensionId Extension id under which field should be deleted
	 * @param extensionFieldId Extension field to be deleted
	 */
	@CacheEvict(value = "extensionFields", key = "#root.args[0]")
	public void deleteExtensionField(long extensionId, long extensionFieldId)
	{
		logger.trace("Deleting extension field for extension - {}", extensionId);
		
		if(!extensionFieldRepository.deleteById(extensionFieldId))
		{
			throw new ServiceException("Failed to delete extension field with id- {}", extensionFieldId);
		}
	}
	
	/**
	 * Deletes all extended fields. Expected to be used by test cases for cleanup
	 */
	@CacheEvict(value = "extensionFields")
	public void deleteAllExtensionFields()
	{
		logger.trace("Deleting all extensions");
		extensionFieldRepository.deleteAll();
	}
	
	/**
	 * Fetches extension field values for specified extension id and entity id
	 * @param extensionId Extension id
	 * @param entityId Entity id for which extended values needs to be fetced
	 * @return Extended field values
	 */
	public List<ExtensionFieldValueEntity> getExtensionValues(long extensionId, long entityId)
	{
		return extensionFieldValueRepository.findExtensionValues(extensionId, entityId);
	}

	/**
	 * Saves the specified value entity
	 * @param valueEntity Entity to be saved
	 */
	public void saveExtensionValue(ExtensionFieldValueEntity valueEntity)
	{
		if(!extensionFieldValueRepository.save(valueEntity))
		{
			throw new InvalidStateException("Failed to save extension field value - {}", valueEntity);
		}
	}
	
	/**
	 * Updates the specified value entity
	 * @param valueEntity Entity to be updated
	 */
	public void updateExtensionValue(ExtensionFieldValueEntity valueEntity)
	{
		if(!extensionFieldValueRepository.update(valueEntity))
		{
			throw new InvalidStateException("Failed to update extension field value - {}", valueEntity);
		}
	}

	/**
	 * Saves extended field values of the specified model
	 * @param extendableModel Model for which extended values needs to be saved
	 */
	public void saveExtendedFields(long entityId, IExtendableModel extendableModel)
	{
		//fetch extended values
		Map<Long, String> extendedValues = extendableModel.getExtendedFields();
		
		//if no values are present
		if(extendedValues == null || extendedValues.isEmpty())
		{
			return;
		}
		
		//fetch extension entity
		ExtensionEntity extensionEntity = extensionUtil.getExtensionEntity(extendableModel);
		
		if(extensionEntity == null)
		{
			return;
		}
		
		try(ITransaction transaction = extensionFieldRepository.currentTransaction())
		{
			List<ExtensionFieldValueEntity> existingFieldValues = getExtensionValues(extensionEntity.getId(), entityId);
			
			//convert existing values into map
			Map<Long, ExtensionFieldValueEntity> existingValueMap = CommonUtils.buildMap(existingFieldValues, "extensionField.id", null);
			
			if(existingValueMap == null)
			{
				existingValueMap = Collections.emptyMap();
			}
			
			ExtensionFieldValueEntity valueEntity = null;
			
			//persist the field values
			for(Long fieldId : extendedValues.keySet())
			{
				valueEntity = existingValueMap.get(fieldId);
				
				if(valueEntity != null)
				{
					updateExtensionValue(new ExtensionFieldValueEntity(valueEntity.getId(), new ExtensionFieldEntity(fieldId), entityId, extendedValues.get(fieldId)));
				}
				else
				{
					saveExtensionValue(new ExtensionFieldValueEntity(0, new ExtensionFieldEntity(fieldId), entityId, extendedValues.get(fieldId)));
				}
			}
			
			transaction.commit();
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An exception occurred while updating extension fields for entity - {}", entityId); 
		}
	}
	
	/**
	 * Fetches extended field values for specified model and sets them on the
	 * specified model
	 * @param extendableModel Model for which extended field values needs to be fetched
	 */
	public void fetchExtendedValues(IExtendableModel extendableModel)
	{
		//fetch extension entity
		ExtensionEntity extensionEntity = extensionUtil.getExtensionEntity(extendableModel);
		
		if(extensionEntity == null)
		{
			return;
		}
		
		long id = extendableModel.getId();
		List<ExtensionFieldValueEntity> existingFieldValues = getExtensionValues(extensionEntity.getId(), id);
		
		//return if no values found in db for extended fields
		if(CollectionUtils.isEmpty(existingFieldValues))
		{
			return;
		}
		
		//convert existing values into map
		Map<Long, String> existingValueMap = CommonUtils.buildMap(existingFieldValues, "extensionField.id", "value");
		extendableModel.setExtendedFields(existingValueMap);
	}
}
