package org.craftercms.social.repositories.security.impl;

import java.util.Set;

import org.craftercms.commons.mongo.AbstractJongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.controllers.rest.v3.security.SecurityActions;
import org.craftercms.social.repositories.security.PermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class PermissionRepositoryImpl extends AbstractJongoRepository<SecurityActions> implements PermissionRepository {

    private Logger log = LoggerFactory.getLogger(PermissionRepositoryImpl.class);

    @Override
    public boolean isAllowed(final String action, final Set<String> profileRoles,
                             String tenant) throws MongoDataException {
        try {
            String query = getQueryFor("social.permissions.isAllowed");
            return findOne(query,action,profileRoles,tenant)!=null;
        } catch (MongoDataException ex) {
            log.error("Unable to check if action belongs to given profile", ex);
            throw new MongoDataException("Unable to check action for given profile roles");
        }
    }
}
