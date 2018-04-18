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

package com.yukthitech.webutils.services;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.yukthitech.utils.exceptions.InvalidConfigurationException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.annotations.AttachmentsExpected;
import com.yukthitech.webutils.common.HttpMethod;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.WebutilsCommonUtils;
import com.yukthitech.webutils.common.annotations.ExtendableModel;
import com.yukthitech.webutils.common.annotations.Model;
import com.yukthitech.webutils.common.controllers.IClientController;
import com.yukthitech.webutils.common.models.ActionModel;
import com.yukthitech.webutils.common.models.ActionParamModel;
import com.yukthitech.webutils.common.models.def.FieldDef;
import com.yukthitech.webutils.common.models.def.FieldType;
import com.yukthitech.webutils.common.models.def.ModelDef;

/**
 * Service to maintain actions that can be invoked from client.
 * 
 * @author akiran
 */
@Service
public class ActionsService
{
	/**
	 * Default http method to be used when no method is defined.
	 */
	private static RequestMethod DEFAULT_METHODS[] = {RequestMethod.POST};
	
	/**
	 * Represents combination of controller class and method.
	 * @author akiran
	 */
	private static class ControllerMethod
	{
		/**
		 * Controller type.
		 */
		private Class<?> controllerType;
		
		/**
		 * Action method.
		 */
		private Method method;

		/**
		 * Instantiates a new controller method.
		 *
		 * @param controllerType the controller type
		 * @param method the method
		 */
		public ControllerMethod(Class<?> controllerType, Method method)
		{
			this.controllerType = controllerType;
			this.method = method;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if(obj == this)
			{
				return true;
			}

			if(!(obj instanceof ActionsService.ControllerMethod))
			{
				return false;
			}

			ActionsService.ControllerMethod other = (ActionsService.ControllerMethod) obj;
			return controllerType.equals(other.controllerType) && method.equals(other.method);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashcode()
		 */
		@Override
		public int hashCode()
		{
			return controllerType.hashCode() + method.hashCode();
		}
	}
	
	/**
	 * Service to scan classes.
	 */
	@Autowired
	private ClassScannerService classScanService;
	
	/**
	 * This service is used to fetch model field details.
	 */
	@Autowired
	private ModelDetailsService modelDetailsService;

	/**
	 * List of actions information resulted from scanning.
	 */
	private List<ActionModel> actionModels;
	
	/**
	 * Mapping from controller method to action.
	 */
	private Map<ControllerMethod, ActionModel> methodToAction = new HashMap<>();
	
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
	 * Fetch remote interface implemented by specified controller type.
	 * @param controllerType Controller to be checked
	 * @return Remote interface class implemented by specified controller type
	 */
	private Class<?> getRemoteInterface(Class<?> controllerType)
	{
		Class<?> interTypes[] = controllerType.getInterfaces();
		
		if(interTypes == null || interTypes.length == 0)
		{
			return null;
		}
		
		for(Class<?> interType : interTypes)
		{
			if( IClientController.class.isAssignableFrom(interType) )
			{
				return interType;
			}
		}
		
		return null;
	}
	
