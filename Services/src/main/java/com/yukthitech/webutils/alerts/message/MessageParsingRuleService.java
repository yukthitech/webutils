package com.yukthitech.webutils.alerts.message;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.yukthitech.webutils.common.alerts.BasicMessageParsingRuleModel;
import com.yukthitech.webutils.services.BaseCrudService;

/**
 * Services for message parsing rules.
 * @author akiran
 */
@Service
public class MessageParsingRuleService extends BaseCrudService<MessageParsingRuleEntity, IMessageParsingRuleRepository>
{
	/**
	 * Fetch parsing rules applicable for specified roles.
	 *
	 * @param roles the roles
	 * @return the list of applicable rules.
	 */
	public List<BasicMessageParsingRuleModel> fetchParsingRules(Set<Object> roles)
	{
		return super.repository.fetchParsingRules(roles);
	}
}
