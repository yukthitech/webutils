package com.yukthitech.webutils.services;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.persistence.ITransaction;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.common.FileInfo;
import com.yukthitech.webutils.common.ImageInfo;
import com.yukthitech.webutils.common.models.def.FieldDef;
import com.yukthitech.webutils.common.models.def.FieldType;
import com.yukthitech.webutils.common.models.def.ModelDef;
import com.yukthitech.webutils.repository.file.IFileRepository;
import com.yukthitech.webutils.security.ISecurityService;

import jakarta.annotation.PostConstruct;

/**
 * Repository based image service to maintain images for different
 * entities.
 * @author akiran
 */
@Service
public class ImageService
{
	private static Logger logger = LogManager.getLogger(ImageService.class);
	
	/**
	 * Autowired repository factory, used to fetch repository.
	 */
	@Autowired
	protected WebutilsRepositoryFactory repositoryFactory;

	/**
	 * Service to fetch image fields out of model.
	 */
	@Autowired
	private ModelDetailsService modelDetailsService;
	
	/**
	 * Security service used to fetch user space identity.
	 */
	@Autowired
	private ISecurityService securityService;

	/**
	 * File repository.
	 */
	private IFileRepository repository;
	
	/**
	 * Initialize method to initialize repository.
	 */
	@PostConstruct
	private void init()
	{
		this.repository = repositoryFactory.getRepository(IFileRepository.class);
	}
	
	/**
	 * Saves the image information from specified model.
	 * @param model Model from which images to be saved will be extracted.
	 * @param entityType Owner entity type.
	 * @param ownerId Owner entity id.
	 */
	public void saveImagesFromModel(Object model, Class<?> entityType, long ownerId)
	{
		if(model == null)
		{
			return;
		}
		
		logger.trace("Trying to save images specified on model - {} under ownership - {}, {}", model, entityType, ownerId);
		
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
		
		ImageInfo imageInfo = null;
		
		try(ITransaction transaction = repository.newOrExistingTransaction())
		{
			//loop through fields and save file information from file fields
			for(FieldDef fieldDef : modelDef.getFields())
			{
				//ignore non file fields
				if(fieldDef.getFieldType() != FieldType.IMAGE)
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
					throw new InvalidStateException(ex, "An error occurred while fetching image information from field - {}.{}", model.getClass().getName(), fieldDef.getName());
				}
				
				if(fieldValue == null)
				{
					continue;
				}
				
				logger.debug("Saving image specified on model field - {}.{}", model.getClass().getName(), field.getName()); 
				
				imageInfo = (ImageInfo) fieldValue;
				
				if(!imageInfo.isNewImage())
				{
					continue;
				}

				repository.deleteByOwner(entityType.getName(), field.getName(), ownerId, securityService.getUserSpaceIdentity());
				repository.updateToPermanentFile(imageInfo.getFileId(), entityType.getName(), field.getName(), ownerId, securityService.getUserSpaceIdentity());
			}
			
			transaction.commit();
		}catch(Exception ex)
		{
			logger.error("An error occurred while saving image fields of model - " + model, ex);
			throw new InvalidStateException(ex, "An error occurred while saving image fields of model - {}", model);
		}
	}

	/**
	 * Reads the images into the model fields from db.
	 * @param model Model into which image information needs to be read.
	 * @param entityType Entity type whose ownership should be used.
	 * @param ownerId Entity id to be used.
	 */
	public void readImagesForModel(Object model, Class<?> entityType, Long ownerId)
	{
		if(model == null || ownerId == null)
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
			logger.trace("Specified model {} is not marked as @Model ", model.getClass().getName());
			return;
		}

		Field field = null;
		List<FileInfo> filesFromDb = null;

		//loop through fields and save image information to image fields
		for(FieldDef fieldDef : modelDef.getFields())
		{
			//ignore non file fields
			if(fieldDef.getFieldType() != FieldType.IMAGE)
			{
				continue;
			}
			
			//fetch files from db
			filesFromDb = repository.fetchByOwner(entityType.getName(), fieldDef.getName(), ownerId, securityService.getUserSpaceIdentity());
			
			if(filesFromDb == null || filesFromDb.isEmpty())
			{
				continue;
			}
			
			logger.debug("Setting image on model field - {}.{}", model.getClass().getName(), fieldDef.getName()); 

			//set the files on field
			try
			{
				field = modelType.getDeclaredField(fieldDef.getName());
				field.setAccessible(true);

				field.set(model, new ImageInfo( filesFromDb.get(0).getId() ));
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while setting file information to field - {}.{}", model.getClass().getName(), field.getName());
			}
		}
	}
}
