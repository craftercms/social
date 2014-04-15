package org.craftercms.social.repositories;

import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.social.domain.Counter;
import org.craftercms.social.exceptions.CounterException;
import org.springframework.stereotype.Repository;

public interface CounterRepository extends CrudRepository<Counter> {
    long getNextSequence(String collectionName) throws CounterException;
}
