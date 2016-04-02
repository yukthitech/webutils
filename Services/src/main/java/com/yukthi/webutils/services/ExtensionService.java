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

import java.util.ArrayList;
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
import com.yukthi.webutils.ExtensionValueDetails;
import com.yukthi.webutils.ServiceException;
import com.yukthi.webutils.annotations.ExtendableEntity;
import com.yukthi.webutils.annotations.ExtensionOwner;
import com.yukthi.webutils.annotations.LovMethod;
import com.yukthi.webutils.common.IExtendableModel;
import com.yukthi.webutils.common.extensions.LovOption;
import com.yukthi.webutils.controllers.ExtensionUtil;
import com.yukthi.webutils.extensions.ExtensionOwnerDetails;
import com.yukthi.webutils.extensions.ExtensionPointDetails;
import com.yukthi.webutils.repository.ExtensionEntity;
import com.yukthi.webutils.repository.ExtensionFieldEntity;
import com.yukthi.webutils.repository.ExtensionFieldValueEntity;
import com.yukthi.webutils.repository.IExtensionFieldRepository;
import com.yukthi.webutils.repository.IExtensionFieldValueRepository;
import com.yukthi.webutils.repository.IExtensionRepository;
import com.yukthi.webutils.security.ISecurityService;
import com.yukthi.webutils.utils.WebUtils;

/**
 * Service related to extensions, fields and values
 * @author akiran
 */
@Service
public class ExtensionService
{
	private static Logger logger = LogManager.getLogger(ExtensionService.class);
	
	private static final String DEF_OWNER_POINT = Object.class.getSimpleName();
	private static final long DEF_OWNER_ID = 0L;
	
	/**
	 * Used to fetch repository instances
	 */
	@Autowired
	private RepositoryFactory repositoryFactory;
	
	@Autowired
	private ClassScannerService classScannerService;

	@Autowired
	private ExtensionUtil extensionUtil;
	
	@Autowired
	private CurrentUserService userService;
	
	@Autowired
	private ISecurityService securityService;

	private Map<String, ExtensionPointDetails> nameToExtension = new HashMap<>();
	private Map<Class<?>, ExtensionPointDetails> typeToExtension = new HashMap<>();
	
	private Map<String, ExtensionOwnerDetails> nameToExtensionOwner = new HashMap<>();
	private Map<Class<?>, ExtensionOwnerDetails> typeToExtensionOwner = new HashMap<>();
	
	private IExtensionRepository extensionRepository;
	
	private IExtensionFieldRepository extensionFieldRepository;
	
	private IExtensionFieldValueRepository extensionFieldValueRepository;
	
	/**
	 * Fetches repositories from autowired repository factory.
	 */
	@PostConstruct
	private void init()
	{
		this.extensionRepository = repositoryFactory.getRepository(IExtensionRepository.class);
		this.extensionFieldRepository = repositoryFactory.getRepository(IExtensionFieldRepository.class);
		this.extensionFieldValueRepository = repositoryFactory.getRepository(IExtensionFieldValueRepository.class);
		
		//load extension points
		Set<Class<?>> extendableTypes = classScannerService.getClassesWithAnnotation(ExtendableEntity.class);
		ExtendableEntity extendableEntity = null;
		ExtensionPointDetails extensionPointDetails = null;
		
		for(Class<?> type : extendableTypes)
		{
			extendableEntity = type.getAnnotation(ExtendableEntity.class);
			extensionPointDetails = new ExtensionPointDetails(extendableEntity.name(), type);
			
			nameToExtension.put(extendableEntity.name(), extensionPointDetails);
			typeToExtension.put(type, extensionPointDetails);
		}

		//load extension owners
		Set<Class<?>> ownerTypes = classScannerService.getClassesWithAnnotation(ExtensionOwner.class);
		ExtensionOwner extensionOwner = null;
		ExtensionOwnerDetails extensionOwnerDetails = null;
		
		for(Class<?> type : ownerTypes)
		{
			extensionOwner = type.getAnnotation(ExtensionOwner.class);
			extensionOwnerDetails = new ExtensionOwnerDetails(extensionOwner.name(), type);
			
			nameToExtensionOwner.put(extensionOwner.name(), extensionOwnerDetails);
			typeToExtensionOwner.put(type, extensionOwnerDetails);
		}
	}
	
