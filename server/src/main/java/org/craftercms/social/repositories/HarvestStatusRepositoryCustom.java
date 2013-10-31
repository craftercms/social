package org.craftercms.social.repositories;

import org.craftercms.social.domain.HarvestStatus;

public interface HarvestStatusRepositoryCustom {
	
	HarvestStatus findHarvestStatusByJobId(String jobId);

}
