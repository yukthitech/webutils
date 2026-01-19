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

package com.webutils.common.form.model;

import java.lang.reflect.Field;
import java.util.List;

import com.webutils.common.form.otp.VerificationType;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Provides details about model field, that can be used by clients for dynamic ui
 * rendering and dynamic validations.
 * 
 * @author akiran
 */
@Data
@NoArgsConstructor
public class FieldDef
{
	/**
	 * Id of the field, in the cases where this field represents extension field.
	 */
	private String id;
	
	/**
	 * Name of the model.
	 */
	private String name;
	
	/**
	 * Label for user display.
	 */
	private String label;
	
	/**
	 * Description for user display.
	 */
	private String description;
	
	/**
	 * Data type of current field.
	 */
	private FieldType fieldType;
	
	/**
	 * Validations on this field.
	 */
	private List<ValidationDef> validations;

	/**
	 * Lov details, if this field type is LOV.
	 */
	private LovDetails lovDetails;
	
	/**
	 * Default value for this field.
	 */
	private String defaultValue;
	
	/**
	 * if this is read only field. Like id of the entity.
	 */
	private boolean readOnly;
	
	/**
	 * if this is displayable field. For example, password is not displayable.
	 */
	private boolean displayable;
	
	/**
	 * Indicates this field will be needed for backend processing.
	 */
	private boolean backend;
	
	/**
	 * Indicates this field holds multiple values.
	 */
	private boolean multiValued;
	
	/**
	 * Compatible collection type that can be used for field value population.
	 */
	private Class<?> compatibleCollectionType;
	
	/**
	 * Java fields, which can be used to get further annotation on field.
	 */
	private Field field;
	
	/**
	 * Indicates that current field needs to occupy full width.
	 */
	private boolean fullWidth;
	
	/**
	 * Format to be used for this field.
	 */
	private String format;
	
	/**
	 * Type of verification needed by field.
	 */
	private VerificationType verificationType;
	
	/**
	 * Instantiates a new field def.
	 *
	 * @param field the field
	 */
	public FieldDef(Field field)
	{
		this.field = field;
	}
}
