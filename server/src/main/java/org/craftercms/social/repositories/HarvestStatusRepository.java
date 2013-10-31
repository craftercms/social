package org.craftercms.social.repositories;

import org.craftercms.social.domain.HarvestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository("harvestStatusRepository")
public interface HarvestStatusRepository extends MongoRepository<HarvestStatus,String>, HarvestStatusRepositoryCustom {
	
	

}
