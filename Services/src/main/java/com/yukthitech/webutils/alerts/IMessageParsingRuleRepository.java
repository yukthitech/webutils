package com.yukthitech.webutils.alerts;

import java.util.List;
import java.util.Set;

import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.JoinOperator;
import com.yukthitech.persistence.repository.annotations.MethodConditions;
import com.yukthitech.persistence.repository.annotations.NullCheck;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.persistence.repository.annotations.SearchResult;
import com.yukthitech.webutils.common.alerts.MessageParsingRuleModel;
import com.yukthitech.webutils.repository.IWebutilsRepository;

/**
 * Repository for message parsing rules.
 * @author akiran
 */
public interface IMessageParsingRuleRepository extends IWebutilsRepository<MessageParsingRuleEntity>
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
	public List<MessageParsingRuleModel> fetchParsingRules(@Condition(value = "userRole", op = Operator.IN) Set<Object> roles);
}
