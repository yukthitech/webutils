package com.yukthitech.webutils.alerts;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.webutils.alerts.agent.IAlertingAgent;
import com.yukthitech.webutils.alerts.event.EventAlertRuleEntity;
import com.yukthitech.webutils.alerts.event.EventAlertRuleService;
import com.yukthitech.webutils.common.alerts.AlertDetails;
import com.yukthitech.webutils.services.AsyncTaskService;
import com.yukthitech.webutils.services.freemarker.FreeMarkerService;

/**
 * Service to register alerts and push alerts to agents.
 * @author akiran
 */
public class AlertEngine
{
	/**
	 * Basic delay to be used.
	 */
	private static final int SIMPLE_DELAY = 10;
	
	/**
	 * Max number of actions supported per alert.
	 */
	private static final int MAX_ACTIONS = 3;
	
	/**
	 * Max length of action.
	 */
	private static final int MAX_ACTION_LENGTH = 10;
	
	private static Logger logger = LogManager.getLogger(AlertEngine.class);
	
	/**
	 * Used to process in background mode.
	 */
	@Autowired
	private AsyncTaskService asyncTaskService;
	
	/**
	 * Event alert service to fetch event based alert rules.
	 */
	@Autowired
	private EventAlertRuleService eventAlertRuleService;
	
	/**
	 * Free marker service to process templates and conditions.
	 */
	@Autowired
	private FreeMarkerService freeMarkerService;
	
	/**
	 * List of registered alerting agents.
	 */
	@Autowired
	private List<IAlertingAgent> alertingAgents;
	
	/**
	 * Alert type enum to be used in current application. Required
	 * to enable error based alerts.
	 */
	private String alertTypeEnum;
	
	/**
	 * Enum constant name to be used when sending alerts. Required
	 * to enable error based alerts.
	 */
	private String errorAlertTypeName;
	
	/**
	 * Alert type to be used when sending alerts.
	 */
	private Object errorAlertTypeConstant;
	
	/**
	 * Name of the system to be used when sending system alerts.
	 */
	private String systemAgentName;
	
	/**
	 * Sets the alert type enum to be used in current application. Required to enable error based alerts.
	 *
	 * @param alertTypeEnum the new alert type enum to be used in current application
	 */
	public void setAlertTypeEnum(String alertTypeEnum)
	{
		this.alertTypeEnum = alertTypeEnum;
	}
	
	/**
	 * Sets the enum constant name to be used when sending alerts. Required to enable error based alerts.
	 *
	 * @param errorAlertType the new enum constant name to be used when sending alerts
	 */
	public void setErrorAlertType(String errorAlertType)
	{
		this.errorAlertTypeName = errorAlertType;
	}
	
	/**
	 * Sets the name of the system to be used when sending system alerts.
	 *
	 * @param systemAgentName the new name of the system to be used when sending system alerts
	 */
	public void setSystemAgentName(String systemAgentName)
	{
		this.systemAgentName = systemAgentName;
	}
	
	/**
	 * Post construct method to fetch agents.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	@PostConstruct
	private void init() throws Exception
	{
		if(StringUtils.isNotBlank(alertTypeEnum) && StringUtils.isNotBlank(errorAlertTypeName))
		{
			Class<?> enumType = Class.forName(alertTypeEnum);
			errorAlertTypeConstant = Enum.valueOf( (Class) enumType, errorAlertTypeName);
		}
	}
	
	/**
	 * Sends confirmation alert for specified close alert.
	 * @param closedAlert closed alert for which confirmation needs to be sent.
	 */
	public void sendConfirmationAlert(AlertDetails closedAlert)
	{
		AlertDetails alertDetails = new AlertDetails();
		alertDetails.setTitle(closedAlert.getTitle() + " - Confirmation");
		alertDetails.setConfirmationAlert(true);
		alertDetails.setSilentAlert(true);
		alertDetails.setMessage("Alert with title '" + closedAlert.getTitle() + "' is processed successfully!");
		
		//reverse source and target
		alertDetails.setSource(closedAlert.getTarget());
		alertDetails.setTarget(closedAlert.getSource());
		
		//set closed alert id as data on confirmation alert
		alertDetails.setData(closedAlert.getId());
		
		sendAlert(alertDetails);
	}
	
