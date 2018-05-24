package com.yukthitech.webutils.parserules.mail;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.common.actionplan.ActionPlan;
import com.yukthitech.webutils.common.client.IRequestCustomizer;
import com.yukthitech.webutils.common.models.BasicReadListResponse;
import com.yukthitech.webutils.common.parserules.mail.IMailParseRuleController;
import com.yukthitech.webutils.common.parserules.mail.MailParseRuleModel;
import com.yukthitech.webutils.controllers.BaseCrudController;
import com.yukthitech.webutils.services.CurrentUserService;

/**
 * Controller for alerts.
 * @author akiran
 */
@RestController
@RequestMapping("/parseRules/mail")
@ActionName("mailParseRule")
public class MailParseRuleController extends BaseCrudController<MailParseRuleModel, MailParseRuleService, IMailParseRuleController> implements IMailParseRuleController
{
	/**
	 * Used to access current user roles.
	 */
	@Autowired
	private CurrentUserService currentUserService;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ActionName("fetchParseRules")
	@RequestMapping(value = "/fetchParseRules", method = RequestMethod.GET)
	@ResponseBody
	@Override
	public BasicReadListResponse<MailParseRuleModel> fetchParsingRules()
	{
		Set<Object> roles = (Set) currentUserService.getCurrentUserDetails().getRoles();
		
		List<MailParseRuleModel> rules = super.getService().fetchParseRules(roles);
		
		if(rules == null)
		{
			rules = Collections.emptyList();
		}
		else
		{
			for(MailParseRuleModel model : rules)
			{
				if(model.getActionPlanXml() == null)
				{
					continue;
				}
				
				ActionPlan actionPlan = new ActionPlan();
				XMLBeanParser.parse(new ByteArrayInputStream(model.getActionPlanXml().getBytes()), actionPlan);
				
				model.setActionPlan( actionPlan );
			}
		}
		
		return new BasicReadListResponse<>(rules);
	}
	
	@Override
	public IMailParseRuleController setRequestCustomizer(IRequestCustomizer customizer)
	{
		return null;
	}
}
