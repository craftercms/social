package org.craftercms.social.services;

import org.craftercms.social.exceptions.CounterException;

public interface CounterService {
	
	long getNextSequence(String collectionName) throws CounterException;

}
