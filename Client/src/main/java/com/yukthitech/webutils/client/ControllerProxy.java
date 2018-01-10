package com.yukthitech.webutils.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.TypeUtils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.yukthitech.utils.beans.BeanProperty;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.rest.RestClient;
import com.yukthitech.utils.rest.RestRequest;
import com.yukthitech.utils.rest.RestResult;
import com.yukthitech.webutils.common.WebutilsCommonUtils;
import com.yukthitech.webutils.common.client.IRequestCustomizer;
import com.yukthitech.webutils.common.models.ActionModel;
import com.yukthitech.webutils.common.models.ActionParamModel;
import com.yukthitech.webutils.common.models.BaseResponse;

class ControllerProxy implements InvocationHandler
{
	/**
	 * Contains details needed during method invocation handling.
	 * @author akiran
	 */
	private class MethodDetails
	{
		/**
		 * Action method for which this details are being maintained.
		 */
		private Method method;
		
		/**
		 * Action model to be used when method is invoked.
		 */
		private ActionModel actionModel;
		
		/**
		 * Return type of the method.
		 */
		private JavaType returnType;

		/**
		 * Instantiates a new method details.
		 *
		 * @param method the method
		 */
		public MethodDetails(Method method)
		{
			this.method = method;
		}
		
		/**
		 * Converts specified type to raw type.
		 * @param varToType mapping from variable to type
		 * @param type type to be converted
		 * @return converted raw type
		 */
		private Class<?> toRawType(Map<TypeVariable<?>, Type> varToType, Type type)
		{
			if(type instanceof TypeVariable)
			{
				return (Class<?>) varToType.get(type);
			}
			
			return (Class<?>) type;
		}
		
		/**
		 * Gets the return type of the method.
		 *
		 * @return the return type of the method
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public JavaType getReturnType()
		{
			if(returnType != null)
			{
				return returnType;
			}
			
			Type genericReturnType = method.getGenericReturnType();
			Class<?> rawReturnType = method.getReturnType();
			
			//fetch class variable to type map if applicable
			Map<TypeVariable<?>, Type> varToType = new HashMap<>();
			TypeVariable<?> clsTypeVar[] = method.getDeclaringClass().getTypeParameters();

			if(clsTypeVar != null && clsTypeVar.length > 0)
			{
				varToType = TypeUtils.getTypeArguments(controllerType, method.getDeclaringClass());	
			}

			if(Collection.class.isAssignableFrom(rawReturnType))
			{
				Type collectionType = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
				Class<?> collectionTypeCls = toRawType(varToType, collectionType);
				
				returnType = TypeFactory.defaultInstance().constructCollectionType(
						(Class) rawReturnType, 
						collectionTypeCls
					);
			}
			else if(genericReturnType instanceof ParameterizedType)
			{
				ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
				Type genericTypeParams[] = parameterizedType.getActualTypeArguments();
				
				Class<?> typeParams[] = new Class<?>[parameterizedType.getActualTypeArguments().length];
				
				for(int i = 0; i < typeParams.length; i++)
				{
					typeParams[i] = toRawType(varToType, genericTypeParams[i]);
				}
				
				returnType = TypeFactory.defaultInstance().constructParametrizedType(rawReturnType, rawReturnType, typeParams);
			}
			else
			{
				returnType = TypeFactory.defaultInstance().uncheckedSimpleType( toRawType(varToType, genericReturnType) );
			}

			return returnType;
		}
	}
	
	private ClientContext clientContext;
	
	private Map<Method, MethodDetails> methodToAction = new HashMap<>();
	
	private Class<?> controllerType;
	
	public ControllerProxy(ClientContext clientContext, Class<?> controllerType)
	{
		this.controllerType = controllerType;
		this.clientContext = clientContext;
		
		Map<String, MethodDetails> signToDetails = Arrays.asList(controllerType.getMethods()).stream()
				.collect(Collectors.toMap(
						met -> WebutilsCommonUtils.getMethodSignature(controllerType, met), 
						met -> new MethodDetails(met)
				));
		
		String controllerTypeName = controllerType.getName();
		
		for(ActionModel actionModel : clientContext.getAllActions())
		{
			if(controllerTypeName.equals(actionModel.getRemoteJavaInterfaceType()))
			{
				MethodDetails metDet = signToDetails.get(actionModel.getRemoteMethodSignature());
				
				//method details will be null, when a method is defined in controller
				//		but not in interface
				if(metDet == null)
				{
					continue;
				}
				
				metDet.actionModel = actionModel;
				
				methodToAction.put(metDet.method, metDet);
			}
		}
		
		if(methodToAction.isEmpty())
		{
			throw new InvalidArgumentException("No remote action methods found with controller type - {}", controllerType.getName());
		}
	}
	
	/**
	 * Loads the properties of specifid bean as params.
	 * @param model model to load
	 * @param paramMap param map
	 */
	private void loadPropertiesAsParams(Object model, Map<String, Object> paramMap)
	{
		List<BeanProperty> properties = BeanProperty.loadProperties(model.getClass(), true, true);
		
		for(BeanProperty prop : properties)
		{
			Object value = prop.getValue(model);
			
			if(value == null)
			{
				continue;
			}
			
			paramMap.put(prop.getName(), value);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		//TODO: correct this condition to use full method signature
		if("setRequestCustomizer".equals(method.getName()))
		{
			clientContext.setRequestCustomizer((IRequestCustomizer) args[0]);
			return proxy;
		}
		
		MethodDetails methodDetails = methodToAction.get(method);
		
		if(methodDetails == null)
		{
			throw new InvalidStateException("Non remote method '{}()' invoked on controller - {}", method.getName(), controllerType.getName());
		}
		
		ActionModel actionModel = methodDetails.actionModel;
		int index = -1;
		
		Map<String, Object> parameters = new HashMap<>();
		Object requestEntity = null;
		
		if(actionModel.getParameters() != null)
		{
			for(ActionParamModel paramModel : actionModel.getParameters())
			{
				index++;
				
				//TODO: Take care of file uploads, if needed
				if(paramModel.getType() == ActionParamModel.TYPE_BODY)
				{
					requestEntity = args[index];
					continue;
				}
				else if(paramModel.getType() == ActionParamModel.TYPE_EMBEDDED_REQUEST_PARAMS)
				{
					loadPropertiesAsParams(args[index], parameters);
					continue;
				}
				
				if(paramModel.getName() == null)
				{
					continue;
				}
				
				parameters.put(paramModel.getName(), args[index]);
			}
		}
		
		RestRequest<?> request = ActionRequestBuilder.buildRequest(clientContext, actionModel.getName(), requestEntity, parameters);
		
		RestClient client = clientContext.getRestClient();
		
		RestResult<BaseResponse> modelDefResult = (RestResult) client.invokeJsonRequest(request, methodDetails.getReturnType());
		BaseResponse response = modelDefResult.getValue();
		
		if(response == null || response.getCode() != 0)
		{
			throw new RestException("An error occurred while fetching Model definition for - " + actionModel.getName(), modelDefResult.getStatusCode(), response);
		}
		
		return response;
	}
}
