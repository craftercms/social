package org.craftercms.social.services.social.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.permissions.annotations.HasPermission;
import org.craftercms.commons.security.permissions.annotations.SecuredObject;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.services.impl.ProfileServiceRestClient;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.social.Flag;
import org.craftercms.social.domain.social.ModerationStatus;
import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.exceptions.IllegalUgcException;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.exceptions.UGCException;
import org.craftercms.social.repositories.ugc.UGCRepository;
import org.craftercms.social.security.SocialPermission;
import org.craftercms.social.security.SocialSecurityUtils;
import org.craftercms.social.services.social.SocialServices;
import org.craftercms.social.services.social.VoteOptions;
import org.craftercms.social.services.system.TenantConfigurationService;
import org.craftercms.social.services.system.impl.TenantConfigurationServiceImpl;
import org.craftercms.social.services.ugc.pipeline.UgcPipeline;
import org.craftercms.social.util.ebus.SocialEvent;
import org.craftercms.social.util.ebus.UGCEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Reactor;
import reactor.event.Event;

import static org.craftercms.social.security.SecurityActionNames.UGC_FLAG;
import static org.craftercms.social.security.SecurityActionNames.UGC_MODERATE;
import static org.craftercms.social.security.SecurityActionNames.UGC_UNFLAG;
import static org.craftercms.social.security.SecurityActionNames.UGC_UPDATE;
import static org.craftercms.social.security.SecurityActionNames.UGC_VOTING;

/**
 *
 */
public class SocialServicesImpl<T extends SocialUgc> implements SocialServices {


    private UGCRepository<T> ugcRepository;
    private Logger log = LoggerFactory.getLogger(SocialServicesImpl.class);
    private Reactor reactor;
    private UgcPipeline pipeline;
    private TenantConfigurationService tenantConfigurationService;
    private ProfileService profileService;


