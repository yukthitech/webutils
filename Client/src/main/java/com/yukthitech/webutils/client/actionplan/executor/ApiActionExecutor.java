package com.yukthitech.webutils.client.actionplan.executor;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.rest.DeleteRestRequest;
import com.yukthitech.utils.rest.GetRestRequest;
import com.yukthitech.utils.rest.PostRestRequest;
import com.yukthitech.utils.rest.RestClient;
import com.yukthitech.utils.rest.RestRequest;
import com.yukthitech.utils.rest.RestResult;
import com.yukthitech.webutils.client.ClientContext;
import com.yukthitech.webutils.client.actionplan.ActionPlanExecutionContext;
import com.yukthitech.webutils.common.FileInfo;
import com.yukthitech.webutils.common.HttpMethod;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.action.ApiAgentAction;
import com.yukthitech.webutils.common.models.ActionModel;

/**
 * Api action executor.
 * @author akiran
 */
public class ApiActionExecutor implements IActionExecutor<ApiAgentAction>
{
	private static Logger logger = LogManager.getLogger(ApiActionExecutor.class);
	
	/**
	 * Http success code start.
	 */
	private static final int SUUCESS_START_CODE = 200;
	
	/**
	 * Http success code end.
	 */
	private static final int SUUCESS_END_CODE = 300;
	
	/**
	 * Object mapper to parser json.
	 */
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * Used to identify file mime type.
	 */
	private static Tika tika = new Tika();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void executeAction(ActionPlanExecutionContext context, ApiAgentAction action) throws Exception
	{
		String paramsJson = context.processTemplate(action.getParameterJson());
		String entityJson = context.processTemplate(action.getModelJson());
		Map<String, String> params = null;
		
		logger.debug("Executing api-action with \nParam Json: {}\nEntity Json: {}", paramsJson, entityJson);
		
		if(StringUtils.isNotBlank(paramsJson))
		{
			params = objectMapper.readValue(paramsJson, new TypeReference<Map<String, String>>(){});
		}
		
		Map<String, Object> entity = null;
		
		if(StringUtils.isNotBlank(entityJson))
		{
			entity = (Map) objectMapper.readValue(entityJson, Object.class);
		}
		
		ClientContext clientContext = context.getClientContext();
		
		RestRequest<?> request = buildRequest(context, action.getAction(), entity, params);
		RestClient client = clientContext.getRestClient();
		
		RestResult<Object> methodResult = (RestResult) client.invokeJsonRequest(request, Object.class);

		//re-authenticate and retry on session timeout
		if(methodResult.getStatusCode() == HttpStatus.SC_UNAUTHORIZED)
		{
			logger.info("As the session got timeout, reauthenticating the session and request will be remade...");
			
			clientContext.reauthenticate();
			methodResult = (RestResult) client.invokeJsonRequest(request, Object.class);
		}
		
		if(methodResult.getStatusCode() < SUUCESS_START_CODE && methodResult.getStatusCode() >= SUUCESS_END_CODE)
		{
			throw new InvalidStateException("Rest call invocation failed.");
		}
		
		Object resposeValue = methodResult.getValue();
		
		if(!processResponse(context, action, resposeValue))
		{
			context.executeNextAction(resposeValue);
		}
	}
	
