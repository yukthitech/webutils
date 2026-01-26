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
package com.webutils.services.form.lov.stored;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webutils.cache.CacheConfig;
import com.webutils.cache.CacheFactory;
import com.webutils.cache.ICache;
import com.webutils.lov.LovOption;
import com.webutils.services.auth.UserContext;
import com.webutils.services.common.SecurityService;
import com.webutils.services.user.UserEntity;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

import jakarta.annotation.PostConstruct;

/**
 * The Class StoredLovService.
 */
@Service
public class StoredLovService
{
	private static Logger logger = LogManager.getLogger(StoredLovService.class);

	@Autowired
	private IStoredLovRepository lovRepository;

	@Autowired
	private IStoredLovOptionRepository lovOptionRepository;

	@Autowired
	private CacheFactory cacheFactory;
	
	@Autowired
	private SecurityService securityService;

	private ICache<String, List<LovOption>> lovOptionsCache;
	
	private ICache<String, StoredLovEntity> lovCache;

	@PostConstruct
	private void init()
	{
		lovOptionsCache = cacheFactory.getCache("storedLovService.lovOptions", new CacheConfig<String, List<LovOption>>()
			.maxSize(50));

		lovCache = cacheFactory.getCache("storedLovService.lov", new CacheConfig<String, StoredLovEntity>());
		
		List<StoredLovEntity> lovEntities = lovRepository.fetchAll();

		for(StoredLovEntity lovEntity : lovEntities)
		{
			lovCache.set(lovEntity.getName(), lovEntity);
		}
	}

	private List<LovOption> fetchLovOptionsFromDb(String lovName)
	{
		logger.info("Fetching lov options from database for lov: {}", lovName);
		return lovOptionRepository.fetchLovOptions(lovName).stream()
			.map(option -> new LovOption("" + option.getId(), option.getLabel()))
			.collect(Collectors.toList());
	}

	public List<LovOption> getLovOptions(String lovName)
	{
		StoredLovEntity lovEntity = lovCache.get(lovName);
		
		if(lovEntity == null)
		{
			throw new InvalidArgumentException("No lov exists with specified name: " + lovName);
		}
		
		securityService.checkAuthorization(lovEntity.isAuthRequired(), lovEntity.getAuthorizedRoles());
		return lovOptionsCache.computeIfAbsent(lovName, () -> fetchLovOptionsFromDb(lovName));
	}

	private List<LovOption> fetchChildLovOptionsFromDb(String parentLovName, String parentLovOptionLabel, String childLov)
	{
		logger.info("Fetching child lov options from database for parent lov: {}, parent lov option label: {}, child lov: {}", parentLovName, parentLovOptionLabel, childLov);
		Long parentLovOptionId = lovOptionRepository.fetchLovOptionId(parentLovName, parentLovOptionLabel);
		return lovOptionRepository.fetchChildLovOptions(parentLovOptionId, childLov).stream()
			.map(option -> new LovOption("" + option.getId(), option.getLabel()))
			.collect(Collectors.toList());
	}

	public List<LovOption> getChildLovOptions(String childLov, String parentLovOptionLabel)
	{
		StoredLovEntity lovEntity = lovCache.get(childLov);

		if(lovEntity == null || lovEntity.getParent() == null)
		{
			throw new InvalidStateException("No lov found with name: {} or it does not have a parent", childLov);
		}
		
		securityService.checkAuthorization(lovEntity.isAuthRequired(), lovEntity.getAuthorizedRoles());

		String parentLovName = lovEntity.getParent().getName();
		return lovOptionsCache.computeIfAbsent(
			parentLovOptionLabel + "/" + childLov, 
			() -> fetchChildLovOptionsFromDb(parentLovName, parentLovOptionLabel, childLov)
		);
	}
	
	public String checkAndSaveLovOption(LovConfig lovConfig, String lovName, String optionLabel)
	{
		Set<String> resSet = checkAndSaveLovOption(lovConfig, lovName, Set.of(optionLabel));
		
		if(resSet == null || resSet.isEmpty())
		{
			return null;
		}
		
		return resSet.iterator().next();
	}

	public Set<String> checkAndSaveLovOption(LovConfig lovConfig, String lovName, Collection<String> optionLabels)
	{
		Set<String> existingLabels = lovOptionRepository.fetchLovOptionLabels(lovName, optionLabels);

		if(existingLabels == null)
		{
			existingLabels = Collections.emptySet();
		}

		Set<String> newExistingLabels = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		newExistingLabels.addAll(existingLabels);

		existingLabels = newExistingLabels;

		if(!lovConfig.isSaveMissingOptions())
		{
			TreeSet<String> extraLabels = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
			extraLabels.addAll(newExistingLabels);

			extraLabels.removeAll(optionLabels);

			if(!extraLabels.isEmpty())
			{
				throw new InvalidStateException("Following lov options are not present [Under Lov: {}]: {}", lovName, extraLabels);
			}

			return existingLabels;
		}

		StoredLovEntity lovEntity = lovRepository.fetchByName(lovName);

		if(lovEntity == null)
		{
			throw new InvalidStateException("No lov found with name: {}", lovName);
		}

		StoredLovOptionEntity parentOption = null;

		if(lovEntity.getParent() != null)
		{
			if(lovConfig.getParentOptionLabel() == null)
			{
				throw new InvalidStateException("Parent option label is required for lov: {} [Parent Lov: {}]", lovName, lovEntity.getParent().getName());
			}

			Long parentOptionId = lovOptionRepository.fetchLovOptionId(lovEntity.getParent().getName(), lovConfig.getParentOptionLabel());

			if(parentOptionId == null)
			{
				throw new InvalidStateException("No parent option found with label: {} [Lov: {}]", lovConfig.getParentOptionLabel(), lovEntity.getParent().getName());
			}

			parentOption = new StoredLovOptionEntity(parentOptionId);
		}

		for(String optionLabel : optionLabels)
		{
			if(existingLabels.contains(optionLabel))
			{
				continue;
			}

			StoredLovOptionEntity option = new StoredLovOptionEntity()
				.setLov(lovEntity)
				.setParentOptionId(parentOption != null ? parentOption.getId() : null)
				.setLabel(optionLabel)
				.setApproved(lovConfig.isRequireApproval() ? false : true)
				.setCreatedBy(new UserEntity(UserContext.getCurrentUser().getId()))
				.setCreatedOn(new Date());

			logger.debug("Saving lov option to database [Lov: {}, Option: {}, Parent Lov: {}, Parent Option: {}]", 
				lovName, optionLabel, 
				lovEntity.getParent() != null ? lovEntity.getParent().getName() : null, 
				lovConfig.getParentOptionLabel()
			);
			
			lovOptionRepository.save(option);
			existingLabels.add(optionLabel);
		}

		return existingLabels;
	}

	public boolean isValidLov(String lovName)
	{
		return (lovCache.get(lovName) != null);
	}
}
