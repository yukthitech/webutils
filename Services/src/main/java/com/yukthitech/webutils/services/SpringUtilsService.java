package com.yukthitech.webutils.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Contains common utility methods to interact with spring.
 * @author akiran
 */
@Service
public class SpringUtilsService
{
	/**
	 * Context to fetch task method wrapper services.
	 */
	@Autowired
	private ApplicationContext applicationContext;
	
	/**
	 * Used to find task based methods in services/components.
	 */
	@Autowired
	private ClassScannerService classScannerService;
	
	/**
	 * Fetches service methods with specified annotation class. If service method is found which are
	 * not matching with expected argument type and has specified annotation, exception will be thrown. 
	 * @param name name of service methods being fetched. Used in exception messages
	 * @param annotationClass expected annotation on methods
	 * @param expectedArgTypes expected method argument types
	 * @return matching service methods
	 */
	public List<ServiceMethod> fetchServiceMethods(String name, Class<? extends Annotation> annotationClass, Class<?>... expectedArgTypes)
	{
		Set<Method> methods = classScannerService.getMethodsAnnotatedWith(annotationClass);
		return validateMethods(name, methods, expectedArgTypes);
	}

	/**
	 * Validates the specified methods and ensures they are in spring managed beans and does not 
	 * have any parameters.
	 * @param name Name of the service methods being searched
	 * @param methods methods to validate.
	 * @param expectedArgTypes Expected argument types of method
	 * @return matching service methods
	 */
	private List<ServiceMethod> validateMethods(String name, Set<Method> methods, Class<?>... expectedArgTypes)
	{
		if(expectedArgTypes == null)
		{
			expectedArgTypes = new Class<?>[]{};
		}
		
		List<ServiceMethod> serviceMethods = new ArrayList<ServiceMethod>();
		
		for(Method method : methods)
		{
			Class<?> declrClass = method.getDeclaringClass();
			final Object service = applicationContext.getBean(declrClass);
			
			if(service == null)
			{
				throw new InvalidStateException("{} method {}.{}() is declared in non-spring managed class.", name, declrClass.getName(), method.getName());
			}
			
			Class<?> paramTypes[] = method.getParameterTypes();
			
			if(!Arrays.equals(expectedArgTypes, paramTypes))
			{
				throw new InvalidStateException("{} method {}.{}() is declared with arguments which are not matching with expected argument types. [Expected Arg Types: {}, Actual Arg Type: {}]", 
						name, declrClass.getName(), method.getName(), Arrays.toString(expectedArgTypes), Arrays.toString(paramTypes));
			}
			
			serviceMethods.add(new ServiceMethod(service, method));
		}
		
		return serviceMethods;
	}
}
