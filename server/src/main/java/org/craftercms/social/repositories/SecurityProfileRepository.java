package org.craftercms.social.repositories;

import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.Actions;

/**
 * Possible data storage operation of a security profile.
 */
public interface SecurityProfileRepository extends CrudRepository<Actions> {

    /**
     * Find default System Security Profile
     *
     * @return Default Security Profile, null if nothing if found.
     */
    Actions findDefault() throws MongoDataException;

    /**
     * Finds all the possible actions for a security Profile.
     *
     * @param securityProfile Id of the security profile.
     * @return A list of possible actions that a security profile has. <b>Null</b> If a security profile is not found.
     * @throws MongoDataException
     */
    Iterable<String> findActionsFor(String securityProfile) throws MongoDataException;
}
