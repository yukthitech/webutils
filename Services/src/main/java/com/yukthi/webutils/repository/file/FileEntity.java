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

package com.yukthi.webutils.repository.file;

import java.io.File;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.yukthi.persistence.annotations.DataType;
import com.yukthi.persistence.annotations.DataTypeMapping;
import com.yukthi.persistence.annotations.Indexed;
import com.yukthi.persistence.annotations.NotUpdateable;
import com.yukthi.webutils.repository.ITrackedEntity;
import com.yukthi.webutils.repository.UserEntity;

/**
 * Entity to store files
 * 
 * @author akiran
 */
@Table(name = "FILE_ENTITY")
public class FileEntity implements ITrackedEntity
{
	/**
	 * Primary key of the entity
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	
	/**
	 * Version of the entity
	 */
	@Column(name = "VERSION")
	@Version
	private Integer version;

	/**
	 * Name of the file
	 */
	@Column(name = "FILE_NAME", nullable = false, length = 500)
	private String fileName;

	/**
	 * File content
	 */
	@DataTypeMapping(type = DataType.BLOB)
	@Column(name = "FILE", nullable = false)
	private File file;
	
	/**
	 * Size of the file in MB
	 */
	@Column(name = "SIZE_IN_MB", nullable = false)
	private long sizeInMb;
	
	/**
	 * Content type of the file
	 */
	@Column(name = "CONTENT_TYPE", length = 200)
	private String contentType;
	
	/**
	 * Custom attribute that can be used by applications to set application
	 * specific custom data
	 */
	@Indexed
	@Column(name = "CUSTOM_ATT1", length = 100)
	private String customAttribute1;
	
	/**
	 * Custom attribute that can be used by applications to set application
	 * specific custom data
	 */
	@Indexed
	@Column(name = "CUSTOM_ATT2", length = 100)
	private String customAttribute2;
	
	/**
	 * Custom attribute that can be used by applications to set application
	 * specific custom data
	 */
	@Indexed
	@Column(name = "CUSTOM_ATT3", length = 100)
	private String customAttribute3;
	
	/**
	 * Custom attribute that can be used by applications to set application
	 * specific custom data
	 */
	@Column(name = "CUSTOM_ATT4", length = 100)
	private String customAttribute4;
	
	/**
	 * Custom attribute that can be used by applications to set application
	 * specific custom data
	 */
	@Column(name = "CUSTOM_ATT5", length = 100)
	private String customAttribute5;
	
	/**
	 * Created by user
	 */
	@NotUpdateable
	@ManyToOne
	@Column(name = "CREATED_BY_ID")
	private UserEntity createdBy;
	
	/**
	 * Created on time
	 */
	@NotUpdateable
	@Column(name = "CREATED_ON")
	@DataTypeMapping(type = DataType.DATE_TIME)
	private Date createdOn;

	/**
	 * Updating user
	 */
	@ManyToOne
	@Column(name = "UPDATED_BY_ID")
	private UserEntity updatedBy;
	
	/**
	 * Updated on
	 */
	@Column(name = "UPDATED_ON")
	@DataTypeMapping(type = DataType.DATE_TIME)
	private Date updatedOn;
	
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
		this.id = id;
	}

	/**
	 * Gets the primary key of the entity.
	 *
	 * @return the primary key of the entity
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the primary key of the entity.
	 *
	 * @param id the new primary key of the entity
	 */
	public void setId(Long id)
	{
		this.id = id;
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
	 * Gets the version of the entity.
	 *
	 * @return the version of the entity
	 */
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.IEntity#getVersion()
	 */
	public Integer getVersion()
	{
		return version;
	}

	/**
	 * Sets the version of the entity.
	 *
	 * @param version the new version of the entity
	 */
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.IEntity#setVersion(java.lang.Integer)
	 */
	public void setVersion(Integer version)
	{
		this.version = version;
	}

	/**
	 * Gets the created by user.
	 *
	 * @return the created by user
	 */
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#getCreatedBy()
	 */
	public UserEntity getCreatedBy()
	{
		return createdBy;
	}

	/**
	 * Sets the created by user.
	 *
	 * @param createdBy the new created by user
	 */
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#setCreatedBy(com.yukthi.webutils.repository.UserEntity)
	 */
	public void setCreatedBy(UserEntity createdBy)
	{
		this.createdBy = createdBy;
	}

	/**
	 * Gets the created on time.
	 *
	 * @return the created on time
	 */
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#getCreatedOn()
	 */
	public Date getCreatedOn()
	{
		return createdOn;
	}

	/**
	 * Sets the created on time.
	 *
	 * @param createdOn the new created on time
	 */
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#setCreatedOn(java.util.Date)
	 */
	public void setCreatedOn(Date createdOn)
	{
		this.createdOn = createdOn;
	}

	/**
	 * Gets the updating user.
	 *
	 * @return the updating user
	 */
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#getUpdatedBy()
	 */
	public UserEntity getUpdatedBy()
	{
		return updatedBy;
	}

	/**
	 * Sets the updating user.
	 *
	 * @param updatedBy the new updating user
	 */
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#setUpdatedBy(com.yukthi.webutils.repository.UserEntity)
	 */
	public void setUpdatedBy(UserEntity updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	/**
	 * Gets the updated on.
	 *
	 * @return the updated on
	 */
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#getUpdatedOn()
	 */
	public Date getUpdatedOn()
	{
		return updatedOn;
	}

	/**
	 * Sets the updated on.
	 *
	 * @param updatedOn the new updated on
	 */
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#setUpdatedOn(java.util.Date)
	 */
	public void setUpdatedOn(Date updatedOn)
	{
		this.updatedOn = updatedOn;
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
