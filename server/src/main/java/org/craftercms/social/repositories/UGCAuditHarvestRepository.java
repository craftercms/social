package org.craftercms.social.repositories;

import org.bson.types.ObjectId;
import org.craftercms.social.domain.UGCAuditHarvest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository("uGCAuditHarvestRepository")
public interface UGCAuditHarvestRepository extends MongoRepository<UGCAuditHarvest,ObjectId>, UGCAuditHarvestRepositoryCustom {
	
	UGCAuditHarvest findUGCAuditHarvestByJobId(String jobId);

}
