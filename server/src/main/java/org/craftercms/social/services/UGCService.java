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
package org.craftercms.social.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;

import org.craftercms.social.domain.AttachmentModel;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.UGC.ModerationStatus;
import org.craftercms.social.domain.UGCAudit.AuditAction;
import org.craftercms.social.domain.Action;
import org.craftercms.social.exceptions.AttachmentErrorException;
import org.craftercms.social.exceptions.PermissionDeniedException;
import org.craftercms.social.util.web.Attachment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.web.multipart.MultipartFile;


public interface UGCService {

	/**
	 * Finds a UGC with the given status
	 * 
	 * @param moderationStatus
	 *            The Status to search for
	 * @return A list of UGC with the given status or a empty list
	 */
	List<UGC> findByModerationStatus(ModerationStatus moderationStatus, String tenant, int page, int pageSize, String sortField, String sortOrder);

	/**
	 * Updates the UGC
	 * 
	 * @param uGCId
	 *            The Id of the ugc to change the {@link ModerationStatus}
	 * @param newStatus
	 *            new {@link ModerationStatus}
	 * @return The Updated UGC
	 */
	UGC updateModerationStatus(ObjectId uGCId, ModerationStatus newStatus, String tenant, String profileId)
            throws PermissionDeniedException;

	/**
	 * Creates a new {@link UGC}
	 * 
	 * @param ugc
	 *            the {@link UGC} to save
	 * @return the saved {@link UGC}
	 */
     UGC newUgc(UGC ugc) throws PermissionDeniedException;

	/**
	 * Creates a new child {@link UGC}
	 * 
	 * @param ugc
	 *            the {@link UGC} to save
	 * @return the saved {@link UGC}
	 * @throws DataIntegrityViolationException
	 *             if the Parent UGC does not exist
	 */

	UGC newChildUgc(UGC ugc) throws PermissionDeniedException;

	/**
	 * Checks if a UGC Exists
	 * 
	 * @param id
	 *            I of the UGC
	 * @return True if exist , False otherwise
	 */
	boolean existsUGC(ObjectId id);

	/**
	 * Gets the Attachment for that UGC
	 * @return a Attachment instance, Null if a IO exception happen reading the File
	 * @throws DataRetrievalFailureException if the Ugc does not exist or if the attachment does not 
	 *         exist either
	 */
	Attachment getAttachment(ObjectId attachmentId);

	UGC likeUGC(ObjectId objectId, String tenant, String profileId);

	UGC dislikeUGC(ObjectId objectId, String tenant, String profileId);
	
	UGC flagUGC(ObjectId objectId, String reason, String tenant, String profileId);

	List<String> getTargets();

	List<UGC> findByModerationStatusAndTargetId(ModerationStatus valueOf, String tenant, String target, int page, int pageSize, String sortField, String sortOrder);

	List<String> findTargetsForModerationStatus(ModerationStatus valueOf, String tenant);

	List<UGC> findByTarget(String tenant, String target, int page, int pageSize, String sortField, String sortOrder);

	List<UGC> findByTargetValidUGC(String tenant, String target, String profileId, int page, int pageSize, String sortField, String sortOrder, String[] excludeWithModerationStatuses);

	List<UGC> findByTargetValidUGC(String tenant, String target, String profileId, String sortField, String sortOrder, String[] excludeWithModerationStatuses);


    long countFindbyAttributes(Map<String, Object> attributes) throws PermissionDeniedException;

    int getTenantTargetCount(String tenant, String target);

	UGC findById(ObjectId ugcId);
	
	void setAttributes(ObjectId ugcId, Map<String, Object> attributeMap, String tenant, String profileId);

	List<UGC> findByProfileAction(String profileId, AuditAction action);

	void streamAttachment(ObjectId attachmentId, OutputStream outputStream) throws Exception;

	UGC findUGCAndChildren(ObjectId ugcId, String tenant, String profileId, String sortField, String sortOrder);

	UGC updateUgc(ObjectId ugcId, String tenant, String targetId, String profileId, ObjectId parentId,
			String textContent, String string, String targetUrl, Map<String, Object> map,String subject
            ) throws PermissionDeniedException, AttachmentErrorException;

    UGC addAttachments(ObjectId ugcId, MultipartFile[] attachments, String tenant, String profileId) throws PermissionDeniedException, AttachmentErrorException;

	void deleteUgc(ObjectId objectId, String tenant, String profileId) throws PermissionDeniedException;
	
	void deleteUgc(List<String> ugcIds, String tenant, String profileId) throws PermissionDeniedException;

	List<UGC> findUGCsByTenant(String tenantName, int page, int pageSize,
			String sortField, String sortOrder);

	List<UGC> findUGCsByTenant(String tenantName, String sortField, String sortOrder);

	List<UGC> updateModerationStatus(List<String> ids, ModerationStatus status,
			String tenant);

    List<String> findPossibleActionsForUGC(String ugcId, List<String> roles);

    List<UGC> findByParentId(ObjectId id);

	List<AttachmentModel> getAttachments(ObjectId objectId, String tenant);

	AttachmentModel addAttachment(ObjectId objectId, MultipartFile attachment,
			String tenant, String profileId) throws PermissionDeniedException, AttachmentErrorException;

	UGC findById(ObjectId ugcId, List<String> attributes);

	int getModerationStatusCount(String moderationStatus, String tenant, String targetId, boolean isOnlyRoot);

    List<UGC> findByTargetRegex(String tenantName, String regex, String profileId, int page, int pageSize, String sortField, String sortOrder);

    /**
     *  Uflags the given UGC
     * @param objectId  Object Id of the Ugc to unflag
     * @param reason Why its been unflag
     * @param tenant Tenant name
     * @param profileId Profile Id of who's doing the action.
     * @return The updated UGC.
     */
    UGC unflagUGC(ObjectId objectId, String reason, String tenant, String profileId);

    UGC unLikeUGC(ObjectId objectId, String tenant, String profileId);

    UGC unDislikeUGC(ObjectId objectId, String tenant, String profileId);

    List<UGC> findbyAttributes(Map<String, Object> attributes) throws PermissionDeniedException;
}
