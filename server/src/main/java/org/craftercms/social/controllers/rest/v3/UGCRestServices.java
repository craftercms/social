package org.craftercms.social.controllers.rest.v3;

import org.craftercms.social.controllers.rest.v1.to.PublicUGC;
import org.craftercms.social.exceptions.SocialException;

/**
 * Defines all Rest Services for UGCs.
 * <p>Implementers Must
 * <ul>
 * <li>Audit all Calls</li>
 * <li>Check that this call are made with an authenticated user and the the UGC belongs to the user's tenant.</li>
 * </ul>
 * </p>
 */
public interface UGCRestServices {

    /**
     * <p>Creates an UGC.</p>
     * <p>Implementers must check if the current user is allow to create UGC for that tenant</p>
     *
     * @param tenant      Tenant owner of the UGC
     * @param parentId    Parent Id of the UGC <i>If not null or empty will be the parent of the UGC else it wont have
     *                    any parent</i>.
     * @param targetId    Target Id of the UGC.
     * @param textContent Actual content of the UGC.
     * @param subject     Subject of the UGC.
     * @param userId      Id of the user creating the UGC.
     * @return A new Public (secure) UGC.
     * @throws org.craftercms.social.exceptions.SocialException If UGC can't be created.
     * @throws java.lang.IllegalArgumentException If given parent UGC does not exist.
     */
    PublicUGC create(final String tenant, final String parentId, final String targetId, final String textContent,
                     final String subject,final String userId) throws SocialException;

    /**
     * <p>Sets an attribute to the given UGC <i>Creates if does not exist</i></p>
     * <p>Implementers must check if the current user is allow to update UGC and that the user belongs to the same
     * ugc tenant</p>.
     *
     * @param ugcId Id of the Ugc to add Attribute.
     * @param name  Name of the attribute to set.
     * @param value Value of the attribute to set.
     * @return A new Public (secure) UGC.
     * @throws org.craftercms.social.exceptions.SocialException if attribute can be set.
     * @throws java.lang.IllegalArgumentException If given UGC does not exist.*
     */
    PublicUGC setAttribute(final String ugcId, final String name, final Object value) throws SocialException;

    /**
     * Deletes a attribute of the given UGC.
     * <p>Implementers must check if the current user is allow to update UGCand that the user belongs to the same
     * ugc tenant</p>.
     *
     * @param ugcId id Id of the Ugc to add Attribute.
     * @param name  Name of the attribute to set.
     * @return A new Public (secure) UGC.
     * @throws org.craftercms.social.exceptions.SocialException if attribute can be deleted.
     * @throws java.lang.IllegalArgumentException If given UGC does not exist.*
     */
    PublicUGC deleteAttribute(final String ugcId, final String name) throws SocialException;

    /**
     * <p>Deletes a UGC <i>If the Ugc is a parent it will delete the whole tree</i></p>
     * <p>Implementers must check if the current user is allow to delete UGC and that the user belongs to the same
     * ugc tenant.t</p>.
     *
     * @param ugcId UGC id to delete.
     * @return True if UGC (and tree) can be deleted , false other wise.
     * @throws org.craftercms.social.exceptions.SocialException if ugc (and or tree) can be deleted)
     * @throws java.lang.IllegalArgumentException If given UGC does not exist.*
     */
    boolean deleteUgc(final String ugcId) throws SocialException;

    /**
     * Updates the given UGC with the given information. Also it will update
     * <p>Implementers must check if the current user is allow to update UGC and that the user belongs to the same
     * ugc tenant.</p>.
     *
     * @param parentId    new Parent Id (in case of a Move)
     * @param targetId    new Target Id (empty of null to leave current value).
     * @param textContent new Text Content (empty of null to leave current value).
     * @param subject     new subject (empty of null to leave current value).
     * @param userId      User id of
     * @return the updated Public (secure) UGC.
     * @throws SocialException If the UGC can be updated.
     * @throws java.lang.IllegalArgumentException If given UGC does not exist.*
     */
    PublicUGC update(final String parentId, final String targetId, final String textContent,
                     final String subject,final String userId) throws SocialException;


    /**
     * Gets a UGC with the given Id.
     * <p>Implementers must check if the current user is allow to read UGC and that the user belongs to the same
     * ugc tenant.</p>.
     *
     * @param ugcId           Id of the desire UGC
     * @param includeChildren True to include the children of this UGC.
     * @param childCount      Amount of children to be include.<i>negative Numbers to all</i>
     * @return The UGC (and its children).
     * @throws java.lang.IllegalArgumentException               If includeChildren is set to true and childCount is
     * set to 0
     * @throws org.craftercms.social.exceptions.SocialException If is unable to get the UGC.
     */
    PublicUGC read(final String ugcId, final boolean includeChildren, final int childCount);

}
