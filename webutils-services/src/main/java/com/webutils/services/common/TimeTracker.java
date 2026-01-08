package com.webutils.services.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.webutils.common.Executable;
import com.webutils.common.ExecutableWithReturn;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class TimeTracker
{
	private static Logger logger = LogManager.getLogger(TimeTracker.class);
	
	public static void execute(String jobName, Executable executable)
	{
		executeWithReturn(jobName, () -> 
		{
			executable.execute();
			return null;
		});
	}

	public static <T> T executeWithReturn(String jobName, ExecutableWithReturn<T> executable)
	{
		try
		{
			long start = System.currentTimeMillis();
			
			T res = executable.execute();
			
			double diff = (System.currentTimeMillis() - start) / 1000.0;
			
			logger.debug("Executed job '{}' in {} Secs.", jobName, diff);
			
			return res;
		}catch(RuntimeException ex)
		{
			throw ex;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while executing job: {}", jobName, ex);
		}
	}
}
