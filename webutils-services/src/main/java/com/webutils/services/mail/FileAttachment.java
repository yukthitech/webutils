/*
 * The MIT License (MIT)
 * Copyright (c) 2016 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

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

package com.webutils.services.mail;

import java.io.File;

/**
 * Represents email file attachment.
 * @author akiran
 */
public class FileAttachment
{
	/**
	 * File to be attachment.
	 */
	private File file;
	
	/**
	 * Name of the file in email.
	 */
	private String fileName;

	/**
	 * Instantiates a new file attachment.
	 *
	 * @param file the file
	 * @param fileName the file name
	 */
	public FileAttachment(File file, String fileName)
	{
		if(file == null)
		{
			throw new NullPointerException("File can not be null");
		}
		
		this.file = file;
		
		if(fileName == null || fileName.trim().length() == 0)
		{
			this.fileName = file.getName();
		}
		else
		{
			this.fileName = fileName; 
		}
	}

	/**
	 * Instantiates a new file attachment.
	 *
	 * @param file the file
	 */
	public FileAttachment(File file)
	{
		this(file, null);
	}

	/**
	 * Gets the file to be attachment.
	 *
	 * @return the file to be attachment
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * Gets the name of the file in email.
	 *
	 * @return the name of the file in email
	 */
	public String getFileName()
	{
		return fileName;
	}
}
