package org.craftercms.social.repositories;

import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.social.domain.HarvestStatus;
import org.craftercms.social.exceptions.HarvestStatusException;

public interface HarvestStatusRepository extends CrudRepository<HarvestStatus> {

    HarvestStatus findHarvestStatusByJobId(String jobId) throws HarvestStatusException;
}
