package org.craftercms.social.repositories;

import org.bson.types.ObjectId;
import org.craftercms.social.domain.Counter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository("counterRepository")
public interface CounterRepository extends MongoRepository<Counter,ObjectId>, CounterRepositoryCustom {

}
