package com.webutils.common.response;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

import lombok.Data;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class BaseResponse 
{
    private boolean success = true;
    private String message;
    private Map<String, String> errors;
    
    public BaseResponse(boolean success, String message)
    {
        this.success = success;
        this.message = message;
    }

    public BaseResponse(String message)
    {
        this.message = message;
    }
}
