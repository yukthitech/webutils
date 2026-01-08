package com.webutils.common.user;

import com.yukthitech.validation.annotations.MinLen;
import com.yukthitech.validation.annotations.NotEmpty;

import lombok.Data;

@Data
public class UserPreference 
{
    @NotEmpty
    @MinLen(3)
    private String key;

    @NotEmpty
    private Object value;
}
