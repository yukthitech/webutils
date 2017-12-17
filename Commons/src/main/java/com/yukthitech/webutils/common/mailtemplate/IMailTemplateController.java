package com.yukthitech.webutils.common.mailtemplate;

import com.yukthitech.webutils.common.RemoteService;
import com.yukthitech.webutils.common.controllers.ICrudController;
import com.yukthitech.webutils.common.models.BasicReadResponse;

/**
 * Controller for operations on mail templates.
 * @author akiran
 */
@RemoteService
public interface IMailTemplateController extends ICrudController<MailTemplateModel>
{
	/**
	 * Fetches the mail template with specified name.
	 * @param name name of template to fetch
	 * @return matching template
	 */
	public BasicReadResponse<MailTemplateModel> fetchByName(String name);
}