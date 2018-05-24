package com.yukthitech.webutils.parserules.message;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.yukthitech.webutils.common.parserules.mssg.BasicMessageParseRuleModel;
import com.yukthitech.webutils.services.BaseCrudService;

/**
 * Services for message parsing rules.
 * @author akiran
 */
@Service
public class MessageParseRuleService extends BaseCrudService<MessageParseRuleEntity, IMessageParseRuleRepository>
{
	/**
	 * Fetch parsing rules applicable for specified roles.
	 *
	 * @param roles the roles
	 * @return the list of applicable rules.
	 */
	public List<BasicMessageParseRuleModel> fetchParsingRules(Set<Object> roles)
	{
		Set<String> roleStrSet = roles.stream()
				.map(role -> role.toString())
				.collect(Collectors.toSet());
		
		return super.repository.fetchParsingRules(roleStrSet);
	}
}
