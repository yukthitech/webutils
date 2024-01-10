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

package com.yukthitech.webutils.services.aop;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;

import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.annotations.AttachmentsExpected;
import com.yukthitech.webutils.common.FileInfo;
import com.yukthitech.webutils.common.annotations.Model;
import com.yukthitech.webutils.common.models.def.FieldDef;
import com.yukthitech.webutils.common.models.def.FieldType;
import com.yukthitech.webutils.common.models.def.ModelDef;
import com.yukthitech.webutils.services.ModelDetailsService;
import com.yukthitech.webutils.utils.WebAttachmentUtils;

import jakarta.annotation.PostConstruct;

/**
 * @author akiran
 */
@Aspect
@Component
public class AttachmentAopService
{
	private static Logger logger = LogManager.getLogger(AttachmentAopService.class);

	/**
	 * Service to get model details
	 */
	@Autowired
	private ModelDetailsService modelDetailsService;
	
	@PostConstruct
	private void init()
	{
		logger.debug("Attachment aop service got initialized");
	}
	
	/**
	 * Sets the specified attachments on the corresponding fields. 
	 * @param joinPoint Used for messaging
	 * @param fieldAttachments Attachments that needs to be set
	 * @param arguments Method arguments in which one of them is expected to be model
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setAttachments(ProceedingJoinPoint joinPoint, Map<String, List<FileInfo>> fieldAttachments, Object arguments[], boolean secured)
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
		List<FileInfo> fieldValue = null;
		
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
				fileDetailsLst = (fileDetailsLst == null || fileDetailsLst.isEmpty()) ? null : fileDetailsLst;
				
				//if no new file attachments are present for a field, retain client sent value of the field
				if(fileDetailsLst == null)
				{
					continue;
				}

				//set secured flags on the files
				for(FileInfo fileInfo : fileDetailsLst)
				{
					fileInfo.setSecured(secured);
				}
				
				//set the file details on the field
				try
				{
					//for multi valued file field
					if(field.isMultiValued())
					{
						fieldValue = (List) PropertyUtils.getProperty(arg, field.getName());
						
						//append attachments to client sent values
						if(fieldValue != null)
						{
							fieldValue.addAll(fileDetailsLst);
						}
						//when there are no client sent values inject attachment details into model
						else
						{
							fieldValue = fileDetailsLst;
						}
						
						PropertyUtils.setProperty(arg, field.getName(), fieldValue);
					}
					//for single file field, replace client sent value with attachment details
					else
					{
						PropertyUtils.setProperty(arg, field.getName(), (fileDetailsLst != null) ? fileDetailsLst.get(0) : null);
					}
				}catch(Exception ex)
				{
					throw new InvalidStateException(ex, "An error occurred while setting file attachment on field - {}.{}", 
							joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
				}
			}
		}
	}

	/**
	 * Expected to be invoked by spring when a service with {@link AttachmentsExpected} annotation is present.
	 * @param joinPoint Joint point details
	 * @return Value returned by target method 
	 * @throws Throwable
	 */
	@Around("execution(@com.yukthitech.webutils.annotations.AttachmentsExpected * *(..))  && @annotation(attachmentsExpected)")
	public Object handleAttachments(ProceedingJoinPoint joinPoint, AttachmentsExpected attachmentsExpected) throws Throwable
	{
		logger.trace("handleAttachments() is called attachment aop service...");

		//fetch multi part request from method arguments
		Object args[] = joinPoint.getArgs();
		MultipartHttpServletRequest request = null;
		
		if(args != null)
		{
			for(Object arg : args)
			{
				if(arg instanceof MultipartRequest)
				{
					request = (MultipartHttpServletRequest)arg;
				}
			}
		}
		
		if(request == null)
		{
			throw new InvalidStateException("No MultipartRequest argument found on method {}.{}() for attachments processing.", 
					joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
		}
		
		//get the files from request
		Map<String, List<FileInfo>> fieldAttachments = WebAttachmentUtils.recieveImports(request);
		
		logger.debug("Found following attachments in request - {}", fieldAttachments);
		
		//set the attachments on the model fields as part of pre processing
		setAttachments(joinPoint, fieldAttachments, joinPoint.getArgs(), attachmentsExpected.secured());
		
		//call actual method
		Object result = joinPoint.proceed();

		//As part of post process clean temp files that were created
		if(fieldAttachments != null)
		{
			for(List<FileInfo> fileLst : fieldAttachments.values())
			{
				for(FileInfo fileInfo : fileLst)
				{
					fileInfo.getFile().delete();
				}
			}
		}
		
		return result;
	}
}
