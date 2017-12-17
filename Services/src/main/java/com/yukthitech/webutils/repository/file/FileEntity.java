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

package com.yukthitech.webutils.repository.file;

import java.io.File;

import javax.persistence.Column;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.Index;
import com.yukthitech.persistence.annotations.Indexed;
import com.yukthitech.persistence.annotations.Indexes;
import com.yukthitech.webutils.repository.WebutilsEntity;

/**
 * Entity to store files.
 * 
 * @author akiran
 */
@Indexes({
	@Index(name = "FILE_OWNER_IDX", fields = {"ownerEntityType", "ownerEntityId"})
	})
@Table(name = "WEBUTILS_FILE_ENTITY")
public class FileEntity extends WebutilsEntity
{
	/**
	 * Name of the file.
	 */
	@Column(name = "FILE_NAME", nullable = false, length = 500)
	private String fileName;

	/**
	 * File content.
	 */
	@DataTypeMapping(type = DataType.ZIP_BLOB)
	@Column(name = "FILE", nullable = false)
	private File file;
	
	/**
	 * Size of the file in MB.
	 */
	@Column(name = "SIZE_IN_MB", nullable = false)
	private long sizeInMb;
	
	/**
	 * Content type of the file.
	 */
	@Column(name = "CONTENT_TYPE", length = 200)
	private String contentType;
	
	/**
	 * Owner entity type.
	 */
	@Column(name = "OWNER_ENTITY_TYPE", length = 250, nullable = false)
	private String ownerEntityType;

	/**
	 * Owner entity field name.
	 */
	@Column(name = "OWNER_ENTITY_FIELD", length = 200, nullable = false)
	private String ownerEntityField;
	
	/**
	 * Owner entity id.
	 */
	@Column(name = "OWNER_ENTITY_ID", nullable = false)
	private Long ownerEntityId;

	/**
	 * Custom attribute that can be used by applications to set application
	 * specific custom data.
	 */
	@Indexed
	@Column(name = "CUSTOM_ATT1", length = 100)
	private String customAttribute1;
	
	/**
	 * Custom attribute that can be used by applications to set application.
	 * specific custom data
	 */
	@Indexed
	@Column(name = "CUSTOM_ATT2", length = 100)
	private String customAttribute2;
	
	/**
	 * Custom attribute that can be used by applications to set application
	 * specific custom data.
	 */
	@Indexed
	@Column(name = "CUSTOM_ATT3", length = 100)
	private String customAttribute3;
	
	/**
	 * Custom attribute that can be used by applications to set application
	 * specific custom data.
	 */
	@Column(name = "CUSTOM_ATT4", length = 100)
	private String customAttribute4;
	
	/**
	 * Custom attribute that can be used by applications to set application
	 * specific custom data.
	 */
	@Column(name = "CUSTOM_ATT5", length = 100)
	private String customAttribute5;
	
	/**
	 * Instantiates a new file entity.
	 */
	public FileEntity()
	{}
	
	/**
	 * Instantiates a new file entity.
	 *
	 * @param id the id
	 */
	public FileEntity(long id)
	{
		super(id);
	}

	/**
	 * Gets the name of the file.
	 *
	 * @return the name of the file
	 */
	public String getFileName()
	{
		return fileName;
	}

	/**
	 * Sets the name of the file.
	 *
	 * @param fileName the new name of the file
	 */
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	/**
	 * Gets the file content.
	 *
	 * @return the file content
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * Sets the file content.
	 *
	 * @param file the new file content
	 */
	public void setFile(File file)
	{
		this.file = file;
	}

	/**
	 * Gets the size of the file in MB.
	 *
	 * @return the size of the file in MB
	 */
	public long getSizeInMb()
	{
		return sizeInMb;
	}

	/**
	 * Sets the size of the file in MB.
	 *
	 * @param sizeInMb the new size of the file in MB
	 */
	public void setSizeInMb(long sizeInMb)
	{
		this.sizeInMb = sizeInMb;
	}

	/**
	 * Gets the content type of the file.
	 *
	 * @return the content type of the file
	 */
	public String getContentType()
	{
		return contentType;
	}

