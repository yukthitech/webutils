package com.webutils.common.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BasicListResponse<T> extends BaseResponse
{
    private List<T> values;
    
    public BasicListResponse(boolean success, String message)
    {
    	super(success, message);
    }
}
