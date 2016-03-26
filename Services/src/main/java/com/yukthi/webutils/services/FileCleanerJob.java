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

package com.yukthi.webutils.services;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthi.persistence.repository.RepositoryFactory;
import com.yukthi.webutils.annotations.CronJob;
import com.yukthi.webutils.repository.file.IFileRepository;
import com.yukthi.webutils.services.job.IJob;

/**
 * Job to delete temporaty files. Expected to run once every day.
 * @author akiran
 */
@CronJob(name = "Temp File Cleaner", cronExpression = "* * 23 * * ?")
public class FileCleanerJob implements IJob
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
	private RepositoryFactory repositoryFactory;
	
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
	
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.services.job.IJob#execute(java.lang.Object, org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(Object jobData, JobExecutionContext context) throws JobExecutionException
	{
		logger.debug("Deleting temporary files");
		
		//delete temp files which are 12 hours older or greater
		Date createdAfter = new Date();
		createdAfter = DateUtils.addHours(createdAfter, -HOUR_12);
		
		fileRepository.deleteTempFiles(createdAfter);
	}
}
