package com.webutils.common;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface IWebUtilsConstants
{
	ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
    String SESSION_TOKEN_HEADER = "Authorization";
    String SESSION_BEARER_PREFIX = "Bearer ";
}
