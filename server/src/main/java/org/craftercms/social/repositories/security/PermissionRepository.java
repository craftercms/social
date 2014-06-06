package org.craftercms.social.repositories.security;

import java.util.Set;

import org.craftercms.commons.mongo.MongoDataException;

/**
 *
 */
public interface PermissionRepository {

    boolean isAllowed(String action, Set<String> profileRoles,String tenant) throws MongoDataException;
}
