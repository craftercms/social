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
import java.util.Collections;
import java.util.Date;
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
import org.craftercms.social.domain.Target;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.UGC.ModerationStatus;
import org.craftercms.social.domain.UGCAudit;
import org.craftercms.social.domain.UGCAudit.AuditAction;
import org.craftercms.social.exceptions.AttachmentErrorException;
import org.craftercms.social.exceptions.PermissionDeniedException;
import org.craftercms.social.helpers.MultipartFileClone;
import org.craftercms.social.moderation.ModerationDecision;
import org.craftercms.social.repositories.UGCAuditRepository;
import org.craftercms.social.repositories.UGCRepository;
import org.craftercms.social.services.CounterService;
import org.craftercms.social.services.PermissionService;
import org.craftercms.social.services.SupportDataAccess;
import org.craftercms.social.services.TenantService;
import org.craftercms.social.services.UGCService;
import org.craftercms.social.services.VirusScannerService;
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
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UGCServiceImpl implements UGCService {

    private final transient Logger log = LoggerFactory.getLogger(UGCServiceImpl.class);
    @Autowired
    private transient UGCRepository uGCRepository;
    @Autowired
    private transient UGCAuditRepository uGCAuditRepository;
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
    
    @Autowired
    private CounterService counterService;
    
    @Autowired
	private TenantService tenantService;

	@Autowired
	private VirusScannerService virusScannerService;

    @Override
    public List<UGC> findByModerationStatus(final ModerationStatus moderationStatus, final String tenant, int page, int pageSize, String sortField, String sortOrder) {
        log.debug("Looking for Users with status %s and tenant %s", moderationStatus, tenant);
        return findUGCs(new String[] {
    			 moderationStatus.toString() },tenant,null, page, pageSize, sortField, sortOrder, ActionEnum.MODERATE);
    }

    @Override
    public UGC updateUgc(ObjectId ugcId, String tenant, String targetId, String profileId, ObjectId parentId,
                         String textContent, MultipartFile[] attachments, String targetUrl, String targetDescription) throws PermissionDeniedException, AttachmentErrorException {

        attachments = scanFilesForVirus(attachments);

        UGC ugc = null;
        if (existsUGC(ugcId)) {
            ugc = uGCRepository.findOne(ugcId);
            ugc.setTargetId(targetId);
            ugc.setParentId(parentId);
            ugc.setTextContent(textContent);
            ugc.setAttachmentId(updateUGCAttachments(ugc, attachments));
            ugc.setTargetUrl(targetUrl);
            ugc.setTargetDescription(targetDescription);
            ugc.setLastModifiedDate(new Date());
            this.uGCRepository.save(ugc);
            ugc.setAttachmentsList(getAttachmentsList(ugc.getAttachmentId(), tenant));
            //Audit call
            auditUGC(ugcId, AuditAction.UPDATE, tenant, profileId, null);
        }
        return ugc;
    }

    @Override
    public UGC addAttachments(ObjectId ugcId, MultipartFile[] attachments, String tenant, String profileId) throws PermissionDeniedException, AttachmentErrorException {
    	
    	attachments = scanFilesForVirus(attachments);
        UGC ugc = null;
        if (existsUGC(ugcId)) {
            ugc = uGCRepository.findOne(ugcId);
            ugc.setAttachmentsList(getAttachmentsList(ugc.getAttachmentId(), tenant));
            ugc.setAttachmentId(updateUGCAttachments(ugc, attachments));
            ugc.setLastModifiedDate(new Date());
            this.uGCRepository.save(ugc);
            
            ugc.setAttachmentsList(getAttachmentsList(ugc.getAttachmentId(), tenant));
            //Audit call
            auditUGC(ugcId, AuditAction.UPDATE, tenant, profileId, null);
        }
        return ugc;
    }
    
    @Override
    public AttachmentModel addAttachment(ObjectId ugcId, MultipartFile attachment, String tenant, String profileId) throws PermissionDeniedException, AttachmentErrorException {
    	
    	attachment = scanFileForVirus(attachment);
        UGC ugc = null;
        AttachmentModel attachmentModel = null;
        if (existsUGC(ugcId)) {
            ugc = uGCRepository.findOne(ugcId);
            ugc.setAttachmentsList(getAttachmentsList(ugc.getAttachmentId(), tenant));
            ugc.setAttachmentId(updateUGCAttachment(ugc, attachment));
            ugc.setLastModifiedDate(new Date());
            this.uGCRepository.save(ugc);
            
            ugc.setAttachmentsList(getAttachmentsList(ugc.getAttachmentId(), tenant));
            //Audit call
            auditUGC(ugcId, AuditAction.UPDATE, tenant, profileId, null);
            attachmentModel = ugc.getAttachmentsList().getAttachment(attachment.getOriginalFilename());
        }
        return attachmentModel;
    }

    @Override
    public void deleteUgc(ObjectId ugcId, String tenant, String profileId) throws PermissionDeniedException{
    	UGC parent = uGCRepository.findOne(ugcId);
        if (parent != null) {
            List<UGC> children = uGCRepository.findByParentId(ugcId);
            for (UGC ugcChild: children) {
                deleteUgc(ugcChild.getId(), tenant, profileId);
            }
            
            removeAttachments(ugcId);
            this.uGCRepository.delete(ugcId);
            //Audit call
            auditForDeleteUGC(parent, profileId);
            
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
    private ObjectId[] updateUGCAttachment(UGC ugc, MultipartFile file) {
    	List<ObjectId> listAttachments = new ArrayList<ObjectId>();
    	if (file != null) {
    		ObjectId attachmentId = null;
    		 
    		if (ugc.getAttachmentId()!=null) {
    			Collections.addAll(listAttachments, ugc.getAttachmentId());
    		}
    		attachmentId = getAttachedId(file, ugc);
			if (attachmentId == null) {
				try {
					attachmentId = supportDataAccess.saveFile(file);
					listAttachments.add(attachmentId);
					
				} catch (IOException e) {
					log.error("Unable to save the attachemnt ", e);
	                attachmentId = new ObjectId();
				}
			} 
    		
			return listAttachments.toArray(new ObjectId[listAttachments.size()]);
    	} else {
    		
    		return ugc.getAttachmentId();
    	}
    }

    private ObjectId getAttachedId(MultipartFile file, UGC ugc) {
        return ugc.getAttachmentsList().findObjectId(file);

    }

    @Override
    public UGC updateModerationStatus(ObjectId uGCId, ModerationStatus newStatus, String tenant, String profileId) {
        if (existsUGC(uGCId)) {
            UGC ugc = uGCRepository.findOne(uGCId);
            ugc.setModerationStatus(newStatus);
            ugc.setLastModifiedDate(new Date());
            //Audit call
            auditUGC(uGCId, AuditAction.MODERATE, tenant, profileId, null);
            return populateUGCWithProfile(save(ugc));
        } else {
            log.error("UGC {} does not exist", uGCId);
            throw new DataIntegrityViolationException("Parent UGC does not exist");
        }
    }
    
    @Override
    public List<UGC> updateModerationStatus(List<String> ids, ModerationStatus newStatus, String tenant) {
    	List<UGC> result = new ArrayList<UGC>();
    	if (ids == null) {
    		return result;
    	}
    	ObjectId uGCId;
    	String profileId = RequestContext.getCurrent().getAuthenticationToken().getProfile().getId();
    	for (String id: ids) {
    		uGCId = new ObjectId(id);
	        if (existsUGC(uGCId)) {
	            UGC ugc = uGCRepository.findOne(uGCId); //TODO: performance improvement. ONLY one call to DB
	            ugc.setModerationStatus(newStatus);
	            ugc.setLastModifiedDate(new Date());
	            //Audit call
	            auditUGC(uGCId, AuditAction.MODERATE, tenant, profileId, null);
	            result.add(populateUGCWithProfile(save(ugc)));
	        } else {
	            log.error("UGC {} does not exist", uGCId);
	            //throw new DataIntegrityViolationException("UGC does not exist");
	        }
    	}
        return result;
    }

    @Override
    public UGC newUgc(UGC ugc)
            throws PermissionDeniedException, AttachmentErrorException  {
    	
    	ugc.setAnonymousFlag(ugc.isAnonymousFlag());
    	List<Action> resolveActions = resolveUGCActions(ugc.getActions(), ugc.getParentId());
    	ugc.setModerationStatus(ModerationStatus.UNMODERATED);
    	ugc.setCreatedDate(new Date());
    	ugc.setLastModifiedDate(new Date());
    	checkForModeration(ugc);
        
        ugc.setActions(resolveActions);
        UGC ugcWithProfile = populateUGCWithProfile(save(ugc));
        ugcWithProfile.setAttachmentsList(getAttachmentsList(ugcWithProfile.getAttachmentId(), ugcWithProfile.getTenant()));
        //Audit call
        auditUGC(ugcWithProfile.getId(), AuditAction.CREATE, ugc.getTenant(), ugc.getProfileId(), null);
        return ugcWithProfile;
    }



    @Override
    public UGC newChildUgc(UGC ugc) throws PermissionDeniedException, AttachmentErrorException {

        if (!existsUGC(ugc.getParentId())) {
            log.error("Parent for {} does not exist", ugc);
            throw new DataIntegrityViolationException("Parent UGC does not exist");
        }

        // resolve and set the actions
        ugc.setActions(resolveUGCActions(ugc.getActions(), ugc.getParentId()));
        ugc.setModerationStatus(ModerationStatus.UNMODERATED);
        ugc.setCreatedDate(new Date());
        ugc.setLastModifiedDate(new Date());
        checkForModeration(ugc);

        UGC ugcWithProfile = populateUGCWithProfile(save(ugc));
        ugcWithProfile.setAttachmentsList(getAttachmentsList(ugcWithProfile.getAttachmentId(), ugcWithProfile.getTenant()));
        //Audit call
        auditUGC(ugcWithProfile.getId(), AuditAction.CREATE, ugc.getTenant(), ugc.getProfileId(), null);
        return ugcWithProfile;
    }

    @Override
    public boolean existsUGC(ObjectId id) {
        return uGCRepository.findOne(id) != null;
    }

    /**
     * Does the actual Saving of the UGC
     *
     * @param ugc
     * @return
     */
    private UGC save(UGC ugc) {
        log.debug("Saving {}", ugc.toString());
        return uGCRepository.save(ugc);
    }

    private void checkForModeration(UGC ugc) {
    	if (moderationDecisionManager.isTrash(ugc) && ugc.getModerationStatus() != ModerationStatus.APPROVED) {
            ugc.setModerationStatus(ModerationStatus.TRASH);
        } else if (moderationDecisionManager.needModeration(ugc) && ugc.getModerationStatus() != ModerationStatus.APPROVED) {
            ugc.setModerationStatus(ModerationStatus.PENDING);
        }
    }

    @Override
    public UGC likeUGC(ObjectId ugcId, String tenant, String profileId) {
        if (existsUGC(ugcId)) {
            UGC ugc = uGCRepository.findOne(ugcId);
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
        UGCAudit r = uGCAuditRepository.findByProfileIdAndUgcIdAndAction(profileId, ugc.getId(), like);
        return (r == null);
    }

    @Override
    public UGC dislikeUGC(ObjectId ugcId, String tenant, String profileId) {
    	if (existsUGC(ugcId)) {
            UGC ugc = uGCRepository.findOne(ugcId);
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
    public List<UGC> findByModerationStatusAndTargetId(ModerationStatus moderationStatus, String tenant, String target, int page, int pageSize, String sortField, String sortOrder) {
    	return findUGCs(new String[] {
    			 moderationStatus.toString() },tenant,target, page, pageSize, sortField, sortOrder, ActionEnum.MODERATE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> findTargetsForModerationStatus(ModerationStatus moderationStatus, String tenant) {
        String query = String.format("{moderationStatus:'%s', tenant:'%s' }", moderationStatus.toString(), tenant);
        return (List<String>) supportDataAccess.mapReduceWithQuery(UGC.class, query, "classpath:/mongo/getTargetsMap.js",
                "classpath:/mongo/getTargetsReduce.js", parser);
    }

    @Override
    public List<UGC> findByTarget(String tenant, String target, int page, int pageSize, String sortField, String sortOrder) {
    	return findUGCs(null,tenant,target, page, pageSize, sortField, sortOrder, ActionEnum.READ);
    }

    @Override
    public List<UGC> findByTargetValidUGC(String tenant, String target, String profileId, String sortField, String sortOrder) {
    	return findUGCs(getModerationFilter(tenant, profileId),tenant,target,-1, -1, sortField, sortOrder, ActionEnum.READ);
    }

    
    private String[] getModerationFilter(String tenantName, String profileId) {
    	Profile p = crafterProfileService.getProfile(profileId);
    	List<String> moderatorRoles = tenantService.getRootModeratorRoles(tenantName);
    	ArrayList<Action> actions = new ArrayList<Action>();
    	Action moderatorAction = new Action(ActionEnum.MODERATE.toString(),moderatorRoles);
		actions.add(moderatorAction);
		UGC newUgc = new UGC();
		newUgc.setActions(actions);
		if (permissionService.allowed(ActionEnum.MODERATE, newUgc, p)) {
			return new String[] {
					ModerationStatus.APPROVED.toString(), ModerationStatus.UNMODERATED.toString(),
					ModerationStatus.PENDING.toString(), ModerationStatus.TRASH.toString()};
		} else {
			return new String[] {
					ModerationStatus.APPROVED.toString(), ModerationStatus.UNMODERATED.toString(), ModerationStatus.PENDING.toString() };
		}
    	
	}

	@Override
    public int getTenantTargetCount(String tenant, String target) {
    	return uGCRepository.findTenantAndTargetIdAndParentIsNull(tenant, target, ActionEnum.READ).size();
    	
    }

    @Override
    public List<UGC> findByTargetValidUGC(String tenant, String target, String profileId, int page, int pageSize, String sortField, String sortOrder) {
    	List<UGC> list = uGCRepository.findByTenantTargetPaging(tenant,target,page,pageSize, ActionEnum.READ, sortField, sortOrder);
        return populateUGCListWithProfiles(list);
    }

    @Override
    public UGC flagUGC(ObjectId ugcId, String reason, String tenant, String profileId) {
        UGC ugc = uGCRepository.findOne(ugcId);
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
        UGC ugc = this.uGCRepository.findOne(ugcId);
    	Target target = null;
    	if (ugc != null) {
    		target = new Target(ugc.getTargetId(),ugc.getTargetDescription(), ugc.getTargetUrl());
    	} else {
    		return;
    	}
    	UGCAudit audit = new UGCAudit(ugcId, tenant, profileId, auditAction, reason, target);
    	audit.setRow(counterService.getNextSequence("uGCAudit"));
        uGCAuditRepository.save(audit);
    }
    
    private void auditForDeleteUGC(UGC ugc, String profileId) {
    	Target target = null;
    	if (ugc != null) {
    		target = new Target(ugc.getTargetId(),ugc.getTargetDescription(), ugc.getTargetUrl());
    	} else {
    		return;
    	}
    	UGCAudit audit = new UGCAudit(ugc.getId(), ugc.getTenant(), profileId, AuditAction.DELETE, null, target);
    	audit.setRow(counterService.getNextSequence("uGCAudit"));
    	uGCAuditRepository.save(audit);
    }
    
    private void removeAuditUGC(ObjectId ugcId, AuditAction auditAction, String tenant, String profileId, String reason) {
        UGCAudit audit = uGCAuditRepository.findByProfileIdAndUgcIdAndAction(profileId, ugcId, auditAction);
    	uGCAuditRepository.delete(audit);
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
                ugc.setLastModifiedDate(new Date());
                uGCRepository.save(ugc);
                //Audit call
                auditUGC(ugcId, AuditAction.UPDATE, tenant, profileId, null);
            }
        }
    }

    @Override
    public List<UGC> findByProfileAction(String profileId, AuditAction action) {
        List<UGCAudit> lst = uGCAuditRepository.findByProfileIdAndAction(profileId, action);
        List<ObjectId> ugcs=new ArrayList<ObjectId>();
        for (UGCAudit audit : lst) {
        	ugcs.add(audit.getUgcId());
        }
        return uGCRepository.findByIds((ObjectId[])ugcs.toArray());
    }

    @Override
    public UGC findById(ObjectId ugcId) {
        return findById(ugcId, null);
    }
    
    @Override
    public UGC findById(ObjectId ugcId, List<String> profileAttributes) {
    	UGC ugc = uGCRepository.findOne(ugcId);
    	if (ugc == null) {
    		return null;
    	}
    	ugc.setAttachmentsList(getAttachmentsList(ugc.getAttachmentId(), ugc.getTenant()));
    	return populateUGCWithProfile(ugc, profileAttributes);
    }
    
        @Override
    public UGC findUGCAndChildren(ObjectId ugcId, String tenant, String profileId, String sortField, String sortOrder) {
    	Profile p = crafterProfileService.getProfile(profileId);
    	String[] moderationStatus = getModerationFilter(tenant, profileId);
    	UGC ugc = uGCRepository.findUGC(ugcId, ActionEnum.READ, moderationStatus);
    	if (ugc == null) {
    		return null;
    	}
        ugc.setAttachmentsList(getAttachmentsList(ugc.getAttachmentId(), ugc.getTenant()));
        ugc = populateUGCWithProfile(ugc);
        
        return initUGCAndChildren(ugc, p, moderationStatus, sortField, sortOrder);

    }

    public UGC initUGCAndChildren(UGC ugc, Profile p, String[] moderationStatus, String sortField, String sortOrder) {
        ugc.setAttachmentsList(getAttachmentsList(ugc.getAttachmentId(), ugc.getTenant()));
        UGC populatedUgc = populateUGCWithProfile(ugc);
        List<UGC> children = uGCRepository.findByParentIdWithReadPermission(populatedUgc.getId(), ActionEnum.READ, moderationStatus, sortField, sortOrder);
        for (UGC ugcChild: children) {
            ugcChild = initUGCAndChildren(ugcChild, p, moderationStatus, sortField, sortOrder);
        }
        populatedUgc.getChildren().addAll(children);
        return populatedUgc;

    }
    
    @Override
    public List<UGC> findByParentId(ObjectId id) {
    	return uGCRepository.findByParentId(id);
    }
    
    @Override
	public List<UGC> findUGCsByTenant(String tenantName, int page,
			int pageSize, String sortField, String sortOrder) {
    	List<UGC> ugcs = null;
    	if (page!=-1 && pageSize!=-1) {
    		ugcs = uGCRepository.findByTenantTargetPaging(tenantName, null, page, pageSize, ActionEnum.READ, sortField, sortOrder);
    	} else {
    		 ugcs = uGCRepository.findByTenantAndSort(tenantName, ActionEnum.READ, sortField, sortOrder);
    	}
    	return populateUGCListWithProfiles(ugcs);
    	
	}

	@Override
	public List<UGC> findUGCsByTenant(String tenantName,
			String sortField, String sortOrder) {
		return findUGCsByTenant(tenantName, -1, -1, sortField, sortOrder); 
	}

	/**
	 *  Clone files so they could be used for virus scanning and further for storing by the UGC service
	 * @param attachments
	 * @return cloned files MultipartFile[] that can be used multiple times
	 * @throws AttachmentErrorException
	 */
	protected MultipartFileClone[] cloneMultipartFiles(MultipartFile[] attachments) throws AttachmentErrorException {
		if (attachments == null) {
			return null;
		}
		MultipartFileClone[] multipartFileClone = new MultipartFileClone[attachments.length];
		for (int i = 0; i < multipartFileClone.length; i++) {
			multipartFileClone[i] =cloneMultipartFile(attachments[i]);
		}
		return multipartFileClone;
	}
	
	/**
	 *  Clone files so they could be used for virus scanning and further for storing by the UGC service
	 * @param attachments
	 * @return cloned files MultipartFile[] that can be used multiple times
	 * @throws AttachmentErrorException
	 */
	protected MultipartFileClone cloneMultipartFile(MultipartFile attachment) throws AttachmentErrorException {
		MultipartFileClone multipartFileClone = null;
		try {
			multipartFileClone = new MultipartFileClone(attachment);
		} catch (IOException e) {
			throw new AttachmentErrorException(e);
		}
		
		return multipartFileClone;
	}

	/**
	 * If the virus scanner service is implemented (e.g it is not the default crafter null scanner service) this
     * method scans the multipartfile attachments looking for viruses. A clone of the multipart files is used
     * for the scanning so they can be read later if needed).
	 * @param attachments
	 * @return  MultipartFile[] (the clone if the scanning is performed or the original if it is not)
	 * @throws AttachmentErrorException if a threat is found or scan fails
	 */
	protected MultipartFile[] scanFilesForVirus(MultipartFile[] attachments) throws AttachmentErrorException {
		if (attachments == null) {
			return null;
		}
		if (virusScannerService.isNullScanner()) {
            log.debug("Virus scanning is disabled");
			return attachments;
		}

        log.debug("Scanning the attachments");

        String errorMessage = "";

		MultipartFileClone[] multipartFileClones = cloneMultipartFiles(attachments);

        for(MultipartFileClone multipartFileClone : multipartFileClones){
            errorMessage = virusScannerService.scan(multipartFileClone.getTempFile(),multipartFileClone.getOriginalFilename());
            if(errorMessage != null){
                log.error(errorMessage);
                throw new AttachmentErrorException(errorMessage);
            }
        }

        log.debug("Successful scanning: The attachments are clean");

		return multipartFileClones;
	}
	
	/**
	 * If the virus scanner service is implemented (e.g it is not the default crafter null scanner service) this
     * method scans the multipartfile attachment looking for viruses. A clone of the multipart file is used
     * for the scanning so they can be read later if needed).
	 * @param attachments
	 * @return  MultipartFile (the clone if the scanning is performed or the original if it is not)
	 * @throws AttachmentErrorException if a threat is found or scan fails
	 */
	protected MultipartFile scanFileForVirus(MultipartFile attachment) throws AttachmentErrorException {
		if (virusScannerService.isNullScanner()) {
            log.debug("Virus scanning is disabled");
			return attachment;
		}

        log.debug("Scanning one attachment");

        String errorMessage = "";

		MultipartFileClone multipartFileClone = cloneMultipartFile(attachment);

    
        errorMessage = virusScannerService.scan(multipartFileClone.getTempFile(),multipartFileClone.getOriginalFilename());
        if(errorMessage != null){
            log.error(errorMessage);
            throw new AttachmentErrorException(errorMessage);
        }
        

        log.debug("Successful scanning: The attachments are clean");

		return multipartFileClone;
	}

    private List<UGC> populateUGCListWithProfiles(List<UGC> ugcList) {
        Map<String, List<UGC>> profileIdUGCMap = new HashMap<String, List<UGC>>();
        List<UGC> anonymousUgc = new ArrayList<UGC>();

        if (ugcList != null && ugcList.size() > 0) {

            for (UGC ugc : ugcList) {
                // TODO: get the profile info from a cache if already cached and set on the UGC without adding to the map
                List<UGC> subUGCList = profileIdUGCMap.get(ugc.getProfileId());
                if (subUGCList == null) {
                    subUGCList = new ArrayList<UGC>();
                    if (isProfileSetable(ugc)) {
                    	profileIdUGCMap.put(ugc.getProfileId(), subUGCList);
                    } 
                }
                if (isProfileSetable(ugc)) {
                	subUGCList.add(ugc);
                } else {
                	anonymousUgc.add(ugc);
                }
                if (ugc.getAttachmentId()!=null) {
                    ugc.setAttachmentsList(getAttachmentsList(ugc.getAttachmentId(), ugc.getTenant()));
                }
            }
            List<String> profileIds = Arrays.asList(profileIdUGCMap.keySet().toArray(
                    new String[profileIdUGCMap.size()]));
            List<Profile> profileList = crafterProfileService.getProfilesByIds(profileIds);
            
            if (profileIds.size() > profileList.size()) {
            	fillProfilesWithEmptyProfiles(profileList, profileIds);
            }

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
            fillUgcWithAnonymousUser(anonymousUgc);
        }



        return ugcList;
    }

    private UGC populateUGCWithProfile(UGC ugc, List<String> attributes) {
    	if (isProfileSetable(ugc)) {
    		ugc.setProfile(crafterProfileService.getProfile(ugc.getProfileId()));
    	} else {
    		Profile anonymousProfile = new Profile(null, "anonymous", "", true, new Date(), new Date(), null,null,null, null, true);
    		ugc.setProfile(anonymousProfile);
    		ugc.setProfileId(null);
    	}
        return ugc;
    }
    private UGC populateUGCWithProfile(UGC ugc) {
    	return populateUGCWithProfile(ugc, null);
    }

    private AttachmentsList getAttachmentsList(ObjectId[] attachmentsId, String tenant) {
    	AttachmentsList data = new AttachmentsList();
        Attachment attachment;
        AttachmentModel a;
        if (attachmentsId != null) {
	        for (ObjectId id: attachmentsId) {
	            attachment = supportDataAccess.getAttachment(id);
	            a = new AttachmentModel(attachment.getFilename(),id,attachment.getContentType(), tenant);
	            data.addAttachmentModel(a);
	        }
        }
        return data;
    }


    /**
     * Resolve the full set of actions for the new UGC
     * @param actions from request
     * @param parentId of ugc
     * @return
     */
    private List<Action> resolveUGCActions(List<Action> actions, ObjectId parentId) {
    	if (actions == null || actions.size() ==0) {
            // if there is a parent, get the parent's actions & set in there
        	return resolveUGCActionsEmpty(parentId);
            
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
            if (parentId != null) {
                parentUgc = this.findById(parentId);
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
    
    private List<Action> resolveUGCActionsEmpty(ObjectId ugcParentId) {
        if (ugcParentId != null) {
            UGC parentUgc = this.findById(ugcParentId);
            if (parentUgc != null) {
                return parentUgc.getActions();
            }
        }
    	// otherwise use the defaults
        return ActionUtil.getDefaultActions();
	}

	private List<UGC> findUGCs(String[] moderationStatus, final String tenant,String target, int page, int pageSize,
			String sortField, String sortOrder, ActionEnum action) {
		List<UGC> grantedList = uGCRepository.findUGCs(tenant, target, moderationStatus, action, page, pageSize, sortField, sortOrder);
    	 return populateUGCListWithProfiles(grantedList);
    }
	
	private void fillProfilesWithEmptyProfiles(List<Profile> profileList,
			List<String> profileIds) {
		Map<String, Profile> dataProfile = new HashMap<String,Profile>();
		Profile empty;
		for (Profile p:profileList) {
			dataProfile.put(p.getId(), p);
		}
		for (String id: profileIds) {
			if (dataProfile.get(id) == null) {
				empty = new Profile();
				empty.setId(id);
				profileList.add(empty);
			}
		}
		
	}
	
	private void fillUgcWithAnonymousUser(List<UGC> anonymousUsers) {
		Profile anonymousProfile = new Profile(null, "anonymous", "", true, new Date(), new Date(), null,null,null, null, true);
		for (UGC currentUGC: anonymousUsers) {
			currentUGC.setProfile(anonymousProfile);
			currentUGC.setProfileId(null);
		}
	}

    private boolean isProfileSetable(UGC ugc) {
    	boolean isSeteable = true;
    	if (ugc.isAnonymousFlag()) {
    		if (this.permissionService.excludeProfileInfo(ugc, ActionEnum.MODERATE,
    				RequestContext.getCurrent().getAuthenticationToken().getProfile().getRoles())) {
    			isSeteable = false;
    		}
    		
    	}
    	return isSeteable;
    }
    
    /**
     * Removes UGC attachments before removing the UGC
     * 
     * @param id unique identifier to the UGC that will be removed and their attachments will be removed.
     */
    private void removeAttachments(ObjectId id) {
		UGC ugc = uGCRepository.findOne(id); 
		if (ugc.getAttachmentId() != null && ugc.getAttachmentId().length > 0) {
			List<ObjectId> attachments = Arrays.asList(ugc.getAttachmentId());
			for (ObjectId attachmentId: attachments) {
				this.supportDataAccess.removeAttachment(attachmentId);
			}
		}
	}
    
	@Override
	public List<AttachmentModel> getAttachments(ObjectId objectId, String tenant) {
		List<AttachmentModel> result = null;
		UGC ugc = uGCRepository.findOne(objectId);
		if (ugc!= null) {
			 ugc.setAttachmentsList(getAttachmentsList(ugc.getAttachmentId(), tenant));
			 result = ugc.getAttachmentsList().flattenAttachments();
		}
		return result;
	}


}
