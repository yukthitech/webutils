package com.test.yukthitech.webutils;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.test.yukthitech.webutils.models.TestAlertType;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.webutils.alerts.agent.IAlertSupport;
import com.yukthitech.webutils.common.alerts.AlertDetails;
import com.yukthitech.webutils.common.alerts.IAlertType;

@Service
public class TestAlertSupport implements IAlertSupport
{

	@Override
	public Set<String> fetchMailRecipients(AlertDetails alertDetails)
	{
		return CommonUtils.toSet("test@test.com");
	}

	@Override
	public Set<String> fetchPullRecipients(AlertDetails alertDetails)
	{
		return CommonUtils.toSet("test@test.com");
	}

	@Override
	public IAlertType getErrorAlertType()
	{
		return TestAlertType.ERROR_ALERT;
	}

	@Override
	public IAlertType getSystemAlertType()
	{
		return TestAlertType.SYSTEM_ALERT;
	}

	@Override
	public IAlertType getConfirmationAlertType()
	{
		return TestAlertType.CONFIRM_ALERT;
	}

	@Override
	public String getSystemAgentName()
	{
		return "Test";
	}

}
