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

package com.yukthi.webutils;

import java.io.File;

/**
 * File details used to store files
 * 
 * @author akiran
 */
public class FileDetails
{
	/**
	 * Name of the file
	 */
	private String fileName;
	
	/**
	 * File content
	 */
	private File file;
	
	/**
	 * Http content type of the file
	 */
	private String contentType;

	/**
	 * Type of entity using this file
	 */
	private String usedByEntityType;

	/**
	 * Id of entity using this file
	 */
	private long usedByEntityId;

	/**
	 * Instantiates a new file entity.
	 */
	public FileDetails()
	{}
	
	/**
	 * Instantiates a new file model.
	 *
	 * @param fileName the file name
	 * @param file the file
	 * @param contentType the content type
	 * @param usedByEntityType the used by entity type
	 * @param usedByEntityId the used by entity id
	 */
	public FileDetails(String fileName, File file, String contentType, String usedByEntityType, long usedByEntityId)
	{
		this.fileName = fileName;
		this.file = file;
		this.contentType = contentType;
		this.usedByEntityType = usedByEntityType;
		this.usedByEntityId = usedByEntityId;
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
