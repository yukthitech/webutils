package com.yukthitech.webutils.client.helpers;

import java.lang.reflect.Method;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.event.EventListenerManager;
import com.yukthitech.webutils.client.ClientControllerFactory;
import com.yukthitech.webutils.common.alerts.AlertDetails;
import com.yukthitech.webutils.common.alerts.AlertProcessedDetails;
import com.yukthitech.webutils.common.alerts.IAlertController;
import com.yukthitech.webutils.common.models.BasicReadListResponse;

/**
 * Manager for alerts listeners which will be invoked when approp alerts come.
 * @author akiran
 */
public class AlertManager
{
	/**
	 * 30sec duration.
	 */
	private static final int THIRTY_SEC = 30000;
	
	private static Logger logger = LogManager.getLogger(AlertManager.class);
	
	/**
	 * Event listener manager for managing listeners.
	 */
	private EventListenerManager<IAlertListener> alertEventListenerManager;
	
	/**
	 * Proxy from listener manager which internally invokes listeners.
	 */
	private IAlertListener proxy;
	
	/**
	 * Controller used to check for alerts.
	 */
	private IAlertController<Object> alertController;
	
	/**
	 * Check duration in millis after which alerts will be checked.
	 */
	private long checkDuration = THIRTY_SEC;
	
	/**
	 * Name of the agent for which alerts needs to be monitored.
	 */
	private String agentName;
	
	/**
	 * Background thread that will be checking for alerts.
	 */
	private Thread alertCheckThread;
	
	/**
	 * Instantiates a new alert manager.
	 *
	 * @param agentName the agent name
	 * @param clientControllerFactory the client controller factory
	 * @param enableParallelExecution the enable parallel execution
	 */
	@SuppressWarnings("unchecked")
	public AlertManager(String agentName, ClientControllerFactory clientControllerFactory, boolean enableParallelExecution)
	{
		this.agentName = agentName;
		this.alertController = clientControllerFactory.getController(IAlertController.class);
		
		alertEventListenerManager = EventListenerManager.newEventListenerManager(IAlertListener.class, enableParallelExecution);
		proxy = alertEventListenerManager.get();
		
		alertCheckThread = new Thread("Alert Checker")
		{
			public void run()
			{
				while(true)
				{
					//check for alerts only when listeners are present
					if( CollectionUtils.isNotEmpty( alertEventListenerManager.getListeners() ) )
					{
						fetchAlerts();
					}
					
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
		
		//create the result processor
		EventListenerManager.ResultProcessor<IAlertListener> resultProcessor = new EventListenerManager.ResultProcessor<IAlertListener>()
		{
			@Override
			public void processResult(Object result, IAlertListener listener, Object data, Method listenerMethod, Object... params)
			{
				AlertDetails alertDetails = (AlertDetails) params[0];
				ObjectWrapper<AlertProcessedDetails> confirmationData = (ObjectWrapper<AlertProcessedDetails>) params[1];
				
				Boolean resFlag = (Boolean) result;
				
				if(resFlag != null && resFlag)
				{
					markAsProcessed(alertDetails, confirmationData.getValue());
				}
			}
		};
		
		alertEventListenerManager.setResultProcessor(resultProcessor);

		alertCheckThread.start();
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
	 */
	public void addAlertListener(IAlertListener listener)
	{
		alertEventListenerManager.addListener(listener);
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
	private void fetchAlerts()
	{
		BasicReadListResponse<AlertDetails> res = null;
		
		try
		{
			res = alertController.fetchAlerts(agentName);
		}catch(Exception ex)
		{
			logger.error("An error occurred while fetching alerts for agent '{}'. Ignoring the alert for now.", ex);
			return;
		}
		
		if(CollectionUtils.isEmpty(res.getValues()))
		{
			return;
		}
		
		logger.debug("Found alerts: {}", res.getValues());
		
		for(AlertDetails alertDetails : res.getValues())
		{
			proxy.onAlert(alertDetails, new ObjectWrapper<AlertProcessedDetails>());
		}
	}
	
	/**
	 * Marks specified alert details as processed.
	 * @param alertDetails alert to be marked.
	 * @param confirmationData confirmation data to be sent to server back
	 */
	private void markAsProcessed(AlertDetails alertDetails, AlertProcessedDetails confirmationData)
	{
		alertController.markProcessed(alertDetails.getId(), confirmationData);
	}
	
	/**
	 * Interrupts the background thread and forces it to check for alerts.
	 */
	public void checkForAlerts()
	{
		alertCheckThread.interrupt();
	}
}
