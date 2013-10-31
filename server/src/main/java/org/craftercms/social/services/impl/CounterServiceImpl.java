package org.craftercms.social.services.impl;

import org.craftercms.social.repositories.CounterRepository;
import org.craftercms.social.services.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 

@Service
public class CounterServiceImpl implements CounterService {
	
	@Autowired
	private CounterRepository counterRepository;
	
	@Override
	public long getNextSequence(String collectionName) {
	  
	  return counterRepository.getNextSequence(collectionName);

  }
}
