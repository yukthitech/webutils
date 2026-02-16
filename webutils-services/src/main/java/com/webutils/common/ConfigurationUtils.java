package com.webutils.common;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.env.Environment;

import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.Encryptor;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class ConfigurationUtils
{

	public static final Pattern ENC_PATTERN = Pattern.compile("^enc\\((.*)\\)$");

	public static <T> T buildConfiguration(Class<T> type, String prefix, Environment environment, Encryptor encryptor)
	{
		try
		{
			T config = type.getDeclaredConstructor().newInstance();

			for(Field field : type.getDeclaredFields())
			{
				// Skip static and/or final fields (constants) - cannot be set
				// via reflection in newer JDK versions
				int modifiers = field.getModifiers();
				if(Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers))
				{
					continue;
				}

				field.setAccessible(true);

				Object value = environment.getProperty(prefix + "." + field.getName());

				if(value == null)
				{
					continue;
				}

				Matcher matcher = ENC_PATTERN.matcher(value.toString());

				if(matcher.matches())
				{
					value = encryptor.decrypt(matcher.group(1));
				}

				value = ConvertUtils.convert(value, field.getType());

				field.set(config, value);
			}

			return config;
		} catch(Exception e)
		{
			throw new InvalidStateException("Error building configuration", e);
		}
	}
}