	/**
	 * Fetches the action parameter details from specified method parameters into specified action.
	 * @param action Action to which parameter details needs to be fetched
	 * @param method Method whose parameters needs to be used
	 * @param mainClass Class from which methods are being loaded.
	 */
	private void fetchActionParameters(ActionModel action, Method method, Class<?> mainClass)
	{
		ActionParamModel actionParam = null;
		Set<String> fileFields = null;
		String paramName = null;
		
		Type genericParamTypes[] = method.getGenericParameterTypes();
		int idx = 0;
		Type paramGenericType = null;
		
		for(Parameter parameter : method.getParameters())
		{
			paramGenericType = genericParamTypes[idx];
			idx++;
			
			actionParam = new ActionParamModel();
			
			//if RequestBody annotation is present on the parameter
			if(parameter.getAnnotation(RequestBody.class) != null)
			{
				if(action.isAttachmentsExpected())
				{
					throw new InvalidConfigurationException("@RequestBody is used in service method where attachments are expected. "
							+ "Use @RequestPart(\"{}\") instead. Method - {}", IWebUtilsCommonConstants.MULTIPART_DEFAULT_PART, 
								action.getRemoteMethodSignature());
				}

				//if multiple parameters are marked for body throw error
				if(action.isBodyExpected())
				{
					throw new InvalidConfigurationException("Multiple parameters are marked as body attributes.  Method - {}",  
								action.getRemoteMethodSignature());
				}
				
				actionParam.setType(ActionParamModel.TYPE_BODY);
			}

			if(parameter.getAnnotation(RequestPart.class) != null)
			{
				if(!action.isAttachmentsExpected())
				{
					throw new InvalidConfigurationException("@RequestPart is used in service method where attachments are not expected. "
							+ "Use @RequestBody instead. Method - {}", action.getRemoteMethodSignature());
				}
				
				String partName = parameter.getAnnotation(RequestPart.class).value();
				
				if(!IWebUtilsCommonConstants.MULTIPART_DEFAULT_PART.equals(partName))
				{
					throw new InvalidConfigurationException("Invalid request part name used '{}'. "
							+ "Only '{}' is supported as part name for action methods. Method - {}", 
							partName, IWebUtilsCommonConstants.MULTIPART_DEFAULT_PART, 
							action.getRemoteMethodSignature());
				}
				
				//if multiple parameters are marked for body throw error
				if(action.isBodyExpected())
				{
					throw new InvalidConfigurationException("Multiple parameters are marked as body attributes. Method - {}",  
								action.getRemoteMethodSignature());
				}

				actionParam.setType(ActionParamModel.TYPE_BODY);
			}
			
			//if body is expected
			if(actionParam.isBodyParameter())
			{
				//if non model is declared as body throw error
				if(parameter.getType().getAnnotation(Model.class) == null && parameter.getType().getAnnotation(ExtendableModel.class) == null)
				{
					throw new InvalidConfigurationException("Non-model parameter type '{}' is defined as body attribute. Method - {}", 
							parameter.getType().getName(), 
							action.getRemoteMethodSignature());
				}
				
				//if attachments are expected
				if(action.isAttachmentsExpected())
				{
					Class<?> paramType = TypeUtils.getRawType(paramGenericType, mainClass);
					fileFields = getFileFields(paramType);

					//if no file fields are found
					if(fileFields == null)
					{
						throw new InvalidConfigurationException("No file fields are found in model '{}' though service method is marked as attachments expected. Method - {}", 
								parameter.getType().getName(), 
								action.getRemoteMethodSignature());
					}
					
					action.setFileFields(fileFields);
				}
			}
			//if the parameter is defined to fetch from request
			else if(parameter.getAnnotation(RequestParam.class) != null)
			{
				paramName = parameter.getAnnotation(RequestParam.class).value();
				
				if( StringUtils.isBlank(paramName) )
				{
					throw new InvalidConfigurationException("@RequestParam is defined without value argument in method {}", 
							action.getRemoteMethodSignature());
				}
				
				actionParam.setName(paramName);
				actionParam.setType(ActionParamModel.TYPE_REQUEST_PARAM);
			}
			//if the parameter is defined to fetch from url path
			else if(parameter.getAnnotation(PathVariable.class) != null)
			{
				paramName = parameter.getAnnotation(PathVariable.class).value();
				
				if( StringUtils.isBlank(paramName) )
				{
					throw new InvalidConfigurationException("@PathVariable is defined without value argument in method {}", 
							action.getRemoteMethodSignature());
				}
				
				actionParam.setName(paramName);
				actionParam.setType(ActionParamModel.TYPE_URL_PARAM);
			}
			
			//if unable to determine action type 
			if(actionParam.getType() == 0)
			{
				//if parameter type is multi part request (needed during file upload)
				if(MultipartHttpServletRequest.class.equals(parameter.getType()))
				{
					actionParam.setType(ActionParamModel.TYPE_NULL);
				}
				else
				{
					//if the parameter is non-model type
					if(parameter.getType().getAnnotation(Model.class) == null && parameter.getType().getAnnotation(ExtendableModel.class) == null)
					{
						throw new InvalidConfigurationException("Unable to determine action parameter type at index {} in method {}", 
								idx, action.getRemoteMethodSignature());
					}
					
					actionParam.setType(ActionParamModel.TYPE_EMBEDDED_REQUEST_PARAMS);
				}
			}
			
			action.addParam(actionParam);
		}
	}

