package com.yukthitech.webutils.parserules.mail;

import java.util.List;
import java.util.Set;

import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.JoinOperator;
import com.yukthitech.persistence.repository.annotations.MethodConditions;
import com.yukthitech.persistence.repository.annotations.NullCheck;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.persistence.repository.annotations.SearchResult;
import com.yukthitech.webutils.common.annotations.Optional;
import com.yukthitech.webutils.common.parserules.mail.MailParseRuleModel;
import com.yukthitech.webutils.repository.IWebutilsRepository;

/**
 * Repository for mail parsing rules.
 * @author akiran
 */
@Optional
public interface IMailParseRuleRepository extends IWebutilsRepository<MailParseRuleEntity>
{
	/**
	 * Fetch parsing rules applicable for specified roles.
	 *
	 * @param roles the roles
	 * @return the list of applicable rules.
	 */
	@MethodConditions(nullChecks = {
		@NullCheck(field = "userRole", joinOperator = JoinOperator.OR)
		})
	@SearchResult
	public List<MailParseRuleModel> fetchParsingRules(@Condition(value = "userRoleString", op = Operator.IN) Set<String> roles);
}
