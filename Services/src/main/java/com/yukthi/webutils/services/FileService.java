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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthi.persistence.ITransaction;
import com.yukthi.persistence.repository.RepositoryFactory;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.common.FileInfo;
import com.yukthi.webutils.common.models.def.FieldDef;
import com.yukthi.webutils.common.models.def.FieldType;
import com.yukthi.webutils.common.models.def.ModelDef;
import com.yukthi.webutils.repository.file.FileEntity;
import com.yukthi.webutils.repository.file.IFileRepository;
import com.yukthi.webutils.security.ISecurityService;
import com.yukthi.webutils.utils.WebUtils;

/**
 * Repository based file service
 * @author akiran
 */
@Service
public class FileService
{
	private static Logger logger = LogManager.getLogger(FileService.class);
	
	/**
	 * Autowired repository factory, used to fetch repository
	 */
	@Autowired
	protected RepositoryFactory repositoryFactory;

	/**
	 * Used to populate tracked fields
	 */
	@Autowired
	private CurrentUserService userService;

	/**
	 * Service to populate security fields on file entity
	 */
	@Autowired
	private ISecurityService securityService;
	
	/**
	 * Service to fetch file fields out of model
	 */
	@Autowired
	private ModelDetailsService modelDetailsService;

	/**
	 * File repository
	 */
	private IFileRepository repository;
	
	/**
	 * Initialize method to initialize repository
	 */
	@PostConstruct
	private void init()
	{
		this.repository = repositoryFactory.getRepository(IFileRepository.class);
	}
	
	/**
	 * Saves single file of specified owner. If no new file content is specified as part of file info
	 * old file will be retained.
	 * @param fileInfo File info to be saved
	 * @param ownerEntityType Owner entity type
	 * @param ownerField Owner field name
	 * @param ownerId Owner id
	 */
	private void saveFileForOwner(FileInfo fileInfo, Class<?> ownerEntityType, String ownerField, long ownerId)
	{
		//if field value is null, assume existing file needs to be deleted
		if(fileInfo == null)
		{
			logger.trace("As field value is null, deleting associated filed for field - [Owner Type: {}, Field: {}, Owner Id: {}]", 
					ownerEntityType.getName(), ownerField, ownerId);
			this.delete(ownerEntityType, ownerField, ownerId);
			return;
		}
		
		//if no new file is specified, retain existing file (though id is changed it will be ignored
			//	 as id change is not expected from client)
		if(fileInfo.getFile() == null)
		{
			return;
		}
		
		//if file content is present delete existing file and save new one
		this.delete(ownerEntityType, ownerField, ownerId);
		this.save(fileInfo, ownerEntityType, ownerField, ownerId);
	}
	
	/**
	 * Saves files for specified owner. Also deletes existing files of specified owner, which are not 
	 * mentioned in specified list.
	 * @param ownerEntityType Owner entity type
	 * @param fileInfoLst Files to save or retain
	 * @param ownerField Owning field
	 * @param ownerId Owning entity
	 */
	private void saveFilesForOwner(Collection<FileInfo> fileInfoLst, Class<?> ownerEntityType, String ownerField, long ownerId)
	{
		//collection to keep track of file ids to delete, by default this will all existing files
		List<Long> idsToRemove = repository.fetchIdsByOwner(ownerEntityType.getName(), ownerField, ownerId);
		
		//if no files are currently present, use empty collection
		if(idsToRemove == null)
		{
			idsToRemove = new ArrayList<>();
		}
		
		//loop through the file list to retain or add
		for(FileInfo info : fileInfoLst)
		{
			//if new content is being specified add new file
			if(info.getFile() != null)
			{
				this.save(info, ownerEntityType, ownerField, ownerId);
				continue;
			}
			
			//if no content is specified, delete info id from id list to remove
			idsToRemove.remove(info.getId());
		}
		
		//existing ids are which are not mentioned as part of input list should be deleted
		for(Long id : idsToRemove)
		{
			repository.deleteById(id);
		}
	}

