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

package com.yukthitech.webutils.common;

import java.io.File;

import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.utils.annotations.IgnorePropertyDestination;
import com.yukthitech.webutils.common.annotations.NonDisplayable;

/**
 * File details used to store files.
 * 
 * @author akiran
 */
public class FileInfo
{
	/**
	 * Id of the corresponding file entity
	 */
	@Field("id")
	private Long id;
	
	/**
	 * Version of the corresponding file entity
	 */
	@Field("version")
	private Integer version;
	
	/**
	 * Name of the file
	 */
	@Field("fileName")
	private String fileName;
	
	/**
	 * File content
	 */
	@IgnorePropertyDestination
	private File file;

	/**
	 * Size of the file in mb
	 */
	@Field("sizeInMb")
	private long sizeInMb;
	
	/**
	 * Http content type of the file
	 */
	@Field("contentType")
	private String contentType;

	/**
	 * Indicates if this is secured file or not
	 */
	@NonDisplayable
	private boolean secured = true;
	
	/**
	 * Instantiates a new file entity.
	 */
	public FileInfo()
	{}
	
	/**
	 * Instantiates a new file model.
	 *
	 * @param fileName the file name
	 * @param file the file
	 * @param contentType the content type
	 */
	public FileInfo(String fileName, File file, String contentType)
	{
		this.fileName = fileName;
		this.file = file;
		this.contentType = contentType;
	}
	
	/**
	 * Gets the id of the corresponding file entity.
	 *
	 * @return the id of the corresponding file entity
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the id of the corresponding file entity.
	 *
	 * @param id the new id of the corresponding file entity
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the version of the corresponding file entity.
	 *
	 * @return the version of the corresponding file entity
	 */
	public Integer getVersion()
	{
		return version;
	}

	/**
	 * Sets the version of the corresponding file entity.
	 *
	 * @param version the new version of the corresponding file entity
	 */
	public void setVersion(Integer version)
	{
		this.version = version;
	}

	/**
	 * Gets the size of the file in mb.
	 *
	 * @return the size of the file in mb
	 */
	public long getSizeInMb()
	{
		return sizeInMb;
	}

	/**
	 * Sets the size of the file in mb.
	 *
	 * @param sizeInMb the new size of the file in mb
	 */
	public void setSizeInMb(long sizeInMb)
	{
		this.sizeInMb = sizeInMb;
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
	 * Gets the http content type of the file.
	 *
	 * @return the http content type of the file
	 */
	public String getContentType()
	{
		return contentType;
	}

	/**
	 * Sets the http content type of the file.
	 *
	 * @param contentType the new http content type of the file
	 */
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}
	
	

	/**
	 * Checks if is indicates if this is secured file or not.
	 *
	 * @return the indicates if this is secured file or not
	 */
	public boolean isSecured()
	{
		return secured;
	}

	/**
	 * Sets the indicates if this is secured file or not.
	 *
	 * @param secured the new indicates if this is secured file or not
	 */
	public void setSecured(boolean secured)
	{
		this.secured = secured;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("File Name: ").append(fileName);
		builder.append(",").append("File: ").append(file.getPath());

		builder.append("]");
		return builder.toString();
	}

}
