package org.craftercms.social.services.social;

import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.exceptions.UGCException;

import java.util.List;

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
     * @param tenantId     Tenant Owner of the UGC.
     * @return A new (updated) Public (secure) UGC.
     */
     T flag(String ugcId, String tenantId, String reason, String userId) throws SocialException;

    /**
     * Un flags the given UGC for the given reason.
     * <p>Implementers must check if the current user is allow to update UGC and that the user belongs to the same
     * ugc tenant</p>.
     *
     * @param ugcId  Id of the UGC to un flag.
     * @param flagId Id of the flag to delete.
     * @param userId Id of the user that is unflagging this UGC.
     * @param tenantId
     * @return A new (updated) Public (secure) UGC.
     */
    boolean unFlag(final String ugcId, final String flagId, final String userId, final String tenantId) throws SocialException;

    /**
     * Change the moderation Status of the given UGC.
     * @param ugcId Id of the UGC to change moderation status.
     * @param moderationStatus new Moderation Status.
     * @param userId Id of the user that is changing the status.
     * @param tenant Tenant Owner of the Ugc.
     */
    T moderate(String ugcId, SocialUgc.ModerationStatus moderationStatus, String userId, String tenant) throws UGCException;


    /**
     * Finds all Comments with the given Moderation status. Optional filter the thread
     * @param status ModerationStatus to filter.
     * @param thread Thread owner of the comments (optional)
     * @param start Where to to start the count.
     * @param limit Amount of Comments to return.
     * @param tenant Tenant Owner of the Ugc.
     * @param sort Sort Fields.
     * @return A Iterable with the results.
     */
    Iterable<T> findByModerationStatus(SocialUgc.ModerationStatus status, String thread, String tenant,
                                       int start, int limit, final List<DefaultKeyValue<String, Boolean>> sort)
            throws UGCException;


    /**
     * Counts all Comments with the given Moderation status. Optional filter the thread
     * @param status ModerationStatus to filter.
     * @param thread Thread owner of the comments (optional)
     * @param tenant Tenant Owner of the Ugc.
     * @return Number of Results.
     */
    long countByModerationStatus(SocialUgc.ModerationStatus status, String thread, String tenant) throws UGCException;
}