	/**
	 * Sends alerts to appropriated agents.
	 * @param alertDetails alert to be sent.
	 */
	public void sendAlert(AlertDetails alertDetails)
	{
		if(alertingAgents == null || alertingAgents.isEmpty())
		{
			logger.warn("No alerting agent is configured to send alerts. Hence ignoring send alert request.");
			return;
		}
		
		if(CollectionUtils.isNotEmpty(alertDetails.getActions()))
		{
			if(alertDetails.getActions().size() > MAX_ACTIONS)
			{
				throw new InvalidArgumentException("Max of 3 actions supported per alert.");
			}
			
			for(String action : alertDetails.getActions())
			{
				if(action.length() > MAX_ACTION_LENGTH)
				{
					throw new InvalidArgumentException("Action length should be less than or equal to 10. Invalid action specified: {}", action);
				}
			}
		}
		
		asyncTaskService.executeTask(new Runnable()   
		{
			public void run()
			{
				Set<String> targetTypes = alertDetails.getTargetAgentTypes();
				
				for(IAlertingAgent agent : alertingAgents)
				{
					if(targetTypes != null && !agent.isCompatible(targetTypes))
					{
						continue;
					}
					
					agent.sendAlert(alertDetails);
				}
			}
		}, SIMPLE_DELAY);
	}
	
	/**
	 * Sends error details occurred in the system as alert.
	 * @param title Title of alert to send.
	 * @param message message of the alert to send.
	 * @param th Error that cause the error.
	 */
	public void alertSystemError(String title, String message, Throwable th)
	{
		AlertDetails alertDetails = new AlertDetails();
		alertDetails.setAlertType(errorAlertTypeConstant);
		alertDetails.setTitle(title);
		alertDetails.setMessage(message);
		alertDetails.setSource(systemAgentName);
		
		String longMsg = message;
		
		if(th != null)
		{
			StringWriter writer = new StringWriter();
			PrintWriter out = new PrintWriter(writer);
			
			out.println("\nAssociated error stack trace");
			out.println("==================================");
			
			th.printStackTrace(out);
			out.flush();
			writer.flush();
			
			longMsg = (longMsg == null) ? "" : longMsg;
			longMsg += writer.toString();
		}
		
		alertDetails.setLongMessage(longMsg);

		sendAlert(alertDetails);
	}
	
	/**
	 * Executes specified function body as function and returns true if evaluation result as "true".
	 * @param functionBody function body to execute
	 * @param eventObject object to be used as context
	 * @return true if evaluation results in "true"
	 */
	private boolean checkConditionFunction(String functionBody, Object eventObject)
	{
		if(functionBody == null)
		{
			return true;
		}
		
		String fullTemplate = String.format("<#function ruleFunc>%s</#function>${ruleFunc()}", functionBody);
		String result = freeMarkerService.processTemplate("condition", fullTemplate, eventObject);
		result = result.trim();
		
		return "true".equalsIgnoreCase(result);
	}
	
	/**
	 * Evaluates specified template and converts the result into alert details and returns the same.
	 * @param template template to process
	 * @param eventObject object to be used as context
	 * @return result alert details.
	 */
	private AlertDetails parseAlertDetails(String template, Object eventObject) throws JsonParseException, JsonMappingException, IOException
	{
		String xml = freeMarkerService.processTemplate("AlertDetails-xml", template, eventObject);
		
		AlertDetails alertDetails = new AlertDetails();
		XMLBeanParser.parse(new ByteArrayInputStream(xml.getBytes()), alertDetails);
		
		return alertDetails;
	}
	
	/**
	 * Sends alerts corresponding to specified event type, if any. Event object class name (FQN) will be used 
	 * as event type name. 
	 *
	 * @param eventObject the event object
	 */
	public void sendEventAlerts(Object eventObject)
	{
		sendEventAlerts(eventObject, eventObject.getClass().getName());
	}
	
	/**
	 * Sends alerts corresponding to specified event type, if any. 
	 * @param eventObject Event object to be used to parse expressions. This object will be used as free marker context.
	 * @param eventType Event type name whose rules needs to be checked.
	 */
	public void sendEventAlerts(Object eventObject, String eventType)
	{
		asyncTaskService.executeTask(new Runnable()
		{
			public void run()
			{
				logger.debug("Sending alerts for event type: " + eventType);
				
				List<EventAlertRuleEntity> rules = eventAlertRuleService.fetchAlerts(eventType);
				
				if(rules == null || rules.isEmpty())
				{
					return;
				}
				
				for(EventAlertRuleEntity rule : rules)
				{
					try
					{
						if(!checkConditionFunction(rule.getConditionFunction(), eventObject))
						{
							continue;
						}
						
						AlertDetails alertDetails = parseAlertDetails(rule.getAlertDetailsTemplate(), eventObject);
						sendAlert(alertDetails);
					}catch(Exception ex)
					{
						logger.error("An error occurred while processing event-alert rule - {}", rule.getName(), ex);
						alertSystemError("Send Event Alert Error", "An error occurred while processing event-alert rule - " + rule.getName(), ex);
					}
				}
			}
		});
	}
}
