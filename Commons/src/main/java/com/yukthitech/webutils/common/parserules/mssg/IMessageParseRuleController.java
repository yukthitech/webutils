package com.yukthitech.webutils.common.parserules.mssg;

import com.yukthitech.webutils.common.controllers.ICrudController;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicReadListResponse;

/**
 * Controller to access alerts.
 * @author akiran
*/
public interface IMessageParseRuleController extends ICrudController<MessageParseRuleModel, IMessageParseRuleController>
{
	/**
	 * Fetches parsing rules for current user.
	 * @return matching rules.
	 */
	public BasicReadListResponse<BasicMessageParseRuleModel> fetchParsingRules();
	
	/**
	 * Indicates a match is found for specified rule and also specifies parsed message.
	 * @param parsingRuleId parsing rule as per which match is found
	 * @param pasedMessage message which is matched with specified rule
	 * @return success or failure status.
	 */
	public BaseResponse matchFound(long parsingRuleId, ParsedMessage pasedMessage);
}
