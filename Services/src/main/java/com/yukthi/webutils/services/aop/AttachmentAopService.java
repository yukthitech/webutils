/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthi.webutils.services.aop;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.annotations.AttachmentsExpected;
import com.yukthi.webutils.common.FileInfo;
import com.yukthi.webutils.common.annotations.Model;
import com.yukthi.webutils.common.models.def.FieldDef;
import com.yukthi.webutils.common.models.def.FieldType;
import com.yukthi.webutils.common.models.def.ModelDef;
import com.yukthi.webutils.services.ModelDetailsService;
import com.yukthi.webutils.utils.WebAttachmentUtils;

/**
 * @author akiran
 */
@Aspect
public class AttachmentAopService
{
	private static Logger logger = LogManager.getLogger(AttachmentAopService.class);

	/**
	 * Current request which needs to be scanned for file attachments
	 */
	@Autowired
	private HttpServletRequest request;
	
	/**
	 * Service to get model details
	 */
	@Autowired
	private ModelDetailsService modelDetailsService;
	
	public AttachmentAopService()
	{
		logger.debug("Attachment aop service got initialized");
	}
	
	@PostConstruct
	private void init()
	{
		logger.debug("Attachment aop service got initialized");
	}
	
	/**
	 * Sets the specified attachments on the corresponding fields 
	 * @param joinPoint Used for messaging
	 * @param fieldAttachments Attachments that needs to be set
	 * @param arguments Method arguments in which one of them is expected to be model
	 */
	private void setAttachments(ProceedingJoinPoint joinPoint, Map<String, List<FileInfo>> fieldAttachments, Object arguments[])
	{
		if(arguments == null || arguments.length == 0)
		{
			logger.info("As no arguments found on target method {}.{} ignoring attachment search", 
					joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
			return;
		}
		
		//if no attachment are found in request, use empty map
		if(fieldAttachments == null || fieldAttachments.isEmpty())
		{
			logger.debug("No attachments found in request");
			fieldAttachments = Collections.emptyMap();
		}

		ModelDef modelDef = null;
		List<FileInfo> fileDetailsLst = null;
		
		//loop through arguments to find model attribute
		for(Object arg : arguments)
		{
			//ignore not model arguments
			if(arg.getClass().getAnnotation(Model.class) == null)
			{
				continue;
			}
			
			modelDef = modelDetailsService.getModelDef(arg.getClass());
			
			//loop through field list and populate file fields
			for(FieldDef field : modelDef.getFields())
			{
				if(field.getFieldType() != FieldType.FILE)
				{
					continue;
				}
				
				fileDetailsLst = fieldAttachments.get(field.getName());
				
				//set the file details on the field
				try
				{
					if(fileDetailsLst == null || fileDetailsLst.isEmpty())
					{
						PropertyUtils.setProperty(arg, field.getName(), fileDetailsLst.get(0));
					}
					else
					{
						PropertyUtils.setProperty(arg, field.getName(), null);
					}
				}catch(Exception ex)
				{
					throw new InvalidStateException(ex, "An error occurred while setting file attachment on field - {}.{}", 
							joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
				}
				
				//TODO: Need to support list of attachment for single field
			}
		}
		
	}

	/**
	 * Expected to be invoked by spring when a service with {@link AttachmentsExpected} annotation is present
	 * @param joinPoint Joint point details
	 * @return Value returned by target method 
	 * @throws Throwable
	 */
	@Around("execution(@com.yukthi.webutils.annotations.AttachmentsExpected * *(..))  && @annotation(attachmentsExpected)")
	public Object handleAttachments(ProceedingJoinPoint joinPoint, AttachmentsExpected attachmentsExpected) throws Throwable
	{
		logger.trace("handleAttachments() is called attachment aop service...");
		//as part of pre process populate file fields from request
		Map<String, List<FileInfo>> fieldAttachments = WebAttachmentUtils.recieveImports(request);
		
		setAttachments(joinPoint, fieldAttachments, joinPoint.getArgs());
		
		//call actual method
		Object result = joinPoint.proceed();

		//Nothing to do in post process
		
		return result;
	}
}
