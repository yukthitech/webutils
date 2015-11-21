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

package com.yukthi.webutils.services;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthi.persistence.repository.RepositoryFactory;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.FileDetails;
import com.yukthi.webutils.IFileService;
import com.yukthi.webutils.repository.file.FileEntity;
import com.yukthi.webutils.repository.file.IFileRepository;
import com.yukthi.webutils.utils.WebUtils;

/**
 * Repository based file service
 * @author akiran
 */
public class RepositoryBasedFileService implements IFileService
{
	private static Logger logger = LogManager.getLogger(RepositoryBasedFileService.class);
	
	/**
	 * Autowired repository factory, used to fetch repository
	 */
	@Autowired
	protected RepositoryFactory repositoryFactory;

	/**
	 * Used to populate tracked fields
	 */
	@Autowired
	private UserService userService;

	private IFileRepository repository;
	
	@PostConstruct
	private void init()
	{
		this.repository = repositoryFactory.getRepository(IFileRepository.class);
	}
	
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.IFileService#addFile(com.yukthi.webutils.FileModel)
	 */
	@Override
	public String addFile(FileDetails fileDetails)
	{
		logger.trace("Adding file - {}", fileDetails);
		
		FileEntity fileEntity = WebUtils.convertBean(fileDetails, FileEntity.class);
		userService.populateTrackingFieldForCreate(fileEntity);
		fileEntity.setSizeInMb(fileDetails.getFile().length());
		
		boolean res = repository.save(fileEntity);
		
		if(!res)
		{
			logger.error("Failed to save file - {}", fileDetails);
			throw new InvalidStateException("Failed to save file - {}", fileDetails);
		}
		
		logger.trace("File got saved with id {}. File - {}", fileEntity.getId(), fileDetails);
		return "" + fileEntity.getId();
	}

	@Override
	public FileDetails getFile(String id)
	{
		logger.trace("Fetching file with id - {}", id);
		
		Long idAsLong = Long.parseLong(id);
		FileEntity entity = repository.findById(idAsLong);
		
		return WebUtils.convertBean(entity, FileDetails.class);
	}

	@Override
	public boolean delete(String id)
	{
		logger.trace("Deleting file with id - {}", id);
		
		Long idAsLong = Long.parseLong(id);
		boolean res = repository.deleteById(idAsLong);
		
		return res;
	}

	
}
