package org.craftercms.social.repositories;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.craftercms.social.domain.HarvestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

public class HarvestStatusRepositoryImpl implements
		HarvestStatusRepositoryCustom {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private static final String ID = "_id";

	@Override
	public HarvestStatus findHarvestStatusByJobId(String jobId) {
		Query query = new Query();
		query(where(ID).is(jobId));
		
		return mongoTemplate.findOne(query, HarvestStatus.class);
	}

}
