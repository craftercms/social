package org.craftercms.social.repositories.security;

import java.util.List;
import java.util.Set;

import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.social.system.SocialSecurityAction;

/**
 *
 */
public interface PermissionRepository {

    /**
     * Checks if the Actions is allow for a given set of Roles for the tenant.
     * @param action Actions to check.
     * @param profileRoles Roles to check action against.
     * @param tenant Tenant owner of the action.
     * @return True if any of the given roles can execute the action, false otherwise.
     * @throws MongoDataException If unable to check.
     */
    boolean isAllowed(String action, Set<String> profileRoles,String tenant) throws MongoDataException;

    /**
     * Returns all the Actions for the Tenant.
     * @param tenant Tenant owner of the action.
     * @return A list of all the security Actions. Empty if nothing is found.
     */
    Iterable<SocialSecurityAction> findActions(String tenant) throws MongoDataException;

    /**
     * Updates the SecurityAction based
     * @param tenant
     * @param actionName
     * @param roles
     * @return The updated SecurityAction, null if tenant/action does not exist.
     */
    SocialSecurityAction updateSecurityAction(final String tenant, final String actionName,
                                              final List<String> roles) throws
        MongoDataException;


}
