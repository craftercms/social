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
package org.craftercms.social.repositories;

import java.util.Arrays;
import java.util.List;

import com.mongodb.MongoException;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.JongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.UGC;
import org.jongo.Find;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UGCRepositoryImpl extends JongoRepository<UGC> implements UGCRepository {

    private Logger log = LoggerFactory.getLogger(UGCRepository.class);

    private SecurityProfileRepositoryImpl securityProfileService;

    /**
     * Creates a instance of a Jongo Repository.
     */
    public UGCRepositoryImpl() throws MongoDataException {
    }


    @Override
    public Iterable<UGC> findTenantAndTargetIdAndParentIsNull(String tenant, String target) throws MongoDataException {
        log.debug("Finding Ugcs with tenant {} and target {}", tenant, target);
        try {
            String query = getQueryFor("social.ugc.ByTenantTargetAndParentNull");
            return find(query, tenant, target);
        } catch (MongoDataException e) {
            log.error("Unable to find UGC by tenant,target and Parent empty", e);
            throw new MongoDataException("Unable to find Ugc", e);
        }
    }


    @Override
    public Iterable<UGC> findByModerationStatusAndTenantAndTargetId(String[] moderationStatus, String tenant,
                                                                    String targetId,
                                                                    boolean isOnlyRoot) throws MongoDataException {
        log.debug("Finding Ugcs by Moderation Status {} for tenant {} and targetId {} and isOnlyRoot {}",
            moderationStatus, tenant, targetId, isOnlyRoot);
        try {
            String query;
            if (isOnlyRoot) {
                query = getQueryFor("social.ugc.byModerationStatusTenantTargetId");
            } else {
                query = getQueryFor("social.ugc.byModerationStatusTenantIsRoot");
            }
            return find(query, moderationStatus, tenant, isOnlyRoot? null: targetId);
        } catch (MongoException ex) {
            log.error("Unable to ", ex);
            throw new MongoDataException("Unable to ", ex);
        }
    }

    @Override
    public Iterable<UGC> findByTenantAndTargetIdRegex(String tenant, String targetIdRegex, int page, int pageSize,
                                                      String sortField, boolean order) throws MongoDataException {
        log.debug("Finding Ugcs with tenant {} and TargetId regex {} ordering  by {} in order {} from {} to {}",
            tenant, targetIdRegex, sortField, order? "ASC": "DESC", getStart(page, pageSize), pageSize);

        if (StringUtils.isBlank(targetIdRegex)) {
            throw new IllegalArgumentException("Regex can't be null or empty");
        }
        if (StringUtils.isBlank(tenant)) {
            throw new IllegalArgumentException("Tenant can't be null or empty");
        }
        try {
            String query = getQueryFor("social.ugc.byTenantAndTargetRegex");
            Find f = getCollection().find(query, tenant, targetIdRegex);
            if (page > 0 && pageSize > 0) {
                int start = getStart(page, pageSize);
                int end = pageSize;
                f = f.skip(start).limit(end);
            }
            f.sort(createSortQuery(Arrays.asList(new DefaultKeyValue<>(sortField, order))));
            return f.as(UGC.class);
        } catch (MongoException ex) {
            log.error("Unable to find UGC by Target id Regex " + targetIdRegex + " and tenant " + tenant, ex);
            throw new MongoDataException("Unable to Find ugc by Target Id Regex and Tenant", ex);

        }
    }


    @Override
    public Iterable<UGC> findByIds(final ObjectId[] ids) throws MongoDataException {
        log.debug("Finding Ugcs with ids {}", ids);
        try {
            String query = getQueryFor("social.ugc.findByIds");
            return find(query, ids);
        } catch (MongoException ex) {
            log.error("Unable to Find Ugc's by with ids " + ids, ex);
            throw new MongoDataException("Unable to find UGC with Ids", ex);
        }

    }

    @Override
    public Iterable<UGC> findByParentId(final ObjectId parentId) throws MongoDataException {
        log.debug("Finding UGcs with parent Id {}", parentId);
        try {
            String query = getQueryFor("social.ugc.byParentId");
            return find(query, parentId);
        } catch (MongoException ex) {
            log.error("Unable to ugc with parent " + parentId, ex);
            throw new MongoDataException("Unable to Find UGC with given parent ", ex);
        }

    }

    @Override
    public Iterable<UGC> findUGCs(String tenant, String target, String[] moderationStatusArr, int page, int pageSize,
                                  String sortField, boolean sortOrder) throws MongoDataException {
        try {
            String query = getQueryFor("social.ugc.byTenantTargetModerationStatus");
            Find find = getCollection().find(query, tenant, target, moderationStatusArr);
            find = find.sort(createSortQuery(Arrays.asList(new DefaultKeyValue<>(sortField, sortOrder))));
            if (page != -1 && pageSize != -1) {
                int start = getStart(page, pageSize);
                int end = pageSize;
                find.skip(start);
                find.limit(end);
            }
            return find.as(UGC.class);
        } catch (MongoException ex) {
            log.error("Unable to ", ex);
            throw new MongoDataException("Unable to ", ex);
        }
    }

    @Override
    public UGC findUGC(ObjectId id, String[] moderationStatus) throws MongoDataException {
        try {
            String query = getQueryFor("social.ugc.byIdModerationStatus");
            return findOne(query, id, moderationStatus);
        } catch (MongoException ex) {
            log.error("Unable to find ugc with id " + id + " and status " + moderationStatus, ex);
            throw new MongoDataException("Unable to ugc with given id and moderationStatus ", ex);
        }
    }

    @Override
    public Iterable<UGC> findByTenantTargetPaging(String tenant, String target, int page, int pageSize,
                                                  String sortField, boolean sortOrder) throws MongoDataException {
        try {
            String query = getQueryFor("social.ugc.byTenantAndTarget");
            Find find = getCollection().find(query, tenant, target);
            find = find.sort(createSortQuery(Arrays.asList(new DefaultKeyValue<>(sortField, sortOrder))));
            int start = getStart(page, pageSize);
            int end = pageSize;
            find.skip(start);
            find.limit(end);
            return find.as(UGC.class);
        } catch (MongoException ex) {
            log.error("Unable to ", ex);
            throw new MongoDataException("Unable to ", ex);
        }
    }

    @Override
    public Iterable<UGC> findByTenantAndSort(String tenant, String sortField,
                                             boolean sortOrder) throws MongoDataException {

        try {
            String query = getQueryFor("social.ugc.ByTenant");
            Find find = getCollection().find(query, tenant);
            find = find.sort(createSortQuery(Arrays.asList(new DefaultKeyValue<>(sortField, sortOrder))));
            return find.as(UGC.class);
        } catch (MongoException ex) {
            log.error("Unable to find by tenant " + tenant, ex);
            throw new MongoDataException("Unable to by tenant", ex);
        }
    }

    @Override
    public Iterable<String> findPossibleActionsForUGC(final String ugcId, final List<String> roles) throws
        MongoDataException {
        try {
            UGC ugc = findById(ugcId);
            return securityProfileService.findActionsFor(ugc.getSecurityProfile());
        } catch (MongoException ex) {
            log.error("Unable to ", ex);
            throw ex;
        }


    }

    @Override
    public Iterable<UGC> findByParentId(ObjectId parentId, String[] moderationStatus, String sortField,
                                        boolean sortOrder) throws MongoDataException {
        log.debug("Finding UGcs with parent Id {}", parentId);
        try {
            String query = getQueryFor("social.ugc.byParentId");
            return getCollection().find(query, parentId).sort(createSortQuery(Arrays.asList(new DefaultKeyValue<>
                (sortField, sortOrder)))).as(UGC.class);
        } catch (MongoException ex) {
            log.error("Unable to ugc with parent " + parentId, ex);
            throw new MongoDataException("Unable to Find UGC with given parent ", ex);
        }
    }

    private int getStart(int page, int pageSize) {
        if (page <= 0) {
            return 0;
        }
        return (page - 1) * pageSize;
    }

    public void setSecurityProfileService(final SecurityProfileRepositoryImpl securityProfileService) {
        this.securityProfileService = securityProfileService;
    }
}
