package org.craftercms.social.services;

import org.craftercms.social.domain.Actions;
import org.craftercms.social.exceptions.SecurityProfileException;

/**
 * UGC Security Profile Services.
 */
public interface SecurityProfileService  {
    /**
     * Returns a List of all the actions for the given Profile.
     * @param securityProfile Id of the Security Profile.
     * @return List of Actions that are register to the security Profile. <b>Null if a security profile with given
     * id can't be found.</b>
     */
    Iterable<String> findActionsFor(final String securityProfile) throws SecurityProfileException;

    Actions getDefaultSecurityProfile() throws  SecurityProfileException;
}
