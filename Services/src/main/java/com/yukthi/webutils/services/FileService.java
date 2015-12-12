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

import java.io.File;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthi.persistence.repository.RepositoryFactory;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.common.FileInfo;
import com.yukthi.webutils.repository.file.FileEntity;
import com.yukthi.webutils.repository.file.IFileRepository;

/**
 * Repository based file service
 * @author akiran
 */
@Service
public class FileService
{
	private static Logger logger = LogManager.getLogger(FileService.class);
	
	/**
	 * Autowired repository factory, used to fetch repository
	 */
	@Autowired
	protected RepositoryFactory repositoryFactory;

	/**
	 * Used to populate tracked fields
	 */
	@Autowired
	private CurrentUserService userService;

	private IFileRepository repository;
	
	@PostConstruct
	private void init()
	{
		this.repository = repositoryFactory.getRepository(IFileRepository.class);
	}
	
	/**
	 * Saves the specified file entity and returns the id
	 * @param fileEntity
	 * @return
	 */
	public Long save(FileEntity fileEntity)
	{
		logger.trace("Saving file - {}", fileEntity);
		
		userService.populateTrackingFieldForCreate(fileEntity);
		fileEntity.setSizeInMb(fileEntity.getFile().length());
		fileEntity.setVersion(1);
		
		boolean res = repository.save(fileEntity);
		
		if(!res)
		{
			logger.error("Failed to save file - {}", fileEntity);
			throw new InvalidStateException("Failed to save file - {}", fileEntity);
		}
		
		logger.trace("File got saved with id {}", fileEntity.getId());
		return fileEntity.getId();
	}

	/**
	 * Fetches the file information for specified id
	 * @param id Id of the file
	 * @return File information
	 */
	public FileInfo getFileInfo(Long id)
	{
		logger.trace("Fetching file-info with id - {}", id);

		return repository.fetchFileInfo(id);
	}
	
	/**
	 * Fetches file informations based on custom attributes specified
	 * @param customAttr1
	 * @param customAttr2
	 * @param customAttr3
	 * @return List of matching file informations
	 */
	public List<FileInfo> fetchFileInformations(String customAttr1, String customAttr2, String customAttr3)
	{
		return repository.fetchWithCustomAttributes(customAttr1, customAttr2, customAttr3);
	}

	/**
	 * Deletes file with specified id
	 * @param id Id of the file to be deleted
	 * @return true if deletion is successful
	 */
	public boolean delete(Long id)
	{
		logger.trace("Deleting file with id - {}", id);
		
		boolean res = repository.deleteById(id);
		
		return res;
	}

	/**
	 * Fetches file content for specified id
	 * @param id
	 * @return Matching file content
	 */
	public File getFileContent(Long id)
	{
		logger.trace("Fetching file content for id - {}", id);
		return repository.fetchFileContent(id);
	}
}
