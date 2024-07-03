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

package com.yukthitech.webutils.services.def;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.yukthitech.utils.exceptions.InvalidConfigurationException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.common.LovType;
import com.yukthitech.webutils.common.annotations.Color;
import com.yukthitech.webutils.common.annotations.CustomType;
import com.yukthitech.webutils.common.annotations.DateTime;
import com.yukthitech.webutils.common.annotations.DefaultValue;
import com.yukthitech.webutils.common.annotations.Format;
import com.yukthitech.webutils.common.annotations.FullWidth;
import com.yukthitech.webutils.common.annotations.Html;
import com.yukthitech.webutils.common.annotations.LOV;
import com.yukthitech.webutils.common.annotations.MultilineText;
import com.yukthitech.webutils.common.annotations.NeedVerification;
import com.yukthitech.webutils.common.annotations.NonDisplayable;
import com.yukthitech.webutils.common.annotations.Password;
import com.yukthitech.webutils.common.annotations.ReadOnly;
import com.yukthitech.webutils.common.models.def.FieldDef;
import com.yukthitech.webutils.common.models.def.FieldType;
import com.yukthitech.webutils.common.models.def.LovDetails;
import com.yukthitech.webutils.common.models.def.ValidationDef;
import com.yukthitech.webutils.services.LovRef;

/**
 * Builder for field definitions.
 * @author akiran
 */
@Component
public class FieldDefBuilder
{
	/**
	 * Common def utils.
	 */
	@Autowired
	private DefUtils defUtils;
	
	/**
	 * Builder to build field validations.
	 */
	@Autowired
	private ValidationDefBuilder validationDefBuilder;
	
	/**
	 * Used to fetch default values for fields.
	 */
	@Autowired
	@Qualifier("defaultValuesMessageSource")
	private MessageSource defaultValuesMessageSource;

	/**
	 * Used to LOV details on field-def, whose type is an enum.
	 * @param fieldDef Field def being built
	 * @param fieldType Field enum type
	 */
	private void getEnumLovDetails(FieldDef fieldDef, Class<?> fieldType)
	{
		fieldDef.setFieldType(FieldType.LIST_OF_VALUES);
		
		LovDetails lovDetails = new LovDetails();
		lovDetails.setLovType(LovType.STATIC_TYPE);
		lovDetails.setLovName(fieldType.getName());

		fieldDef.setLovDetails(lovDetails);
	}
	
	/**
	 * Gets LOV field details for field which is marked as LOV field. 
	 * @param fieldDef
	 * @param field
	 * @param parentCls
	 */
	private void getCustomLovDetails(Class<?> modelType, FieldDef fieldDef, Field field, Set<LovRef> requiredLovs)
	{
		LOV lovAnnotation = field.getAnnotation(LOV.class);
		
		fieldDef.setFieldType(FieldType.LIST_OF_VALUES);
		
		LovDetails lovDetails = new LovDetails();
		lovDetails.setLovType(LovType.DYNAMIC_TYPE);
		
		//add current lov as required lov, which would be validated
		//  at end of application load by Model def builder
		requiredLovs.add(new LovRef(lovAnnotation.name(), modelType.getName() + "." + field.getName()));
		
		lovDetails.setLovName(lovAnnotation.name());
		
		//fetch and validate parent field
		if(StringUtils.isNotBlank(lovAnnotation.parentField()))
		{
			try
			{
				modelType.getDeclaredField(lovAnnotation.parentField());
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "Invalid lov-parent field name '{}' specified in field {}.{}", 
							lovAnnotation.parentField(), modelType.getName(), field.getName());
			}
			
			lovDetails.setParentField(lovAnnotation.parentField());
		}
		
		lovDetails.setActualType(FieldType.getFieldType(field.getType()));

