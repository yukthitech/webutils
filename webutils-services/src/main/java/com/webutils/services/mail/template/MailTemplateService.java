package com.webutils.services.mail.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webutils.common.repo.IMissingTableRepository;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Service class to manage mail templates.
 * @author akiran
 */
@Service
public class MailTemplateService
{
	@Autowired
	private IMailTemplateRepository repository;
	
	/**
	 * Fetches mail template with specified name and ownership.
	 * @param templateName Name of the template.
	 * @return Matching mail template.
	 */
	public MailTemplateEntity fetchBySpace(String templateName, String customSpace)
	{
		if(repository instanceof IMissingTableRepository)
		{
			throw new InvalidStateException("No mail template table is defined");
		}
		
		return repository.fetchByNameAndSpace(templateName, customSpace);
	}

	/**
	 * Fetches the mail template with specified name.
	 * @param name name of template to fetch
	 * @return matching template
	 */
	public MailTemplateEntity fetchByName(String name)
	{
		if(repository instanceof IMissingTableRepository)
		{
			throw new InvalidStateException("No mail template table is defined");
		}
		
		MailTemplateEntity entity = repository.fetchByName(name);
		return entity;
	}
}
