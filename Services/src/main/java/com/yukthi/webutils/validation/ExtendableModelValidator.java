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

package com.yukthi.webutils.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.yukthi.webutils.WebutilsConfiguration;
import com.yukthi.webutils.common.IExtendableModel;
import com.yukthi.webutils.common.annotations.ExtendableModel;
import com.yukthi.webutils.common.extensions.FieldConfiguration;
import com.yukthi.webutils.controllers.IExtensionContextProvider;
import com.yukthi.webutils.repository.ExtensionEntity;
import com.yukthi.webutils.repository.ExtensionFieldEntity;
import com.yukthi.webutils.services.ExtensionService;

/**
 * Validator to validate extended field values
 * @author akiran
 */
@Component
public class ExtendableModelValidator implements Validator
{
	@Autowired
	private ExtensionService extensionService;
	
	@Autowired
	private WebutilsConfiguration webutilsConfiguration;
	
	@Autowired
	private IExtensionContextProvider extensionContextProvider;
	
	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz)
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors)
	{
		ExtendableModel extendableModel = target.getClass().getAnnotation(ExtendableModel.class);
		
		if(extendableModel == null)
		{
			return;
		}
		
		String extensionName = extensionContextProvider.getExtensionName(target);
		
		if(extensionName == null)
		{
			extensionName = extendableModel.name();
		}
		
		ExtensionEntity extensionEntity = extensionService.getExtensionEntity(extensionName);
		
		if(extensionEntity == null)
		{
			return;
		}
		
		List<ExtensionFieldEntity> extendedFields = extensionService.getExtensionFields(extensionName);
		Map<String, String> extendedFieldValues = new HashMap<>( ((IExtendableModel) target).getExtendedFields() );
		String value = null;
		FieldConfiguration fieldConfig = null;
		
		//loop through the fields and validate the values
		for(ExtensionFieldEntity field : extendedFields)
		{
			value = extendedFieldValues.remove(field.getName());
			
			//ensure values are provided for all mandatory fields
			if(field.isRequired())
			{
				if(StringUtils.isBlank(value))
				{
					errors.reject("extended.required.field.missing", new String[]{field.getName()}, "No value specified for mandatory field - " + field.getName());
					continue;
				}
			}
			
			//ignore null and blank values
			if(StringUtils.isBlank(value))
			{
				continue;
			}
			
			fieldConfig = new FieldConfiguration(webutilsConfiguration.getDateFormat(), field.getLovValues(), field.getMaxLength());
			
			//ensure proper value provided according to data type
			if( !field.getType().validateValue(value, fieldConfig) )
			{
				errors.reject("extended.invalid.value", new String[]{field.getName()}, "Invalid value specified for field - " + field.getName());
			}
		}
		
		if(!extendedFieldValues.isEmpty())
		{
			errors.reject("extended.extra.field", "Extra fields specified for extension");
		}
	}
}
