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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.yukthi.persistence.repository.RepositoryFactory;
import com.yukthi.utils.exceptions.InvalidArgumentException;
import com.yukthi.utils.exceptions.InvalidConfigurationException;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.IWebUtilsInternalConstants;
import com.yukthi.webutils.ServiceException;
import com.yukthi.webutils.annotations.ExtendableEntity;
import com.yukthi.webutils.annotations.LovMethod;
import com.yukthi.webutils.common.IExtendableModel;
import com.yukthi.webutils.common.annotations.ExtendableModel;
import com.yukthi.webutils.common.extensions.LovOption;
import com.yukthi.webutils.controllers.IExtensionContextProvider;
import com.yukthi.webutils.extensions.ExtensionEntityDetails;
import com.yukthi.webutils.repository.ExtensionEntity;
import com.yukthi.webutils.repository.ExtensionFieldEntity;
import com.yukthi.webutils.repository.ExtensionFieldsData;
import com.yukthi.webutils.repository.IExtensionFieldRepository;
import com.yukthi.webutils.repository.IExtensionRepository;
import com.yukthi.webutils.repository.IWebutilsRepository;
import com.yukthi.webutils.repository.WebutilsExtendableEntity;
import com.yukthi.webutils.security.ISecurityService;
import com.yukthi.webutils.utils.WebUtils;

/**
 * Service related to extensions, fields and values.
 * @author akiran
 */
@Service
public class ExtensionService
{
	private static Logger logger = LogManager.getLogger(ExtensionService.class);
	
	/**
	 * Default owner type to be used, when owner type is not specified.
	 */
	private static final String DEF_OWNER_TYPE = Object.class.getSimpleName();
	
	/**
	 * Default owner id to be used when owner type is not specified.
	 */
	private static final long DEF_OWNER_ID = 0L;
	
	/**
	 * Used to fetch repository instances.
	 */
	@Autowired
	private RepositoryFactory repositoryFactory;
	
	/**
	 * Service used to scan for extendable entities.
	 */
	@Autowired
	private ClassScannerService classScannerService;

	/**
	 * Used to fetch current user details for tracking purpose.
	 */
	@Autowired
	private CurrentUserService userService;
	
	/**
	 * To filter entities for current session.
	 */
	@Autowired
	private ISecurityService securityService;

	/**
	 * Maintains name to extension mapping.
	 */
	private Map<String, ExtensionEntityDetails> nameToExtension = new HashMap<>();
	
	/**
	 * Maintains type to extension mapping.
	 */
	private Map<String, ExtensionEntityDetails> typeToExtension = new HashMap<>();
	
	/**
	 * To persist and read extensions.
	 */
	private IExtensionRepository extensionRepository;
	
	/**
	 * To persist and read extension fields.
	 */
	private IExtensionFieldRepository extensionFieldRepository;
	
	/**
	 * Used to fetch extension name of the required models.
	 */
	@Autowired
	private IExtensionContextProvider extensionContextProvider;
	
	/**
	 * Fetches repositories from autowired repository factory.
	 */
	@PostConstruct
	private void init()
	{
		this.extensionRepository = repositoryFactory.getRepository(IExtensionRepository.class);
		this.extensionFieldRepository = repositoryFactory.getRepository(IExtensionFieldRepository.class);
		
		//load extension points
		Set<Class<?>> extendableTypes = classScannerService.getClassesWithAnnotation(ExtendableEntity.class);
		ExtendableEntity extendableEntity = null;
		ExtensionEntityDetails extensionPointDetails = null;
		String name = null;
		
		for(Class<?> type : extendableTypes)
		{
			extendableEntity = type.getAnnotation(ExtendableEntity.class);
			name = extendableEntity.name();
			
			if(name.contains("$"))
			{
				throw new InvalidConfigurationException("$ found in the extension name configured on entity - {}", type.getName());
			}
			
			extensionPointDetails = new ExtensionEntityDetails(name, type);
			
			nameToExtension.put(extendableEntity.name(), extensionPointDetails);
			typeToExtension.put(type.getName(), extensionPointDetails);
		}
	}
	
