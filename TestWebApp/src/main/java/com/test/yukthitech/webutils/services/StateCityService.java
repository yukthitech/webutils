package com.test.yukthitech.webutils.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.yukthitech.webutils.entity.CityEntity;
import com.test.yukthitech.webutils.entity.ICityRepository;
import com.test.yukthitech.webutils.entity.IStateRepository;
import com.test.yukthitech.webutils.entity.StateEntity;
import com.yukthitech.persistence.repository.RepositoryFactory;

import jakarta.annotation.PostConstruct;

@Service
public class StateCityService
{
	@Autowired
	private RepositoryFactory repositoryFactory;
	
	private ICityRepository cityRepository;
	
	private IStateRepository stateRepository;
	
	@PostConstruct
	private void init()
	{
		cityRepository = repositoryFactory.getRepository(ICityRepository.class);
		stateRepository = repositoryFactory.getRepository(IStateRepository.class);
		
		cityRepository.deleteAll();
		stateRepository.deleteAll();
		
		/*
		StateEntity apState = new StateEntity("Andhra Pradesh", "admin");
		StateEntity karState = new StateEntity("Karnataka", "admin");
		
		stateRepository.save(apState);
		stateRepository.save(karState);
		
		cityRepository.save(new CityEntity("Hyderabad", apState, "admin"));
		cityRepository.save(new CityEntity("Vijayawada", apState, "admin"));
		
		cityRepository.save(new CityEntity("Bangalore", karState, "admin"));
		*/
		StateEntity apState = new StateEntity("Andhra Pradesh");
		StateEntity karState = new StateEntity("Karnataka");
		
		stateRepository.save(apState);
		stateRepository.save(karState);
		
		cityRepository.save(new CityEntity("Hyderabad", apState));
		cityRepository.save(new CityEntity("Vijayawada", apState));
		
		cityRepository.save(new CityEntity("Bangalore", karState));
	}
}
