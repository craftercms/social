package org.craftercms.social.repositories;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.craftercms.social.domain.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class CounterRepositoryImpl implements CounterRepositoryCustom {
	
	@Autowired 
	  private MongoTemplate mongoTemplate;
	
	public int getNextSequence(String collectionName) {
		Query q = new Query();
		query(where("_id").is(collectionName));
		Update u = new Update().inc("seq", 1);
		Counter counter = mongoTemplate.findAndModify(q, u, Counter.class);
		if (counter == null) {
			counter = new Counter();
			counter.setId(collectionName);
			counter.setSeq(1);
			mongoTemplate.save(counter);
		}
       
		return counter.getSeq();
	} 

}
