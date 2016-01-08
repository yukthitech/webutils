/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthi.webutils.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.yukthi.utils.exceptions.InvalidConfigurationException;
import com.yukthi.webutils.annotations.ActionName;
import com.yukthi.webutils.annotations.AttachmentsExpected;
import com.yukthi.webutils.annotations.RequestParam;
import com.yukthi.webutils.common.HttpMethod;
import com.yukthi.webutils.common.IWebUtilsCommonConstants;
import com.yukthi.webutils.common.annotations.ExtendableModel;
import com.yukthi.webutils.common.annotations.Model;
import com.yukthi.webutils.common.models.ActionModel;
import com.yukthi.webutils.common.models.def.FieldDef;
import com.yukthi.webutils.common.models.def.FieldType;
import com.yukthi.webutils.common.models.def.ModelDef;

/**
 * Service to maintain actions that can be invoked from client
 * 
 * @author akiran
 */
@Service
public class ActionsService
{
	/**
	 * Pattern to find url parameters inside url
	 */
	private static Pattern URL_PARAM_PATTERN = Pattern.compile("\\{(\\w+)\\}");
	
	/**
	 * Default http method to be used when no method is defined
	 */
	private static RequestMethod DEFAULT_METHODS[] = {RequestMethod.POST};
	
	/**
	 * Service to scan classes
	 */
	@Autowired
	private ClassScannerService classScanService;
	
	@Autowired
	private ModelDetailsService modelDetailsService;

	private List<ActionModel> actionModels;
	
	/**
	 * Fetches fields name from specified model type whose type is File.
	 * @param modelType Model type from which file fields needs to be fetched
	 * @return List of file fields
	 */
	private Set<String> getFileFields(Class<?> modelType)
	{
		ModelDef modelDef = modelDetailsService.getModelDef(modelType);
		HashSet<String> fileFields = new HashSet<>();
		
		//loop through fields
		for(FieldDef field : modelDef.getFields())
		{
			//if file field is found and to res set
			if(field.getFieldType() == FieldType.FILE)
			{
				fileFields.add(field.getName());
			}
		}
		
		//if no file fields are found
		if(fileFields.isEmpty())
		{
			return null;
		}
		
		return fileFields;
	}

