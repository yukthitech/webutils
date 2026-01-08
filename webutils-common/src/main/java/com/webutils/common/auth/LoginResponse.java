package com.webutils.common.auth;

import com.webutils.common.response.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse extends BaseResponse
{
    private long userId;
    private String authToken;
    private String role;
}
