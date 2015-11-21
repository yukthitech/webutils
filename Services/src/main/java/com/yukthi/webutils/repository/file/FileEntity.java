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
import javax.persistence.Table;

import com.yukthi.persistence.annotations.DataType;
import com.yukthi.persistence.annotations.DataTypeMapping;
import com.yukthi.webutils.repository.ITrackedEntity;

/**
 * Entity to store files
 * 
 * @author akiran
 */
@Table(name = "ENTITY_EXTENSIONS")
public class FileEntity implements ITrackedEntity
{
	/**
	 * Primary key of the entity
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;

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
	@Column(name = "CONTENT_TYPE")
	private String contentType;
	
	/**
	 * Type of entity using this file
	 */
	@Column(name = "USED_BY_ENTITY_TYPE", nullable = false, length = 500)
	private String usedByEntityType;

	/**
	 * Id of entity using this file
	 */
	@Column(name = "USED_BY_ENTITY_ID", nullable = false)
	private long usedByEntityId;

	/**
	 * Created on date
	 */
	@Column(name = "CREATED_ON")
	private Date createdOn = new Date();
	
	/**
	 * Created By
	 */
	@Column(name = "CREATED_BY")
	private long createdBy;

	/**
	 * Gets the primary key of the entity.
	 *
	 * @return the primary key of the entity
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * Sets the primary key of the entity.
	 *
	 * @param id the new primary key of the entity
	 */
	public void setId(long id)
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

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#getCreatedOn()
	 */
	public Date getCreatedOn()
	{
		return createdOn;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#setCreatedOn(java.util.Date)
	 */
	public void setCreatedOn(Date createdOn)
	{
		this.createdOn = createdOn;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#getCreatedBy()
	 */
	public long getCreatedBy()
	{
		return createdBy;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#setCreatedBy(long)
	 */
	public void setCreatedBy(long createdBy)
	{
		this.createdBy = createdBy;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#getUpdatedOn()
	 */
	public Date getUpdatedOn()
	{
		return createdOn;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#setUpdatedOn(java.util.Date)
	 */
	public void setUpdatedOn(Date updatedOn)
	{
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#getUpdatedBy()
	 */
	public long getUpdatedBy()
	{
		return createdBy;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#setUpdatedBy(long)
	 */
	public void setUpdatedBy(long updatedBy)
	{
	}

	/**
	 * Gets the type of entity using this file.
	 *
	 * @return the type of entity using this file
	 */
	public String getUsedByEntityType()
	{
		return usedByEntityType;
	}

	/**
	 * Sets the type of entity using this file.
	 *
	 * @param usedByEntityType the new type of entity using this file
	 */
	public void setUsedByEntityType(String usedByEntityType)
	{
		this.usedByEntityType = usedByEntityType;
	}

	/**
	 * Gets the id of entity using this file.
	 *
	 * @return the id of entity using this file
	 */
	public long getUsedByEntityId()
	{
		return usedByEntityId;
	}

	/**
	 * Sets the id of entity using this file.
	 *
	 * @param usedByEntityId the new id of entity using this file
	 */
	public void setUsedByEntityId(long usedByEntityId)
	{
		this.usedByEntityId = usedByEntityId;
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

	
}