	/**
	 * Loops through fields of specified model and saves the files of all the fields of type FILE.
	 * @param model Model from which files needs to be saved
	 * @param entityType Entity type which will act as owner type
	 * @param ownerId Entity id
	 */
	@SuppressWarnings("unchecked")
	public void saveFilesFromModel(Object model, Class<?> entityType, long ownerId)
	{
		if(model == null)
		{
			return;
		}
		
		logger.trace("Trying to save files specified on model - {} under ownership - {}, {}", model, entityType, ownerId);
		
		Class<?> modelType = model.getClass();
		
		//fetch model details
		ModelDef modelDef = modelDetailsService.getModelDef(modelType);
		
		//if specified model is not marked as Model, ignore
		if(modelDef == null)
		{
			logger.trace("Specified model {} is not marked as @Model", model.getClass().getName());
			return;
		}
		
		Object fieldValue = null;
		Field field = null;

		try(ITransaction transaction = repository.newOrExistingTransaction())
		{
			//loop through fields and save file information from file fields
			for(FieldDef fieldDef : modelDef.getFields())
			{
				//ignore non file fields
				if(fieldDef.getFieldType() != FieldType.FILE)
				{
					continue;
				}
				
				//fetch file information
				try
				{
					field = modelType.getDeclaredField(fieldDef.getName());
					field.setAccessible(true);
					fieldValue = field.get(model);
				}catch(Exception ex)
				{
					throw new InvalidStateException(ex, "An error occurred while fetching file information from field - {}.{}", model.getClass().getName(), fieldDef.getName());
				}
				
				logger.debug("Saving file(s) specified on model field - {}.{}", model.getClass().getName(), field.getName()); 
				
				//save file informations
				if(fieldDef.isMultiValued())
				{
					//if null is specified on the field, assume all existing files has to be deleted
						// and empty collection needs to be retained
					if(fieldValue == null)
					{
						fieldValue = Collections.emptyList();
					}
					
					saveFilesForOwner( (Collection<FileInfo>)fieldValue, entityType, fieldDef.getName(), ownerId);
				}
				else
				{
					saveFileForOwner( (FileInfo)fieldValue, entityType, fieldDef.getName(), ownerId);
				}
			}
			
			transaction.commit();
		}catch(Exception ex)
		{
			logger.error("An error occurred while saving file fields of model - " + model, ex);
			throw new InvalidStateException(ex, "An error occurred while saving file fields of model - {}", model);
		}
	}
	
	/**
	 * Saves the specified file entity and returns the id
	 * @param file File to be saved
	 * @param ownerEntityType Owner entity type
	 * @param ownerEntityField Owner entity field
	 * @param ownerEntityId owner entity id
	 * @return Saved file id
	 */
	public Long save(FileInfo file, Class<?> ownerEntityType, String ownerEntityField, long ownerEntityId)
	{
		logger.trace("Saving file {} under owner - {}, {}, {}", file, ownerEntityType.getName(), ownerEntityField, ownerEntityId);
		
		FileEntity fileEntity = WebUtils.convertBean(file, FileEntity.class);
		
		//get auditing fields
		userService.populateTrackingFieldForCreate(fileEntity);

		//set extra file info
		fileEntity.setSizeInMb(fileEntity.getFile().length());
		fileEntity.setVersion(1);
		fileEntity.setOwnerEntityType(ownerEntityType.getName());
		fileEntity.setOwnerEntityField(ownerEntityField);
		fileEntity.setOwnerEntityId(ownerEntityId);
		fileEntity.setSecured(file.isSecured());
		
		//get security customization
		securityService.addSecurityCustomization(fileEntity);
		
		boolean res = repository.save(fileEntity);
		
		if(!res)
		{
			logger.error("Failed to save file - {}", fileEntity);
			throw new InvalidStateException("Failed to save file - {}", fileEntity);
		}
		
		logger.trace("File got saved with id {}", fileEntity.getId());
		return fileEntity.getId();
	}

