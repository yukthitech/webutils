package com.yukthitech.webutils.services.job;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Wrapper over thread pool to provide it as a service.
 * @author akiran
 */
@Component
public class BackgroundThreadManager
{
	private static Logger logger = LogManager.getLogger(BackgroundThreadManager.class);
	
	/**
	 * Thread pool for back ground threads.
	 */
	private ScheduledExecutorService executorService;
	
	/**
	 * Thread pool size for background threads.
	 */
	@Value("${webutils.background.theadPoolSize:20}")
	private int backgroundThreadPoolSize;
	
	/**
	 * Post construct method to initialize thread pool.
	 */
	@PostConstruct
	private void init()
	{
		logger.debug("Starting background thread pool with size: {}", backgroundThreadPoolSize);
		
		executorService = Executors.newScheduledThreadPool(backgroundThreadPoolSize);
	}
	
	/**
	 * Executes the specified runnable after specified delay.
	 * @param runnable Runnable to be executed.
	 * @param delay Delay time after which runnable should be executed.
	 * @param timeUnit Time unit of delay.
	 */
	public void schedule(Runnable runnable, long delay, TimeUnit timeUnit)
	{
		executorService.schedule(runnable, delay, timeUnit);
	}
	
	/**
	 * Executes specified runnable repeatedly with specified delay.
	 * @param runnable Runnable to execute
	 * @param initDelay initial delay
	 * @param delay delay between executions.
	 * @param timeUnit Time unit used.
	 */
	public void scheduleWithFixedDelay(Runnable runnable, long initDelay, long delay, TimeUnit timeUnit)
	{
		executorService.scheduleWithFixedDelay(runnable, initDelay, delay, timeUnit);
	}
}
