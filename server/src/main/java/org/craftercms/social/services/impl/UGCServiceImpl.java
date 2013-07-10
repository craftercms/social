/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.social.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.security.api.RequestContext;
import org.craftercms.social.domain.Action;
import org.craftercms.social.domain.AttachmentModel;
import org.craftercms.social.domain.AttachmentsList;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.UGC.ModerationStatus;
import org.craftercms.social.domain.UGCAudit;
import org.craftercms.social.domain.UGCAudit.AuditAction;
import org.craftercms.social.exceptions.PermissionDeniedException;
import org.craftercms.social.moderation.ModerationDecision;
import org.craftercms.social.repositories.UGCAuditRepository;
import org.craftercms.social.repositories.UGCRepository;
import org.craftercms.social.services.PermissionService;
import org.craftercms.social.services.SupportDataAccess;
import org.craftercms.social.services.UGCService;
import org.craftercms.social.util.action.ActionEnum;
import org.craftercms.social.util.action.ActionUtil;
import org.craftercms.social.util.support.CrafterProfile;
import org.craftercms.social.util.support.ResultParser;
import org.craftercms.social.util.web.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UGCServiceImpl implements UGCService {

    private final transient Logger log = LoggerFactory.getLogger(UGCServiceImpl.class);
    @Autowired
    private transient UGCRepository repository;
    @Autowired
    private transient UGCAuditRepository auditRepository;
    @Autowired
    private ModerationDecision moderationDecisionManager;
    @Autowired
    @Qualifier("targetMapReduseParser")
    private ResultParser parser;
    @Autowired
    private SupportDataAccess supportDataAccess;
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private CrafterProfile crafterProfileService;

    @Override
    public List<UGC> findByModerationStatus(final ModerationStatus moderationStatus, final String tenant) {
        log.debug("Looking for Users with status %s and tenant %s", moderationStatus, tenant);
        String profileId = RequestContext.getCurrent().getAuthenticationToken().getProfile().getId();
    	 return findUGCs(new String[] {
    			 moderationStatus.toString() },tenant,null,profileId,true);
    }

    @Override
    public UGC updateUgc(ObjectId ugcId, String tenant, String targetId, String profileId, ObjectId parentId,
                         String textContent, MultipartFile[] attachments) throws PermissionDeniedException {
        UGC ugc = null;
        if (existsUGC(ugcId)) {
            ugc = repository.findOne(ugcId);
            ugc.setAttachmentsList(getAttachmentsList(ugc.getAttachmentId(), tenant));
            ugc.setTargetId(targetId);
            ugc.setParentId(parentId);
            ugc.setTextContent(textContent);
            ugc.setAttachmentId(updateUGCAttachments(ugc, attachments));
            this.repository.save(ugc);
            //Audit call
            auditUGC(ugcId, AuditAction.UPDATE, tenant, profileId, null);
        }
        return ugc;
    }

    @Override
    public void deleteUgc(ObjectId ugcId, String tenant, String profileId) throws PermissionDeniedException{
        if (existsUGC(ugcId)) {
            if (!permissionService.allowed(ActionEnum.DELETE, ugcId, profileId)) {
                log.error("Delete UGC permission not granted", ugcId);
                throw new PermissionDeniedException("Delete UGC action not granted to current user", ActionEnum.DELETE);
            }
            List<UGC> children = repository.findByParentId(ugcId);
            for (UGC ugcChild: children) {
                deleteUgc(ugcChild.getId(), tenant, profileId);
            }
            this.repository.delete(ugcId);
            //Audit call
            auditUGC(ugcId, AuditAction.DELETE, tenant, profileId, null);
        }
    }

    @Override
    public void deleteUgc(List<String> ugcIds, String tenant, String profileId) throws PermissionDeniedException{
        for (String id: ugcIds) {
            try {
                deleteUgc(new ObjectId(id), tenant, profileId);
            } catch(PermissionDeniedException e) {
                log.error("Permission denied to delete " + id, id);
            }
        }
    }

    private ObjectId[] updateUGCAttachments(UGC ugc, MultipartFile[] files) {
        if (files != null) {
            ObjectId[] attachments = new ObjectId[files.length];
            ObjectId currentId = null;
            try {
                for (int i = 0; i < files.length; i++) {
                    currentId = getAttachedId(files[i], ugc);
                    if (currentId == null) {
                        attachments[i] = supportDataAccess.saveFile(files[i]);
                    } else {
                        attachments[i] = currentId;
                    }
                }
            } catch (IOException ex) {
                log.error("Unable to save the attachemnts ", ex);
                attachments = new ObjectId[] {};
            }
            return attachments;
        } else {
            return new ObjectId[] {};
        }
    }

    private ObjectId getAttachedId(MultipartFile file, UGC ugc) {
        return ugc.getAttachmentsList().findObjectId(file);

    }


    @Override
    public UGC updateModerationStatus(ObjectId uGCId, ModerationStatus newStatus, String tenant, String profileId) {
        if (existsUGC(uGCId)) {
            UGC ugc = repository.findOne(uGCId);
            ugc.setModerationStatus(newStatus);
            //Audit call
            auditUGC(uGCId, AuditAction.MODERATE, tenant, profileId, null);
            return populateUGCWithProfile(save(ugc));
        } else {
            log.error("UGC {} does not exist", uGCId);
            throw new DataIntegrityViolationException("Parent UGC does not exist");
        }
    }

    @Override
    public UGC newUgc(UGC ugc, MultipartFile[] files, List<Action> actions, String tenant, String profileId)
            throws PermissionDeniedException {
    	List<Action> resolveActions = resolveUGCActions(ugc, actions);
    	ugc.setModerationStatus(ModerationStatus.UNMODERATED);
        checkForModeration(ugc);
        ugc.setAttachmentId(saveUGCAttachments(files));
        ugc.setActions(resolveActions);
        UGC ugcWithProfile = populateUGCWithProfile(save(ugc));
        ugcWithProfile.setAttachmentsList(getAttachmentsList(ugcWithProfile.getAttachmentId(), ugcWithProfile.getTenant()));
        //Audit call
        auditUGC(ugcWithProfile.getId(), AuditAction.CREATE, tenant, profileId, null);
        return ugcWithProfile;
    }



    @Override
    public UGC newChildUgc(UGC ugc, MultipartFile[] files, List<Action> actions, String tenant, String profileId)
            throws PermissionDeniedException {
        if (!existsUGC(ugc.getParentId())) {
            log.error("Parent for {} does not exist", ugc);
            throw new DataIntegrityViolationException("Parent UGC does not exist");
        }
        // resolve and set the actions
        ugc.setActions(resolveUGCActions(ugc, actions));
        ugc.setModerationStatus(ModerationStatus.UNMODERATED);
        checkForModeration(ugc);
        ugc.setAttachmentId(saveUGCAttachments(files));
        UGC ugcWithProfile = populateUGCWithProfile(save(ugc));
        ugcWithProfile.setAttachmentsList(getAttachmentsList(ugcWithProfile.getAttachmentId(), ugcWithProfile.getTenant()));
        //Audit call
        auditUGC(ugcWithProfile.getId(), AuditAction.CREATE, tenant, profileId, null);
        return ugcWithProfile;
    }

    private ObjectId[] saveUGCAttachments(MultipartFile[] files) {
        if (files != null) {
            ObjectId[] attacments = new ObjectId[files.length];
            try {
                for (int i = 0; i < files.length; i++) {
                    attacments[i] = supportDataAccess.saveFile(files[i]);
                }
            } catch (IOException ex) {
                log.error("Unable to save the attachemnts ", ex);
                attacments = new ObjectId[] {};
            }
            return attacments;
        } else {
            return new ObjectId[] {};
        }
    }

    @Override
    public boolean existsUGC(ObjectId id) {
        return repository.findOne(id) != null;
    }

    /**
     * Does the actual Saving of the UGC
     *
     * @param ugc
     * @return
     */
    private UGC save(UGC ugc) {
        log.debug("Saving {}", ugc.toString());
        return repository.save(ugc);
    }

    private void checkForModeration(UGC ugc) {
        if (moderationDecisionManager.needModeration(ugc) && ugc.getModerationStatus() != ModerationStatus.APPROVED) {
            ugc.setModerationStatus(ModerationStatus.PENDING);
        }
    }

    @Override
    public UGC likeUGC(ObjectId ugcId, String tenant, String profileId) {
        if (existsUGC(ugcId)) {
            UGC ugc = repository.findOne(ugcId);
            if (userCan(AuditAction.LIKE, ugc, profileId)) {
                ugc.setLikeCount(ugc.getLikeCount() + 1);
                auditUGC(ugcId, AuditAction.LIKE, tenant, profileId, null);
                checkForModeration(ugc);
                if (!userCan(AuditAction.DISLIKE, ugc, profileId)) {
                	ugc.setOffenceCount(ugc.getOffenceCount() - 1);
                	removeAuditUGC(ugcId, AuditAction.DISLIKE, tenant, profileId, null);
                }
                return populateUGCWithProfile(save(ugc));
            } else {
            	ugc.setLikeCount(ugc.getLikeCount() - 1);
            	removeAuditUGC(ugcId, AuditAction.LIKE, tenant, profileId, null);
            	return populateUGCWithProfile(save(ugc));
            }
        } else {
            log.debug("UGC Id {} does not exist", ugcId);
            throw new DataIntegrityViolationException("UGC does not exist");
        }
    }

    private boolean userCan(AuditAction like, UGC ugc, String profileId) {
        UGCAudit r = auditRepository.findByProfileIdAndUgcIdAndAction(profileId, ugc.getId(), like);
        return (r == null);
    }

    @Override
    public UGC dislikeUGC(ObjectId ugcId, String tenant, String profileId) {
        if (existsUGC(ugcId)) {
            UGC ugc = repository.findOne(ugcId);
            if (userCan(AuditAction.DISLIKE, ugc, profileId)) {
                ugc.setOffenceCount(ugc.getOffenceCount() + 1);
                auditUGC(ugcId, AuditAction.DISLIKE, tenant, profileId, null);
                checkForModeration(ugc);
                if (!userCan(AuditAction.LIKE, ugc, profileId)) {
                	ugc.setLikeCount(ugc.getLikeCount() - 1);
                	removeAuditUGC(ugcId, AuditAction.LIKE, tenant, profileId, null);
                }
                return populateUGCWithProfile(save(ugc));
            } else {
                //return null;
            	ugc.setOffenceCount(ugc.getOffenceCount() - 1);
            	removeAuditUGC(ugcId, AuditAction.DISLIKE, tenant, profileId, null);
            	return populateUGCWithProfile(save(ugc));
            }
        } else {
            log.debug("UGC Id {} does not exist", ugcId);
            throw new DataIntegrityViolationException("UGC does not exist");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getTargets() {
        return (List<String>) supportDataAccess.mapReduce(UGC.class, "classpath:/mongo/getTargetsMap.js",
                "classpath:/mongo/getTargetsReduce.js", parser);
    }

    @Override
    public Attachment getAttachment(ObjectId attachmentId) {
        return supportDataAccess.getAttachment(attachmentId);
    }

    @Override
    public void streamAttachment(ObjectId attachmentId, HttpServletResponse response) {
        supportDataAccess.streamAttachment(attachmentId, response);
    }

    @Override
    public List<UGC> findByModerationStatusAndTargetId(ModerationStatus moderationStatus, String tenant, String target) {
    	String profileId = RequestContext.getCurrent().getAuthenticationToken().getProfile().getId();
    	 return findUGCs(new String[] {
    			 moderationStatus.toString() },tenant,target,profileId,true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> findTargetsForModerationStatus(ModerationStatus moderationStatus, String tenant) {
        String query = String.format("{moderationStatus:'%s', tenant:'%s' }", moderationStatus.toString(), tenant);
        return (List<String>) supportDataAccess.mapReduceWithQuery(UGC.class, query, "classpath:/mongo/getTargetsMap.js",
                "classpath:/mongo/getTargetsReduce.js", parser);
    }

    @Override
    public List<UGC> findByTarget(String tenant, String target) {
    	String profileId = RequestContext.getCurrent().getAuthenticationToken().getProfile().getId();
        
        return findUGCs(null,tenant,target,profileId,true);
    }

    @Override
    public List<UGC> findByTargetValidUGC(String tenant, String target, String profileId, boolean sortChronological) {
    	return findUGCs(new String[] {
				ModerationStatus.APPROVED.toString(), ModerationStatus.UNMODERATED.toString() },tenant,target,profileId,sortChronological);
    }

    @Override
    public int getTenantTargetCount(String tenant, String target) {
    	String profileId = RequestContext.getCurrent().getAuthenticationToken().getProfile().getId();
    	Profile p = crafterProfileService.getProfile(profileId);
    	Query q = this.permissionService.getQuery(ActionEnum.READ, p);
    	return repository.findTenantAndTargetIdAndParentIsNull(tenant, target, q).size();
    	
    }

    @Override
    public List<UGC> findByTargetValidUGC(String tenant, String target, String profileId, int page, int pageSize, boolean sortChronological) { //TODO: target is not being used. Filter is not applied
    	Profile p = crafterProfileService.getProfile(profileId);
    	Query q = this.permissionService.getQuery(ActionEnum.READ, p);
    	List<UGC> list = repository.findByTenantTargetPaging(tenant,target,page,pageSize,sortChronological, q);
        return populateUGCListWithProfiles(list);
    }

    @Override
    public UGC flagUGC(ObjectId ugcId, String reason, String tenant, String profileId) {
        UGC ugc = repository.findOne(ugcId);
        if (userCan(AuditAction.FLAG, ugc, profileId)) {
            ugc.setFlagCount(ugc.getFlagCount() + 1);
            auditUGC(ugcId, AuditAction.FLAG, tenant, profileId, reason);
            checkForModeration(ugc);
            return populateUGCWithProfile(save(ugc));
        } else {
            return null;
        }
    }

    private void auditUGC(ObjectId ugcId, AuditAction auditAction, String tenant, String profileId, String reason) {
        auditRepository.save(new UGCAudit(ugcId, tenant, profileId, auditAction, reason));
    }
    
    private void removeAuditUGC(ObjectId ugcId, AuditAction auditAction, String tenant, String profileId, String reason) {
        UGCAudit audit = auditRepository.findByProfileIdAndUgcIdAndAction(profileId, ugcId, auditAction);
    	auditRepository.delete(audit);
    }

    @Override
    public UGC threadTree(UGC last) {
        // TODO Find A better way
        if (last.getParentId() == null) {
            return last;
        } else {
            UGC parent = repository.findOne(last.getParentId());
            parent.addChild(last);
            return threadTree(parent);
        }
    }

    @Override
    public void setAttributes(ObjectId ugcId, Map<String, Object> attributeMap, String tenant, String profileId) {
        if (attributeMap != null) {
            UGC ugc = findById(ugcId);
            if (ugc != null) {
                Map<String, Object> existingAttibuteMap = ugc.getAttributes();

                if (existingAttibuteMap == null) {
                    ugc.setAttributes(attributeMap);
                } else {
                    existingAttibuteMap.putAll(attributeMap);
                }

                repository.save(ugc);
                //Audit call
                auditUGC(ugcId, AuditAction.UPDATE, tenant, profileId, null);
            }
        }
    }

    @Override
    public List<UGC> findByProfileAction(String profileId, AuditAction action) {
        List<UGCAudit> lst = auditRepository.findByProfileIdAndAction(profileId, action);
        List<ObjectId> ugcs=new ArrayList<ObjectId>();
        for (UGCAudit audit : lst) {
            ugcs.add(audit.getUgcId());
        }
        return repository.findByIds((ObjectId[])ugcs.toArray());
    }

    @Override
    public UGC findById(ObjectId ugcId) {
        UGC ugc = repository.findOne(ugcId);
        if (ugc == null) {
        	return null;
        }
        ugc.setAttachmentsList(getAttachmentsList(ugc.getAttachmentId(), ugc.getTenant()));
        return populateUGCWithProfile(ugc);
    }
    
        @Override
    public UGC findUGCAndChildren(ObjectId ugcId) {
    	String profileId = RequestContext.getCurrent().getAuthenticationToken().getProfile().getId();
    	Profile p = crafterProfileService.getProfile(profileId);
    	Query q = this.permissionService.getQuery(ActionEnum.READ, p);	
    	UGC ugc = repository.findUGC(ugcId, q);
    	if (ugc == null) {
    		return null;
    	}
        ugc.setAttachmentsList(getAttachmentsList(ugc.getAttachmentId(), ugc.getTenant()));
        ugc = populateUGCWithProfile(ugc);
        
        return initUGCAndChildren(ugc, p);

    }

    public UGC initUGCAndChildren(UGC ugc, Profile p) {
        ugc.setAttachmentsList(getAttachmentsList(ugc.getAttachmentId(), ugc.getTenant()));
        UGC populatedUgc = populateUGCWithProfile(ugc);
        
    	Query q = this.permissionService.getQuery(ActionEnum.READ, p);
        List<UGC> children = repository.findByParentIdWithReadPermission(populatedUgc.getId(), q);
        for (UGC ugcChild: children) {
            ugcChild = initUGCAndChildren(ugcChild, p);
        }
        populatedUgc.getChildren().addAll(children);
        return populatedUgc;

    }

    private List<UGC> populateUGCListWithProfiles(List<UGC> ugcList) {
        Map<String, List<UGC>> profileIdUGCMap = new HashMap<String, List<UGC>>();

        if (ugcList != null && ugcList.size() > 0) {

            for (UGC ugc : ugcList) {
                // TODO: get the profile info from a cache if already cached and set on the UGC without adding to the map

                List<UGC> subUGCList = profileIdUGCMap.get(ugc.getProfileId());
                if (subUGCList == null) {
                    subUGCList = new ArrayList<UGC>();
                    profileIdUGCMap.put(ugc.getProfileId(), subUGCList);
                }
                subUGCList.add(ugc);
                if (ugc.getAttachmentId()!=null) {
                    ugc.setAttachmentsList(getAttachmentsList(ugc.getAttachmentId(), ugc.getTenant()));
                }
            }

            List<Profile> profileList = crafterProfileService.getProfilesByIds(Arrays.asList(profileIdUGCMap.keySet().toArray(
                    new String[profileIdUGCMap.size()])));

            if (profileList != null) {
                for (Profile profile : profileList) {
                    List<UGC> subUGCList = profileIdUGCMap.get(profile.getId());
                    if (subUGCList != null) {
	                    for (UGC ugc : subUGCList) {
	
	                        ugc.setProfile(profile);
	                    }
                    }
                }
            }
        }



        return ugcList;
    }

    private UGC populateUGCWithProfile(UGC ugc) {
        ugc.setProfile(crafterProfileService.getProfile(ugc.getProfileId()));
        return ugc;
    }

    private AttachmentsList getAttachmentsList(ObjectId[] attachmentsId, String tenant) {
    	AttachmentsList data = new AttachmentsList();
        Attachment attachment;
        AttachmentModel a;
        for (ObjectId id: attachmentsId) {
            attachment = supportDataAccess.getAttachment(id);
            a = new AttachmentModel(attachment.getFilename(),id,attachment.getContentType(), tenant);
            data.addAttachmentModel(a);
        }
        return data;
    }


    /**
     * Resolve the full set of actions for the new UGC
     * @param ugc
     * @param actions from request
     * @return
     */
    private List<Action> resolveUGCActions(UGC ugc,
                                                List<Action> actions) {
    	if (actions == null || actions.size() ==0) {
            // if there is a parent, get the parent's actions & set in there
        	return resolveUGCActionsEmpty(ugc);
            
        } 
        // resolve what actions need to be added from the parent if any
        // if there is no parent, take them from the default actions
        List<Action> actionsToAdd = new ArrayList<Action>();
        List<String> actionsMissing = new ArrayList<String>();
        for (String action : ActionUtil.ACTIONS) {
            actionsMissing.add(action);
        }

        for (Action action : actions) {
            int aIndex = actionsMissing.indexOf(action.getName());
            if (aIndex != -1) {
                actionsMissing.remove(aIndex);
            }
        }

        if (actionsMissing.size() > 0) {

            // get parent actions
            UGC parentUgc = null;
            if (ugc.getParentId() != null) {
                parentUgc = this.findById(ugc.getParentId());
            }

            List<Action> actionsToMerge = null;
            if (parentUgc != null) {
                actionsToMerge = parentUgc.getActions();
            } else {
                actionsToMerge = ActionUtil.getDefaultActions();
            }

            for (Action action : actionsToMerge) {
                if (actionsMissing.contains(action.getName())) {
                    actionsToAdd.add(action);
                }
            }
            // sets default actions if any missing
            actions.addAll(actionsToAdd);
        }
        return actions;
    }
    
    private List<Action> resolveUGCActionsEmpty(UGC ugc) {
    	UGC parentUgc = this.findById(ugc.getParentId());
        if (parentUgc != null) {
        	return parentUgc.getActions();
        }   else {
            // otherwise use the defaults
        	return ActionUtil.getDefaultActions();
        }
	}

	private List<UGC> findUGCs(String[] moderationStatus, final String tenant,String target, String profileId, boolean sortChronological) {
    	Profile p = crafterProfileService.getProfile(profileId);
        String[] roles = null;
        if (p.getRoles() != null) {
            roles = p.getRoles().toArray(new String[p.getRoles().size()]);
        }
    	
    	Query q = this.permissionService.getQuery(ActionEnum.READ, p);
    	
    	 List<UGC> grantedList = repository.findUGCs(tenant, target, moderationStatus, roles, sortChronological, q);
    	 return populateUGCListWithProfiles(grantedList);
    }


}
