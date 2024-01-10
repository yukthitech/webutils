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

package com.yukthitech.webutils.services.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import com.yukthitech.webutils.services.JsonService;

/**
 * @author akiran
 *
 */
public class QuartzJobBridge implements Job
{
	private static Logger logger = LogManager.getLogger(QuartzJobBridge.class);
	
	/**
	 * Spring application context that will be set by {@link JobService} during initialization
	 */
	static ApplicationContext applicationContext;

	/**
	 * Used to convert json into object.
	 */
	private JsonService jsonService = new JsonService();
	
	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		IJob job = null;
		Object jobData = null;
		
		try
		{
			String jobDataJson = context.getJobDetail().getJobDataMap().getString(IJobConstants.ATTR_JOB_DETAILS);
			
			if(jobDataJson != null)
			{
				jobData = jsonService.parseJson(jobDataJson);
			}
			
			String jobType = context.getJobDetail().getJobDataMap().getString(IJobConstants.ATTR_JOB_TYPE);
			job = (IJob)Class.forName(jobType).newInstance(); 
		}catch(Exception ex)
		{
			throw new JobExecutionException("An error occurred while creating actual job instance", ex);
		}
		
		//autowire dependencies
		applicationContext.getAutowireCapableBeanFactory().autowireBean(job);
		applicationContext.getAutowireCapableBeanFactory().applyBeanPostProcessorsBeforeInitialization(job, null);

		try
		{
			//execute the job
			job.execute(jobData, context);
		}catch(Exception ex)
		{
			logger.error("An error occurred while executing job - " + job.getClass().getName(), ex);
		}
	}
	
	
}
