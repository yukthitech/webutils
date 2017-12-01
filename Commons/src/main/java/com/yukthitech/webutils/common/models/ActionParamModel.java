package com.yukthitech.webutils.common.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents the action parameter of an action.
 * @author akiran
 */
public class ActionParamModel
{
	/**
	 * Indicates this is server side parameter. And should be ignored or passed null from client side
	 */
	public static final int TYPE_NULL = 0;

	/**
	 * Indicates the action parameter is expected as request parameter.
	 */
	public static final int TYPE_REQUEST_PARAM = 1;

	/**
	 * Indicates the action parameter contains fields which are expected to come as request parameters.
	 */
	public static final int TYPE_EMBEDDED_REQUEST_PARAMS = 2;

	/**
	 * Indicates the action parameter is expected as url path parameter.
	 */
	public static final int TYPE_URL_PARAM = 3;

	/**
	 * Indicates the action data is expected as request body.
	 */
	public static final int TYPE_BODY = 4;
	
	/**
	 * Name of the action parameter. Would be null for body type parameter.
	 */
	private String name;
	
	/**
	 * Type of parameter.
	 */
	private int type = 0;
	
	/**
	 * Instantiates a new action param model.
	 */
	public ActionParamModel()
	{}

	/**
	 * Gets the name of the action parameter. Would be null for body type parameter.
	 *
	 * @return the name of the action parameter
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the action parameter. Would be null for body type parameter.
	 *
	 * @param name the new name of the action parameter
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the type of parameter.
	 *
	 * @return the type of parameter
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * Sets the type of parameter.
	 *
	 * @param type the new type of parameter
	 */
	public void setType(int type)
	{
		this.type = type;
	}
	
	/**
	 * Tells if this parameter value is expected as request parameter.
	 * @return true, if this parameter value is expected as request parameter.
	 */
	@JsonIgnore
	public boolean isRequestParameter()
	{
		return (this.type == TYPE_REQUEST_PARAM);
	}

	/**
	 * Tells if this parameter value is expected as request parameter.
	 * @return true, if this parameter value is expected as request parameter.
	 */
	@JsonIgnore
	public boolean isEmbeddedRequestParameters()
	{
		return (this.type == TYPE_EMBEDDED_REQUEST_PARAMS);
	}

	/**
	 * Tells if this parameter value is expected as url path parameter.
	 * @return true, if this parameter value is expected as url path parameter.
	 */
	@JsonIgnore
	public boolean isUrlParameter()
	{
		return (this.type == TYPE_URL_PARAM);
	}

	/**
	 * Tells if this parameter value is expected as request body.
	 * @return true, if this parameter value is expected as request body.
	 */
	@JsonIgnore
	public boolean isBodyParameter()
	{
		return (this.type == TYPE_BODY);
	}
}