	/**
	 * Builds REST request for specified action and add request parameters and entity as required.
	 * @param executionContext Context used to fetch action details
	 * @param action Action name to be invoked
	 * @param requestEntity Request entity to set as body for POST method
	 * @param parameters Parameters to be used in url and request parameters
	 * @return Rest result from server
	 */
	public static RestRequest<?> buildRequest(ActionPlanExecutionContext executionContext, String action, Map<String, Object> requestEntity, Map<String, ? extends Object> parameters)
	{
		logger.trace("Building request object for action - {}", action);
		
		ClientContext context = executionContext.getClientContext();
		ActionModel actionModel = context.getAction(action);
		
		if(actionModel == null)
		{
			throw new IllegalArgumentException("Invalid action name specified - " + action);
		}
		
		RestRequest<?> request = null;
		
		if(actionModel.getMethod() == HttpMethod.GET)
		{
			//build GET request
			request = new GetRestRequest(actionModel.getUrl());
			
			addParameters(request, requestEntity);
			addParameters(request, parameters);
		}
		else if(actionModel.getMethod() == HttpMethod.DELETE)
		{
			request = new DeleteRestRequest(actionModel.getUrl());
		}
		else
		{
			//build POST request
			PostRestRequest postRequest = new PostRestRequest(actionModel.getUrl());
			
			//if attachments are expected
			if(actionModel.isAttachmentsExpected())
			{
				logger.trace("Found files on action request entity. Building multi part request. Action - {}", action);
				
				//build multi part request
				postRequest.setMultipartRequest(true);
				Set<String> fileFields = actionModel.getFileFields();
				
				//process each file field 
				for(String field : fileFields)
				{
					processFileField(executionContext, requestEntity, field, postRequest);
				}
				
				postRequest.addJsonPart(IWebUtilsCommonConstants.MULTIPART_DEFAULT_PART, requestEntity);
			}
			else
			{
				if(requestEntity != null)
				{
					postRequest.setJsonBody(requestEntity);
				}
			}
			
			request = postRequest;
		}
		
		//add all required url parameters
		if(actionModel.getUrlParameters() != null)
		{
			for(String param : actionModel.getUrlParameters())
			{
				//if required url param is not provided
				if(parameters == null || !parameters.containsKey(param))
				{
					throw new IllegalArgumentException("Required url-param is not specified with name - " + param);
				}
				
				request.addPathVariable(param, "" + parameters.get(param));
			}
		}
		
		//add all request parameters
		if(actionModel.getRequestParameters() != null)
		{
			for(String param : actionModel.getRequestParameters())
			{
				request.addParam(param, "" + parameters.get(param));
			}
		}

		request.addHeader("Accept", "application/json");
		return request;
	}

	/**
	 * Adds the specified parameters to specified request.
	 *
	 * @param request the request
	 * @param parameters the parameters
	 */
	private static void addParameters(RestRequest<?> request, Map<String, ? extends Object> parameters)
	{
		if(parameters == null || parameters.isEmpty())
		{
			return;
		}
		
		Object value = null;
		
		for(String key : parameters.keySet())
		{
			value = parameters.get(key);
			
			if(value == null)
			{
				continue;
			}
			
			request.addParam(key, "" + value);
		}
	}

	/**
	 * Process specified file field. Add files as attachments (multi parts) to request
	 * @param context used to fetch attachment on need basis
	 * @param requestEntity the request entity
	 * @param field the field
	 * @param postRequest the post request
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void processFileField(ActionPlanExecutionContext context, Map<String, Object> requestEntity, String field, PostRestRequest postRequest)
	{
		Object fileFieldValue = requestEntity.get(field);
		
		//if field value is null, ignore
		if(fileFieldValue == null)
		{
			fileFieldValue = context.getContextAttribute(field);
			
			if(fileFieldValue == null)
			{
				return;
			}
		}
		
		//if file is single valued
		if(fileFieldValue instanceof FileInfo)
		{
			FileInfo fileInfo = (FileInfo) fileFieldValue;
			
			try
			{
				postRequest.addAttachment(field, fileInfo.getFileName(), fileInfo.getFile(), tika.detect(fileInfo.getFile()));
			}catch(IOException ex)
			{
				throw new InvalidStateException("An error occurred while fetching mime type of file: {}", fileInfo.getFile().getPath());
			}
		}
		//if field is multi file valued
		else if(fileFieldValue instanceof Collection)
		{
			Collection<FileInfo> fileInfoLst = (Collection) fileFieldValue;
			
			for(FileInfo fileInfoObj : fileInfoLst)
			{
				try
				{
					postRequest.addAttachment(field, fileInfoObj.getFile(), tika.detect(fileInfoObj.getFile()));
				}catch(IOException ex)
				{
					throw new InvalidStateException("An error occurred while fetching mime type of file: {}", fileInfoObj.getFile().getPath());
				}
			}
		}
	}
	
	/**
	 * Child classes are expected to override this method and handle any custom errors.
	 * @param response
	 * @return should return true, if next action execution is handled by child class implementation.
	 */
	protected boolean processResponse(ActionPlanExecutionContext context, ApiAgentAction action, Object response)
	{
		return false;
	}
}