	/**
	 * Fetches extensions as LOV list. This method returns extensions which are current user
	 * is authorized for.
	 * @return Extensions as lov list
	 */
	@LovMethod(name = "extensionLov")
	public List<LovOption> getExtensionList()
	{
		List<LovOption> filteredExtensions = new ArrayList<>(nameToExtension.size());
		
		//loop through extensions
		for(ExtensionPointDetails extPoint : this.nameToExtension.values())
		{
			//if current user has access to current extension
			if(securityService.isExtensionAuthorized(extPoint))
			{
				filteredExtensions.add(new LovOption(extPoint.getName(), extPoint.getName()));
			}
		}
		
		return filteredExtensions;
	}
	
	/**
	 * Fetches extension point with specified name.
	 * @param name Name of the extension point
	 * @return
	 */
	public ExtensionPointDetails getExtensionPoint(String name)
	{
		return nameToExtension.get(name);
	}
	
	/**
	 * Fetches the extension owner details with specified name.
	 * @param name Name of the extension owner
	 * @return Extension owner details
	 */
	public ExtensionOwnerDetails getExtensionOwner(String name)
	{
		return this.nameToExtensionOwner.get(name);
	}
	
	/**
	 * Gets extension owner details with specified type.
	 * @param ownerType
	 * @return
	 */
	public ExtensionOwnerDetails getExtensionOwner(Class<?> ownerType)
	{
		return this.typeToExtensionOwner.get(ownerType);
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
		
		ExtensionPointDetails extensionPointDetails = typeToExtension.get(entityType);
		
		if(extensionPointDetails == null)
		{
			throw new InvalidArgumentException("Specified entity type is not an extension entity - {}", entityType.getName());
		}
		
		String ownerName = null;
		
		//if owner entity type is not specified use Object class
		if(ownerEntityType == null)
		{
			ownerName = DEF_OWNER_POINT;
		}
		//if owner type is specified
		else
		{
			ExtensionOwnerDetails extensionOwnerDetails = typeToExtensionOwner.get(ownerEntityType);
			
			//if owner entity does not have required annotation
			if(extensionOwnerDetails == null)
			{
				throw new InvalidArgumentException("Invalid extension owner specified - {}. Owner entity should be marked with @{}", ownerEntityType.getName(), ExtensionOwner.class.getName());
			}
			
			ownerName = extensionOwnerDetails.getName();
		}
		
		//try to find extension with specified details
		return extensionRepository.findEntity(extensionPointDetails.getName(), ownerName, ownerId);
	}
	
	/**
	 * Creates new extension with specified details.
	 * @param extension Extension to be saved
	 */
	public void saveExtensionEntity(ExtensionEntity extension)
	{
		ExtensionPointDetails extensionPointDetails = nameToExtension.get(extension.getTargetPointName());
		
		if(extensionPointDetails == null)
		{
			throw new InvalidArgumentException("Specified entity type is not an extension entity - {}", extension.getTargetPointName());
		}
		
		//if owner entity type is not specified use Object class
		if(extension.getOwnerPointName() == null)
		{
			extension.setOwnerPointName(DEF_OWNER_POINT);
			extension.setOwnerId(DEF_OWNER_ID);
		}
		//if owner type is specified
		else
		{
			ExtensionOwnerDetails extensionOwnerDetails = nameToExtensionOwner.get(extension.getOwnerPointName());
			
			//if owner entity does not have required annotation
			if(extensionOwnerDetails == null)
			{
				throw new InvalidArgumentException("Invalid extension owner specified - {}.", extension.getOwnerPointName());
			}
		}

		userService.populateTrackingFieldForCreate(extension);
		
		//set default version
		extension.setVersion(1);
		
		if(!extensionRepository.save(extension))
		{
			throw new ServiceException("Failed to save extension - {}", extension);
		}
	}
	
