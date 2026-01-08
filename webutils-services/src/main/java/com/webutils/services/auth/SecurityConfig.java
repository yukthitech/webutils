package com.webutils.services.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Security configuration for the AI Assistant application
 * 
 * Implements OAuth 2.0 authentication with session-based authorization
 * for stateless APIs using custom session validation
 * 
 * Note: SessionAuthorizationFilter has been replaced with SecurityInterceptor
 * which is registered via WebMvcConfig
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer
{
	private static Logger logger = LogManager.getLogger(SecurityConfig.class);
	
	@Autowired
	private SecurityInterceptor securityInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry)
	{
		registry.addInterceptor(securityInterceptor)
			.addPathPatterns("/**")
			.order(1);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
	{
		logger.trace("Adding security config..");

		// Enable CORS
		//http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
		
		http
			.csrf(csrf -> csrf.disable())
			// Disable default HTTP Basic authentication popup
			.httpBasic(httpBasic -> httpBasic.disable())
			// Disable form login (we use custom login page)
			.formLogin(formLogin -> formLogin.disable())
			// Stateless session management (using JWT tokens)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(authz -> authz
				.anyRequest().permitAll());
			// Commented out - using SecurityInterceptor instead of filter
			//.addFilterBefore(sessionAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