	/**
	 * Loads client executable action details from the specified class "cls"
	 * 
	 * @param classActionName Action name defined on main controller class
	 * @param clsRequestMapping Request mapping path defined on main controller class
	 * @param cls Class from which service methods needs to be scanned
	 * @param nameToModel Map into which action details needs to be populated, using action name as key
	 */
	private void loadActions(String classActionName, String clsRequestMapping, Class<?> cls, Map<String, ActionModel> nameToModel)
	{
		//if class is part of core java
		if(cls.getName().startsWith("java"))
		{
			return;
		}

		ActionName actName = null;
		RequestMapping requestMapping = null;

		String actionName = null;
		RequestMethod requestMethods[] = null;
		Annotation fullParamAnnotations[][] = null;
		boolean isBodyParam = false, isBodySpecified = false;
		Set<String> expectedRequestParams = new TreeSet<>(); 

		RequestParam requestParam = null;
		String url = null;
		boolean attachmentsExpected = false;
		HttpMethod httpMethod = null;
		
		Class<?> paramTypes[] = null;
		int paramIndex = 0;
		Set<String> fileFields = null;
		
		//get controller methods
		for(Method method : cls.getMethods())
		{
			//if current method is static, ignore
			if(Modifier.isStatic(method.getModifiers()))
			{
				continue;
			}
			
			expectedRequestParams.clear();

			//get request mapping and action from current method
			requestMapping = method.getAnnotation(RequestMapping.class);
			actName = method.getAnnotation(ActionName.class);

			//if no request mapping is defined on method (that is it is not service method)
			if(requestMapping == null)
			{
				continue;
			}

			//use method name as action name if action is not defined
			actionName = (actName == null) ? method.getName() : actName.value();

			//combine with class action name, to build final action name
			if(classActionName != null)
			{
				actionName = classActionName + "." + actionName;
			}

			//if action name is duplicated throw error
			if(nameToModel.containsKey("actionName"))
			{
				throw new IllegalStateException("Duplicate action configuration encountered for action: " + actionName);
			}

			//get the http method of the service method
			requestMethods = requestMapping.method();

			if(requestMethods.length == 0)
			{
				requestMethods = DEFAULT_METHODS;
			}

			//get the service method parameter annotations
			fullParamAnnotations = method.getParameterAnnotations();
			
			attachmentsExpected = (method.getAnnotation(AttachmentsExpected.class) != null);
			paramTypes = method.getParameterTypes();
			paramIndex = -1;
			isBodySpecified = false;
			
			//based on param annotation check if request is expected as HTTP body
			if(fullParamAnnotations != null)
			{
				for(Annotation paramAnnotations[] : fullParamAnnotations)
				{
					paramIndex++;
					isBodyParam = false;
					
					if(paramAnnotations == null || paramAnnotations.length == 0)
					{
						continue;
					}

					for(Annotation annotation : paramAnnotations)
					{
						if(RequestBody.class.equals(annotation.annotationType()))
						{
							if(attachmentsExpected)
							{
								throw new InvalidConfigurationException("@RequestBody is used in service method where attachments are expected. "
										+ "Use @RequestPart(\"{}\") instead. Method - {}.{}()", IWebUtilsCommonConstants.MULTIPART_DEFAULT_PART, 
											method.getDeclaringClass().getName(), method.getName());
							}

							//if multiple parameters are marked for body throw error
							if(isBodySpecified)
							{
								throw new InvalidConfigurationException("Multiple parameters are marked as body attributes. Method - {}.{}()",  
											method.getDeclaringClass().getName(), method.getName());
							}
							
							isBodyParam = true;
						}
						
						if(RequestPart.class.equals(annotation.annotationType()))
						{
							if(!attachmentsExpected)
							{
								throw new InvalidConfigurationException("@RequestPart is used in service method where attachments are not expected. "
										+ "Use @RequestBody instead. Method - {}.{}()", method.getDeclaringClass().getName(), method.getName());
							}
							
							String partName = ((RequestPart)annotation).value();
							
							if(!IWebUtilsCommonConstants.MULTIPART_DEFAULT_PART.equals(partName))
							{
								throw new InvalidConfigurationException("Invalid request part name used '{}'. "
										+ "Only '{}' is supported as part name for action methods. Method - {}.{}()", 
										partName, IWebUtilsCommonConstants.MULTIPART_DEFAULT_PART, 
										method.getDeclaringClass().getName(), method.getName());
							}
							
							//if multiple parameters are marked for body throw error
							if(isBodySpecified)
							{
								throw new InvalidConfigurationException("Multiple parameters are marked as body attributes. Method - {}.{}()",  
											method.getDeclaringClass().getName(), method.getName());
							}

							isBodyParam = true;
						}

						//if body is expected
						if(isBodyParam)
						{
							//if non model is declared as body throw error
							if(paramTypes[paramIndex].getAnnotation(Model.class) == null && paramTypes[paramIndex].getAnnotation(ExtendableModel.class) == null)
							{
								throw new InvalidConfigurationException("Non-model parameter type '{}' is defined as body attribute. Method - {}.{}()", 
										paramTypes[paramIndex].getName(), 
										method.getDeclaringClass().getName(), method.getName());
							}
							
							//if attachments are expected
							if(attachmentsExpected)
							{
								fileFields = getFileFields(paramTypes[paramIndex]);

								//if no file fields are found
								if(fileFields == null)
								{
									throw new InvalidConfigurationException("No file fields are found in model though service method is marked as attachments expected. Method - {}.{}()", 
											paramTypes[paramIndex].getName(), 
											method.getDeclaringClass().getName(), method.getName());
								}
							}
							
							isBodySpecified = true;
						}
						
						//if the parameter is defined to fetch from request, collect the param names
						if(RequestParam.class.equals(annotation.annotationType()))
						{
							requestParam = (RequestParam)annotation;
							
							if( StringUtils.isBlank(requestParam.value()) )
							{
								throw new IllegalStateException( String.format("@RequestParam is defined without value argument in method %s.%s()", 
										cls.getName(), method.getName()) );
							}
							
							expectedRequestParams.add(requestParam.value());
						}
					}
				}
			}

			//build action model object and cache it
			url = clsRequestMapping + requestMapping.value()[0];
			httpMethod = toHttpMethod(requestMethods[0]);
			
			if(httpMethod != HttpMethod.POST && attachmentsExpected)
			{
				throw new InvalidConfigurationException("Non-POST method is configured with @{}. Method - {}.{}()", 
						AttachmentsExpected.class.getName(), method.getDeclaringClass().getName(), method.getName());
			}
			
			nameToModel.put( actionName, new ActionModel(actionName, url, 
					httpMethod, isBodyParam, expectedRequestParams.toArray(new String[0]), getUrlParameters(url), attachmentsExpected, fileFields) );
		}
	}
	
