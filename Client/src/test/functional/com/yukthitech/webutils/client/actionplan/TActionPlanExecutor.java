package com.yukthitech.webutils.client.actionplan;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.test.yukthitech.webutils.client.TFBase;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.webutils.common.actionplan.ActionPlan;

/**
 * Test action plan executor.
 * @author akiran
 */
public class TActionPlanExecutor extends TFBase
{
	/**
	 * Test action plan execution.
	 */
	@Test
	public void testActionPlanExecution()
	{
		ActionPlan actionPlan = new ActionPlan();
		XMLBeanParser.parse(TActionPlanExecutor.class.getResourceAsStream("/test-action-plan.xml"), actionPlan);
		
		ActionPlanExecutor.executeActionPlan(super.clientControllerFactory, actionPlan, new HashMap<String, Object>(), new FreeMarkerTemplateProcessor(), null);
	}
}
