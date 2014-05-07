package org.craftercms.social.controllers.rest.v3;

import org.craftercms.social.domain.Actions;
import org.craftercms.social.domain.Tenant;
import org.craftercms.social.exceptions.SocialException;

/**
 * Defines all Rest Services for Security of UGCs and Social.
 * <p>Implementers Must
 * <ul>
 * <li>Check that this call are made with an authenticated user.</li>
 * </ul>
 * </p>
 */
public interface SecurityRestServices {

    /**
     * Returns all possible actions for the current tenant.
     *
     * @return A List of Actions configured for the user's tenant.
     * @throws org.craftercms.social.exceptions.SocialException If unable to get actions.
     */
    Iterable<Actions> getActions() throws SocialException;

    /**
     * Gets all tenants.
     * <b>This is only for SystemAdmin Usage!!!</b>
     *
     * @return A list of all Tenants in the system.
     * @throws SocialException
     */
    Iterable<Tenant> getTenants() throws SocialException;

    /**
     * Gets current user tenant.
     *
     * @return Current Logged User Tenant Information.
     * @throws SocialException If unable to get Current User tenant.
     */
    Tenant getTenant() throws SocialException;

}
