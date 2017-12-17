package com.yukthitech.webutils.mail.template;

import org.springframework.stereotype.Service;

import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.webutils.common.mailtemplate.MailTemplateModel;
import com.yukthitech.webutils.services.BaseCrudService;

/**
 * Service class to manage mail templates.
 * @author akiran
 */
@Service
public class MailTemplateService extends BaseCrudService<MailTemplateEntity, IMailTemplateRepository>
{
	/**
	 * Instantiates a new mail template service.
	 */
	public MailTemplateService()
	{
		//super(MailTemplateEntity.class, IMailTemplateRepository.class);
	}
	
	/**
	 * Fetches mail template with specified name and ownership.
	 * @param templateName Name of the template.
	 * @param ownerEntityType Owner entity type.
	 * @param ownerEntityId Owner entity id.
	 * @return Matching mail template.
	 */
	public MailTemplateEntity fetchByOwner(String templateName, String ownerEntityType,	Long ownerEntityId)
	{
		return super.repository.fetchByOwner(templateName, ownerEntityType, ownerEntityId);
	}

	/**
	 * Deletes mails under specified ownership.
	 * @param ownerEntityType Owner entity type.
	 * @param ownerEntityId Owner entity id.
	 * @return Number of records deleted.
	 */
	public int deleteByOwner(@Condition("ownerEntityType") String ownerEntityType, @Condition("ownerEntityId") Long ownerEntityId)
	{
		return repository.deleteByOwner(ownerEntityType, ownerEntityId);
	}
	
	/**
	 * Fetches the mail template with specified name.
	 * @param name name of template to fetch
	 * @return matching template
	 */
	public MailTemplateModel fetchByName(String name)
	{
		MailTemplateEntity entity = repository.fetchByName(name);
		return toModel(entity, MailTemplateModel.class);
	}
}