	/**
	 * Reads corresponding file information from db for specified model under specified entity ownership 
	 * and sets the file information on the model
	 * @param model Model for which file information needs to be fetched
	 * @param entityType Owner entity type
	 * @param ownerId Owner id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void readFilesForModel(Object model, Class<?> entityType, long ownerId)
	{
		if(model == null)
		{
			return;
		}
		
		logger.trace("Trying to save files specified on model - {} under ownership - {}, {}", model, entityType, ownerId);
		
		Class<?> modelType = model.getClass();
		
		//fetch model details
		ModelDef modelDef = modelDetailsService.getModelDef(modelType);
		
		//if specified model is not marked as Model, ignore
		if(modelDef == null)
		{
			logger.trace("Specified model {} is not marked as @Model", model.getClass().getName());
			return;
		}

		Field field = null;
		List<FileInfo> filesFromDb = null;

		//loop through fields and save file information from file fields
		for(FieldDef fieldDef : modelDef.getFields())
		{
			//ignore non file fields
			if(fieldDef.getFieldType() != FieldType.FILE)
			{
				continue;
			}
			
			//fetch files from db
			filesFromDb = repository.fetchByOwner(entityType.getName(), fieldDef.getName(), ownerId);
			
			if(filesFromDb == null || filesFromDb.isEmpty())
			{
				continue;
			}
			
			logger.debug("Saving file(s) specified on model field - {}.{}", model.getClass().getName(), fieldDef.getName()); 

			//set the files on field
			try
			{
				field = modelType.getDeclaredField(fieldDef.getName());
				field.setAccessible(true);

				if(fieldDef.isMultiValued())
				{
					Collection<Object> resCollection = (Collection)fieldDef.getCompatibleCollectionType().newInstance();
					resCollection.addAll(filesFromDb);
					
					field.set(model, resCollection);
				}
				else
				{
					field.set(model, filesFromDb.get(0));
				}
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while setting file information to field - {}.{}", model.getClass().getName(), field.getName());
			}
		}
	}

	/**
	 * Fetches the file information for specified id
	 * @param id Id of the file
	 * @return File information
	 */
	public FileInfo getFileInfo(Long id)
	{
		logger.trace("Fetching file-info with id - {}", id);

		return repository.fetchFileInfo(id);
	}

	/**
	 * Fetches file informations based on custom attributes specified
	 * @param customAttr1
	 * @param customAttr2
	 * @param customAttr3
	 * @return List of matching file informations
	 */
	public List<FileInfo> fetchFileInformations(String customAttr1, String customAttr2, String customAttr3)
	{
		return repository.fetchWithCustomAttributes(customAttr1, customAttr2, customAttr3);
	}

	/**
	 * Deletes file with specified id
	 * @param id Id of the file to be deleted
	 * @return true if deletion is successful
	 */
	public boolean delete(Long id)
	{
		logger.trace("Deleting file with id - {}", id);
		
		boolean res = repository.deleteById(id);
		
		return res;
	}
	
	/**
	 * Deletes files belonging to specified owner
	 * @param ownerEntityType Owner entity type
	 * @param ownerEntityField Owner entity field which owns the file
	 * @param ownerEntityId Entity id of the owning entity
	 * @return True, if at least one matching file is deleted
	 */
	public boolean delete(Class<?> ownerEntityType, String ownerEntityField, long ownerEntityId)
	{
		logger.trace("Deleting file matching with owner - {}, {}, {}", ownerEntityType.getName(), ownerEntityField, ownerEntityId);
		
		int count = repository.deleteByOwner(ownerEntityType.getName(), ownerEntityField, ownerEntityId);
		
		logger.debug("Number of files deleted - " + count);
		
		return (count > 0);
	}

	/**
	 * Fetches file entity for specified id
	 * @param id
	 * @return Matching file entity
	 */
	public FileEntity getFileEntity(Long id)
	{
		logger.trace("Fetching file content for id - {}", id);
		return repository.findById(id);
	}

	/**
	 * Fetches file entity based on id and secured flag
	 * @param id
	 * @param secured
	 * @return
	 */
	public FileEntity getFileEntity(Long id, boolean secured)
	{
		logger.trace("Fetching file content for id - {} and security flag - {}", id, secured);
		return repository.findBySecurityFlag(id, secured);
	}
}
