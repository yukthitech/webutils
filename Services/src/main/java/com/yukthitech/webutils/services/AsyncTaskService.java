package com.yukthitech.webutils.services;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Service to execute in execute async jobs.
 * @author akiran
 */
public class AsyncTaskService
{
	/**
	 * Default thread count to be used.
	 */
	public static final int DEF_THREAD_COUNT = 10;

	private static Logger logger = LogManager.getLogger(AsyncTaskService.class);
	
	/**
	 * Number of threads to be used.
	 */
	private int threadCount = DEF_THREAD_COUNT;
	
	/**
	 * Thread pool to be used.
	 */
	private ScheduledExecutorService threadPool;
	
	/**
	 * Sets the number of threads to be used.
	 *
	 * @param threadCount the new number of threads to be used
	 */
	public void setThreadCount(int threadCount)
	{
		if(threadCount < 1)
		{
			throw new InvalidArgumentException("Invalid number of threads specified: {}", threadCount);
		}
		
		this.threadCount = threadCount;
	}
	
	/**
	 * Post construct method to initialize the thread pool.
	 */
	@PostConstruct
	private void init()
	{
		threadPool = Executors.newScheduledThreadPool(threadCount);
	}
	
	/**
	 * Wrapps runnable object so that on exception it is printed in log.
	 * @param runnable runnable to execute
	 * @return wrapped runnable
	 */
	private Runnable wrap(Runnable runnable)
	{
		Runnable wrapper = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					runnable.run();
				}catch(Exception ex)
				{
					logger.error("An error occurred while executing task in background.", ex);
				}
			}
		};
		
		return wrapper;
	}
	
	/**
	 * Executes specified runnable object in background.
	 * @param runnable runnable to execute
	 * @param delay time in millis after which task should be executed.
	 * @param timeUnit Time unit in which delay is specified.
	 * @return future object to track the execution.
	 */
	public Future<?> executeTask(Runnable runnable, long delay, TimeUnit timeUnit)
	{
		return threadPool.schedule( wrap(runnable), delay, timeUnit );
	}

	/**
	 * Executes specified runnable object in background.
	 * @param runnable runnable to execute
	 * @param delay time in millis after which task should be executed.
	 * @return future object to track the execution.
	 */
	public Future<?> executeTask(Runnable runnable, long delay)
	{
		return threadPool.schedule( wrap(runnable), delay, TimeUnit.MILLISECONDS );
	}

	/**
	 * Executes specified runnable object in background.
	 * @param runnable runnable to execute
	 * @return future object to track the execution.
	 */
	public Future<?> executeTask(Runnable runnable)
	{
		return threadPool.submit( wrap(runnable) );
	}
}
