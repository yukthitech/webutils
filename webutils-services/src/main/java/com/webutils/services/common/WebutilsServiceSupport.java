package com.webutils.services.common;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webutils.common.form.model.FieldDef;
import com.webutils.common.form.model.FieldType;
import com.webutils.common.form.model.LovDetails;
import com.webutils.common.form.model.LovType;
import com.webutils.common.form.model.ModelDef;
import com.webutils.services.form.lov.LovService;
import com.webutils.services.form.lov.stored.LovConfig;
import com.webutils.services.form.lov.stored.StoredLovService;
import com.webutils.services.form.model.ModelService;
import com.yukthitech.utils.PropertyAccessor;
import com.yukthitech.utils.exceptions.InvalidStateException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

@Service
public class WebutilsServiceSupport
{
	@Autowired
	private FileService fileService;

	@Autowired
	private ModelService modelService;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private LovService lovService;

	@Autowired
	private StoredLovService storedLovService;

	/**
	 * This method should be called before calling actual entity save or update. As this method populate final lov values
	 * into the model.
	 * 
	 * If existing entity is passed, then files persisted in that entity will be cleaned first. Note, this is required
	 * to remove old files and retain new files. During first entity save, this can be null.
	 * 
	 * Note: existingEntity is required only when files are involved.
	 * 
	 * @param model
	 * @param entityId
	 * @return
	 */
	public Object processModel(Object model, Object existingEntity)
	{
		ModelDef modelDef = modelService.getModelDef(model.getClass());

		if(modelDef == null)
		{
			return model;
		}

		modelDef.getFields().forEach(fieldDef -> 
		{
			if(fieldDef.getFieldType() == FieldType.FILE || fieldDef.getFieldType() == FieldType.IMAGE)
			{
				populateFileInfos(model, existingEntity, modelDef, fieldDef);
				return;
			}

			if(fieldDef.getFieldType() == FieldType.LIST_OF_VALUES)
			{
				processListOfValues(model, modelDef, fieldDef);
				return;
			}
		});


		return model;
	}
	
	private void populateFileInfos(Object model, Object entityFromDb, ModelDef modelDef, FieldDef fieldDef)
	{
		if(request.getContentType() == null || !request.getContentType().toLowerCase().startsWith("multipart/"))
		{
			throw new InvalidRequestException("No attachment found for field (Non multi part request): {}", 
					fieldDef.getName());
		}

		// Save new files and populate file info
		List<String> fileNames = new ArrayList<>();
		
		try
		{
			Collection<Part> partLst = request.getParts();
			
			if(partLst == null || partLst.isEmpty())
			{
				return;
			}

			boolean isMultiValued = fieldDef.isMultiValued();
			int partCount = 0;

			String filePrefix = String.format("%s-%s-%s", modelDef.getName(), fieldDef.getName(), UUID.randomUUID().toString());

			for(Part part : request.getParts())
			{
				if(!fieldDef.getName().equals(part.getName()))
				{
					continue;
				}
				
				if(!isMultiValued && partCount > 1)
				{
					throw new InvalidRequestException("Multiple files found single value field: {}", 
						fieldDef.getName());
				}

				// If file name is blank, then it indicates existing file is retained
				if(StringUtils.isBlank(part.getSubmittedFileName()))
				{
					// get part content as string, which should be existing file name
					String fileName = IOUtils.toString(part.getInputStream(), StandardCharsets.UTF_8);
					fileName = StringUtils.isNotBlank(fileName) ? fileName.trim() : null;

					// if file exists, then add to file names
					if(fileName != null && fileService.isExistingFile(fieldDef.getGroupName(), fileName))
					{
						fileNames.add(fileName);
					}

					// if file does not exist, then skip the part
					continue;
				}

				String finalName = fileService.save(fieldDef.getGroupName(), filePrefix, part);
				fileNames.add(finalName);
			}

			// If no files are uploaded, then set the field to null
			if(fileNames.isEmpty())
			{
				PropertyAccessor.setProperty(model, fieldDef.getName(), null);
				removeOldFiles(entityFromDb, modelDef, fieldDef, fileNames);
				return;
			}
			
			if(isMultiValued)
			{
				PropertyAccessor.setProperty(model, fieldDef.getName(), convertToCollection(fileNames, fieldDef));
			}
			else
			{
				PropertyAccessor.setProperty(model, fieldDef.getName(), fileNames.get(0));
			}
		} catch(Exception e)
		{
			throw new RuntimeException("Failed to extract multipart files", e);
		}
		
		// Remove old files mentioned in entity, which are not used in the request
		removeOldFiles(entityFromDb, modelDef, fieldDef, fileNames);
	}