	/**
	 * Fetches extensions as LOV list. This method returns extensions which are current user
	 * is authorized for.
	 * @return Extensions as lov list
	 */
	@LovMethod(name = "extensionLov")
	public List<LovOption> getExtendableEntityList()
	{
		List<LovOption> filteredExtensions = new ArrayList<>(nameToExtension.size());
		
		//loop through extensions
		for(ExtensionEntityDetails extPoint : this.nameToExtension.values())
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
	 * Gets extension entity details based on specified name.
	 * @param name Name of the extension entity.
	 * @return Matching extension entity
	 */
	public ExtensionEntityDetails getExtensionEntityDetailsByName(String name)
	{
		int idx = name.indexOf("$");
		
		if(idx > 0)
		{
			name = name.substring(0, idx);
		}
		
		return nameToExtension.get(name);
	}
	
	/**
	 * Fetches extension with specified criteria.
	 * @param name Name of the extension to fetch. 
	 * @return Matching extension entity
	 */
	public ExtensionEntity getExtensionEntity(String name)
	{
		logger.trace("Fetching extension entity - [Name: {}]", name);
		
		return extensionRepository.findExtensionByName(name);
	}
	
	/**
	 * Checks if the specified name is valid extension or not.
	 * @param name Name to be checked.
	 * @return True if its valid extension.
	 */
	public boolean isValidExtension(String name)
	{
		ExtensionEntityDetails extensionEntityDetails = nameToExtension.get(name);
		
		if(extensionEntityDetails != null && securityService.isExtensionAuthorized(extensionEntityDetails))
		{
			return true;
		}
		
		return extensionRepository.isValidExtension(name);
	}
	
	/**
	 * Creates new extension with specified details.
	 * @param extension Extension to be saved
	 */
	public void saveExtensionEntity(ExtensionEntity extension)
	{
		ExtensionEntityDetails extensionPointDetails = typeToExtension.get(extension.getTargetEntityType());
		
		if(extensionPointDetails == null)
		{
			throw new InvalidArgumentException("Specified entity type is not an extension entity - {}", extension.getTargetEntityType());
		}
		
		//if owner entity type is not specified use Object class
		if(extension.getOwnerEntityType() == null)
		{
			extension.setOwnerEntityType(DEF_OWNER_TYPE);
			extension.setOwnerEntityId(DEF_OWNER_ID);
		}

		userService.populateTrackingFieldForCreate(extension);
		extension.setSpaceIdentity(securityService.getUserSpaceIdentity());
		
		//set default version
		extension.setVersion(1);
		
		if(!extensionRepository.save(extension))
		{
			throw new ServiceException("Failed to save extension - {}", extension);
		}
	}
	
	/**
	 * Fetches extension fields for specified extension name.
	 * @param extensionName Specified extension name
	 * @return List of extension fields
	 */
	public List<ExtensionFieldEntity> getExtensionFields(String extensionName)
	{
		logger.trace("Fetching extension fields for extension - {}", extensionName);
		
		return extensionFieldRepository.findExtensionFields(extensionName);
	}
	
	/**
	 * Fetches extension fields for specified extension id.
	 * @param extensionId Specified extension id
	 * @return List of extension fields
	 */
	public List<ExtensionFieldEntity> getExtensionFields(long extensionId)
	{
		logger.trace("Fetching extension fields for extension - {}", extensionId);
		
		return extensionFieldRepository.findExtensionFieldsByExtensionId(extensionId);
	}

	/**
	 * Fetches extension fields of specified entity under all owners restricted to current user space.
	 * This is mainly used for search query customization.
	 * 
	 * @param entityType Entity type for which extension fields needs to be fetched.
	 * @return Matching extension fields.
	 */
	public List<ExtensionFieldEntity> getExtensionFieldsForEntity(String entityType)
	{
		logger.trace("Fetching extension fields for entity type - {}", entityType);
		
		return extensionFieldRepository.findExtensionFieldsByEntity(entityType);
	}
	
	/**
	 * Gets extension based on specified target type and owner details.
	 * @param targetEntityType Target type.
	 * @param ownerEntityType Owner type.
	 * @param ownerId Owner id.
	 * @return Matching extension.
	 */
	public ExtensionEntity getExtension(Class<?> targetEntityType, Class<?> ownerEntityType, long ownerId)
	{
		logger.trace("Fetching extension for extension - Target: {}, Owner: {}, Owner Id: {}", targetEntityType.getName(), ownerEntityType.getName(), ownerId);
		
		return extensionRepository.findExtension(targetEntityType.getName(), ownerEntityType.getName(), ownerId);
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
		extensionFieldEntity.setSpaceIdentity(securityService.getUserSpaceIdentity());
		
		//set default version
		extensionFieldEntity.setVersion(1);
		
		//set the unused column name for the new extension field entity
		Set<String> usedFields = extensionFieldRepository.fetchUsedColumnNames(extensionId);
		String columnName = IWebUtilsInternalConstants.EXT_FIELD_PREFIX + "0";
		
		if(usedFields != null && !usedFields.isEmpty())
		{
			if(usedFields.size() == IWebUtilsInternalConstants.EXT_FIELD_COUNT)
			{
				throw new InvalidStateException("All extension fields are consumed for entension - {}", extensionId);
			}
			
			//find the first column which is not used till now
			for(int i = 0; i < IWebUtilsInternalConstants.EXT_FIELD_COUNT; i++)
			{
				columnName = IWebUtilsInternalConstants.EXT_FIELD_PREFIX + i;
				
				if(!usedFields.contains(columnName))
				{
					break;
				}
			}
		}
		
		extensionFieldEntity.setColumnName(columnName);
		
		//save the entity
		if(!extensionFieldRepository.save(extensionFieldEntity))
		{
			throw new ServiceException("Failed to save extension field entity.");
		}
	}
	
	/**
	 * updates specified extension field under specified extension.
	 * @param extensionFieldEntity Extension field to update.
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
	 * Maps data from specified model to entity.
	 * @param model model from which extended field data needs to be mapped.
	 * @param entity Entity to which extended field data needs to be set.
	 */
	public void mapExtendedFieldsToEntity(IExtendableModel model, WebutilsExtendableEntity entity)
	{
		//fetch extension name of the model
		String extensionName = extensionContextProvider.getExtensionName(model);
		
		//if context provider is not able to provide the name, fall back to default way of fetching
		if(extensionName == null)
		{
			ExtendableModel extendableModel = model.getClass().getAnnotation(ExtendableModel.class);
			extensionName = extendableModel.name();
		}
		
		//get extended field details
		List<ExtensionFieldEntity> fields = getExtensionFields(extensionName);
		
		if(fields == null || fields.isEmpty())
		{
			return;
		}
		
		//get extended field data, maps from field name to value
		Map<String, String> extFieldData = model.getExtendedFields();
		
		//set data mapping column name to value
		for(ExtensionFieldEntity field : fields)
		{
			entity.addExtendedField(field.getColumnName(), extFieldData.get(field.getName()));
		}
	}
	
	/**
	 * Maps the data from specified entity to model.
	 * @param repository Repository to be used for fetching extended field values
	 * @param entityId Entity from which extended data needs to be fetched.
	 * @param model Model to which extended fields needs to be mapped.
	 */
	public void mapExtendedFieldsToModel(IWebutilsRepository<?> repository, long entityId, IExtendableModel model)
	{
		//fetch extension name of the model
		String extensionName = extensionContextProvider.getExtensionName(model);
		
		//if context provider is not able to provide the name, fall back to default way of fetching
		if(extensionName == null)
		{
			ExtendableModel extendableModel = model.getClass().getAnnotation(ExtendableModel.class);
			extensionName = extendableModel.name();
		}
		
		//get extended field details
		List<ExtensionFieldEntity> fields = getExtensionFields(extensionName);
		
		if(fields == null || fields.isEmpty())
		{
			return;
		}
		
		//build extension field names and fetch extension field data
		Set<String> extendedFieldNames = new HashSet<>(); 
		fields.stream().map(extFld -> extFld.getColumnName()).forEach(name -> extendedFieldNames.add(name));
		
		ExtensionFieldsData extensionFieldsData = repository.fetchExtendedFields(entityId, extendedFieldNames);
		
		//get extended field data, maps from column name to value
		Map<String, String> extFieldData = extensionFieldsData.getExtendedFields();
		Map<String, String> modelExtFieldData = new HashMap<>();
		
		//set data mapping name to value
		for(ExtensionFieldEntity field : fields)
		{
			modelExtFieldData.put(field.getName(), extFieldData.get(field.getColumnName()));
		}
		
		model.setExtendedFields(modelExtFieldData);
	}
	
	/**
	 * Updates specified extension with specified name.
	 * @param extensionId Id of extension to update.
	 * @param newName New name for extension.
	 */
	public void updateExtensionName(long extensionId, String newName)
	{
		if(!extensionRepository.updateExtensionName(extensionId, newName))
		{
			throw new InvalidStateException("Failed to update extension '{}' to name - {}", extensionId, newName);
		}
	}
	
	/**
	 * Deletes extension with specified id.
	 * @param id Id of extension to be deleted.
	 */
	public void deleteExtension(long id)
	{
		extensionRepository.deleteById(id);
	}
}
