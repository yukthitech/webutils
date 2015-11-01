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

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents validation configuration which helps in mapping validation annotation to dynamic validation
 * configuration.
 * @author akiran
 */
public class ValidationConfigDetails
{
	/**
	 * Annotation type for which this configuration is being defined
	 */
	private Class<? extends Annotation> type;
	
	/**
	 * Target field data type for which this configuration is eligible
	 */
	private Class<?> targetType;
	
	/**
	 * Annotation attribute details that needs to be included in dynamic configuration
	 */
	private Set<String> valueAttrs = new HashSet<>();
	
	/**
	 * Client side name of the validation
	 */
	private String name;

	/**
	 * Gets the annotation type for which this configuration is being defined.
	 *
	 * @return the annotation type for which this configuration is being defined
	 */
	public Class<? extends Annotation> getType()
	{
		return type;
	}

	/**
	 * Sets the annotation type for which this configuration is being defined.
	 *
	 * @param type the new annotation type for which this configuration is being defined
	 */
	public void setType(Class<? extends Annotation> type)
	{
		this.type = type;
	}

	/**
	 * Gets the target field data type for which this configuration is eligible.
	 *
	 * @return the target field data type for which this configuration is eligible
	 */
	public Class<?> getTargetType()
	{
		return targetType;
	}

	/**
	 * Sets the target field data type for which this configuration is eligible.
	 *
	 * @param targetType the new target field data type for which this configuration is eligible
	 */
	public void setTargetType(Class<?> targetType)
	{
		this.targetType = targetType;
	}

	/**
	 * Sets the attributes.
	 *
	 * @param valueAttr the new attributes
	 */
	public void setAttributes(String valueAttr)
	{
		String attrLst[] = valueAttr.split("\\s*\\,\\s*");
		
		for(String attr: attrLst)
		{
			valueAttrs.add(attr);
		}
	}

	/**
	 * Gets the annotation attribute details that needs to be included in dynamic configuration.
	 *
	 * @return the annotation attribute details that needs to be included in dynamic configuration
	 */
	public Set<String> getValueAttrs()
	{
		return valueAttrs;
	}

	/**
	 * Sets the annotation attribute details that needs to be included in dynamic configuration.
	 *
	 * @param valueAttrs the new annotation attribute details that needs to be included in dynamic configuration
	 */
	public void setValueAttrs(Set<String> valueAttrs)
	{
		this.valueAttrs = valueAttrs;
	}

	/**
	 * Gets the client side name of the validation.
	 *
	 * @return the client side name of the validation
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the client side name of the validation.
	 *
	 * @param simpleName the new client side name of the validation
	 */
	public void setName(String simpleName)
	{
		this.name = simpleName;
	}

}