	/**
	 * Converts {@link RequestMethod} to {@link HttpMethod} instance
	 * @param requestMethod
	 * @return
	 */
	private HttpMethod toHttpMethod(RequestMethod requestMethod)
	{
		if(requestMethod == RequestMethod.GET)
		{
			return HttpMethod.GET;
		}
		else if(requestMethod == RequestMethod.DELETE)
		{
			return HttpMethod.DELETE;
		}
		
		return HttpMethod.POST;
	}
	
	/**
	 * Loads actions from specified class recursively in the specified class hierarchy
	 * @param cls Class from which actions should be loaded
	 * @param nameToModel Map to which action models neees to be loaded
	 */
	private void loadActions(Class<?> cls, Map<String, ActionModel> nameToModel)
	{
		//get the url mapping defined at controller level
		String clsRequestMapping = null;
		RequestMapping requestMapping = cls.getAnnotation(RequestMapping.class);

		if(requestMapping != null)
		{
			clsRequestMapping = requestMapping.value()[0];
		}
		else
		{
			clsRequestMapping = "";
		}

		//Get the action name from controller level
		ActionName actName = cls.getAnnotation(ActionName.class);
		String classActionName = (actName != null) ? actName.value() : null;
		
		Class<?> currentClass = cls;
		
		while(true)
		{
			if(currentClass.getName().startsWith("java."))
			{
				break;
			}
			
			loadActions(classActionName, clsRequestMapping, currentClass, nameToModel);
			currentClass = currentClass.getSuperclass();
		}
	}
	
	/**
	 * Fetches url parameters from specified url
	 * @param url url string from which url-params can be found
	 * @return url parameters find
	 */
	private String[] getUrlParameters(String url)
	{
		Matcher matcher = URL_PARAM_PATTERN.matcher(url);
		List<String> params = new ArrayList<>();
		
		while(matcher.find())
		{
			params.add(matcher.group(1));
		}
		
		if(params.isEmpty())
		{
			return null;
		}
		
		return params.toArray(new String[0]);
	}

	/**
	 * Called by spring post construction, which will load all available actions into memory
	 */
	@PostConstruct
	private void init()
	{
		//map to hold action details
		Map<String, ActionModel> nameToModel = new TreeMap<>();

		//fetch all controller classes annotated with @Controller and @RestController
		Set<Class<?>> types = new HashSet<>(classScanService.getClassesWithAnnotation(Controller.class));
		types.addAll(classScanService.getClassesWithAnnotation(RestController.class));

		//load actions from each controller class
		for(Class<?> cls : types)
		{
			loadActions(cls, nameToModel);
		}

		//build final action details list
		this.actionModels = new ArrayList<>(nameToModel.values());
	}
	
	/**
	 * Service method to get available actions with details
	 * @return Available action details
	 */
	public List<ActionModel> getActions()
	{
		return actionModels;
	}
}