		fieldDef.setLovDetails(lovDetails);
	}

	/**
	 * If specified type is collection, then compatible concrete collection will be computed and returned. Null will be returned
	 * if specified type is not collection. Exception will be thrown if compatible collection type can not be found.
	 * @param type Type to be analyzed. 
	 * @return Compatible collection type for collection. For non-collection null will be returned.
	 */
	private Class<?> getCompatiableCollectionType(Class<?> type, Field field)
	{
		//if specified type is not collection return null
		if(!Collection.class.isAssignableFrom(type))
		{
			return null;
		}
		
		//if specified type is List and compatible with ArrayList
		if(type.isAssignableFrom(ArrayList.class))
		{
			return ArrayList.class;
		}
		
		//if specified type is Set and compatible with HashSet
		if(type.isAssignableFrom(HashSet.class))
		{
			return HashSet.class;
		}
		
		try
		{
			//return same type if it is concrete and can be instantiated with default constructor
			type.getConstructor().newInstance();
			return type;
		}catch(Exception ex)
		{
		}
		
		throw new InvalidConfigurationException("Invalid/unsupported collection type used '{}' for field - {}.{}", 
				type, field.getDeclaringClass().getName(), field.getName());
	}
	
	public FieldDef getFieldDef(Class<?> modelType, Field field, Set<LovRef> requiredLovs)
	{
		String fqn = modelType.getName() + "." + field.getName();
		
		FieldDef fieldDef = new FieldDef(field);
		
		fieldDef.setName(field.getName());
		fieldDef.setLabel(defUtils.getLabel(field, field.getName(), fqn));
		fieldDef.setDescription(defUtils.getDescription(field, field.getName(), fqn));
		
		//fetch and set the default value if any
		DefaultValue defaultValue = field.getAnnotation(DefaultValue.class);
		
		if(defaultValue != null)
		{
			if(StringUtils.isNotBlank(defaultValue.value()))
			{
				fieldDef.setDefaultValue(defaultValue.value());
			}
			else if(StringUtils.isNotBlank(defaultValue.property()))
			{
				String value = defaultValuesMessageSource.getMessage(defaultValue.property(), null, "", null);
				
				if(StringUtils.isNotBlank(value))
				{
					fieldDef.setDefaultValue(value);
				}
			}
			else if(StringUtils.isNotBlank(defaultValue.resource()))
			{
				try
				{
					String value = IOUtils.toString(FieldDefBuilder.class.getResourceAsStream(defaultValue.resource()), Charset.defaultCharset());
	
					if(StringUtils.isNotBlank(value))
					{
						fieldDef.setDefaultValue(value);
					}
				}catch(Exception ex)
				{
					throw new InvalidStateException("An error occurred while fetching default value for field {} from configured resource: {}", 
							fqn, defaultValue.resource(), ex);
				}
			}
		}
		
		Class<?> fieldType = field.getType();
		Class<?> compatibleCollectionType = getCompatiableCollectionType(fieldType, field);
		
		if(compatibleCollectionType != null)
		{
			fieldDef.setMultiValued(true);
			fieldDef.setCompatibleCollectionType(compatibleCollectionType);

			ParameterizedType type = (ParameterizedType)field.getGenericType();
			
			if(type.getActualTypeArguments().length != 1)
			{
				throw new InvalidConfigurationException("Failed to determine collection type argument field - {}.{}", modelType.getName(), field.getName());
			}
			
			Type paramType = type.getActualTypeArguments()[0];
			
			if(!(paramType instanceof Class))
			{
				throw new InvalidConfigurationException("Failed to determine collection type argument field - {}.{}", modelType.getName(), field.getName());
			}
			
			fieldType = (Class<?>)paramType;
		}
		
		//if field is marked as dynamic LOV
		if(field.getAnnotation(LOV.class) != null)
		{
			getCustomLovDetails(modelType, fieldDef, field, requiredLovs);
		}
		//if field type is enum
		else if(field.getAnnotation(CustomType.class) != null)
		{
			fieldDef.setFieldType(FieldType.CUSTOM_TYPE);
		}
		else if(field.getAnnotation(DateTime.class) != null)
		{
			fieldDef.setFieldType(FieldType.DATE_TIME);
		}
		//if field type is enum
		else if(fieldType.isEnum())
		{
			getEnumLovDetails(fieldDef, fieldType);
		}
		//if it is a simple field
		else
		{
			FieldType dynFieldType = FieldType.getFieldType(fieldType);
			
			if(dynFieldType == null)
			{
				throw new InvalidStateException("Non supported data type '{}' found for model field - {}", fieldType.getName(), fqn);
			}
			
			//if field is string field, check if it needs to be multi-lined or simple string
			if(dynFieldType == FieldType.STRING)
			{
				if(field.getAnnotation(MultilineText.class) != null)
				{
					fieldDef.setFieldType(FieldType.MULTI_LINE_STRING);
				}
				else if(field.getAnnotation(Password.class) != null)
				{
					fieldDef.setFieldType(FieldType.PASSWORD);
				}
				else if(field.getAnnotation(Html.class) != null)
				{
					fieldDef.setFieldType(FieldType.HTML);
					fieldDef.setFullWidth(true);
				}
				else if(field.getAnnotation(Color.class) != null)
				{
					fieldDef.setFieldType(FieldType.COLOR);
				}
				else if(field.getAnnotation(NeedVerification.class) != null)
				{
					NeedVerification needVerification = field.getAnnotation(NeedVerification.class);
					
					fieldDef.setFieldType(FieldType.VERIFICATION);
					fieldDef.setVerificationType(needVerification.type().name());
					
					Field tokenField = null;
					
					try
					{
						tokenField = modelType.getDeclaredField(needVerification.tokenField());
					}catch(NoSuchFieldException ex)
					{
						throw new InvalidStateException("Invalid token-field '{}' specified by field: {}", needVerification.tokenField(), fqn);
					}
					
					if(!tokenField.getType().equals(String.class))
					{
						throw new InvalidStateException("Non-string token-field '{}' specified by field: {}", needVerification.tokenField(), fqn);
					}
					
					fieldDef.setVerificationTokenField(needVerification.tokenField());
				}
				else
				{
					fieldDef.setFieldType(FieldType.STRING);
				}
			}
			//else set the field type
			else
			{
				fieldDef.setFieldType(dynFieldType);
			}
		}
		
		//set other flags of field def
		fieldDef.setReadOnly( field.getAnnotation(ReadOnly.class) != null );
		
		NonDisplayable nonDisplayable = field.getAnnotation(NonDisplayable.class);
		fieldDef.setDisplayable(nonDisplayable == null);
		fieldDef.setBackend(nonDisplayable != null && nonDisplayable.backend());
		
		//if full width is not set because of data type, then check for FullWidth annotation
		if(!fieldDef.isFullWidth())
		{
			fieldDef.setFullWidth( field.getAnnotation(FullWidth.class) != null );
		}
		
		//set format if it is specified
		if(field.getAnnotation(Format.class) != null)
		{
			String format = field.getAnnotation(Format.class).value();
			
			if(StringUtils.isNotBlank(format))
			{
				fieldDef.setFormat(format);
			}
		}
		
		//fetch validation details
		Collection<ValidationDef> validations = validationDefBuilder.getValidations(field);

		//set validators to field-def if not empty
		if(CollectionUtils.isNotEmpty(validations))
		{
			fieldDef.setValidations(new ArrayList<>(validations));
		}
		
		return fieldDef;
	}
}