    @Override
    @HasPermission(action = UGC_VOTING, type = SocialPermission.class)
    public SocialUgc vote(@SecuredObject final String ugcId, final VoteOptions voteOptions, final String userId,
                          final String contextId) throws SocialException {
        try {
            T ugc = ugcRepository.findUGC(contextId, ugcId);
            if (ugc == null) {
                log.debug("Given UGC does not exist with that id {} for that context {}", ugcId, contextId);
                throw new IllegalUgcException("Given UGC does not exist for given context");
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
            pipeline.processUgc(ugc);
            ugcRepository.save(ugc);
            reactor.notify(UGCEvent.VOTE.getName(), Event.wrap(new SocialEvent(ugc, SocialSecurityUtils.getCurrentProfile()
                .getId().toString(),UGCEvent.VOTE)));
            return ugc;
        } catch (MongoDataException ex) {
            throw new UGCException("Unable to find UGC with given Id and contextId");
        }
    }

    @Override
    @HasPermission(action = UGC_FLAG, type = SocialPermission.class)
    public SocialUgc flag(final String ugcId, final String contextId, final String reason,
                          final String userId) throws SocialException {
        log.debug("Flagging ugc {} due {}", ugcId, reason);
        try {
            T ugcToFlag = ugcRepository.findUGC(contextId, ugcId);
            if (ugcToFlag == null) {
                throw new IllegalUgcException("Given ugc does not belong to given context");
            }
            Flag f = new Flag(reason, userId);
            ugcToFlag.getFlags().add(f);
            pipeline.processUgc(ugcToFlag);
            ugcRepository.save(ugcToFlag);
            reactor.notify(UGCEvent.FLAG.getName(), Event.wrap(new SocialEvent(ugcToFlag,SocialSecurityUtils.getCurrentProfile
                ().getId().toString(),UGCEvent.FLAG)));
            return ugcToFlag;
        } catch (MongoDataException ex) {
            log.error("Unable to flag ugc " + ugcId, ex);
            throw new SocialException("Unable to flag given ugc", ex);
        }
    }

    @Override
    @HasPermission(action = UGC_UNFLAG, type = SocialPermission.class)
    public boolean unFlag(final String ugcId, final String flagId, final String userId,
                          final String contextId) throws SocialException {
        log.debug("Removing flag {} for ugc Id {}", flagId, ugcId);
        try {
            T ugcToUpdate = ugcRepository.findUGC(contextId, ugcId);
            if (ugcToUpdate == null) {
                throw new IllegalUgcException("Given ugc does not belong to given context");
            }

            ugcToUpdate.getFlags().remove(new Flag(new ObjectId(flagId)));
            pipeline.processUgc(ugcToUpdate);
            ugcRepository.save(ugcToUpdate);
            reactor.notify(UGCEvent.UNFLAG.getName(), Event.wrap(new SocialEvent(ugcToUpdate,
                SocialSecurityUtils.getCurrentProfile().getId().toString(),UGCEvent.UNFLAG)));
            return true;
        } catch (MongoDataException ex) {
            log.error("Unable to delete flag " + flagId + " from " + ugcId, ex);
            throw new SocialException("Unable to delete flag from UGC");
        }
    }

    @Override
    @HasPermission(action = UGC_MODERATE, type = SocialPermission.class)
    public SocialUgc moderate(final String ugcId, final ModerationStatus moderationStatus,
                              final String userId, final String contextId) throws SocialException {
        try {
            T ugc = ugcRepository.findUGC(contextId, ugcId);
            if (ugc == null) {
                throw new IllegalUgcException("Given UGC does not exist for current user's context");
            }
            if (ugc.getModerationStatus() != ModerationStatus.TRASH) { // Once is trash stays thrash (TBC)
                ugc.setModerationStatus(moderationStatus);
            }
            pipeline.processUgc(ugc);
            ugcRepository.save(ugc);
            reactor.notify(UGCEvent.UNFLAG.getName(), Event.wrap(new SocialEvent(ugc,SocialSecurityUtils.getCurrentProfile()
                .getId().toString(),UGCEvent.UNFLAG)));
            return ugc;
        } catch (MongoDataException ex) {
            log.debug("Unable to change ugc moderation status", ex);
            throw new UGCException("Unable to change ugc moderation status", ex);
        }
    }

    @Override
    public Iterable<T> findByModerationStatus(final ModerationStatus status, final String thread,
                                              final String contextId, final int start, final int limit, final List sort)
            throws UGCException {
        try {
            return ugcRepository.findByModerationStatus(status, thread, contextId, start, limit, sort);
        } catch (MongoDataException ex) {
            log.error("Unable to find by Moderation Status.", ex);
            throw new UGCException("Unable to find by status", ex);
        }
    }

    @Override
    public long countByModerationStatus(final ModerationStatus status, final String thread, final String contextId) throws UGCException {

        try {
            return ugcRepository.countFindByModerationStatus(status,thread, contextId);
        } catch (MongoDataException e) {
            log.error("Unable to count comments by Moderation Status", e);
            throw new UGCException("Unable to count comments by there status",e);
        }
    }

    @Override
    public Iterable<T> findAllFlaggedUgs(final String context, final int start, final int pageSize, final List
    sortOrder) {
        return ugcRepository.findAllFlagged(context,start,pageSize,sortOrder);
    }

    @Override
    public long countAllFlaggedUgs(final String context, final int start, final int pageSize, final List
        sortOrder) {
        return ugcRepository.countAllFlagged(context,start,pageSize,sortOrder);
    }

    @Override
    public Map<? extends String, Object> approveComment(final UGC ugc, final Profile profile) throws ProfileException, SocialException {
       Map<String,Object> map= new HashMap<>();
        if(ugc instanceof SocialUgc){
            T socialUgc= (T)ugc;
            if(socialUgc.getModerationStatus()==ModerationStatus.APPROVED){
                map.put("alreadyApprove",true);
                map.put("approver",profileService.getProfile(ugc.getLastModifiedBy()));
            }else{
                final List<String> roles = SocialSecurityUtils.getSocialRoles(profile);
                String moderationName=tenantConfigurationService.getProperty(ugc.getContextId(),"moderateByMailRole");
                if(roles.contains(moderationName)){
                    if (socialUgc.getModerationStatus() != ModerationStatus.TRASH) { // Once is trash stays thrash (TBC)
                        socialUgc.setModerationStatus(ModerationStatus.APPROVED);
                    }
                    pipeline.processUgc(socialUgc);
                    try {
                        ugcRepository.save(socialUgc);
                    } catch (MongoDataException e) {
                        throw new SocialException("Unable to update UGC");
                    }
                    reactor.notify(UGCEvent.UNFLAG.getName(), Event.wrap(new SocialEvent(ugc,SocialSecurityUtils.getCurrentProfile()
                        .getId().toString(),UGCEvent.UNFLAG)));
                }
                map.put("alreadyApprove",false);
            }
        }
        return map;
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

    public void setReactor(final Reactor reactor) {
        this.reactor = reactor;
    }


    public void setUgcPipeline(UgcPipeline ugcPipeline) {
        this.pipeline = ugcPipeline;
    }


    public void setTenantConfigurationServiceImpl(TenantConfigurationService tenantConfigurationService) {
        this.tenantConfigurationService=tenantConfigurationService;
    }


    public void setProfileServiceRestClient(ProfileService profileService) {
        this.profileService=profileService;
    }
}