	@SuppressWarnings("unchecked")
	private void removeOldFiles(Object entityFromDb, ModelDef modelDef, FieldDef fieldDef, List<String> usedFileNames)
	{
		if(entityFromDb == null)
		{
			return;
		}
		
		Object oldValue = null;

		try
		{
			oldValue = PropertyUtils.getProperty(entityFromDb, fieldDef.getName());
			
			if(oldValue == null)
			{
				return;
			}
		}catch(Exception ex)
		{
			throw new InvalidStateException("Error in getting old value for field: {}.{}", modelDef.getName(), fieldDef.getName(), ex);
		}

		Collection<String> oldFileNames = null;

		if(oldValue instanceof Collection)
		{
			oldFileNames = (Collection<String>) oldValue;
		}
		else if(oldValue instanceof String)
		{
			oldFileNames = Arrays.asList((String) oldValue);
		}

		if(oldFileNames != null)
		{
			Set<String> fileNamesToDelete = new HashSet<>(oldFileNames);
			fileNamesToDelete.removeAll(usedFileNames);

			// Delete files that are not used
			if(!fileNamesToDelete.isEmpty())
			{
				fileService.delete(fieldDef.getGroupName(), fileNamesToDelete);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Collection<Object> convertToCollection(Collection<?> values, FieldDef fieldDef)
	{
		Field field = fieldDef.getField();
		Class<?> type = field.getType();

		if(List.class.isAssignableFrom(type) || Collection.class.isAssignableFrom(type))
		{
			return new ArrayList<>(values);
		}
		else if(Set.class.isAssignableFrom(type))
		{
			return new HashSet<>(values);
		}

		try
		{
			Collection<Object> collection = (Collection<Object>) type.getConstructor().newInstance();
			collection.addAll(values);
			return collection;
		}catch(Exception ex)
		{
			throw new InvalidStateException("Error in creating collection for field: {}.{}", 
				field.getDeclaringClass().getName(), field.getName(), ex);
		}
	}

	@SuppressWarnings("unchecked")
	private void processListOfValues(Object model, ModelDef modelDef, FieldDef fieldDef)
	{
		LovDetails lovDetails = fieldDef.getLovDetails();

		Object value = null;

		try
		{
			value = PropertyUtils.getProperty(model, fieldDef.getName());
		}catch(Exception ex)
		{
			throw new InvalidStateException("Error in getting value for field: {}.{}", modelDef.getName(), fieldDef.getName(), ex);
		}

		if(value == null)
		{
			return;
		}

		Collection<Object> lovValues = null;

		if(value instanceof Collection)
		{
			lovValues = (Collection<Object>) value;
		}
		else
		{
			lovValues = Arrays.asList(value);
		}
		
		if(lovValues.isEmpty())
		{
			return;
		}

		if(lovDetails.getLovType() == LovType.STATIC_TYPE)
		{
			for(Object lovValue : lovValues)
			{
				if(lovService.isValidStaticLovValue(lovDetails.getLovName(), "" + lovValue))
				{
					return;
				}
			}
		}
		else if(lovDetails.getLovType() == LovType.DYNAMIC_TYPE)
		{
			for(Object lovValue : lovValues)
			{
				if(lovService.isValidDynamicLovValue(lovDetails.getLovName(), "" + lovValue))
				{
					return;
				}
			}
		}
		else if(lovDetails.getLovType() == LovType.STORED_TYPE)
		{
			LovConfig lovConfig = new LovConfig()
				.setSaveMissingOptions(lovDetails.isEditableLov())
				;

			if(lovDetails.getParentField() != null)
			{
				Object parentValue = null;

				try
				{
					parentValue = PropertyUtils.getProperty(model, lovDetails.getParentField());
				}catch(Exception ex)
				{
					throw new InvalidStateException("Error in getting parent value for field: {}.{}", modelDef.getName(), fieldDef.getName(), ex);
				}

				if(parentValue == null)
				{
					throw new InvalidStateException("Parent field ({}) value is missing for field: {}.{}", 
						lovDetails.getParentField(), modelDef.getName(), fieldDef.getName());
				}

				lovConfig.setParentOptionLabel("" + parentValue);
			}

			Set<String> stringLovValues = lovValues.stream().map(Object::toString).collect(Collectors.toSet());
			Set<String> savedLovValues = storedLovService.checkAndSaveLovOption(lovConfig, lovDetails.getLovName(), stringLovValues);

			try
			{
				if(fieldDef.isMultiValued())
				{
					PropertyUtils.setProperty(model, fieldDef.getName(), convertToCollection(savedLovValues, fieldDef));
				}
				else
				{
					PropertyUtils.setProperty(model, fieldDef.getName(), savedLovValues.iterator().next());
				}
			}catch(Exception ex)
			{
				throw new InvalidStateException("Error in setting lov value(s) for field: {}.{}", modelDef.getName(), fieldDef.getName(), ex);
			}
		}
	}
}
