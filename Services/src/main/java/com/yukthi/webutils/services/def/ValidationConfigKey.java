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

/**
 * Represents key that can be used to uniquely identify validation configuration
 * @author akiran
 */
public class ValidationConfigKey
{
	/**
	 * Annotation type for which configuration needs to be found
	 */
	private Class<? extends Annotation> annotationType;
	
	/**
	 * Field target type for which validation should be elgible
	 */
	private Class<?> targetType;
	
	/**
	 * if this is actual configuration or built for searching. Which in turn is used in equals method, to take care
	 * of matching the configuration for assignable types
	 */
	private boolean actualConfiguration;
	
	public ValidationConfigKey(Class<? extends Annotation> annotationType, Class<?> targetType, boolean actualConfiguration)
	{
		this.annotationType = annotationType;
		this.targetType = targetType;
		this.actualConfiguration = actualConfiguration;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}

		if(!(obj instanceof ValidationConfigKey))
		{
			return false;
		}
		
		ValidationConfigKey actual = null, other = null;
		
		if(actualConfiguration)
		{
			actual = this;
			other = (ValidationConfigKey)obj;
		}
		else
		{
			actual = (ValidationConfigKey)obj;
			other = this;
		}
		
		return (actual.annotationType.equals(other.annotationType) && (actual.targetType.isAssignableFrom(other.targetType)));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		/*
		 * target type is not considered in hash code. Reason is two ResolverKeys are considered equal
		 * even if the target types are compatible (they need not be equal).
		 */
		return annotationType.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Annotation: ").append(annotationType.getSimpleName());
		builder.append(",").append("Target: ").append(targetType.getSimpleName());
		builder.append(",").append("Actual: ").append(actualConfiguration);

		builder.append("]");
		return builder.toString();
	}
}
