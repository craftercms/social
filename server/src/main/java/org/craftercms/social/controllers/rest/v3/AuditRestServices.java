package org.craftercms.social.controllers.rest.v3;

import org.craftercms.social.controllers.rest.v1.to.PublicUGC;
import org.craftercms.social.domain.UGCAudit;
import org.craftercms.social.exceptions.SocialException;

/**
 * Defines all Rest Services for Moderation of UGCs.
 * <p>Implementers Must
 * <ul>
 * <li>Audit all Calls</li>
 * <li>Check that this call are made with an authenticated user and the the UGC belongs to the user's tenant.</li>
 * </ul>
 * </p>
 *
 */
public interface AuditRestServices {

    /**
     * Finds all Audit UGC's with the given Social Query String.
     * <p>Implementers must check if the current user is allow to read the UGC Audits for its tenant</p>.
     * <b>Social Query String must follow the rules define in {@link org.craftercms.social.controllers.rest.v3.SearchRestServices}</b>
     * @param query String representation of the Social Query String.
     * @param start Where to start getting the results. <b>It must be a non-negative value</b>
     * @param limit Maximum number to return, Value of 0 (i.e. .limit(0)) is equivalent to setting no limit.
     *              Negative Values will be treat as 0.
     * @return A Iterable of Fully filled Audit Ugc.
     * @throws org.craftercms.social.exceptions.SocialException If Unable to do the search.
     * @throws java.lang.IllegalArgumentException If
     * <ul>
     *     <li>Start value is less or equals to 0.</li>
     *     <li>Query value is null,whitespace.</li>
     *     <li>Is not a valid Social Query String.</li>
     * </ul>
     */
    Iterable<UGCAudit> findBy(final String query,int start,int limit) throws SocialException;

    /**
     * Finds all UGC's with the given Social Query String.
     * <p>Implementers must check if the current user is allow to read UGC Audits for its tenant</p>.
     * <b>Social Query String must follow the rules define in {@link org.craftercms.social.controllers.rest.v3.SearchRestServices}</b>
     * @param query String representation of the Social Query String.
     * @return A Iterable of Fully filled PublicUGC's.
     * @throws java.lang.IllegalArgumentException If
     * <ul>
     *     <li>Start value is less or equals to 0.</li>
     *     <li>Query value is null,whitespace.</li>
     *     <li>Is not a valid Social Query String.</li>
     * </ul>
     * @throws org.craftercms.social.exceptions.SocialException If Unable to do the search.
     */
    Iterable<UGCAudit> findBy(final String query) throws SocialException;

}
