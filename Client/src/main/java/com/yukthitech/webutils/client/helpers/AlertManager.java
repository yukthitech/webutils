package com.yukthitech.webutils.client.helpers;

import java.lang.reflect.Method;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.event.EventListenerManager;
import com.yukthitech.webutils.client.ClientControllerFactory;
import com.yukthitech.webutils.common.alerts.AlertDetails;
import com.yukthitech.webutils.common.alerts.IAlertController;
import com.yukthitech.webutils.common.models.BasicReadListResponse;

/**
 * Manager for alerts listeners which will be invoked when approp alerts come.
 * @author akiran
 */
public class AlertManager
{
	private static Logger logger = LogManager.getLogger(AlertManager.class);
	
	/**
	 * Event listener manager for managing listeners.
	 */
	private EventListenerManager<IAlertListener> alertEventListenerManager = EventListenerManager.newEventListenerManager(IAlertListener.class, true);
	
	/**
	 * Proxy from listener manager which internally invokes listeners.
	 */
	private IAlertListener proxy = alertEventListenerManager.get();
	
	/**
	 * Controller used to check for alerts.
	 */
	private IAlertController<Object> alertController;
	
	/**
	 * Check duration in millis after which alerts will be checked.
	 */
	private long checkDuration = 30000;
	
	/**
	 * Name of the agent for which alerts needs to be monitored.
	 */
	private String agentName;
	
	/**
	 * Instantiates a new alert manager.
	 *
	 * @param agentName Name of agent
	 * @param clientControllerFactory the client controller factory
	 */
	@SuppressWarnings("unchecked")
	public AlertManager(String agentName, ClientControllerFactory clientControllerFactory)
	{
		this.agentName = agentName;
		this.alertController = clientControllerFactory.getController(IAlertController.class);
		
		Thread alertCheckThread = new Thread("Alert Checker")
		{
			public void run()
			{
				while(true)
				{
					checkForAlerts();
					
					try
					{
						Thread.sleep(checkDuration);
					}catch(InterruptedException ex)
					{
						//ignore
					}
				}
			}
		};
		
		alertCheckThread.start();
		
		//create the filter for listener invocation
		EventListenerManager.ListenerFilter<IAlertListener> filter = new EventListenerManager.ListenerFilter<IAlertListener>()
		{
			@Override
			public boolean filter(IAlertListener listener, Object data, Method listenerMethod, Object... params)
			{
				AlertDetails alertDetails = (AlertDetails) params[0];
				String targetAlertType = (String) data;
				
				if(targetAlertType == null)
				{
					return true;
				}
				
				String curAlertType = alertDetails.getAlertType().toString();
				return targetAlertType.equals(curAlertType);
			}
		};
		
		alertEventListenerManager.setFilter(filter);
		
		//create the result processor
		EventListenerManager.ResultProcessor<IAlertListener> resultProcessor = new EventListenerManager.ResultProcessor<IAlertListener>()
		{
			@Override
			public void processResult(Object result, IAlertListener listener, Object data, Method listenerMethod, Object... params)
			{
				AlertDetails alertDetails = (AlertDetails) params[0];
				Boolean resFlag = (Boolean) result;
				
				if(resFlag != null && resFlag)
				{
					markAsProcessed(alertDetails);
				}
			}
		};
		
		alertEventListenerManager.setResultProcessor(resultProcessor);
	}
	
	/**
	 * Sets the check duration in millis after which alerts will be checked.
	 *
	 * @param checkDuration the new check duration in millis after which alerts will be checked
	 */
	public void setCheckDuration(long checkDuration)
	{
		this.checkDuration = checkDuration;
	}
	
	/**
	 * Adds specified listener.
	 * @param listener listener to add.
	 * @param alertType Alert type for which listener should be invoked.
	 */
	public void addAlertListener(IAlertListener listener, final String alertType)
	{
		alertEventListenerManager.addListener(listener, alertType);
	}
	
	/**
	 * Removes specified listener.
	 * @param listener listener to remove.
	 */
	public void removeAlertListener(IAlertListener listener)
	{
		alertEventListenerManager.removeListener(listener);
	}
	
	/**
	 * Checks for alerts for current agent, if found approp
	 * listeners will be invoked.
	 */
	private void checkForAlerts()
	{
		BasicReadListResponse<AlertDetails> res = alertController.fetchAlerts(agentName);
		
		if(CollectionUtils.isEmpty(res.getValues()))
		{
			return;
		}
		
		logger.debug("Found alerts: {}", res.getValues());
		
		for(AlertDetails alertDetails : res.getValues())
		{
			proxy.onAlert(alertDetails);
		}
	}
	
	/**
	 * Marks specified alert details as processed.
	 * @param alertDetails alert to be marked.
	 */
	public void markAsProcessed(AlertDetails alertDetails)
	{
		alertController.markProcessed(alertDetails.getId());
	}
}
