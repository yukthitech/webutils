package com.webutils.services.test;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.webutils.common.test.SetConfigRequest;
import com.webutils.services.common.InvalidRequestException;
import com.yukthitech.utils.ConvertUtils;

/**
 * Mutates an in-memory Spring bean field for test runtime overrides.
 */
@Service
public class TestConfigService
{
	@Autowired
	private ApplicationContext applicationContext;

	public void setConfig(SetConfigRequest request)
	{
		Class<?> beanType;
		try
		{
			beanType = Class.forName(request.getClassName());
		}
		catch(ClassNotFoundException ex)
		{
			throw new InvalidRequestException("Class not found: {}", request.getClassName());
		}

		Object bean;
		try
		{
			bean = applicationContext.getBean(beanType);
		}
		catch(NoUniqueBeanDefinitionException ex)
		{
			throw new InvalidRequestException("Multiple Spring beans found for class: {}", request.getClassName());
		}
		catch(NoSuchBeanDefinitionException ex)
		{
			throw new InvalidRequestException("No Spring bean found for class: {}", request.getClassName());
		}

		Object target = bean;
		Field field = FieldUtils.getField(target.getClass(), request.getPropertyName(), true);
		if(field == null)
		{
			throw new InvalidRequestException("Property not found: {}", request.getPropertyName());
		}

		Object converted;
		try
		{
			converted = ConvertUtils.convert(request.getValue(), field.getType());
		}
		catch(Exception ex)
		{
			throw new InvalidRequestException("Unable to convert value '{}' to type {}",
					request.getValue(), field.getType().getName());
		}

		try
		{
			field.setAccessible(true);
			field.set(target, converted);
		}
		catch(IllegalAccessException ex)
		{
			throw new InvalidRequestException("Unable to update property: {}", request.getPropertyName());
		}
	}
}
