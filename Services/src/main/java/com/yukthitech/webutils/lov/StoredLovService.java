/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.webutils.lov;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.webutils.InvalidRequestException;
import com.yukthitech.webutils.common.lov.ValueLabel;
import com.yukthitech.webutils.security.ISecurityService;
import com.yukthitech.webutils.security.UnauthorizedException;
import com.yukthitech.webutils.services.BaseCrudService;

import jakarta.annotation.PostConstruct;

/**
 * The Class StoredLovService.
 */
@Service
public class StoredLovService extends BaseCrudService<StoredLovEntity, IStoredLovRepository>
{
	
	/**
	 * The security service.
	 */
	@Autowired
	private ISecurityService securityService;
	
	/**
	 * The lov val repository.
	 */
	private IStoredLovOptionRepository lovValRepository;
	
	/**
	 * Inits the.
	 */
	@PostConstruct
	private void init()
	{
		this.lovValRepository = super.webutilsRepositoryFactory.getRepository(IStoredLovOptionRepository.class);
	}
	
	/**
	 * Gets the lov values.
	 *
	 * @param name the name
	 * @param dependencyValue the dependency value
	 * @param locale the locale
	 * 
	 * @return the lov values
	 */
	public List<ValueLabel> getLovValues(String name, String dependencyValue, Locale locale)
	{
		Long dependencyId = null;
		
		if(StringUtils.isNotBlank(dependencyValue))
		{
			try
			{
				dependencyId = Long.parseLong(dependencyValue);
			}catch(Exception ex)
			{
				throw new InvalidRequestException("Invalid dependency-value specified (should be numerical value): {}", dependencyValue);
			}
		}
		
		StoredLovEntity lovEntity = super.repository.fetchByName(name);
		
		if(lovEntity == null)
		{
			throw new InvalidRequestException("Invalid stored-lov name specified: " + name);
		}
		
		if(!securityService.isAuthorized(lovEntity))
		{
			throw new UnauthorizedException("Current user is not authorized to access stored-lov - {}", name);
		}
		
		List<StoredLovOptionEntity> options = lovValRepository.fetchByLov(lovEntity.getId(), dependencyId);
		
		if(CollectionUtils.isEmpty(options))
		{
			return Collections.emptyList();
		}
		
		return options.stream()
				.map(opt -> new ValueLabel("" + opt.getId(), opt.getLabel()))
				.collect(Collectors.toList());
	}

	/**
	 * Gets the lov values.
	 *
	 * @param name the name
	 * @param locale the locale
	 * 
	 * @return the lov values
	 */
	public List<ValueLabel> getLovValues(String name, Locale locale)
	{
		return getLovValues(name, null, locale);
	}
	
	/**
	 * Checks if is valid lov.
	 *
	 * @param name the name
	 * 
	 * @return true, if is valid lov
	 */
	public boolean isValidLov(String name)
	{
		return repository.isValid(name);
	}
}
