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

import com.mongodb.MongoException;
import org.apache.commons.lang3.ArrayUtils;
import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.JongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.UGCAudit;
import org.jongo.Find;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UGCAuditRepositoryImpl extends JongoRepository<UGCAudit> implements UGCAuditRepository {

    private static final String ROW = "row";
    private static final String ACTION = "action";
    private Logger log = LoggerFactory.getLogger(UGCAuditRepositoryImpl.class);

    /**
     * Creates a instance of a Jongo Repository.
     */
    public UGCAuditRepositoryImpl() throws MongoDataException {
    }

    @Override
    public UGCAudit findByProfileIdAndUgcIdAndAction(final String profileId, final ObjectId ugcId,
                                                     final UGCAudit.AuditAction action) throws MongoDataException {
        log.debug("Finding UGC Audit for profile {}, id {} and action {} ");
        String query = getQueryFor("social.audit.byProfileIdAction");
        return findOne(query, profileId, ugcId, action);

    }

    @Override
    public Iterable<UGCAudit> findByUgcId(final ObjectId ugcID) throws MongoDataException {
        log.debug("Finding ugc by id {}", ugcID);
        String query = getQueryFor("social.audit.byUgcId");
        return find(query, ugcID);
    }

    @Override
    public Iterable<UGCAudit> findByUgcIdAndAction(final ObjectId ugcId, final UGCAudit.AuditAction auditAction)
        throws MongoDataException {
        log.debug("Finding ugc by id {} and action {} ", ugcId, auditAction);
        String query = getQueryFor("social.audit.byUgcIdAction");
        return find(query, ugcId, auditAction);
    }

    @Override
    public Iterable<UGCAudit> findByUgcIdAndProfileId(final ObjectId ugcId, final String profileId) throws
        MongoDataException {
        log.debug("Finding ugc by id {} and profile Id {} ", ugcId, profileId);
        String query = getQueryFor("social.audit.byUgcIdAction");
        return find(query, ugcId, profileId);
    }

    @Override
    public Iterable<UGCAudit> findByProfileIdAndAction(final String profileId,
                                                       final UGCAudit.AuditAction auditAction) throws
        MongoDataException {
        log.debug("Finding ugc by action {} and profile Id {} ", auditAction, profileId);
        String query = getQueryFor("social.audit.byUgcIdAction");
        return find(query, auditAction, profileId);
    }

    @Override
    public Iterable<UGCAudit> findByProfileId(final String profileId) throws MongoDataException {
        log.debug("Finding ugc by profile Id {}", profileId);
        String query = getQueryFor("social.audit.byProfileId");
        return find(query, profileId);
    }

    @Override
    public Iterable<UGCAudit> findByLastRetrievedRow(long lastRowRetrieve, String[] actionFilters) throws
        MongoDataException {
        try {
            if (ArrayUtils.isEmpty(actionFilters)) {
                String query = getQueryFor("social.audit.ByLastRow");
                return find(query, lastRowRetrieve);
            } else {
                String query = getQueryFor("social.audit.ByLastRowAndActions");
                return find(query, lastRowRetrieve, actionFilters);
            }
        } catch (MongoException ex) {
            log.error("Unable to find by LastRetrievedRow " + lastRowRetrieve, ex);
            throw new MongoDataException("Unable to find by LastRetrievedRow ", ex);
        }
    }

    @Override
    public Iterable<UGCAudit> findByLastRetrievedRow(long lastRowRetrieve, int start, int end,
                                                     String[] actionFilters) throws MongoDataException {

        try {
            Find find = null;
            if (ArrayUtils.isEmpty(actionFilters)) {
                String query = getQueryFor("social.audit.ByLastRow");
                find = getCollection().find(query, lastRowRetrieve);
            } else {
                String query = getQueryFor("social.audit.ByLastRowAndActions");
                find = getCollection().find(query, lastRowRetrieve, actionFilters);
            }
            return find.skip(start).limit(end).as(UGCAudit.class);
        } catch (MongoException ex) {
            log.error("Unable to find by LastRetrievedRow " + lastRowRetrieve, ex);
            throw new MongoDataException("Unable to find by LastRetrievedRow ", ex);
        }
    }

    @Override
    public long count(long lastRowRetrieve, String[] actionFilters) throws MongoDataException {
        try {
            if (ArrayUtils.isEmpty(actionFilters)) {
                String query = getQueryFor("social.audit.ByLastRow");
                return getCollection().count(query, lastRowRetrieve);
            } else {
                String query = getQueryFor("social.audit.ByLastRowAndActions");
                return getCollection().count(query, lastRowRetrieve, actionFilters);
            }
        } catch (MongoException ex) {
            log.error("Unable to count audits for actionFilters" + actionFilters + " and ", ex);
            throw new MongoDataException("Unable to count audits", ex);
        }
    }

    @Override
    public void deleteByRow(final long row) {
        try {
            String query = getQueryFor("social.audit.deleteRow");
            getCollection().remove(query, row);
        } catch (MongoException ex) {
            log.error("Unable to delete UGC by row's id " + row, ex);
        }
    }

}
