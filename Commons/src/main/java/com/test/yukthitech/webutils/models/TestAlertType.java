package com.test.yukthitech.webutils.models;

import java.util.Set;

import com.yukthitech.webutils.common.alerts.AlertingAgentType;
import com.yukthitech.webutils.common.alerts.IAlertType;

import com.yukthitech.utils.CommonUtils;

public enum TestAlertType implements IAlertType
{
	ERROR_ALERT(CommonUtils.toSet(AlertingAgentType.SYSTEM_ALERT)),
	
	SYSTEM_ALERT(CommonUtils.toSet(AlertingAgentType.SYSTEM_ALERT)),
	
	CONFIRM_ALERT(CommonUtils.toSet(AlertingAgentType.SYSTEM_ALERT)),
	
	TEST_ALERT(CommonUtils.toSet(AlertingAgentType.PULL_ALERTING_AGENT))
	
	;
	
	Set<AlertingAgentType> alertAgentTypes;
	
	private TestAlertType(Set<AlertingAgentType> alertAgentTypes)
	{
		this.alertAgentTypes = alertAgentTypes;
	}

	@Override
	public Set<AlertingAgentType> getAlertingAgentTypes()
	{
		return alertAgentTypes;
	}
}
