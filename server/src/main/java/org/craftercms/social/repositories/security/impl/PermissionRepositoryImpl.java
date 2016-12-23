package org.craftercms.social.repositories.security.impl;

import java.util.List;
import java.util.Set;

import org.craftercms.commons.mongo.AbstractJongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.social.system.SocialSecurityAction;
import org.craftercms.social.repositories.security.PermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class PermissionRepositoryImpl extends AbstractJongoRepository<SocialSecurityAction> implements
    PermissionRepository {

    private Logger log = LoggerFactory.getLogger(PermissionRepositoryImpl.class);


    public PermissionRepositoryImpl() {
        super();
    }

    @Override
    public boolean isAllowed(final String action, final Set<String> profileRoles,
                             String context) throws MongoDataException {
        try {
            String query = getQueryFor("social.permissions.isAllowed");
            return findOne(query, action, profileRoles, context) != null;
        } catch (MongoDataException ex) {
            log.error("Unable to check if action belongs to given profile", ex);
            throw new MongoDataException("Unable to check action for given profile roles");
        }
    }

    @Override
    public Iterable<SocialSecurityAction> findActions(final String context) throws MongoDataException {
        String query = getQueryFor("social.permissions.byContextId");
        return find(query, context);
    }

    @Override
    public SocialSecurityAction updateSecurityAction(final String context, final String actionName,
                                                     final List<String> roles) throws MongoDataException {
        String query = getQueryFor("social.permissions.byContextIdAndActionName");
        String update = getQueryFor("social.permissions.updateRoles");
        SocialSecurityAction securityAction = findOne(query, context, actionName);
        if (securityAction == null) {
            return null;
        }
        update(securityAction.getId().toString(), update, false, false, roles);

        return findOne(query, context, actionName);
    }

}
