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

package com.yukthi.webutils.services.def;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.common.LovType;
import com.yukthi.webutils.common.annotations.DefaultValue;
import com.yukthi.webutils.common.annotations.LOV;
import com.yukthi.webutils.common.annotations.MultilineText;
import com.yukthi.webutils.common.annotations.NonDisplayable;
import com.yukthi.webutils.common.annotations.ReadOnly;
import com.yukthi.webutils.common.models.def.FieldDef;
import com.yukthi.webutils.common.models.def.FieldType;
import com.yukthi.webutils.common.models.def.LovDetails;
import com.yukthi.webutils.common.models.def.ValidationDef;
import com.yukthi.webutils.services.LovService;

/**
 * Builder for field definitions
 * @author akiran
 */
@Component
public class FieldDefBuilder
{
	/**
	 * Common def utils
	 */
	@Autowired
	private DefUtils defUtils;
	
	/**
	 * Lov service to check validity of LOV definition in fields
	 */
	@Autowired
	private LovService lovService;
	
	/**
	 * Builder to build field validations
	 */
	@Autowired
	private ValidationDefBuilder validationDefBuilder;
	
	/**
	 * Used to LOV details on field-def, whose type is an enum
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
	 * Gets LOV field details for field which is marked as LOV field 
	 * @param fieldDef
	 * @param field
	 * @param parentCls
	 */
	private void getCustomLovDetails(Class<?> modelType, FieldDef fieldDef, Field field)
	{
		LOV lovAnnotation = field.getAnnotation(LOV.class);
		
		fieldDef.setFieldType(FieldType.LIST_OF_VALUES);
		
		LovDetails lovDetails = new LovDetails();
		lovDetails.setLovType(LovType.DYNAMIC_TYPE);
		
		//ensure valid lov name is specified
		if(!lovService.isValidDynamicLov(lovAnnotation.name()))
		{
			throw new InvalidStateException("Invalid lov name '{}' specified on field {}.{}", lovAnnotation.name(), modelType.getName(), field.getName());
		}
		
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

		fieldDef.setLovDetails(lovDetails);
	}

	
	public FieldDef getFieldDef(Class<?> modelType, Field field)
	{
		String fqn = modelType.getName() + "." + field.getName();
		
		FieldDef fieldDef = new FieldDef();
		
		fieldDef.setName(field.getName());
		fieldDef.setLabel(defUtils.getLabel(field, field.getName(), fqn));
		fieldDef.setDescription(defUtils.getDescription(field, field.getName(), fqn));
		
		//fetch and set the default value if any
		DefaultValue defaultValue = field.getAnnotation(DefaultValue.class);
		
		if(defaultValue != null)
		{
			fieldDef.setDefaultValue(defaultValue.value());
		}
		
		Class<?> fieldType = field.getType();
		
		//if field type is enum
		if(fieldType.isEnum())
		{
			getEnumLovDetails(fieldDef, fieldType);
		}
		//if field is marked as dynamic LOV
		else if(field.getAnnotation(LOV.class) != null)
		{
			getCustomLovDetails(modelType, fieldDef, field);
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
		fieldDef.setDisplayable( field.getAnnotation(NonDisplayable.class) == null );
		
		//fetch validation details
		Collection<ValidationDef> validations = validationDefBuilder.getValidations(modelType, field);

		//set validators to field-def if not empty
		if(CollectionUtils.isNotEmpty(validations))
		{
			fieldDef.setValidations(new ArrayList<>(validations));
		}
		
		return fieldDef;
	}
}
