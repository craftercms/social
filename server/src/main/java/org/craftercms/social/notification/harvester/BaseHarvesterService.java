/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.social.notification.harvester;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.HarvestStatus;
import org.craftercms.social.exceptions.AuditException;
import org.craftercms.social.exceptions.HarvestStatusException;
import org.craftercms.social.repositories.HarvestStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Abstract base harvester class.
 * Handles reading & updating the harvester properties, and invoking the internal method.
 *
 * @author Sandra O'Keeffe
 */
public abstract class BaseHarvesterService implements HarvesterService {

    protected final transient Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    protected HarvestStatusRepository harvestStatusRepository;

    @Autowired
    protected MongoTemplate mongoTemplate;

    protected List<String> actionFilters;
    
    public BaseHarvesterService() {
    	actionFilters = new ArrayList<String>();
    }


    /**
     * doHarvest implementation tries to get the lock for this jobId.
     * If it succeeds, it invokes the doHarvestInternal method.
     * It is also responsible for updating the attributes at the end of the job.
     *
     * @param params
     */
    public void doHarvest(Map<String, ?> params) throws HarvestStatusException {

        String jobId = (String) params.get(JOB_ID_PARAM);
        String applicationId = (String) params.get(APPLICATION_ID_PARAM);
       	log.debug("Starting harvester job ID->" + jobId + " with application ID->" + applicationId);
        HarvestStatus harvestStatus = getHarvesterLock(jobId, applicationId);
        if (harvestStatus.getStatus().equals(HARVESTER_STATUS_RUNNING))  {
            try {
                // call the harvest internal method
                doHarvestInternal(harvestStatus.getAttributes());
            } catch (AuditException e) {
                throw new HarvestStatusException("Unable to harvest ",e);
            } finally {
                // Release the lock on the harvester
                releaseHarvesterLock(harvestStatus);
            }
        }
    }

    /**
     * Release the lock for this harvester
     *
     * @param harvestStatus
     * @return
     */
    private HarvestStatus releaseHarvesterLock(HarvestStatus harvestStatus) throws HarvestStatusException {

        harvestStatus.setStatus(HARVESTER_STATUS_IDLE);
        harvestStatus.setApplicationId(""); // clear the application id
        harvestStatus.setLastRunDate(new Date());
        HarvestStatus updatedHarvestStatus = null;
        try {
             harvestStatusRepository.save(harvestStatus);
        } catch (MongoDataException e) {
            log.error("Unable to release Lock for "+harvestStatus, e);
            throw new HarvestStatusException("Unable to release lock",e);
        }

        return updatedHarvestStatus;
    }


    /**
     * Gets the harvester lock for the jobId & applicationId if it is not already taken.
     *
     * If the return HarvestStatus is null, the lock was not attained.
     *
     * @param jobId
     * @param applicationId
     * @return
     */
    private HarvestStatus getHarvesterLock(String jobId, String applicationId) {
        // If HarvestStatus is null, it means that:
        //  1) the harvester is already running, OR
        //  2) the harvester is running for the first time, to a new entry needs to be created

        Query query = new Query(Criteria.where(JOB_ID_DB_PARAM).is(jobId).and(STATUS_DB_PARAM).is(HARVESTER_STATUS_IDLE));

        Update update = new Update();
        update.set(STATUS_DB_PARAM, HARVESTER_STATUS_RUNNING);
        update.set(JOB_ID_DB_PARAM, jobId);
        update.set(APPLICATION_ID_DB_PARAM, applicationId);
        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
        findAndModifyOptions.returnNew(true);
        // upsert will create a new document if it doesn't already exist
        findAndModifyOptions.upsert(true);


        return mongoTemplate.findAndModify(query, update, findAndModifyOptions, HarvestStatus.class);
    }


    /**
     * Concrete class implementation of the harvest.  The harvester properties will be passed to the method.
     * The concrete class should update any changes to the harvesterProperties e.g. lastRowRetrieved
     *
     * @param harvesterProperties
     */
    protected abstract void doHarvestInternal(Map<String, ?> harvesterProperties) throws HarvestStatusException, AuditException;

    public List<String> getActionFilters() {
        return actionFilters;
    }

    public String[] getActionFiltersAsStringArray() {
        return actionFilters.toArray(new String[actionFilters.size()]);
    }

    public void setActionFilters(List<String> actionFilters) {
        this.actionFilters = actionFilters;
    }
}