	/**
	 * Sets the content type of the file.
	 *
	 * @param contentType the new content type of the file
	 */
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}
	
	/**
	 * Gets the owner entity type.
	 *
	 * @return the owner entity type
	 */
	public String getOwnerEntityType()
	{
		return ownerEntityType;
	}

	/**
	 * Sets the owner entity type.
	 *
	 * @param ownerEntityType the new owner entity type
	 */
	public void setOwnerEntityType(String ownerEntityType)
	{
		this.ownerEntityType = ownerEntityType;
	}

	/**
	 * Gets the owner entity type with field name.
	 *
	 * @return the owner entity type with field name
	 */
	public String getOwnerEntityField()
	{
		return ownerEntityField;
	}

	/**
	 * Sets the owner entity type with field name.
	 *
	 * @param ownerEntityField the new owner entity type with field name
	 */
	public void setOwnerEntityField(String ownerEntityField)
	{
		this.ownerEntityField = ownerEntityField;
	}

	/**
	 * Gets the owner entity id.
	 *
	 * @return the owner entity id
	 */
	public Long getOwnerEntityId()
	{
		return ownerEntityId;
	}

	/**
	 * Sets the owner entity id.
	 *
	 * @param ownerEntityId the new owner entity id
	 */
	public void setOwnerEntityId(Long ownerEntityId)
	{
		this.ownerEntityId = ownerEntityId;
	}

	/**
	 * Gets the custom attribute that can be used by applications to set application specific custom data.
	 *
	 * @return the custom attribute that can be used by applications to set application specific custom data
	 */
	public String getCustomAttribute1()
	{
		return customAttribute1;
	}

	/**
	 * Sets the custom attribute that can be used by applications to set application specific custom data.
	 *
	 * @param customAttribute1 the new custom attribute that can be used by applications to set application specific custom data
	 */
	public void setCustomAttribute1(String customAttribute1)
	{
		this.customAttribute1 = customAttribute1;
	}

	/**
	 * Gets the custom attribute that can be used by applications to set application specific custom data.
	 *
	 * @return the custom attribute that can be used by applications to set application specific custom data
	 */
	public String getCustomAttribute2()
	{
		return customAttribute2;
	}

	/**
	 * Sets the custom attribute that can be used by applications to set application specific custom data.
	 *
	 * @param customAttribute2 the new custom attribute that can be used by applications to set application specific custom data
	 */
	public void setCustomAttribute2(String customAttribute2)
	{
		this.customAttribute2 = customAttribute2;
	}

	/**
	 * Gets the custom attribute that can be used by applications to set application specific custom data.
	 *
	 * @return the custom attribute that can be used by applications to set application specific custom data
	 */
	public String getCustomAttribute3()
	{
		return customAttribute3;
	}

	/**
	 * Sets the custom attribute that can be used by applications to set application specific custom data.
	 *
	 * @param customAttribute3 the new custom attribute that can be used by applications to set application specific custom data
	 */
	public void setCustomAttribute3(String customAttribute3)
	{
		this.customAttribute3 = customAttribute3;
	}

	/**
	 * Gets the custom attribute that can be used by applications to set application specific custom data.
	 *
	 * @return the custom attribute that can be used by applications to set application specific custom data
	 */
	public String getCustomAttribute4()
	{
		return customAttribute4;
	}

	/**
	 * Sets the custom attribute that can be used by applications to set application specific custom data.
	 *
	 * @param customAttribute4 the new custom attribute that can be used by applications to set application specific custom data
	 */
	public void setCustomAttribute4(String customAttribute4)
	{
		this.customAttribute4 = customAttribute4;
	}

	/**
	 * Gets the custom attribute that can be used by applications to set application specific custom data.
	 *
	 * @return the custom attribute that can be used by applications to set application specific custom data
	 */
	public String getCustomAttribute5()
	{
		return customAttribute5;
	}

	/**
	 * Sets the custom attribute that can be used by applications to set application specific custom data.
	 *
	 * @param customAttribute5 the new custom attribute that can be used by applications to set application specific custom data
	 */
	public void setCustomAttribute5(String customAttribute5)
	{
		this.customAttribute5 = customAttribute5;
	}
}
