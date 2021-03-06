package com.yukthitech.webutils.common.mailtemplate;

import com.yukthitech.webutils.common.models.BasicReadListResponse;
import com.yukthitech.webutils.common.models.BasicReadResponse;

/**
 * Controller for fetching mail template configurations.
 * @author akiran
 */
public interface IMailTemplateConfigController
{
	/**
	 * Used to fetch names of available mail template configurations.
	 * @return List of available configuraion names.
	 */
	BasicReadListResponse<String> fetchNames();

	/**
	 * Fetches mail template configuration with specified name.
	 * @param name Name of mail template configuration to fetch.
	 * @return Matching mail template configuration.
	 */
	BasicReadResponse<MailTemplateConfiguration> fetchConfiguration(String name);
}