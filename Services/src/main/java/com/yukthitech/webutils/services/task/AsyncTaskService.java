package com.yukthitech.webutils.services.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.services.ServiceMethod;
import com.yukthitech.webutils.services.SpringUtilsService;

/**
 * Service to execute in execute async jobs.
 * @author akiran
 */
@Service
public class AsyncTaskService
{
	private static Logger logger = LogManager.getLogger(AsyncTaskService.class);
	
	/**
	 * Holds milli seconds per day.
	 */
	private static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
	
	/**
	 * Standard date format to be used in reports.
	 */
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	/**
	 * Date and time format.
	 */
	private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
	
	/**
	 * Used to fetch service methods.
	 */
	@Autowired
	private SpringUtilsService springUtilsService;
	
	/**
	 * Number of threads to be used.
	 */
	@Value("${webutils.async.threadCount:15}")
	private int threadCount;
	
	/**
	 * Thread pool to be used.
	 */
	private ScheduledExecutorService threadPool;
	
	/**
	 * Post application construct method to initialize the thread pool.
	 * @param event event object
	 */
	@EventListener
	private void init(ContextRefreshedEvent event)
	{
		//ensure initialization happens only once
		if(threadPool != null)
		{
			return;
		}
		
		threadPool = Executors.newScheduledThreadPool(threadCount);
		
		//Register schedule task methods
		List<ServiceMethod> methods = springUtilsService.fetchServiceMethods("Schedule-task", ScheduledTask.class);
		
		for(ServiceMethod method : methods)
		{
			registerScheduledMethod(method);
		}

		//Register repetitive task methods
		methods = springUtilsService.fetchServiceMethods("Schedule-task", RepetitiveTask.class);
		
		for(ServiceMethod method : methods)
		{
			registerRepetitiveMethod(method);
		}
	}
	
	/**
	 * Creates date with specified date and time.
	 * @param date date 
	 * @param time time
	 * @return returns date time string
	 */
	private Date setTime(Date date, String time)
	{
		String dateStr = DATE_FORMAT.format(date);
		
		dateStr = dateStr + " " + time;
		
		try
		{
			return DATE_TIME_FORMAT.parse(dateStr);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while parsing date string: {}", dateStr, ex);
		}
	}

	/**
	 * Registers the schedule method for execution.
	 * @param serviceMethod method to register
	 */
	private void registerScheduledMethod(ServiceMethod serviceMethod)
	{
		ScheduledTask scheduledTask = serviceMethod.getMethod().getAnnotation(ScheduledTask.class);
		
		//calculate the delay after which first execution should happen
		Date now = new Date();
		Date todayWithTime = setTime(now, scheduledTask.time());
		long firstExecDelay = todayWithTime.getTime() - now.getTime();
		String firstExeTimeStr = DATE_TIME_FORMAT.format(todayWithTime);
		
		if(firstExecDelay < 0)
		{
			Date tomorrow = DateUtils.addDays(now, 1);
			tomorrow = setTime(tomorrow, scheduledTask.time());
			firstExecDelay = todayWithTime.getTime() - now.getTime();
			
			firstExeTimeStr = DATE_TIME_FORMAT.format(tomorrow);
		}
		
		Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					serviceMethod.getMethod().invoke(serviceMethod.getService());
				}catch(Exception ex)
				{
					logger.error("An error occurred while executing scheduled method: {}", serviceMethod);
				}
			}
		};
		
		logger.debug("Scheduling daily task {} with first time scheduled-execution at - {}", serviceMethod, firstExeTimeStr);
		threadPool.scheduleWithFixedDelay(runnable, firstExecDelay, MILLIS_PER_DAY, TimeUnit.MILLISECONDS);
		
		//if initial delay is specified and it is less than first execution delay
		if(scheduledTask.initialDelay() > 0 && scheduledTask.initialDelay() < firstExecDelay)
		{
			logger.debug("Scheduling {} for first time execution after {} millis.", serviceMethod, scheduledTask.initialDelay());
			
			//execute it after specified initial delay
			threadPool.schedule(runnable, scheduledTask.initialDelay(), TimeUnit.MILLISECONDS);
		}
	}
	
	/**
	 * Registers the repetitive method for execution.
	 * @param serviceMethod method to register
	 */
	private void registerRepetitiveMethod(ServiceMethod serviceMethod)
	{
		RepetitiveTask repetitiveTask = serviceMethod.getMethod().getAnnotation(RepetitiveTask.class);
		
		long interval = repetitiveTask.interval();
		
		if(interval <= 0)
		{
			throw new InvalidArgumentException("Invalid interval value '{}' specified for repetitive method: {}", interval, serviceMethod);
		}
		
		long initialDelay = repetitiveTask.initialDelay();
		initialDelay = (initialDelay >= 0) ? initialDelay : interval;
		
		Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					serviceMethod.getMethod().invoke(serviceMethod.getService());
				}catch(Exception ex)
				{
					logger.error("An error occurred while executing scheduled method: {}", serviceMethod, ex);
				}
			}
		};
		
		logger.debug("Scheduling repetitive task {} with first time execution after {} millis", serviceMethod, initialDelay);
		threadPool.scheduleWithFixedDelay(runnable, initialDelay, interval, TimeUnit.MILLISECONDS);
	}

	/**
	 * Wraps runnable object so that on exception it is printed in log.
	 * @param name Name of the execution
	 * @param runnable runnable to execute
	 * @return wrapped runnable
	 */
	private Runnable wrap(String name, Runnable runnable)
	{
		Runnable wrapper = new Runnable()
		{
			@Override
			public void run()
			{
				Thread thread = Thread.currentThread();
				String currentName = thread.getName();
				
				thread.setName(name);
				
				try
				{
					runnable.run();
				}catch(Exception ex)
				{
					logger.error("An error occurred while executing task in background.", ex);
				}finally
				{
					thread.setName(currentName);
				}
			}
		};
		
		return wrapper;
	}
	
	/**
	 * Executes specified runnable object in background.
	 * @param name Name of the execution
	 * @param runnable runnable to execute
	 * @param delay time in millis after which task should be executed.
	 * @param timeUnit Time unit in which delay is specified.
	 * @return future object to track the execution.
	 */
	public Future<?> executeTask(String name, Runnable runnable, long delay, TimeUnit timeUnit)
	{
		return threadPool.schedule( wrap(name, runnable), delay, timeUnit );
	}

	/**
	 * Executes specified runnable object in background.
	 * @param name Name of the execution
	 * @param runnable runnable to execute
	 * @param delay time in millis after which task should be executed.
	 * @return future object to track the execution.
	 */
	public Future<?> executeTask(String name, Runnable runnable, long delay)
	{
		return threadPool.schedule( wrap(name, runnable), delay, TimeUnit.MILLISECONDS );
	}

	/**
	 * Executes specified runnable object in background.
	 * @param name Name of the execution
	 * @param runnable runnable to execute
	 * @return future object to track the execution.
	 */
	public Future<?> executeTask(String name, Runnable runnable)
	{
		return threadPool.submit( wrap(name, runnable) );
	}
}