	/**
	 * Fetches extension fields for specified extension id.
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
	 * Fetches the extension field for specified extension with specified id.
	 * @param extensionName Extension under which field is defined
	 * @param fieldId Id of the field that needs to be fetched
	 * @return Extension field entity
	 */
	public ExtensionFieldEntity fetchExtensionField(String extensionName, long fieldId)
	{
		logger.trace("Fetching extension fields for extension - {}", extensionName);
		
		return extensionFieldRepository.findExtensionField(extensionName, fieldId);
	}

	/**
	 * Saves specified extension field for specified extension id.
	 * @param extensionId Extension id under which field should be save
	 * @param extensionFieldEntity Extension field to be saved
	 */
	@CacheEvict(value = "extensionFields", key = "#root.args[0]")
	public void saveExtensionField(long extensionId, ExtensionFieldEntity extensionFieldEntity)
	{
		logger.trace("Saving new extension field for extension - {}", extensionId);
		
		extensionFieldEntity.setExtension(new ExtensionEntity(extensionId));
		
		userService.populateTrackingFieldForCreate(extensionFieldEntity);
		
		//set default version
		extensionFieldEntity.setVersion(1);
		
		if(!extensionFieldRepository.save(extensionFieldEntity))
		{
			throw new ServiceException("Failed to save extension field entity.");
		}
	}
	
	/**
	 * updates specified extension field under specified extension.
	 * @param extensionFieldEntity
	 */
	public void updateExtensionField(ExtensionFieldEntity extensionFieldEntity)
	{
		logger.trace("Updating extension field - {}", extensionFieldEntity.getId());
		
		WebUtils.validateEntityForUpdate(extensionFieldEntity);
		
		userService.populateTrackingFieldForUpdate(extensionFieldEntity);
		
		if(!extensionFieldRepository.update(extensionFieldEntity))
		{
			throw new ServiceException("Failed to update extension field details");
		}
	}
	
	/**
	 * Fetches extension id for specified field id.
	 * @param extensionFieldId Field id for which extension id needs to be fetched
	 * @return Extension id
	 */
	public long getExtensionIdForField(long extensionFieldId)
	{
		return extensionFieldRepository.fetchExtensionIdById(extensionFieldId);
	}
	
	/**
	 * Deletes extension values for specified entity.
	 * @param entityId Entity id for which fields needs to be deleted
	 * @return Number of deleted records
	 */
	public int deleteExtensionValues(long entityId)
	{
		logger.trace("Deleting extension values for entity - {}", entityId);
		
		return extensionFieldValueRepository.deleteByEntityId(entityId);
	}
	
