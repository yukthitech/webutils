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

import org.quartz.Job;

/**
 * Details of job to execute
 * @author akiran
 */
public class JobDetails
{
	/**
	 * Name of the job
	 */
	private String name;
	
	/**
	 * Cron expression for job scheduling
	 */
	private String cronExpression;
	
	/**
	 * Java class representing job execution
	 */
	private Class<? extends Job> jobClass;
	
	/**
	 * Data that needs to be passed during job execution
	 */
	private Object jobData;
	
	/**
	 * Instantiates a new job details.
	 *
	 * @param name the name
	 * @param cronExpression the cron expression
	 * @param jobClass the job class
	 * @param jobData the job data
	 */
	public JobDetails(String name, String cronExpression, Class<? extends Job> jobClass, Object jobData)
	{
		this.name = name;
		this.cronExpression = cronExpression;
		this.jobClass = jobClass;
		this.jobData = jobData;
	}

	/**
	 * Gets the name of the job.
	 *
	 * @return the name of the job
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the cron expression for job scheduling.
	 *
	 * @return the cron expression for job scheduling
	 */
	public String getCronExpression()
	{
		return cronExpression;
	}

	/**
	 * Gets the data that needs to be passed during job execution.
	 *
	 * @return the data that needs to be passed during job execution
	 */
	public Object getJobData()
	{
		return jobData;
	}
	
	/**
	 * Gets the java class representing job execution.
	 *
	 * @return the java class representing job execution
	 */
	public Class<? extends Job> getJobClass()
	{
		return jobClass;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Name: ").append(name);
		builder.append(",").append("Cron: ").append(cronExpression);
		builder.append(",").append("Job Class: ").append(jobClass.getName());
		builder.append(",").append("Data: ").append(jobData);

		builder.append("]");
		return builder.toString();
	}

}
