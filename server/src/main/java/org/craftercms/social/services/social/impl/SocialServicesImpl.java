package org.craftercms.social.services.social.impl;

import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.permissions.annotations.HasPermission;
import org.craftercms.commons.security.permissions.annotations.SecuredObject;
import org.craftercms.social.domain.social.Flag;
import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.exceptions.IllegalUgcException;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.exceptions.UGCException;
import org.craftercms.social.repositories.ugc.UGCRepository;
import org.craftercms.social.security.SocialPermission;
import org.craftercms.social.services.social.SocialServices;
import org.craftercms.social.services.social.VoteOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.craftercms.social.security.SecurityActionNames.*;

/**
 *
 */
public class SocialServicesImpl<T extends SocialUgc> implements SocialServices {


    private UGCRepository<T> ugcRepository;
    private Logger log = LoggerFactory.getLogger(SocialServicesImpl.class);

    @Override
    @HasPermission(action = UGC_UPDATE, type = SocialPermission.class)
    public SocialUgc vote(@SecuredObject final String ugcId, final VoteOptions voteOptions, final String userId,
                          final String tenantId) throws SocialException {
        try {
            T ugc = ugcRepository.findUGC(tenantId, ugcId);
            if (ugc == null) {
                log.debug("Given UGC does not exist with that id {} for that tenant {}", ugcId, tenantId);
                throw new IllegalUgcException("Given UGC does not exist for given tenant");
            }
            switch (voteOptions) {
                case VOTE_UP:
                    voteUp(ugc, userId);
                    break;
                case VOTE_DOWN:
                    voteDown(ugc, userId);
                    break;
                case UNVOTE_DOWN:
                    unvoteDown(ugc, userId);
                case UNVOTE_UP:
                    unvoteUp(ugc, userId);
                default:
                    neutral(ugc, userId);
            }
            ugcRepository.save(ugc);
            return ugc;
        } catch (MongoDataException ex) {
            throw new UGCException("Unable to find UGC with given Id and TenantId");
        }
    }

    @Override
    @HasPermission(action = UGC_FLAG, type = SocialPermission.class)
    public SocialUgc flag(final String ugcId, final String tenantId, final String reason,
                          final String userId) throws SocialException {
        log.debug("Flagging ugc {} due {}", ugcId, reason);
        try {
            T ugcToFlag = ugcRepository.findUGC(tenantId, ugcId);
            if (ugcToFlag == null) {
                throw new IllegalUgcException("Given ugc does not belong to given tenant");
            }
            Flag f = new Flag(reason, userId);
            ugcToFlag.getFlags().add(f);
            ugcRepository.save(ugcToFlag);
            return ugcToFlag;
        } catch (MongoDataException ex) {
            log.error("Unable to flag ugc " + ugcId, ex);
            throw new SocialException("Unable to flag given ugc", ex);
        }
    }

    @Override
    @HasPermission(action = UGC_UNFLAG, type = SocialPermission.class)
    public boolean unFlag(final String ugcId, final String flagId, final String userId,
                          final String tenantId) throws SocialException {
        log.debug("Removing flag {} for ugc Id {}", flagId, ugcId);
        try {
            T ugcToUpdate = ugcRepository.findUGC(tenantId, ugcId);
            if (ugcToUpdate == null) {
                throw new IllegalUgcException("Given ugc does not belong to given tenant");
            }
            ugcToUpdate.getFlags().remove(new Flag(new ObjectId(flagId)));
            ugcRepository.save(ugcToUpdate);
            return true;
        } catch (MongoDataException ex) {
            log.error("Unable to delete flag " + flagId + " from " + ugcId, ex);
            throw new SocialException("Unable to delete flag from UGC");
        }
    }

    @Override
    @HasPermission(action = UGC_MODERATE, type = SocialPermission.class)
    public SocialUgc moderate(final String ugcId, final SocialUgc.ModerationStatus moderationStatus,
                              final String userId, final String tenant) throws UGCException {
        try {
            T ugc = ugcRepository.findUGC(tenant, ugcId);
            if (ugc == null) {
                throw new IllegalUgcException("Given UGC does not exist for current user's tenant");
            }
            if (ugc.getModerationStatus() != SocialUgc.ModerationStatus.TRASH) { // Once is trash stays thrash (TBC)
                ugc.setModerationStatus(moderationStatus);
            }
            ugcRepository.save(ugc);
            return ugc;
        } catch (MongoDataException ex) {
            log.debug("Unable to change ugc moderation status", ex);
            throw new UGCException("Unable to change ugc moderation status", ex);
        }
    }

    @Override
    public Iterable<T> findByModerationStatus(final SocialUgc.ModerationStatus status, final String thread,
                                              final String tenant, final int start, final int limit, final List sort)
            throws UGCException {
        try {
            return ugcRepository.findByModerationStatus(status, thread, tenant, start, limit, sort);
        } catch (MongoDataException ex) {
            log.error("Unable to find by Moderation Status.", ex);
            throw new UGCException("Unable to find by status", ex);
        }
    }

    @Override
    public long countByModerationStatus(final SocialUgc.ModerationStatus status, final String thread, final String
        tenant) throws UGCException {

        try {
            return ugcRepository.countFindByModerationStatus(status,thread,tenant);
        } catch (MongoDataException e) {
            log.error("Unable to count comments by Moderation Status", e);
            throw new UGCException("Unable to count comments by there status",e);
        }
    }

    protected void voteUp(final T ugc, final String userId) {
        unvoteDown(ugc, userId);
        ugc.getVotesUp().add(userId);

    }

    private void voteDown(final T ugc, final String userId) {
        unvoteUp(ugc, userId);
        ugc.getVotesDown().add(userId);
    }

    protected void unvoteDown(final SocialUgc ugc, final String userId) {
        ugc.getVotesDown().remove(userId);
    }

    private void unvoteUp(final T ugc, final String userId) {
        ugc.getVotesUp().remove(userId);
    }

    private void neutral(final T ugc, final String userId) {
        ugc.getVotesDown().remove(userId);
        ugc.getVotesUp().remove(userId);
    }

    public void setUgcRepository(final UGCRepository<T> ugcRepository) {
        this.ugcRepository = ugcRepository;
    }
}
