package com.yukthitech.webutils.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
	private ClientContext clientContext;
	
	private Map<String, ActionModel> methodToAction = new HashMap<String, ActionModel>();
	
	private Class<?> controllerType;
	
	public ControllerProxy(ClientContext clientContext, Class<?> controllerType)
	{
		this.controllerType = controllerType;
		this.clientContext = clientContext;
		
		String controllerTypeName = controllerType.getName();
		
		for(ActionModel actionModel : clientContext.getAllActions())
		{
			if(controllerTypeName.equals(actionModel.getRemoteJavaInterfaceType()))
			{
				methodToAction.put(actionModel.getRemoteMethodSignature(), actionModel);
			}
		}
		
		if(methodToAction.isEmpty())
		{
			throw new InvalidArgumentException("No remote action methods found with controller type - {}", controllerType.getName());
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
		
		String methodSign = WebutilsCommonUtils.getMethodSignature(method);
		ActionModel actionModel = methodToAction.get(methodSign);
		
		if(actionModel == null)
		{
			throw new InvalidStateException("Non remote method '{}' invoked on controller - {}", methodSign, controllerType.getName());
		}
		
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
				
				parameters.put(paramModel.getName(), args[index]);
			}
		}
		
		RestRequest<?> request = ActionRequestBuilder.buildRequest(clientContext, actionModel.getName(), requestEntity, parameters);
		
		RestClient client = clientContext.getRestClient();
		
		RestResult<BaseResponse> modelDefResult = null;
		Type genericReturnType = method.getGenericReturnType();
		Class<?> rawReturnType = method.getReturnType();
		
		if(Collection.class.isAssignableFrom(rawReturnType))
		{
			modelDefResult = (RestResult) client.invokeJsonRequestForList(request, (Class) rawReturnType, 
					(Class) ((ParameterizedType) genericReturnType).getActualTypeArguments()[0]);
		}
		else if(genericReturnType instanceof ParameterizedType)
		{
			modelDefResult = (RestResult) client.invokeJsonRequest(request, rawReturnType, 
					(Class) ((ParameterizedType) genericReturnType).getActualTypeArguments()[0]);
		}
		else
		{
			modelDefResult = (RestResult) client.invokeJsonRequest(request, method.getReturnType());
		}
		
		BaseResponse response = modelDefResult.getValue();
		
		if(response == null || response.getCode() != 0)
		{
			throw new RestException("An error occurred while fetching Model definition for - " + actionModel.getName(), modelDefResult.getStatusCode(), response);
		}
		
		return response;
	}

}
