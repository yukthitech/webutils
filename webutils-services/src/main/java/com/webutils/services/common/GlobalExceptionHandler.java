package com.webutils.services.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.webutils.common.response.BaseResponse;

/**
 * Global exception handler for the Acharya application
 * 
 * Provides centralized error handling and consistent error responses across all
 * REST endpoints
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
	private static Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

	/**
	 * Handle validation errors
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<BaseResponse> handleValidationErrors(MethodArgumentNotValidException ex)
	{
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		BaseResponse response = new BaseResponse()
			.setSuccess(false)
			.setErrors(errors)
			.setMessage("Validation errors");

		return ResponseEntity.badRequest().body(response);
	}

	/**
	 * Handle generic exceptions
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<BaseResponse> handleGenericError(Exception ex)
	{
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		String message = "An unexpected error occurred";
		
		if((ex instanceof UnauthenticatedRequestException) || (ex instanceof BadCredentialsException))
		{
			logger.debug("Authentication failed: {}", ex.getMessage());
			status = HttpStatus.UNAUTHORIZED;
			message = "Invalid credentials. " + ex.getMessage();
		}
		else if(ex instanceof AccessDeniedException)
		{
			logger.debug("Authorization failed: {}", ex.getMessage());
			status = HttpStatus.FORBIDDEN;
			message = "Insufficient permissions. " + ex.getMessage();
		}
		else if((ex instanceof InvalidRequestException) || (ex instanceof HttpMessageNotReadableException))
		{
			logger.debug("Invalid request: {}", ex.getMessage());
			status = HttpStatus.BAD_REQUEST;
			message = ex.getMessage();
		}
		else if(ex instanceof NoResourceFoundException)
		{
			logger.debug("Resource not found: {}", ex.getMessage());
			status = HttpStatus.NOT_FOUND;
			message = ex.getMessage();
		}
		else if(ex instanceof MissingServletRequestParameterException)
		{
			logger.debug("Request parameter missing. Error: {}", ex.getMessage());
			status = HttpStatus.BAD_REQUEST;
			message = ex.getMessage();
		}
		else
		{
			logger.error("Unexpected error occurred", ex);
		}
		
		BaseResponse response = new BaseResponse()
			.setSuccess(false)
			.setMessage(message);

		return ResponseEntity.status(status).body(response);
	}
}
