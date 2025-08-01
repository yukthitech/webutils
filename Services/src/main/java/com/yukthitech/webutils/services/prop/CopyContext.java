package com.yukthitech.webutils.services.prop;

import com.yukthitech.utils.PropertyAccessor.Property;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CopyContext
{
	/**
	 * Source pojo from where properties are being copied.
	 */
	private Object source;
	
	/**
	 * Target pojo to where properties are being copied.
	 */
	private Object target;
	
	/**
	 * Source field from where copy has to be done.
	 */
	private Property sourceField;
	
	/**
	 * Target field where value has to be copied.
	 */
	private Property targetField;
	
	private Class<?> sourceElementType;
	
	private Class<?> targetElementType;

	public CopyContext(Object source, Object target, Property sourceField, Property targetField)
	{
		this.source = source;
		this.target = target;
		this.sourceField = sourceField;
		this.targetField = targetField;
	}

	public CopyContext(CopyContext parent, Class<?> sourceElementType, Class<?> targetElementType)
	{
		this(parent.source, parent.target, parent.sourceField, parent.targetField);
		
		this.sourceElementType = sourceElementType;
		this.targetElementType = targetElementType;
	}
	
	public Class<?> getSourceType()
	{
		return (sourceElementType == null) ? sourceField.getType() : sourceElementType;
	}

	public Class<?> getTargetType()
	{
		return (targetElementType == null) ? targetField.getType() : targetElementType;
	}
}
