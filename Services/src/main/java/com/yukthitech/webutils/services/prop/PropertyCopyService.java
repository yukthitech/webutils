package com.yukthitech.webutils.services.prop;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.repository.WebutilsBaseEntity;
import com.yukthitech.webutils.services.ClassScannerService;

import jakarta.annotation.PostConstruct;
import lombok.Data;

@Service
public class PropertyCopyService
{
	@Data
	private static class ClassPair
	{
		private final Class<?> source;
		private final Class<?> target;

		public ClassPair(Class<?> source, Class<?> target)
		{
			this.source = source;
			this.target = target;
		}
		
		public boolean isCompatible(Class<?> sourceType, Class<?> targetType)
		{
			return CommonUtils.isAssignable(sourceType, source) 
					&& CommonUtils.isAssignable(targetType, target);
		}
	}
	
	private static class ConverterFunction
	{
		private Object service;
		
		private Method method;

		public ConverterFunction(Object service, Method method)
		{
			this.service = service;
			this.method = method;
		}
	}
	
	@Autowired
	private ClassScannerService classScannerService;
	
	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * Cache for storing properties of scanned types.
	 */
	private final Map<Class<?>, Map<String, FieldDescriptor>> propertyCache = new ConcurrentHashMap<>();

	/**
	 * Custom mappers: (sourceClass, targetClass) -> conversion function
	 */
	private final Map<ClassPair, ConverterFunction> customMappers = new ConcurrentHashMap<>();
	
	@EventListener(ApplicationReadyEvent.class)
	private void init()
	{
		try
		{
			register(this, PropertyCopyService.class.getMethod("longToEntity", Long.class, CopyContext.class));
			register(this, PropertyCopyService.class.getMethod("entityToLong", WebutilsBaseEntity.class, CopyContext.class));
		} catch(Exception ex)
		{
			throw new InvalidStateException("An exception occurred while registering local methods", ex);
		}
			
		// For test cases classScannerService null condition is handled
		Set<Method> methods = classScannerService == null ? Collections.emptySet() : 
			classScannerService.getMethodsAnnotatedWith(ValueConverter.class);

		for(Method method : methods)
		{
			Class<?> serviceType = method.getDeclaringClass();
			
			if(PropertyCopyService.class.equals(serviceType))
			{
				continue;
			}
			
			Object service = applicationContext.getBean(serviceType);

			register(service, method);
		}
	}
	
	private void register(Object service, Method method)
	{
		ValueConverter propertyConverter = method.getAnnotation(ValueConverter.class);
		
		if(method.getParameterCount() != 2)
		{
			throw new InvalidConfigurationException("Value-converter method {}.{}() accepting wrong number of arguments. "
					+ "\nExpected [Param Types: ({}, {}), Return Type: {}]",
					method.getDeclaringClass().getName(), method.getName(),
					propertyConverter.sourceType(), CopyContext.class.getName(), propertyConverter.targetType()
					);
		}
		
		if(!propertyConverter.sourceType().equals(method.getParameterTypes()[0]) || 
				!CopyContext.class.equals(method.getParameterTypes()[1]) ||
				!propertyConverter.targetType().equals(method.getReturnType()))
		{
			throw new InvalidConfigurationException("Value-converter method {}.{}() having wrong argument/return type(s). "
					+ "\nExpected [Param Types: ({}, {}), Return Type: {}]"
					+ "\nFound [Param Types: ({}, {}), Return Type: {}]", 
					method.getDeclaringClass().getName(), method.getName(),
					propertyConverter.sourceType(), CopyContext.class.getName(), propertyConverter.targetType(),
					method.getParameterTypes()[0].getName(), method.getParameterTypes()[1].getName(), method.getReturnType()
					);
		}
		
		customMappers.put(
				new ClassPair(propertyConverter.sourceType(), propertyConverter.targetType()), 
				new ConverterFunction(service, method));
	}
	
	@ValueConverter(sourceType = Long.class, targetType = WebutilsBaseEntity.class)
	public WebutilsBaseEntity longToEntity(Long longVal, CopyContext context)
	{
		try
		{
			WebutilsBaseEntity entity = (WebutilsBaseEntity) context.getTargetType().getConstructor().newInstance();
			entity.setId(longVal);
			return entity;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating instance of type: " + context.getTargetType().getName(), ex);
		}
	}
	
	@ValueConverter(sourceType = WebutilsBaseEntity.class, targetType = Long.class)
	public Long entityToLong(WebutilsBaseEntity entity, CopyContext context)
	{
		return entity.getId();
	}

	private synchronized Map<String, FieldDescriptor> getProperties(Class<?> type)
	{
		return propertyCache.computeIfAbsent(type, t -> 
		{
			try
			{
				Map<String, FieldDescriptor> map = FieldDescriptor.loadFields(type);
				return map;
			} catch(Exception e)
			{
				throw new RuntimeException("Failed to extract fields for class: " + t.getName(), e);
			}
		});
	}
	
