package com.webutils.services.common;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ExecutionService 
{
	private static Logger logger = LogManager.getLogger(ExecutionService.class);
	
    public class TimeBasedTask implements Runnable
    {
        private String name;
        private int hour24;
        private int minute;
        private Runnable task;

        public TimeBasedTask(String name, int hour24, int minute, Runnable task)
        {
        	this.name = name;
            this.hour24 = hour24;
            this.minute = minute;
            this.task = task;
        }

        @Override
        public void run()
        {
        	String oldName = Thread.currentThread().getName();
        	Thread.currentThread().setName(name);
        	
            try
            {
                logger.info("Executing time based task '{}'", name);
                task.run();
            }catch(Exception ex)
            {
                logger.error("Error executing time based task", ex);
            }finally
            {
            	Thread.currentThread().setName(oldName);
            }

            scheduleNextRun();
        }

        private void scheduleNextRun()
        {
            Date nextRunTime = computeNextRunTime();
            long delay = nextRunTime.getTime() - System.currentTimeMillis();
            executorService.schedule(this, delay, TimeUnit.MILLISECONDS);

            logger.info("Task '{}' is scheduled for next run at {}", name, nextRunTime);
        }

        private Date computeNextRunTime()
        {
            Date now = new Date();

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(now);
            calendar.set(Calendar.HOUR_OF_DAY, hour24);
            calendar.set(Calendar.MINUTE, minute);
            Date nextRunTime = calendar.getTime();

            if(nextRunTime.after(now))
            {
                return nextRunTime;
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            return calendar.getTime();
        }
    }

    private class ScheduledTask implements Runnable
    {
        private String name;
        private Runnable task;
        
        public ScheduledTask(String name, Runnable task)
        {
            this.name = name;
            this.task = task;
        }

        @Override
        public void run()
        {
        	String oldName = Thread.currentThread().getName();
        	Thread.currentThread().setName(name);
        	
            try
            {
                logger.info("Executing scheduled task '{}'", name);
                task.run();
            }catch(Exception ex)
            {
                logger.error("Error executing scheduled task", ex);
            }finally
            {
            	Thread.currentThread().setName(oldName);
            }
        }
    }

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    public void scheduleRepeatedTask(String name, Runnable task, long period, TimeUnit periodUnit)
    {
    	logger.debug("Submiting repeated task [Name: {}, Duration: {} {}]", name, period, periodUnit);

    	ScheduledTask scheduledTask = new ScheduledTask(name, task);
        executorService.scheduleAtFixedRate(scheduledTask, 10, period, periodUnit);
    }

    public void submitTimeBasedTask(String name, int hour24, int minute, Runnable task, boolean runImmediately)
    {
    	logger.debug("Submiting time based repeated task [Name: {}, Time: {}:{}]", name, hour24, minute);
    	
        TimeBasedTask timeBasedTask = new TimeBasedTask(name, hour24, minute, task);

        if(runImmediately)
        {
            executorService.schedule(timeBasedTask, 10, TimeUnit.MILLISECONDS);
        }
        else
        {
            // schedule for next run
            timeBasedTask.scheduleNextRun();
        }
    }
    
    public void executeAsync(String name,Runnable task)
    {
        String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName(name);
        
        try
        {
            executorService.execute(task);
        }catch(Exception ex)
        {
            logger.error("Error executing async task: {}", name, ex);
        } finally
        {
            Thread.currentThread().setName(oldName);
        }
    }
}
