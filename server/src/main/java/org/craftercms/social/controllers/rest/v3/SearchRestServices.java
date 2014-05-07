package org.craftercms.social.controllers.rest.v3;


import org.craftercms.social.controllers.rest.v1.to.PublicUGC;
import org.craftercms.social.exceptions.SocialException;

/**
 * Defines all Rest Services for Moderation of UGCs.
 * <p>Implementers Must
 * <ul>
 * <li>Check that this call are made with an authenticated user and the the UGC belongs to the user's tenant.</li>
 * </ul>
 * </p>
 * <h1>Query Specification</h1>
 * <p>The Social Query String is a String representation of a JSON object with 2 main keys <i>query</i> where all the
 * filters are define (using Mongo's valid operator) and the <i>sort</i> where the sorting order is define using
 * default mongo syntax. Below is a empty sample.</p>
 * <pre>
 *     {@code
 *      {
 *           query:{
 *
 *           },
 *           sort:{
 *
 *           }
 *       }
 *     }
 * </pre>
 * <p>The <i>query</i> key Must be allays present and contain at least one valid filter,
 * the <i>sort</i> key is optional but when present must contain at least one valid sorting field.</p>
 *  <p> Exceptions for the query filters:
 *  <ul>
 *       <li>
 *           The Mongo command $where.
 *       </li>
 *       <li>
 *           Is illegal to filter by the field tenant.Implementation Must add that field base on
 *           logged user tenant's.
 *       </li>
 *       <li>
 *           Any attempt to filter fields have and will be ignore.
 *           Queries will always return the a full
 *           {@link org.craftercms.social.controllers.rest.v1.to.PublicUGC}
 *       </li>
 *  </ul>
 *  {@code}
 *
 * Implementations must remove form the query any of the operators or filters listed above.
 * </p>
 */
public interface SearchRestServices {

    /**
     * Finds all UGC's with the given Social Query String.
     * <p>Implementers must check if the current user is allow to read UGC for its tenant</p>.
     * @param query String representation of the Social Query String.
     * @param start Where to start getting the results. <b>It must be a non-negative value</b>
     * @param limit Maximum number to return, Value of 0 (i.e. .limit(0)) is equivalent to setting no limit.
     *              Negative Values will be treat as 0.
     * @return A Iterable of Fully filled PublicUGC's.
     * @throws org.craftercms.social.exceptions.SocialException If Unable to do the search.
     * @throws java.lang.IllegalArgumentException If
     * <ul>
     *     <li>Start value is less or equals to 0.</li>
     *     <li>Query value is null,whitespace.</li>
     *     <li>Is not a valid Social Query String.</li>
     * </ul>
     */
    Iterable<PublicUGC> findBy(final String query,int start,int limit) throws SocialException;

    /**
     * Finds all UGC's with the given Social Query String.
     * <p>Implementers must check if the current user is allow to read UGC for its tenant</p>.
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
    Iterable<PublicUGC> findBy(final String query) throws SocialException;
}
