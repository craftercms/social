package org.craftercms.social.services.social;

import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.exceptions.UGCException;

/**
 * Defines all Rest Services for Moderation of UGCs.
 * <p>Implementers Must
 * <ul>
 * <li>Audit all Calls</li>
 * <li>Check that this call are made with an authenticated user and the the UGC belongs to the user's tenant.</li>
 * </ul>
 * </p>
 */
public interface SocialServices<T extends SocialUgc> {


    /**
     * Executes the given UserContentInteraction for the given User Content Action.
     * <p>Implementers must check if the current user is allow to update UGC and that the user belongs to the same
     * ugc tenant</p>.
     *
     * @param ugcId       Id of the UGC.
     * @param voteOptions Interaction to be executed.
     * @param userId      Id of the user that is interacting with the content.
     * @param tenantId     Tenant Owner of the UGC.
     * @return A new Public (secure) UGC.
     * @throws org.craftercms.social.exceptions.SocialException if attribute can be deleted.
     * @throws java.lang.IllegalArgumentException               If given UGC does not exist.
     */
    T vote(final String ugcId, final VoteOptions voteOptions, final String userId,
                   final String tenantId) throws SocialException;

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
    T flag(final String ugcId, final String reason, final String userId);

    /**
     * Unflags the given UGC for the given reason.
     * <p>Implementers must check if the current user is allow to update UGC and that the user belongs to the same
     * ugc tenant</p>.
     *
     * @param ugcId  Id of the UGC to unflag.
     * @param reason The reason for this ugc is been unflag.
     * @param userId Id of the user that is unflagging this UGC.
     * @return A new (updated) Public (secure) UGC.
     */
    T unFlag(final String ugcId, final String reason, final String userId);

    /**
     * Change the moderation Status of the given UGC.
     * @param ugcId Id of the UGC to change moderation status.
     * @param moderationStatus new Moderation Status.
     * @param userId Id of the user that is changing the status.
     * @param tenant Tenant Owner of the Ugc.
     */
    T moderate(String ugcId, SocialUgc.ModerationStatus moderationStatus, String userId, String tenant) throws UGCException;
}
