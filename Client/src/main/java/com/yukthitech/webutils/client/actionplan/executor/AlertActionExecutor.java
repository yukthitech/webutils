package com.yukthitech.webutils.client.actionplan.executor;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.webutils.client.actionplan.ActionPlanExecutionContext;
import com.yukthitech.webutils.common.action.AlertAgentAction;
import com.yukthitech.webutils.common.alerts.AlertDetails;
import com.yukthitech.webutils.common.alerts.IAlertController;

/**
 * Alert action executor.
 * @author akiran
 */
public class AlertActionExecutor implements IActionExecutor<AlertAgentAction>
{
	@SuppressWarnings({ "unchecked" })
	@Override
	public void executeAction(ActionPlanExecutionContext context, AlertAgentAction action) throws Exception
	{
		AlertDetails alertDetails = action.getAlert();
		
		alertDetails.setTitle(context.processTemplate(alertDetails.getTitle()));
		alertDetails.setMessage(context.processTemplate(alertDetails.getMessage()));
		
		if(StringUtils.isNotBlank(action.getAlertDataJson()))
		{
			String dataJson = context.processTemplate(action.getAlertDataJson());
			alertDetails.setData( context.parseJson(dataJson, Object.class) );
		}
		
		IAlertController<Object> alertController = (IAlertController<Object>) context.getClientControllerFactory().getController(IAlertController.class);
		alertController.sendAlert(alertDetails, null);
		
		context.executeNextAction(null);
	}
}
