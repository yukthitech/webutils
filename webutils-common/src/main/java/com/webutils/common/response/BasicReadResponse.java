package com.webutils.common.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Accessors(chain = true)
public class BasicReadResponse<T> extends BaseResponse
{
    private T value;
    
    public BasicReadResponse(T value)
    {
    	this.value = value;
    }

    public BasicReadResponse(boolean success, String message)
    {
    	super(success, message);
    }
}
