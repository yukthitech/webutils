package com.webutils.common.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Accessors(chain = true)
public class BasicSaveResponse extends BaseResponse
{
    /**
     * Newly created entity id.
     */
    private long id;
    
    public BasicSaveResponse(long id)
    {
    	this.id = id;
    }
}
