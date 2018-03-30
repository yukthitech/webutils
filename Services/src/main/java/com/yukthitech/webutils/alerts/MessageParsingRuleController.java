package com.yukthitech.webutils.alerts;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.common.alerts.IMessageParsingRuleController;
import com.yukthitech.webutils.common.alerts.MessageParsingRuleModel;
import com.yukthitech.webutils.common.client.IRequestCustomizer;
import com.yukthitech.webutils.common.models.BasicReadListResponse;
import com.yukthitech.webutils.controllers.BaseCrudController;
import com.yukthitech.webutils.services.CurrentUserService;

/**
 * Controller for alerts.
 * @author akiran
 */
@RestController
@RequestMapping("/alerts/parsingRules")
@ActionName("alertParsingRules")
public class MessageParsingRuleController extends BaseCrudController<MessageParsingRuleModel, MessageParsingRuleService, IMessageParsingRuleController> implements IMessageParsingRuleController
{
	/**
	 * Used to access current user roles.
	 */
	@Autowired
	private CurrentUserService currentUserService;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public BasicReadListResponse<MessageParsingRuleModel> fetchParsingRules()
	{
		Set<Object> roles = (Set) currentUserService.getCurrentUserDetails().getRoles();
		
		List<MessageParsingRuleModel> rules = super.getService().fetchParsingRules(roles);
		
		if(rules == null)
		{
			rules = Collections.emptyList();
		}
		
		return new BasicReadListResponse<>(rules);
	}
	
	@Override
	public IMessageParsingRuleController setRequestCustomizer(IRequestCustomizer customizer)
	{
		return null;
	}
}