	/**
	 * Loads client executable action details from the specified class "cls".
	 * 
	 * @param classActionName Action name defined on main controller class
	 * @param clsRequestMapping Request mapping path defined on main controller class
	 * @param cls Class from which service methods needs to be scanned
	 * @param nameToModel Map into which action details needs to be populated, using action name as key
	 * @param remoteInterTypeName remote interface type of the controller class being loaded
	 * @param inheritedActions actions already inherited by current controller
	 * @param mainClass Main class from which actions are being loaded. "cls" can be parent class of mainClass
	 * @return actions loaded by current class
	 */
	private Set<String> loadActions(String classActionName, String clsRequestMapping, Class<?> cls, Map<String, ActionModel> nameToModel, 
			String remoteInterTypeName, Set<String> inheritedActions, Class<?> mainClass)
	{
		ActionName actName = null;
		RequestMapping requestMapping = null;

		String actionName = null;
		Set<String> expectedRequestParams = new TreeSet<>(); 

		String url = null;
		ActionModel action = null;
		
		Set<String> newActions = new HashSet<>();
		
		//get controller methods
		for(Method method : cls.getDeclaredMethods())
		{
			//if current method is static, ignore
			if(Modifier.isStatic(method.getModifiers()) || method.isSynthetic() || !Modifier.isPublic(method.getModifiers()))
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
			
			//if action is already found in child class, ignore in parent class
			if(inheritedActions.contains(actionName))
			{
				continue;
			}
			
			//if action name is duplicated throw error
			if(nameToModel.containsKey(actionName))
			{
				throw new InvalidStateException("Duplicate action configuration encountered for action: {}. Duplicates: [{}, {}]", 
						actionName, nameToModel.get(actionName).getRemoteMethodSignature(), action.getRemoteMethodSignature());
			}

			action = new ActionModel(remoteInterTypeName, WebutilsCommonUtils.getMethodSignature(mainClass, method));
			
			newActions.add(actionName);
			
			//get the http method of the service method
			if(requestMapping.method().length == 0)
			{
				action.setMethod( toHttpMethod(DEFAULT_METHODS[0]) );
			}
			else
			{
				action.setMethod( toHttpMethod(requestMapping.method()[0]) );
			}

			action.setAttachmentsExpected(method.getAnnotation(AttachmentsExpected.class) != null);
			fetchActionParameters(action, method, mainClass);
			
			//build action model object and cache it
			url = clsRequestMapping + requestMapping.value()[0];
			
			if(action.getMethod() != HttpMethod.POST && action.isAttachmentsExpected())
			{
				throw new InvalidConfigurationException("Non-POST method is configured with @{}. Method - {}.{}()", 
						AttachmentsExpected.class.getName(), method.getDeclaringClass().getName(), method.getName());
			}
			
			action.setName(actionName);
			action.setUrl(url);
			
			nameToModel.put(actionName, action);
			methodToAction.put(new ControllerMethod(mainClass, method), action);
		}
		
		return newActions;
	}
	
	/**
	 * Loads actions from specified class recursively in the specified class hierarchy.
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

		//fetch the remote interface of the current controller
		Class<?> remoteInterType = getRemoteInterface(cls);
		String remoteInterTypeName = remoteInterType != null ? remoteInterType.getName() : null;
		
		Set<String> inheritedActions = new HashSet<>();
		Set<String> newActions = null;
		Class<?> curCls = cls;

		/*
		 * Load actions hierarchically
		 */
		while(true)
		{
			if(curCls.getName().startsWith("java"))
			{
				break;
			}
			
			newActions = loadActions(classActionName, clsRequestMapping, curCls, nameToModel, remoteInterTypeName, inheritedActions, cls);
			inheritedActions.addAll(newActions);
			
			curCls = curCls.getSuperclass();
		}
	}
	
	/**
	 * Converts {@link RequestMethod} to {@link HttpMethod} instance.
	 * @param requestMethod Request method to be converted
	 * @return Converted http method
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
	 * Called by spring post construction, which will load all available actions into memory.
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
	 * Service method to get available actions with details.
	 * @return Available action details
	 */
	public List<ActionModel> getActions()
	{
		return actionModels;
	}
	
	/**
	 * Fetches action details for specified controller class and method.
	 * @param controllerType controller class
	 * @param method action method
	 * @return action details
	 */
	public ActionModel getActionDetails(Class<?> controllerType, Method method)
	{
		return methodToAction.get(new ControllerMethod(controllerType, method));
	}
}