	/**
	 * Deletes extension field.
	 * @param extensionFieldId Extension field to be deleted
	 */
	public void deleteExtensionField(long extensionFieldId)
	{
		logger.trace("Deleting extension field with id - {}", extensionFieldId);
		
		if(!extensionFieldRepository.deleteById(extensionFieldId))
		{
			throw new ServiceException("Failed to delete extension field with id '{}'", extensionFieldId);
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
	 * Fetches extension field values for specified extension id and entity id.
	 * @param extensionId Extension id
	 * @param entityId Entity id for which extended values needs to be fetched
	 * @return Extended field values
	 */
	public List<ExtensionFieldValueEntity> getExtensionValues(long extensionId, long entityId)
	{
		return extensionFieldValueRepository.findExtensionValues(extensionId, entityId);
	}

	/**
	 * Saves the specified value entity.
	 * @param valueEntity Entity to be saved
	 */
	public void saveExtensionValue(ExtensionFieldValueEntity valueEntity)
	{
		userService.populateTrackingFieldForCreate(valueEntity);
		
		//set default version
		valueEntity.setVersion(1);
		
		if(!extensionFieldValueRepository.save(valueEntity))
		{
			throw new InvalidStateException("Failed to save extension field value - {}", valueEntity);
		}
	}
	
	/**
	 * Updates the specified value entity.
	 * @param valueEntity Entity to be updated
	 */
	public void updateExtensionValue(ExtensionFieldValueEntity valueEntity)
	{
		//validate entity for update
		WebUtils.validateEntityForUpdate(valueEntity);
		
		userService.populateTrackingFieldForUpdate(valueEntity);
		
		if(!extensionFieldValueRepository.update(valueEntity))
		{
			throw new InvalidStateException("Failed to update extension field value - {}", valueEntity);
		}
	}

	/**
	 * Saves extended field values of the specified model.
	 * @param extendableModel Model for which extended values needs to be saved
	 */
	public void saveExtendedFields(long entityId, IExtendableModel extendableModel)
	{
		//fetch extended values
		Map<String, String> newExtendedValues = extendableModel.getExtendedFields();
		
		//if no values are present
		if(newExtendedValues == null || newExtendedValues.isEmpty())
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
			List<ExtensionFieldEntity> extensionFields = getExtensionFields(extensionEntity.getId());
			List<ExtensionValueDetails> existingFieldValues = extensionFieldValueRepository.findExtensionValueDetails(extensionEntity.getId(), entityId); 
			
			//convert existing values into map
			Map<String, ExtensionValueDetails> existingValueMap = CommonUtils.buildMap(existingFieldValues, "extensionFieldName", null);
			Map<String, ExtensionFieldEntity> nameToField = CommonUtils.buildMap(extensionFields, "name", null);
			
			if(existingValueMap == null)
			{
				existingValueMap = Collections.emptyMap();
			}
			
			ExtensionValueDetails valueDetails = null;
			ExtensionFieldValueEntity valueEntity = null;
			ExtensionFieldEntity extensionField = null;
			
			//persist the field values
			for(String fieldName : newExtendedValues.keySet())
			{
				valueDetails = existingValueMap.get(fieldName);
				
				if(valueDetails != null)
				{
					valueEntity = new ExtensionFieldValueEntity(valueDetails.getId(), new ExtensionFieldEntity(valueDetails.getExtensionFieldId()), 
								entityId, newExtendedValues.get(fieldName));
					valueEntity.setVersion(valueDetails.getVersion());
					
					userService.populateTrackingFieldForUpdate(valueEntity);
					
					updateExtensionValue(valueEntity);
				}
				else
				{
					extensionField = nameToField.get(fieldName);
					
					if(extensionField == null)
					{
						throw new InvalidArgumentException("Invalid extension field name specified - {}", fieldName);
					}
					
					valueEntity = new ExtensionFieldValueEntity(0, new ExtensionFieldEntity(extensionField.getId()), entityId, newExtendedValues.get(fieldName));
					userService.populateTrackingFieldForUpdate(valueEntity);
					
					saveExtensionValue(valueEntity);
				}
			}
			
			transaction.commit();
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An exception occurred while updating extension fields for entity - {}", entityId); 
		}
	}
	
	/**
	 * Fetches extended field values for specified model and sets them on the.
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
		List<ExtensionValueDetails> existingFieldValues = extensionFieldValueRepository.findExtensionValueDetails(extensionEntity.getId(), id);
		
		//return if no values found in db for extended fields
		if(CollectionUtils.isEmpty(existingFieldValues))
		{
			return;
		}
		
		//convert existing values into map
		Map<String, String> existingValueMap = CommonUtils.buildMap(existingFieldValues, "extensionFieldName", "value");
		extendableModel.setExtendedFields(existingValueMap);
	}
}
