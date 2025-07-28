package com.yukthitech.webutils.services.prop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.yukthitech.utils.exceptions.InvalidStateException;

public class FieldDescriptor
{
	/**
	 * Field of a a class.
	 */
	private Field field;

	public FieldDescriptor(Field field)
	{
		this.field = field;
		this.field.setAccessible(true);
	}
	
	public static Map<String, FieldDescriptor> loadFields(Class<?> beanType)
	{
		Class<?> curType = beanType;
		Map<String, FieldDescriptor> res = new HashMap<>();
		
		while(true)
		{
			if(curType == null || curType.getName().startsWith("java"))
			{
				break;
			}
			
			Field[] fields = beanType.getDeclaredFields();
			
			for(Field field : fields)
			{
				if(Modifier.isStatic(field.getModifiers()))
				{
					continue;
				}
				
				res.put(field.getName(), new FieldDescriptor(field));
			}
			
			curType = curType.getSuperclass();
		}
		
		return res;
	}
	
	public Object getValue(Object bean)
	{
		try
		{
			return field.get(bean);
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while fetching field ({}.{}) value", field.getDeclaringClass().getName(), field.getName(), ex);
		}
	}
	
	public void setValue(Object bean, Object value)
	{
		try
		{
			field.set(bean, value);
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while setting field ({}.{}) value: {}", 
					field.getDeclaringClass().getName(), field.getName(), value, ex);
		}
	}
	
	public <A extends Annotation> A getAnnotation(Class<A> annotationType)
	{
		return field.getAnnotation(annotationType);
	}
	
	public Class<?> getType()
	{
		return field.getType();
	}
	
	public Type getGenericType()
	{
		return field.getGenericType();
	}
}
