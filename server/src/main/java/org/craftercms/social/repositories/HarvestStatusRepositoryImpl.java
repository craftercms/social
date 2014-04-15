package org.craftercms.social.repositories;

import org.craftercms.commons.mongo.JongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.HarvestStatus;
import org.craftercms.social.exceptions.HarvestStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HarvestStatusRepositoryImpl extends JongoRepository<HarvestStatus> implements HarvestStatusRepository {

    private Logger log = LoggerFactory.getLogger(HarvestStatusRepositoryImpl.class);

    /**
     * Creates a instance of a Jongo Repository.
     */
    public HarvestStatusRepositoryImpl() throws MongoDataException {
    }


    @Override
    public HarvestStatus findHarvestStatusByJobId(String jobId) throws HarvestStatusException {
        log.debug("Getting harvest Status by JobId");
        try {
            return findById(jobId);
        } catch (MongoDataException ex) {
            log.error("Unable to get Harvest Status by Job Id " + jobId, ex);
            throw new HarvestStatusException("Unable to get Harvest Status", ex);
        }
    }
}
