package com.test.yukthi.webutils.services;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.yukthi.webutils.entity.CityEntity;
import com.test.yukthi.webutils.entity.ICityRepository;
import com.test.yukthi.webutils.entity.IStateRepository;
import com.test.yukthi.webutils.entity.StateEntity;
import com.yukthi.persistence.repository.RepositoryFactory;

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
		
		StateEntity apState = new StateEntity("Andhra Pradesh");
		StateEntity karState = new StateEntity("Karnataka");
		
		stateRepository.save(apState);
		stateRepository.save(karState);
		
		cityRepository.save(new CityEntity("Hyderabad", apState));
		cityRepository.save(new CityEntity("Vijayawada", apState));
		
		cityRepository.save(new CityEntity("Bangalore", karState));
	}
}
