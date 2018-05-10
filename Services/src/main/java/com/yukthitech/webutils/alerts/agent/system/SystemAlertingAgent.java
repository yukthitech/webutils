package com.yukthitech.webutils.alerts.agent.system;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.webutils.alerts.agent.IAlertingAgent;
import com.yukthitech.webutils.common.alerts.AlertDetails;
import com.yukthitech.webutils.common.alerts.AlertingAgentType;
import com.yukthitech.webutils.services.ServiceMethod;
import com.yukthitech.webutils.services.SpringUtilsService;
import com.yukthitech.webutils.services.freemarker.FreeMarkerService;

/**
 * Abstract class alerting agent which invokes alert processors within the application.
 * @author akiran
 */
@Service
public class SystemAlertingAgent implements IAlertingAgent
{
	private static Logger logger = LogManager.getLogger(SystemAlertingAgent.class);
	
	/**
	 * Details of alert processor.
	 * @author akiran
	 */
	private static class AlertProcessor
	{
		/**
		 * Service method to be invoked.
		 */
		private ServiceMethod serviceMethod;
		
		/**
		 * Confirmation flag from annotation.
		 */
		private boolean confirmation;
		
		/**
		 * Condition from annotation.
		 */
		private String condition;

		/**
		 * Instantiates a new alert processor.
		 *
		 * @param serviceMethod the service method
		 * @param confirmation the confirmation
		 * @param condition the condition
		 */
		public AlertProcessor(ServiceMethod serviceMethod, boolean confirmation, String condition)
		{
			this.serviceMethod = serviceMethod;
			this.confirmation = confirmation;
			this.condition = condition;
		}
	}
	
	/**
	 * Handler to handle proxy context object method invocations.
	 * @author akiran
	 */
	private class ContextInvocationHandler implements InvocationHandler
	{
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			if("setAttribute".equals(method.getName()))
			{
				appAlertContextService.setAttribute((String) args[0], args[1]);
			}
			else if("getAttribute".equals(method.getName()))
			{
				return appAlertContextService.getAttribute((String) args[0]);
			}
			else if("removeAttribte".equals(method.getName()))
			{
				appAlertContextService.removeAttribute((String) args[0]);
			}
			
			return null;
		}
	}
	
	/**
	 * Context to fetch all registered application alert processor methods.
	 */
	@Autowired
	private SpringUtilsService springUtilsService;
	
	/**
	 * Used to evaluate method conditions.
	 */
	@Autowired
	private FreeMarkerService freeMarkerService;
	
	/**
	 * Service to manage context attributes.
	 */
	@Autowired
	private SystemAlertContextAttrService appAlertContextService;

	/**
	 * Mapping from target to processor.
	 */
	private Map<String, List<AlertProcessor>> nameToProcessors;
	
	/**
	 * Invocation handler that will be used proxy context objects.
	 */
	private ContextInvocationHandler contextInvocationHandler = new ContextInvocationHandler();

	@Override
	public AlertingAgentType getType()
	{
		return AlertingAgentType.SYSTEM_ALERT;
	}

	/**
	 * Fetches application alert processors from spring context.
	 */
	private synchronized void fetchAlertProcessors()
	{
		if(nameToProcessors != null)
		{
			return;
		}
		
		nameToProcessors = new HashMap<>();
		
		List<ServiceMethod> serviceMethods = springUtilsService.fetchServiceMethods("App-alert-processor", 
				SystemAlertProcessor.class, //expected annotation 
				ISystemAlertProcessorContext.class, AlertDetails.class //expected arguments
				);
		
		if(serviceMethods == null || serviceMethods.isEmpty())
		{
			return;
		}
		
		SystemAlertProcessor sysAlertProcessor = null;
		List<AlertProcessor> serviceMethodLst = null;
		
		for(ServiceMethod serviceMethod : serviceMethods)
		{
			sysAlertProcessor = serviceMethod.getMethod().getAnnotation(SystemAlertProcessor.class);
			serviceMethodLst = nameToProcessors.get(sysAlertProcessor.name());
			
			if(serviceMethodLst == null)
			{
				serviceMethodLst = new ArrayList<>();
				nameToProcessors.put(sysAlertProcessor.name(), serviceMethodLst);
			}
			
			serviceMethodLst.add(new AlertProcessor(serviceMethod, sysAlertProcessor.confirmation(), sysAlertProcessor.condition()));
		}
	}

	@Override
	public boolean sendAlert(AlertDetails alertDetails)
	{
		if(nameToProcessors == null)
		{
			fetchAlertProcessors();
		}
		
		List<AlertProcessor> alertProcessors = nameToProcessors.get(alertDetails.getName());
		
		if(alertProcessors == null || alertProcessors.isEmpty())
		{
			logger.warn("Application alert is ignored as no processor found with specified target. Alert received: {}", alertDetails);
			return false;
		}
		
		ISystemAlertProcessorContext context = (ISystemAlertProcessorContext) Proxy.newProxyInstance(
				SystemAlertingAgent.class.getClassLoader(), 
				new Class[] {ISystemAlertProcessorContext.class}, 
				contextInvocationHandler);
		
		Object args[] = new Object[] {context, alertDetails};

		for(AlertProcessor alertProcessor : alertProcessors)
		{
			if(alertDetails.isConfirmationAlert() != alertProcessor.confirmation)
			{
				continue;
			}
			
			if(StringUtils.isNotBlank(alertProcessor.condition))
			{
				if(!freeMarkerService.processMethodCondition(alertProcessor.condition, alertProcessor.serviceMethod.getMethod(), 
						alertProcessor.serviceMethod.getService(), args, null))
				{
					continue;
				}
			}
			
			try
			{
				alertProcessor.serviceMethod.invoke(args);
			} catch(Exception ex)
			{
				logger.error("An error occurred while processing alert. Alert: {}", alertDetails, ex);
			}
		}
		
		return true;
	}
}
