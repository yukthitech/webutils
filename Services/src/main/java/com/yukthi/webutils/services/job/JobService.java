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
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.annotations.CronJob;

/**
 * Service class to load job classes and schedule them as configured
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
	@Autowired
	private Scheduler scheduler;

	@PostConstruct
	private void init() throws ParseException
	{
		//fetch all the configured job beans
		Map<String, Object> quartzJobBeans = applicationContext.getBeansWithAnnotation(CronJob.class);
		//CronTriggerFactoryBean cronTriggerFactory = null;
		Object jobObj = null;
		CronJob cronJob = null;
		//JobDetailFactoryBean jobDetailFactory = null;
		JobDetailImpl jobDetail = null;
		CronTriggerImpl cronTrigger = null;

		for(String name : quartzJobBeans.keySet())
		{
			jobObj = quartzJobBeans.get(name);

			if(!(jobObj instanceof QuartzJobBean))
			{
				logger.error("Non QuartzJobBean is marked as CronJob - {}", jobObj.getClass().getName());
				throw new InvalidStateException("Non QuartzJobBean is marked as CronJob - {}", jobObj.getClass().getName());
			}
			
			logger.info("Scheduling job class - {}", jobObj.getClass().getName());

			cronJob = jobObj.getClass().getAnnotation(CronJob.class);

			/*
			cronTriggerFactory = new CronTriggerFactoryBean();
			cronTriggerFactory.setName(cronJob.name() + "_trigger");
			cronTriggerFactory.setCronExpression(cronJob.cronExpression());

			jobDetailFactory = new JobDetailFactoryBean();
			jobDetailFactory.setName(cronJob.name());
			jobDetailFactory.setJobClass(jobObj.getClass());
			jobDetail = jobDetailFactory.getObject();
			
			cronTriggerFactory.setJobDetail(jobDetailFactory.getObject());
			
			try
			{
				scheduler.scheduleJob(jobDetail, cronTriggerFactory.getObject());
			}catch(Exception ex)
			{
				logger.error("An error occurred while scheduling job class - " + jobObj.getClass().getName(), ex);
				throw new InvalidStateException(ex, "An error occurred while scheduling job class - {}", jobObj.getClass().getName());
			}
			
			*/
			cronTrigger = new CronTriggerImpl();
			cronTrigger.setName(cronJob.name() + "_trigger");
			cronTrigger.setCronExpression(cronJob.cronExpression());

			jobDetail = new JobDetailImpl();
			jobDetail.setName(cronJob.name());
			jobDetail.setJobClass((Class)jobObj.getClass());
			
			try
			{
				scheduler.scheduleJob(jobDetail, cronTrigger);
			}catch(Exception ex)
			{
				logger.error("An error occurred while scheduling job class - " + jobObj.getClass().getName(), ex);
				throw new InvalidStateException(ex, "An error occurred while scheduling job class - {}", jobObj.getClass().getName());
			}
			
			
		}
	}
}
