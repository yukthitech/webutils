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

package com.yukthi.webutils.repository;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.yukthi.persistence.annotations.DeleteWithParent;
import com.yukthi.persistence.annotations.NotUpdateable;
import com.yukthi.persistence.annotations.UniqueConstraint;
import com.yukthi.persistence.annotations.UniqueConstraints;

/**
 * Represents extension field value entity.
 * 
 * @author akiran
 */
@Table(name = "EXTENSION_FIELD_VALUES")
@UniqueConstraints({
	@UniqueConstraint(name = "EXT_FLD_ENT_ID", fields = {"extensionField", "entityId"})
})
public class ExtensionFieldValueEntity extends WebutilsEntity
{
	/**
	 * Extension field for which value is being stored.
	 */
	@NotUpdateable
	@ManyToOne
	@Column(name = "EXT_FIELD_ID", nullable = false)
	@DeleteWithParent
	private ExtensionFieldEntity extensionField;

	/**
	 * entity id for which extended field value is being stored.
	 */
	@Column(name = "ENTITY_ID", nullable = false)
	private long entityId;

	/**
	 * Value of the field.
	 */
	@Column(name = "VALUE", length = 2000)
	private String value;

	/**
	 * Instantiates a new extension field value entity.
	 */
	public ExtensionFieldValueEntity()
	{}
	
	/**
	 * Instantiates a new extension field value entity.
	 *
	 * @param id the id
	 * @param extensionField the extension field
	 * @param entityId the entity id
	 * @param value the value
	 */
	public ExtensionFieldValueEntity(long id, ExtensionFieldEntity extensionField, long entityId, String value)
	{
		super(id);
		this.extensionField = extensionField;
		this.entityId = entityId;
		this.value = value;
	}
	/**
	 * Gets the extension field for which value is being stored.
	 *
	 * @return the extension field for which value is being stored
	 */
	public ExtensionFieldEntity getExtensionField()
	{
		return extensionField;
	}

	/**
	 * Sets the extension field for which value is being stored.
	 *
	 * @param extensionField the new extension field for which value is being stored
	 */
	public void setExtensionField(ExtensionFieldEntity extensionField)
	{
		this.extensionField = extensionField;
	}

	/**
	 * Gets the entity id for which extended field value is being stored.
	 *
	 * @return the entity id for which extended field value is being stored
	 */
	public long getEntityId()
	{
		return entityId;
	}

	/**
	 * Sets the entity id for which extended field value is being stored.
	 *
	 * @param entityId the new entity id for which extended field value is being stored
	 */
	public void setEntityId(long entityId)
	{
		this.entityId = entityId;
	}

	/**
	 * Gets the value of the field.
	 *
	 * @return the value of the field
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the value of the field.
	 *
	 * @param value the new value of the field
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Id: ").append(super.getId());
		builder.append(",").append("Ext Field: ").append(extensionField != null ? extensionField.getId() : null);
		builder.append(",").append("Entity: ").append(entityId);
		builder.append(",").append("Value: ").append(value);

		builder.append("]");
		return builder.toString();
	}
}
 