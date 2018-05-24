package com.yukthitech.webutils.parserules.message;

import java.util.List;
import java.util.Set;

import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.JoinOperator;
import com.yukthitech.persistence.repository.annotations.MethodConditions;
import com.yukthitech.persistence.repository.annotations.NullCheck;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.persistence.repository.annotations.SearchResult;
import com.yukthitech.webutils.common.parserules.mssg.BasicMessageParseRuleModel;
import com.yukthitech.webutils.repository.IWebutilsRepository;

/**
 * Repository for message parsing rules.
 * @author akiran
 */
public interface IMessageParseRuleRepository extends IWebutilsRepository<MessageParseRuleEntity>
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
	public List<BasicMessageParseRuleModel> fetchParsingRules(@Condition(value = "userRoleString", op = Operator.IN) Set<String> roles);
}
