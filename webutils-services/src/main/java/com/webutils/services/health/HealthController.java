package com.webutils.services.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webutils.common.auth.NoAuthentication;
import com.webutils.common.response.BaseResponse;

@RestController
@RequestMapping("/api")
public class HealthController
{
    /**
     * Used to check health of the server. This is bypassed by auth filter.
     * @return
     */
	@NoAuthentication
    @GetMapping("/health")
    public BaseResponse health() 
    {
        return new BaseResponse();
    }

    /**
     * Used to check if current tokens are valid. This is NOT bypassed by auth filter.
     * So success will be returned only when token is valid.
     * @return
     */
    @GetMapping("/tokenCheck")
    public BaseResponse tokenCheck() 
    {
        return new BaseResponse();
    }
}
