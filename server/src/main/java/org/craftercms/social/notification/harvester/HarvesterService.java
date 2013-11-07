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

import java.util.Map;

/**
 *  Harvester interface.  Implementers must override the doHarvest method.
 */
public interface HarvesterService {

    // parameters that get passed to harvester job
    static final String JOB_ID_PARAM = "jobId";
    static final String APPLICATION_ID_PARAM = "applicationId";

    static final String JOB_ID_DB_PARAM = "jobId";
    static final String APPLICATION_ID_DB_PARAM = "applicationId";
    static final String STATUS_DB_PARAM = "status";
    static final String COLLECTION_NAME_DB_PARAM = "collectionName";
    static final String ATTRIBUTES = "attributes";

    static final String LAST_ROW_RETRIEVED = "LAST_ROW_RETRIEVED";
    static final String HARVESTER_STATUS_IDLE = "IDLE";
    static final String HARVESTER_STATUS_RUNNING = "RUNNING";
    static final int DEFAULT_PAGE = 0;
    static final int DEFAULT_PAGE_SIZE = 0;

    /**
     * The main doHarvest method.  A map of harvest details are passed in as parameters.
     * @param params
     */
    void doHarvest(Map<String,?> params);

}
