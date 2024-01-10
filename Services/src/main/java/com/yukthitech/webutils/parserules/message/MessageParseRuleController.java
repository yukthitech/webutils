package com.yukthitech.webutils.parserules.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.InvalidRequestParameterException;
import com.yukthitech.webutils.alerts.AlertEngine;
import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.common.client.IRequestCustomizer;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicReadListResponse;
import com.yukthitech.webutils.common.parserules.MessageParser;
import com.yukthitech.webutils.common.parserules.mssg.BasicMessageParseRuleModel;
import com.yukthitech.webutils.common.parserules.mssg.IMessageParseRuleController;
import com.yukthitech.webutils.common.parserules.mssg.MessageParseRuleModel;
import com.yukthitech.webutils.common.parserules.mssg.ParsedMessage;
import com.yukthitech.webutils.controllers.BaseCrudController;
import com.yukthitech.webutils.security.ISecurityService;
import com.yukthitech.webutils.services.CurrentUserService;

import jakarta.validation.Valid;

/**
 * Controller for alerts.
 * @author akiran
 */
@RestController
@RequestMapping("/parseRules/mssg")
@ActionName("mssgParseRules")
public class MessageParseRuleController extends BaseCrudController<MessageParseRuleModel, MessageParseRuleService, IMessageParseRuleController> implements IMessageParseRuleController
{
	/**
	 * Used to access current user roles.
	 */
	@Autowired
	private CurrentUserService currentUserService;
	
	/**
	 * Used to parse incoming messages.
	 */
	private MessageParser messageParser = new MessageParser();
	
	/**
	 * Used to fetch target user contact details.
	 */
	@Autowired
	private ISecurityService securityService;
	
	/**
	 * Used to send alerts based on parsed rules.
	 */
	@Autowired(required = false)
	private AlertEngine alertEngine;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ActionName("fetchParsingRules")
	@RequestMapping(value = "/fetchParsingRules", method = RequestMethod.GET)
	@ResponseBody
	@Override
	public BasicReadListResponse<BasicMessageParseRuleModel> fetchParsingRules()
	{
		Set<Object> roles = (Set) currentUserService.getCurrentUserDetails().getRoles();
		
		List<BasicMessageParseRuleModel> rules = super.getService().fetchParsingRules(roles);
		
		if(rules == null)
		{
			rules = Collections.emptyList();
		}
		
		for(BasicMessageParseRuleModel rule : rules)
		{
			if(rule.getTargetUserRole() == null)
			{
				continue;
			}
			
			//if target role is specified, fetch approp user contact details and attach them to rule
			rule.setTargetUsers( securityService.fetchUserContactDetails(rule.getTargetUserRole()) );
		}
		
		return new BasicReadListResponse<>(rules);
	}
	
	@ActionName("matchFound")
	@RequestMapping(value = "/matchFound/{parsingRuleId}", method = RequestMethod.POST)
	@ResponseBody
	@Override
	public BaseResponse matchFound(@PathVariable("parsingRuleId") long parsingRuleId, @RequestBody @Valid ParsedMessage parsedMessage)
	{
		MessageParseRuleEntity parsingRuleEntity = super.getService().fetch(parsingRuleId);
		
		if(parsingRuleEntity == null)
		{
			throw new InvalidRequestParameterException("No parsing rule found with id: " + parsingRuleId);
		}
		
		BasicMessageParseRuleModel basicRule = new BasicMessageParseRuleModel();
		
		try
		{
			BeanUtils.copyProperties(basicRule, parsingRuleEntity);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while building basic rule object", ex);
		}
		
		ObjectWrapper<String> errMssg = new ObjectWrapper<>();
		
		if(!parsedMessage.isMatchingWith(basicRule, errMssg))
		{
			throw new InvalidRequestParameterException("Specified message is not matching with specified rule: {}.\nError: {}", parsingRuleId, errMssg.getValue());
		}
		
		//Build values by parsing message body with patterns
		Map<String, Object> parsedModel = new HashMap<String, Object>();
		
		if(parsingRuleEntity.getDefaultAttributes() != null)
		{
			parsedModel.putAll(parsingRuleEntity.getDefaultAttributes());
		}
		
		if(parsingRuleEntity.getMessagePatterns() != null)
		{
			for(String patternStr : parsingRuleEntity.getMessagePatterns())
			{
				messageParser.loadContext(patternStr, parsedMessage.getMessage(), parsedModel);
			}
		}
		
		//load system default variables
		parsedModel.put("message", parsedMessage);
		
		//send event alert
		alertEngine.sendEventAlerts(parsedModel, parsingRuleEntity.getAlertEventType());
		
		return new BaseResponse();
	}
	
	@Override
	public IMessageParseRuleController setRequestCustomizer(IRequestCustomizer customizer)
	{
		return null;
	}
}
