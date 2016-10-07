package com.yukthi.webutils.mail.template;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.common.models.mails.MailTemplateConfiguration;
import com.yukthi.webutils.services.ClassScannerService;

/**
 * Service to maintain mail template configurations.
 * @author akiran
 */
@Service
public class MailTemplateConfigService
{
	/**
	 * Cache map which maintains mail template configuration.
	 */
	private TreeMap<String, MailTemplateConfiguration> nameToConfig = new TreeMap<>();
	
	/**
	 * Class scan service, to scan mail template configuraions.
	 */
	@Autowired
	private ClassScannerService classScannerService;
	
	/**
	 * Post construct method used to load configurations.
	 */
	@PostConstruct
	private void init()
	{
		Set<Class<?>> configTypes = classScannerService.getClassesWithAnnotation(MailTemplateConfig.class);
		
		if(configTypes == null)
		{
			return;
		}
		
		MailTemplateConfig mailTemplateConfig = null;
		MailTemplateConfiguration newConfig = null;
		
		for(Class<?> type : configTypes)
		{
			mailTemplateConfig = type.getAnnotation(MailTemplateConfig.class);
			newConfig = new MailTemplateConfiguration(mailTemplateConfig.name(), mailTemplateConfig.description(), type.getName());
			
			if(nameToConfig.containsKey(newConfig.getName()))
			{
				throw new InvalidStateException("Same name '{}' is used by multiple mail template configurations - {}, {}", 
						newConfig.getName(), type.getName(), 
						nameToConfig.get(newConfig.getName()).getType());
			}
			
			loadFields(type, "", newConfig);
			
			if(mailTemplateConfig.attachments().length > 0)
			{
				for(AttachmentConfig attachment : mailTemplateConfig.attachments())
				{
					newConfig.addAttachment(new MailTemplateConfiguration.Attachment(attachment.name(), attachment.description(), attachment.image()));
				}
			}
			
			nameToConfig.put(newConfig.getName(), newConfig);
		}
	}
	
	/**
	 * Returns true, if the specified type can be used directly or needed to do recursion.
	 * @param type Type to check.
	 * @return true if direct type.
	 */
	private boolean isDirectType(Class<?> type)
	{
		if(type.isPrimitive() || CommonUtils.isWrapperClass(type))
		{
			return true;
		}
		
		if(String.class.equals(type) || Date.class.equals(type))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Loads the field details from specified type into specified configuration.
	 * @param type Type to load from.
	 * @param prefix Prefix to be used on fields (used in recursion)
	 * @param newConfig Config object to which field details should be loaded.
	 */
	private void loadFields(Class<?> type, String prefix, MailTemplateConfiguration newConfig)
	{
		Field fields[] = type.getDeclaredFields();
		MailConfigField mailConfigField = null;
		
		Class<?> fieldType = null, fieldCollectionType = null;
		String fieldName = null;
		
		for(Field field : fields)
		{
			mailConfigField = field.getAnnotation(MailConfigField.class);
			
			if(mailConfigField == null)
			{
				continue;
			}
			
			fieldName = field.getName();
			fieldType = field.getType();
			
			if(Collection.class.isAssignableFrom(fieldType))
			{
				fieldCollectionType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
				
				newConfig.addField(new MailTemplateConfiguration.Field(fieldName, mailConfigField.description(), fieldCollectionType.getName() + "[]"));
				
				if(!isDirectType(fieldCollectionType))
				{
					loadFields(fieldCollectionType, prefix + fieldName + "[].", newConfig);
				}
			}
			else if(Map.class.isAssignableFrom(fieldType))
			{
				//Maps will not of good help in free marker, so exluding map types
				throw new InvalidStateException("Field {}.{} is a map field which is marked as mail-config-template. Maps are currently not supported.", type.getName(), fieldName);
			}
			else if(isDirectType(fieldType))
			{
				newConfig.addField(new MailTemplateConfiguration.Field(prefix + fieldName, mailConfigField.description(), fieldType.getName()));
			}
			else
			{
				newConfig.addField(new MailTemplateConfiguration.Field(fieldName, mailConfigField.description(), fieldType.getName() + "{}"));
				loadFields(fieldType, prefix + fieldName + ".", newConfig);
			}
		}
	}
	
	/**
	 * Fetches available mail template configuration names.
	 * @return Mail template configuration names.
	 */
	public Set<String> getMailTemplateConfigurationNames()
	{
		return Collections.unmodifiableSet(nameToConfig.keySet());
	}
	
	/**
	 * Fetches mail template configuration with specified name.
	 * @param name Name of the configuration to fetch.
	 * @return Matching configuration.
	 */
	public MailTemplateConfiguration getMailTemplateConfiguration(String name)
	{
		return nameToConfig.get(name);
	}
}
