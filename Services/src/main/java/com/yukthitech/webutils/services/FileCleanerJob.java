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

package com.yukthitech.webutils.services;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.webutils.repository.file.IFileRepository;
import com.yukthitech.webutils.services.task.ScheduledTask;

import jakarta.annotation.PostConstruct;

/**
 * Job to delete temporary files. Expected to run once every day.
 * @author akiran
 */
@Component
public class FileCleanerJob
{
	private static Logger logger = LogManager.getLogger(FileCleanerJob.class);
	
	/**
	 * 12 hours time.
	 */
	private static final int HOUR_12 = 12;
	
	/**
	 * Repository factory to get file repository.
	 */
	@Autowired
	private WebutilsRepositoryFactory repositoryFactory;
	
	/**
	 * File repository to delete temp files.
	 */
	private IFileRepository fileRepository;
	
	/**
	 * Post constructor to get file repository.
	 */
	@PostConstruct
	private void init()
	{
		fileRepository = repositoryFactory.getRepository(IFileRepository.class);
	}
	
	@ScheduledTask(time = "01:00 am")
	public void deleteTempFiles()
	{
		logger.debug("Deleting temporary files");
		
		//delete temp files which are 12 hours older or greater
		Date createdAfter = new Date();
		createdAfter = DateUtils.addHours(createdAfter, -HOUR_12);
		
		fileRepository.deleteTempFiles(createdAfter);
	}
}
