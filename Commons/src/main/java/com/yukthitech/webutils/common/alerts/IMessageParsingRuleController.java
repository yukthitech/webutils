package com.yukthitech.webutils.common.alerts;

import com.yukthitech.webutils.common.controllers.ICrudController;
import com.yukthitech.webutils.common.models.BasicReadListResponse;

/**
 * Controller to access alerts.
 * @author akiran
*/
public interface IMessageParsingRuleController extends ICrudController<MessageParsingRuleModel, IMessageParsingRuleController>
{
	/**
	 * Fetches parsing rules for current user.
	 * @return matching rules.
	 */
	public BasicReadListResponse<MessageParsingRuleModel> fetchParsingRules();
}
