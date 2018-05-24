package com.yukthitech.webutils.parserules.mail;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.yukthitech.webutils.common.parserules.mail.MailParseRuleModel;
import com.yukthitech.webutils.services.BaseCrudService;

/**
 * Services for mail parsing rules.
 * @author akiran
 */
@Service
public class MailParseRuleService extends BaseCrudService<MailParseRuleEntity, IMailParseRuleRepository>
{
	/**
	 * Fetch parsing rules applicable for specified roles.
	 *
	 * @param roles the roles
	 * @return the list of applicable rules.
	 */
	public List<MailParseRuleModel> fetchParseRules(Set<Object> roles)
	{
		Set<String> roleStrSet = roles.stream()
				.map(role -> role.toString())
				.collect(Collectors.toSet());
		
		return super.repository.fetchParsingRules(roleStrSet);
	}
}
