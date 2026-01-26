package com.webutils.common.auth;

import java.util.Set;

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
    private Set<String> roles;
}
