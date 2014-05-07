package org.craftercms.social.controllers.rest.v3;

import org.craftercms.social.controllers.rest.v1.to.PublicUGC;
import org.craftercms.social.exceptions.SocialException;

/**
 * Defines all Rest Services for Moderation of UGCs.
 * <p>Implementers Must
 * <ul>
 * <li>Audit all Calls</li>
 * <li>Check that this call are made with an authenticated user and the the UGC belongs to the user's tenant.</li>
 * </ul>
 * </p>
 */
public interface ModerationRestServices {


    /**
     * Executes the given UserContentInteraction for the given User Content Action.
     * <p>Implementers must check if the current user is allow to update UGC and that the user belongs to the same
     * ugc tenant</p>.
     *
     * @param ugcId                  Id of the UGC.
     * @param userContentInteraction Interaction to be executed.
     * @param userId                 Id of the user that is interacting with the content.
     * @return A new Public (secure) UGC.
     * @throws org.craftercms.social.exceptions.SocialException if attribute can be deleted.
     * @throws java.lang.IllegalArgumentException               If given UGC does not exist.
     */
    PublicUGC userContentInteraction(final String ugcId, final UserContentInteractions userContentInteraction,
                                     final String userId) throws SocialException;

    /**
     * Flags the given UGC, with for given reason.
     * <p>Implementers must check if the current user is allow to update UGC and that the user belongs to the same
     * ugc tenant</p>.
     *
     * @param ugcId  Id of the UGC to flag.
     * @param reason The reason for this ugc is been flag.
     * @param userId Id of the user that is flagging this UGC.
     * @return A new (updated) Public (secure) UGC.
     */
    PublicUGC flag(final String ugcId, final String reason, final String userId);

    /**
     * Unflags the given UGC for the given reason.
     * <p>Implementers must check if the current user is allow to update UGC and that the user belongs to the same
     * ugc tenant</p>.
     *
     * @param ugcId Id of the UGC to unflag.
     * @param reason The reason for this ugc is been unflag.
     * @param userId Id of the user that is unflagging this UGC.
     * @return A new (updated) Public (secure) UGC.
     */
    PublicUGC unFlag(final String ugcId, final String reason, final String userId);
}
