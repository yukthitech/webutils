package com.yukthitech.webutils.common;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

import org.apache.commons.lang3.reflect.TypeUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Contains common util methods required by webutils.
 * @author akiran
 */
public class WebutilsCommonUtils
{
	/**
	 * Generates bare minimal method signature that can be used to identify method uniquely.
	 * @param activeClass Active class from which "method" is accessible.
	 * @param method Method for which signature needs to be generated
	 * @return Bare minimal method signature
	 */
	public static String getMethodSignature(Class<?> activeClass, Method method)
	{
		try
		{
			StringBuilder builder = new StringBuilder(method.getName());
			
			builder.append("(");
			
			Class<?> paramTypes[] = method.getParameterTypes();
			Type genericParamTypes[] = method.getGenericParameterTypes();
	
			//fetch class variable to type map if applicable
			Map<TypeVariable<?>, Type> varToType = null;
			TypeVariable<?> clsTypeVar[] = method.getDeclaringClass().getTypeParameters();
			
			if(clsTypeVar != null && clsTypeVar.length > 0)
			{
				varToType = TypeUtils.getTypeArguments(activeClass, method.getDeclaringClass());	
			}
			
			//loop through method params
			if(paramTypes.length > 0)
			{
				int idx = 0;
				
				for(Class<?> ptype : paramTypes)
				{
					if(varToType != null && genericParamTypes[idx] instanceof TypeVariable)
					{
						ptype = (Class<?>) varToType.get(genericParamTypes[idx]);
					}
					
					if(ptype.getName().contains("MultipartHttpServletRequest"))
					{
						ptype = Object.class;
					}
					
					builder.append(ptype.getName()).append(",");
					idx++;
				}
				
				builder.deleteCharAt(builder.length() - 1);
			}
			
			builder.append(")");
			return builder.toString();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading method signature: {}.{}()", activeClass.getName(), method.getName(), ex);
		}
	}
}