	public <T> T cloneBean(Object source, Class<T> targetType)
	{
		T target = null;
		
		try
		{
			target = targetType.getConstructor().newInstance();
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("An error occurred while creating instance of type: {}", targetType.getName(), ex);
		}
		
		copyProperties(source, target);
		return target;
	}

	public <T> List<T> cloneList(List<? extends Object> srcLst, Class<T> targetType)
	{
		List<T> res = new ArrayList<>();
				
		for(Object src : srcLst)
		{
			res.add(cloneBean(src, targetType));
		}

		return res;
	}

	public void copyProperties(Object source, Object target)
	{
		if(source == null || target == null)
		{
			return;
		}

		Map<String, FieldDescriptor> sourceProps = getProperties(source.getClass());
		Map<String, FieldDescriptor> targetProps = getProperties(target.getClass());

		for(Map.Entry<String, FieldDescriptor> entry : sourceProps.entrySet())
		{
			String propName = entry.getKey();
		
			FieldDescriptor sourcePd = entry.getValue();
			FieldDescriptor targetPd = targetProps.get(propName);

			if(targetPd == null)
			{
				continue;
			}

			try
			{
				Object srcValue = sourcePd.getValue(source);
				
				if(srcValue == null)
				{
					continue;
				}
				
				CopyContext context = new CopyContext(source, target, sourcePd, targetPd);

				Object tgtValue = convertValue(srcValue, context);
				targetPd.setValue(target, tgtValue);
			} catch(Exception e)
			{
				throw new RuntimeException("Error copying property: " + propName, e);
			}
		}
	}

	private Object convertValue(Object srcValue, CopyContext copyContext)
	{
		Class<?> srcType = copyContext.getSourceType();
		Class<?> tgtType = copyContext.getTargetType();
		
		// === First, handle collections ===
		if(Collection.class.isAssignableFrom(srcType) && 
				Collection.class.isAssignableFrom(tgtType))
		{
			return convertCollection((Collection<?>) srcValue, copyContext);
		}

		// === Direct assignment if compatible ===
		if(CommonUtils.isAssignable(srcType, tgtType))
		{
			return srcValue;
		}
		
		ConverterFunction mapper = null;

		// === Custom mapper ===
		for(Map.Entry<ClassPair, ConverterFunction> entry : customMappers.entrySet())
		{
			if(entry.getKey().isCompatible(srcType, tgtType))
			{
				mapper = entry.getValue();
				break;
			}
		}
		
		if(mapper != null)
		{
			try
			{
				return mapper.method.invoke(mapper.service, srcValue, copyContext);
			} catch(Exception e)
			{
				throw new InvalidStateException("Failed to convert value for from source type {} to target type {} with custom mapper method: {}.{}()", 
						srcType.getName(), tgtType.getName(),
						mapper.method.getDeclaringClass().getName(), mapper.method.getName(),
						e);
			}
		}

		// === Recursive object copy ===
		try
		{
			Object tgtInstance = tgtType.getDeclaredConstructor().newInstance();
			copyProperties(srcValue, tgtInstance);
			return tgtInstance;
		} catch(Exception e)
		{
			throw new InvalidStateException("Failed to convert value for from source type {} to target type {}", 
					srcType.getName(), tgtType.getName(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private Object convertCollection(Collection<?> srcCollection, CopyContext copyContext)
	{
		// Extract element types
		Class<?> srcElementType = extractElementType(copyContext.getSourceField().getGenericType());
		Class<?> tgtElementType = extractElementType(copyContext.getTargetField().getGenericType());
		
		Class<?> targetType = copyContext.getTargetField().getType();
		
		// if target collection type and its element type is compatible with source, return input value
		if(targetType.isAssignableFrom(srcCollection.getClass()) && tgtElementType.isAssignableFrom(srcElementType))
		{
			return srcCollection;
		}

		try
		{
			Collection<Object> tgtCollection;

			if(targetType.isInterface())
			{
				if(List.class.isAssignableFrom(targetType))
				{
					tgtCollection = new ArrayList<>();
				}
				else if(Set.class.isAssignableFrom(targetType))
				{
					tgtCollection = new HashSet<>();
				}
				else
				{
					throw new UnsupportedOperationException("Unsupported collection type: " + targetType.getName());
				}
			}
			else
			{
				tgtCollection = (Collection<Object>) targetType.getDeclaredConstructor().newInstance();
			}

			for(Object item : srcCollection)
			{
				if(item == null)
				{
					tgtCollection.add(null);
					continue;
				}

				if(tgtElementType.isAssignableFrom(item.getClass()))
				{
					tgtCollection.add(item); // direct add
				}
				else
				{
					CopyContext elemCopyContext = new CopyContext(copyContext, srcElementType, tgtElementType);
					Object convertedItem = convertValue(item, elemCopyContext);
					tgtCollection.add(convertedItem);
				}
			}

			return tgtCollection;

		} catch(Exception e)
		{
			throw new RuntimeException("Failed to convert collection to type: " + targetType.getName(), e);
		}
	}
	
	private Class<?> extractElementType(Type collectionType)
	{
		Type actualType = ((ParameterizedType) collectionType).getActualTypeArguments()[0];
		return (Class<?>) actualType;
	}
}
