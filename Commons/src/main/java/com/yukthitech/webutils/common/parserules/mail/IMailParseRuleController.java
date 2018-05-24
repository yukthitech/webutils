package com.yukthitech.webutils.common.parserules.mail;

import com.yukthitech.webutils.common.controllers.ICrudController;
import com.yukthitech.webutils.common.models.BasicReadListResponse;

/**
 * Controller to access mail parsing rules.
 * @author akiran
*/
public interface IMailParseRuleController extends ICrudController<MailParseRuleModel, IMailParseRuleController>
{
	/**
	 * Fetches parsing rules for current user.
	 * @return matching rules.
	 */
	public BasicReadListResponse<MailParseRuleModel> fetchParsingRules();
}
