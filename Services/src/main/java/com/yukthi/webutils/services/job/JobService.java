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

package com.yukthi.webutils.services.job;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.yukthi.common.util.JsonWrapper;
import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.exceptions.InvalidArgumentException;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.annotations.CronJob;
import com.yukthi.webutils.services.ClassScannerService;

/**
 * Service class to load job classes and schedule them as configured. Dynamic jobs can be 
 * scheduled using {@link #scheduleJob(JobDetails)}
 * 
 * @author akiran
 */
@Service
public class JobService
{
	private static Logger logger = LogManager.getLogger(JobService.class);
	
	/**
	 * Spring application context to fetch configured job beans
	 */
	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * Quartz scheduler for scheduling
	 */
	private Scheduler scheduler;
	
	@Autowired
	private ClassScannerService classScannerService;
	
	/**
	 * The job name job detail, contains all the job name and job details for
	 * scheduling the job.
	 */
	private Map<String, JobKey> nameToKey = new HashMap<>();	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostConstruct
	private void init() throws ParseException
	{
		//set application context on bridge, which in turn will be used for autowiring
		QuartzJobBridge.applicationContext = applicationContext;
		
		//create scheduler and start it
		try
		{
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while starting up the scheduler");
		}
		
		//fetch all the configured job beans
		Set<Class<?>> quartzJobBeanTypes = classScannerService.getClassesWithAnnotation(CronJob.class);
		CronJob cronJob = null;

		for(Class<?> jobBeanType : quartzJobBeanTypes)
		{
			if(!IJob.class.isAssignableFrom(jobBeanType))
			{
				logger.error("Non job is marked as CronJob - {}", jobBeanType.getName());
				throw new InvalidStateException("Non job is marked as CronJob - {}", jobBeanType.getName());
			}
			
			cronJob = jobBeanType.getAnnotation(CronJob.class);
			
			scheduleJob(new JobDetails(cronJob.name(), cronJob.cronExpression(), (Class)jobBeanType, null));
		}
	}
	
	/**
	 * Schedules job with specified details
	 * @param jobDetails Details of job to configure
	 */
	public void scheduleJob(JobDetails jobDetails)
	{
		logger.info("Scheduling job with details - {}", jobDetails);
		
		String jobDataJson = null;
		
		if(jobDetails.getJobData() != null)
		{
			try
			{
				jobDataJson = JsonWrapper.format(jobDetails.getJobData());
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred wile converting jobdetails into json - {}", jobDetails);
			}
		}
		
		//ensure default constructor is available for job type
		try
		{
			if(!(jobDetails.getJobClass().newInstance() instanceof IJob))
			{
				throw new InvalidArgumentException("Invalid job type specified - {}", jobDetails.getJobClass().getName());
			}
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "Failed to created specified job type instance - {}", jobDetails.getJobClass().getName());
		}
		
		//create job instance
		JobDetail job = JobBuilder.newJob(QuartzJobBridge.class)
				.withIdentity(jobDetails.getName(), "defaultGroup")
				.usingJobData(new JobDataMap(CommonUtils.toMap(
							IJobConstants.ATTR_JOB_TYPE, jobDetails.getJobClass().getName(),
							IJobConstants.ATTR_JOB_DETAILS, jobDataJson,
							IJobConstants.ATTR_JOB_NAME, jobDetails.getName()
						)))
				.build();

		// Trigger the job to run on the next round minute
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(jobDetails.getName() + "_trigger", "defaultGroup")
				.withSchedule(CronScheduleBuilder.cronSchedule(jobDetails.getCronExpression()))
				.build();

		// schedule it
		try
		{
			scheduler.scheduleJob(job, trigger);
			nameToKey.put(jobDetails.getName(), job.getKey());
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while scheduling job - {}", jobDetails);
		}
	}
	
	/**
	 * Executes the specified job on demand.
	 *
	 * @param jobName Name of the job to execute.
	 */
	public void executeJob(String jobName)
	{
		JobKey jobKey = nameToKey.get(jobName);

		if(jobKey == null)
		{
			throw new InvalidArgumentException("An error occured while executing job - {},  JobKey is null", jobName);
		}

		//trigger the job immediately
		try
		{
			scheduler.triggerJob(jobKey);
		} catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while scheduling job - {}", jobName);
		}
	}
}
