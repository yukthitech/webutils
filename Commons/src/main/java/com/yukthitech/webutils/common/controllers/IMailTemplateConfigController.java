package com.yukthitech.webutils.common.controllers;

import com.yukthitech.webutils.common.RemoteService;
import com.yukthitech.webutils.common.models.BasicReadListResponse;
import com.yukthitech.webutils.common.models.BasicReadResponse;
import com.yukthitech.webutils.common.models.mails.MailTemplateConfiguration;

/**
 * Controller for fetching mail template configurations.
 * @author akiran
 */
@RemoteService
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