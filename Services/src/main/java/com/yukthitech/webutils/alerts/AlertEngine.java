package com.yukthitech.webutils.alerts;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.alerts.agent.IAlertSupport;
import com.yukthitech.webutils.alerts.agent.IAlertingAgent;
import com.yukthitech.webutils.alerts.event.EventAlertRuleEntity;
import com.yukthitech.webutils.alerts.event.EventAlertRuleService;
import com.yukthitech.webutils.common.action.IAgentAction;
import com.yukthitech.webutils.common.alerts.AlertConfirmationInfo;
import com.yukthitech.webutils.common.alerts.AlertDetails;
import com.yukthitech.webutils.common.alerts.AlertingAgentType;
import com.yukthitech.webutils.services.freemarker.FreeMarkerService;
import com.yukthitech.webutils.services.task.AsyncTaskService;

/**
 * Service to register alerts and push alerts to agents.
 * @author akiran
 */
@Service
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
	
	/**
	 * Name of the component.
	 */
	private static final String COMP_NAME = "AlertEngine";
	
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
	 * Support provided by applications.
	 */
	@Autowired(required = false)
	private IAlertSupport alertSupport;
	
	/**
	 * Custom alert event handler if any.
	 */
	@Autowired(required = false)
	private ICustomAlertEventHandler customAlertEventHandler;

	/**
	 * Mapping from alerting agent type to agent.
	 */
	private Map<AlertingAgentType, IAlertingAgent> typeToAgent = new HashMap<>();
	
	/**
	 * Name of system agent.
	 */
	private String systemAgentName;
	
	/**
	 * Used to cache phase alert objects.
	 */
	private Map<String, PhasedAlert> phasedAlerts = new HashMap<>();
	
	/**
	 * Post construct method to fetch agents.
	 */
	@PostConstruct
	private void init() throws Exception
	{
		for(IAlertingAgent agent : alertingAgents)
		{
			typeToAgent.put(agent.getType(), agent);
		}
		
		if(alertSupport != null)
		{
			systemAgentName = alertSupport.getSystemAgentName();
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
		alertDetails.setName(closedAlert.getName());
		
		//reverse source and target
		alertDetails.setSource(closedAlert.getTarget());
		alertDetails.setTarget(closedAlert.getSource());
		
		if(systemAgentName.equals(closedAlert.getSource()))
		{
			alertDetails.setAlertType(alertSupport.getSystemAlertType());
		}
		else
		{
			alertDetails.setAlertType(alertSupport.getConfirmationAlertType());
		}
		
		//set closed alert id as data on confirmation alert
		alertDetails.setData( new AlertConfirmationInfo(closedAlert.getData(), closedAlert.getAlertProcessedDetails()) );
		
		sendAlert(alertDetails);
	}
	
	/**
	 * Sends alerts to appropriated agents.
	 * @param alertDetails alert to be sent.
	 */
	public void sendAlert(AlertDetails alertDetails)
	{
		logger.debug("Sending alert: {}", alertDetails);

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
			
			for(IAgentAction action : alertDetails.getActions())
			{
				if(action.getLabel().length() > MAX_ACTION_LENGTH)
				{
					throw new InvalidArgumentException("Action length should be less than or equal to 10. Invalid action specified: {}", action);
				}
			}
		}
		
		final Set<AlertingAgentType> targetTypes = alertDetails.getAlertType().getAlertingAgentTypes();
		
		if(targetTypes == null || targetTypes.isEmpty())
		{
			logger.warn("As no target-agent-type is specified ignoring sending alert: {}", alertDetails);
			return;
		}
		
		asyncTaskService.executeTask(COMP_NAME, new Runnable()   
		{
			public void run()
			{
				for(AlertingAgentType type : targetTypes)
				{
					IAlertingAgent agent = typeToAgent.get(type);
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
		logger.debug("Sending system error [Title: {}, Message: {}, Error: {}]", title, message, "" + th);
		AlertDetails alertDetails = new AlertDetails();
		alertDetails.setAlertType(alertSupport.getErrorAlertType());
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
	 * @param eventObject object to be used in context
	 * @param event event rule to be used in context
	 * @return result alert details.
	 */
	private <T> T parseAlertDetails(String template, Object eventObject, EventAlertRuleEntity event, Class<T> type) throws JsonParseException, JsonMappingException, IOException
	{
		Map<String, Object> context = CommonUtils.toMap(
				"eventData", eventObject,
				"event", event
		);
		
		String xml = freeMarkerService.processTemplate("AlertDetails-xml", template, context);
		
		T result = null;
		
		try
		{
			result = type.newInstance();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating instance of result type: {}", type.getName());
		}
		
		XMLBeanParser.parse(new ByteArrayInputStream(xml.getBytes()), result);
		
		
		AlertDetails alertDetails = (result instanceof AlertDetails) ? (AlertDetails) result : null;
		
		if(alertDetails != null && alertDetails.getData() == null)
		{
			alertDetails.setData(eventObject);
		}
		
		return result;
	}
	
	/**
	 * Sends simple system event alert by using specified alert type, event object as alert data
	 * and alert name as name of alert. Thereby calling any registered alert processors by this name.
	 * @param eventObject event object which needs to be sent as alert data
	 * @param alertName name of alert
	 */
	public void sendSystemEventAlert(Object eventObject, String alertName)
	{
		logger.debug("Sending system alert with name: {}", alertName);
		
		AlertDetails alertDetails = new AlertDetails();
		alertDetails.setData(eventObject);
		alertDetails.setAlertType(alertSupport.getSystemAlertType());
		alertDetails.setName(alertName);
		
		sendAlert(alertDetails);
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
	 * Schedules alert to be sent after 30 sec. So that if phased alert comes again with same id, old alert is reaplced
	 * with new one.
	 * @param dynamicId id based on which alert should be grouped
	 * @param eventObject event object to be used
	 * @param eventType event type to be used.
	 */
	public synchronized void sendPhasedEventAlert(final String dynamicId, Object eventObject, String eventType)
	{
		boolean existingAlert = phasedAlerts.containsKey(dynamicId);
		phasedAlerts.put(dynamicId, new PhasedAlert(eventObject, eventType));
		
		if(existingAlert)
		{
			return;
		}
		
		asyncTaskService.executeTask(COMP_NAME, new Runnable()
		{
			@Override
			public void run()
			{
				PhasedAlert phasedAlert = null;
				
				synchronized(AlertEngine.this)
				{
					phasedAlert = phasedAlerts.remove(dynamicId);
				}
				
				sendEventAlerts(phasedAlert.getEventObject(), phasedAlert.getEventType());
			}
		}, 30000);
	}
	
	/**
	 * Sends alerts corresponding to specified event type, if any. 
	 * @param eventObject Event object to be used to parse expressions. This object will be used as free marker context.
	 * @param eventType Event type name whose rules needs to be checked.
	 */
	public void sendEventAlerts(Object eventObject, String eventType)
	{
		logger.debug("Sending alerts for event: {}", eventType);
		
		asyncTaskService.executeTask(COMP_NAME, new Runnable()
		{
			public void run()
			{
				List<EventAlertRuleEntity> rules = eventAlertRuleService.fetchAlerts(eventType);
				
				if(rules == null || rules.isEmpty())
				{
					logger.debug("For alert event '{}' no rules found to execute", eventType);
					return;
				}
				
				for(EventAlertRuleEntity rule : rules)
				{
					try
					{
						if(!checkConditionFunction(rule.getConditionFunction(), eventObject))
						{
							logger.debug("For alert event '{}' rule '{}' is skipped as pre condition is not met", eventType, rule.getName());
							continue;
						}
						
						boolean processed = false;
						
						if(rule.getAlertDetailsTemplate() != null)
						{
							logger.debug("For alert event '{}' processing rule '{}' and sending alert", eventType, rule.getName());
							
							AlertDetails alertDetails = parseAlertDetails(rule.getAlertDetailsTemplate(), eventObject, rule, AlertDetails.class);
							sendAlert(alertDetails);
							
							processed = true;
						}
						
						//handle rules with custom data
						if(customAlertEventHandler != null && rule.getCustomData() != null && rule.getCustomDataType() != null)
						{
							logger.debug("For alert event '{}' processing rule '{}' invoking custom alert event handler", eventType, rule.getName());
							
							Class<?> type = Class.forName(rule.getCustomDataType());
							Object customData = parseAlertDetails(rule.getCustomData(), eventObject, rule, type);
							
							customAlertEventHandler.handleCustomRule(rule, customData);
							processed = true;
						}
						
						if(!processed)
						{
							logger.warn("For alert event '{}' for rule '{}' neither alert was sent not custom event handling was done", eventType, rule.getName());
						}
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
