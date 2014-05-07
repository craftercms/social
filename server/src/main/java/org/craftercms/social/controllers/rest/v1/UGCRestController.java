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
package org.craftercms.social.controllers.rest.v1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.craftercms.security.api.RequestContext;
import org.craftercms.social.controllers.rest.v1.to.PublicUGC;
import org.craftercms.social.controllers.rest.v1.to.UGCRequest;
import org.craftercms.social.domain.AttachmentModel;
import org.craftercms.social.domain.Subscriptions;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.UGC.ModerationStatus;
import org.craftercms.social.exceptions.AttachmentErrorException;
import org.craftercms.social.exceptions.AuditException;
import org.craftercms.social.exceptions.PermissionsException;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.exceptions.TenantException;
import org.craftercms.social.services.UGCService;
import org.craftercms.social.util.HierarchyGenerator;
import org.craftercms.social.util.HierarchyList;
import org.craftercms.social.util.action.ActionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/2/ugc")
public class UGCRestController {
    private final Logger log = LoggerFactory.getLogger(UGCRestController.class);

    @Autowired
    private transient UGCService ugcService;

    private static final List<String> ugcFieldList = Arrays.asList("parentId", "textContent", "attachmentId",
        "anonymousFlag", "targetUrl", "targetDescription", "moderationStatus", "profileId", "tenant", "target",
        "ticket", "attachments", "action_read", "action_create", "action_update", "action_delete", "action_act_on",
        "action_moderate");
    private static final Set<String> ugcFieldSet = new HashSet<>(ugcFieldList);

    @RequestMapping(value = "/moderation/{moderationStatus}/target", method = RequestMethod.GET)
    @ModelAttribute
    @PreAuthorize(value = "can('readUGC')")
    public List<PublicUGC> findByModerationStatusAndTargetId(@PathVariable final String moderationStatus,
                                                             @RequestParam final String tenant,
                                                             @RequestParam final String target,
                                                             @RequestParam(required = false,
        defaultValue = "0") int page, @RequestParam(required = false,
        defaultValue = "0") int pageSize, @RequestParam(required = false,
        defaultValue = "createdDate") String sortField, @RequestParam(required = false,
        defaultValue = "DESC") String sortOrder) throws IOException {
        log.debug("Get Request for UGC with status {}", moderationStatus);
        List<UGC> list = ugcService.findByModerationStatusAndTargetId(ModerationStatus.valueOf(moderationStatus
            .toUpperCase()), tenant, target, page, pageSize, sortField, sortOrder);
        return toPublicUGCList(list);
    }

    @RequestMapping(value = "/moderation/{moderationStatus}", method = RequestMethod.GET)
    @ModelAttribute
    public List<PublicUGC> findByModerationStatus(@PathVariable final String moderationStatus,
                                                  @RequestParam final String tenant, @RequestParam(required = false,
        defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "0") int pageSize,
                                                  @RequestParam(required = false,
        defaultValue = "createdDate") String sortField, @RequestParam(required = false,
        defaultValue = "DESC") String sortOrder) throws IOException {
        log.debug("Get Request for UGC with status {}", moderationStatus);
        List<UGC> list = ugcService.findByModerationStatus(ModerationStatus.valueOf(moderationStatus.toUpperCase()),
            tenant, page, pageSize, sortField, sortOrder);
        return toPublicUGCList(list);
    }

    @RequestMapping(value = "/target", method = RequestMethod.GET)
    @ModelAttribute
    public HierarchyList<PublicUGC> getTargetUgcs(@RequestParam final String tenant,
                                                  @RequestParam final String target, @RequestParam(required = false,
        defaultValue = "99") int rootCount, @RequestParam(required = false, defaultValue = "99") int childCount,
                                                  @RequestParam(required = false, defaultValue = "0") int page,
                                                  @RequestParam(required = false, defaultValue = "0") int pageSize,
                                                  @RequestParam(required = false,
        defaultValue = "createdDate") String sortField, @RequestParam(required = false,
        defaultValue = "DESC") String sortOrder) throws TenantException {


        if (page >= 0 && pageSize > 0) {
            List<UGC> ugcs = ugcService.findByTargetValidUGC(tenant, target, getProfileId(), page, pageSize,
                sortField, sortOrder, new String[] {ModerationStatus.TRASH.toString(), ModerationStatus.SPAM.toString()}
            );

            return HierarchyGenerator.generateHierarchy(toPublicUGCList(ugcs), null, rootCount, childCount);
        } else {
            List<UGC> ugcs = ugcService.findByTargetValidUGC(tenant, target, getProfileId(), sortField, sortOrder,
                new String[] {ModerationStatus.TRASH.toString(), ModerationStatus.SPAM.toString()});

            return HierarchyGenerator.generateHierarchy(toPublicUGCList(ugcs), null, rootCount, childCount);
        }
    }

    @RequestMapping(value = "/target/regex", method = RequestMethod.GET)
    @ModelAttribute
    public HierarchyList<PublicUGC> getUgcByTargetRegex(@RequestParam final String regex,
                                                        @RequestParam(required = false,
        defaultValue = "99") int rootCount, @RequestParam(required = false, defaultValue = "99") int childCount,
                                                        @RequestParam(required = false, defaultValue = "0") int page,
                                                        @RequestParam(required = false,
        defaultValue = "0") int pageSize, @RequestParam(required = false,
        defaultValue = "createdDate") String sortField, @RequestParam(required = false,
        defaultValue = "DESC") String sortOrder) {
        List<UGC> list = ugcService.findByTargetRegex(getTenantName(), regex, getProfileId(), page, pageSize,
            sortField, sortOrder);
        log.debug("Found {} ugs using {} regex", list.size(), regex);
        return HierarchyGenerator.generateHierarchy(toPublicUGCList(list), null, rootCount, childCount);
    }

    @RequestMapping(value = "/moderation/{tenantName}/all", method = RequestMethod.GET)
    @ModelAttribute
    public List<PublicUGC> getUgcsByTenant(@PathVariable final String tenantName, @RequestParam(required = false,
        defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "0") int pageSize,
                                           @RequestParam(required = false, defaultValue = "createdDate") String
                                               sortField, @RequestParam(required = false,
        defaultValue = "DESC") String sortOrder) {
        List<UGC> list;
        if (page >= 0 && pageSize > 0) {
            list = ugcService.findUGCsByTenant(tenantName, page, pageSize, sortField, sortOrder);
        } else {
            list = ugcService.findUGCsByTenant(tenantName, sortField, sortOrder);
        }
        return toPublicUGCList(list);
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ModelAttribute
    public int getCount(@RequestParam final String tenant, @RequestParam final String target) {
        return ugcService.getTenantTargetCount(tenant, target);
    }

    @RequestMapping(value = "/moderation/{moderationStatus}/count", method = RequestMethod.GET)
    @ModelAttribute
    public int getCountByModerationStatus(@PathVariable final String moderationStatus,
                                          @RequestParam final String tenant, @RequestParam(required = false) String
        targetId, @RequestParam(required = false, defaultValue = "false") boolean rootOnly) {
        return ugcService.getModerationStatusCount(moderationStatus, tenant, targetId, rootOnly);
    }

    @RequestMapping(value = "/moderation/{ugcId}/status", method = RequestMethod.POST)
    @ModelAttribute
    public PublicUGC updateModerationStatus(@PathVariable final String ugcId,
                                            @RequestParam final String moderationStatus,
                                            @RequestParam final String tenant) throws IOException, SocialException {
        UGC ugc = ugcService.updateModerationStatus(new ObjectId(ugcId), ModerationStatus.valueOf(moderationStatus
            .toUpperCase()), tenant, getProfileId());
        return toPublicUgc(ugc);
    }

    @RequestMapping(value = "/moderation/update/status", method = RequestMethod.POST)
    @ModelAttribute
    public List<PublicUGC> updateModerationStatus(@RequestParam(required = false) List<String> ids,
                                                  @RequestParam final String moderationStatus,
                                                  @RequestParam final String tenant) throws IOException,
        SocialException, PermissionsException, AuditException {
        List<UGC> list = ugcService.updateModerationStatus(ids, ModerationStatus.valueOf(moderationStatus.toUpperCase
            ()), tenant);
        return toPublicUGCList(list);
    }

    @RequestMapping(value = "/get_ugc/{ugcId}", method = RequestMethod.GET)
    public PublicUGC findByUGCId(@PathVariable String ugcId, @RequestParam final String tenant,
                                 @RequestParam(required = false, defaultValue = "createdDate") String sortField,
                                 @RequestParam(required = false, defaultValue = "DESC") String sortOrder) throws
        IOException, TenantException {
        UGC ugc = ugcService.findUGCAndChildren(new ObjectId(ugcId), tenant, getProfileId(), sortField, sortOrder);
        return toPublicUgc(ugc);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST,
        headers = "Accept=application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ModelAttribute
    public PublicUGC addUGC(@RequestBody(required = true) UGCRequest ugcRequest) throws SocialException,
        AuditException {

        /** Pre validations **/

        if (ugcRequest.getTargetId() == null) {
            throw new IllegalArgumentException("Target must a valid not empty String");
        }
        UGC newUgc;
        if (ugcRequest.getParentId() == null) {
            newUgc = ugcService.newUgc(new UGC(ugcRequest, getProfileId()));
        } else {
            newUgc = ugcService.newChildUgc(new UGC(ugcRequest, getProfileId()));
        }
        return toPublicUgc(newUgc);

    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, headers = "Accept=application/json")
    @ModelAttribute
    public PublicUGC updateUGC(@RequestBody(required = true) UGCRequest ugcRequest) throws SocialException,
        AttachmentErrorException, AuditException {

        UGC updatedUgc = ugcService.updateUgc(new ObjectId(ugcRequest.getUgcId()), ugcRequest.getTenant(),
            ugcRequest.getTargetId(), getProfileId(), ugcRequest.getParentId() == null? null: new ObjectId(ugcRequest
                .getParentId()), ugcRequest.getTextContent(), ugcRequest.getTargetUrl(),
            ugcRequest.getTargetDescription(), ugcRequest.getAttributes(), ugcRequest.getSubject()
        );
        return toPublicUgc(updatedUgc);

    }

    @RequestMapping(value = "/{ugcId}/add_attachments", method = RequestMethod.POST)
    @ModelAttribute
    public PublicUGC addAttachments(@PathVariable final String ugcId, @RequestParam final String tenant,
                                    @RequestParam(required = true) MultipartFile[] attachments) throws SocialException {

        UGC ugc = ugcService.addAttachments(new ObjectId(ugcId), attachments, tenant, getProfileId());
        return toPublicUgc(ugc);
    }

    @RequestMapping(value = "/{ugcId}/add_attachment", method = RequestMethod.POST)
    @ModelAttribute
    public AttachmentModel addAttachments(@PathVariable final String ugcId, @RequestParam final String tenant,
                                          @RequestParam(required = true) MultipartFile attachment) throws
        SocialException {

        return ugcService.addAttachment(new ObjectId(ugcId), attachment, tenant, getProfileId());
    }

    @RequestMapping(value = "/{ugcId}/get_attachments", method = RequestMethod.GET)
    @ModelAttribute
    public List<AttachmentModel> getAttachments(@PathVariable final String ugcId,
                                                @RequestParam(required = true) final String tenant) throws PermissionsException, AttachmentErrorException {

        return ugcService.getAttachments(new ObjectId(ugcId), tenant);
    }

    @RequestMapping(value = "/delete/{ugcId}", method = RequestMethod.POST)
    @ModelAttribute
    public void deleteUGC(@PathVariable final String ugcId, @RequestParam(required = true) final String tenant)
        throws SocialException {

        ugcService.deleteUgc(new ObjectId(ugcId), tenant, getProfileId());
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ModelAttribute
    public void deleteUGC(@RequestParam List<String> ugcIds, @RequestParam(required = true) String tenant) throws
        SocialException {
        ugcService.bulkDeleteUgc(ugcIds, tenant, getProfileId());
    }

    @RequestMapping(value = "/like/{ugcId}", method = RequestMethod.POST)
    @ModelAttribute
    public PublicUGC likeUGC(@PathVariable() String ugcId, @RequestParam(required = true) final String tenant) throws
        SocialException {
        UGC ugc = ugcService.likeUGC(new ObjectId(ugcId), tenant, getProfileId());
        return toPublicUgc(ugc);
    }

    @RequestMapping(value = "/unlike/{ugcId}", method = RequestMethod.POST)
    @ModelAttribute
    public PublicUGC unLikeUGC(@PathVariable() String ugcId) throws SocialException {
        UGC ugc = ugcService.unLikeUGC(new ObjectId(ugcId), getTenantName(), getProfileId());
        return toPublicUgc(ugc);
    }

    @RequestMapping(value = "/flag/{ugcId}", method = RequestMethod.POST)
    @ModelAttribute
    public PublicUGC flagUGC(@PathVariable() String ugcId, @RequestParam final String tenant,
                             @RequestParam final String reason) throws SocialException {
        UGC ugc = ugcService.flagUGC(new ObjectId(ugcId), reason, tenant, getProfileId());
        return toPublicUgc(ugc);
    }

    @RequestMapping(value = "/unflag/{ugcId}", method = RequestMethod.POST)
    @ModelAttribute
    public PublicUGC unflagUGC(@PathVariable() String ugcId, @RequestParam final String reason) throws SocialException {
        UGC ugc = ugcService.unflagUGC(new ObjectId(ugcId), reason, getTenantName(), getProfileId());
        return toPublicUgc(ugc);
    }

    @RequestMapping(value = "/dislike/{ugcId}", method = RequestMethod.POST)
    @ModelAttribute
    public PublicUGC unDislikeUGC(@PathVariable() String ugcId, @RequestParam final String tenant) throws
        SocialException {
        UGC ugc = ugcService.dislikeUGC(new ObjectId(ugcId), tenant, getProfileId());
        return toPublicUgc(ugc);
    }

    @RequestMapping(value = "/undislike/{ugcId}", method = RequestMethod.POST)
    @ModelAttribute
    public PublicUGC dislikeUGC(@PathVariable() String ugcId) throws SocialException {
        UGC ugc = ugcService.unDislikeUGC(new ObjectId(ugcId), getTenantName(), getProfileId());
        return toPublicUgc(ugc);
    }

    @RequestMapping(value = "/{ugcId}/set_attibutes", method = RequestMethod.POST)
    @ModelAttribute
    public void setAttributes(@RequestParam final String tenant, @PathVariable String ugcId,
                              HttpServletRequest request) throws SocialException {
        Map<String, Object> attributeMap = parseAttibutes(request);
        ugcService.setAttributes(new ObjectId(ugcId), attributeMap, tenant, getProfileId());
    }

    private String getProfileId() {
        return RequestContext.getCurrent().getAuthenticationToken().getProfile().getId();
    }

    private boolean userWatchTarget(String ugcTargetId) {
        Object profileAttributes = RequestContext.getCurrent().getAuthenticationToken().getProfile().getAttribute
            (Subscriptions.ATTRIBUTE_TARGETS);
        return profileAttributes != null && profileAttributes instanceof List && ((List)profileAttributes).contains
            (ugcTargetId);
    }

    private List<String> getProfileRoles() {
        List<String> roles = RequestContext.getCurrent().getAuthenticationToken().getProfile().getRoles();
        if (roles == null) {
            roles = new ArrayList<>();
            roles.add(ActionConstants.ANONYMOUS);
        }
        return roles;
    }

    private List<String> getPossibleActionsForUGC(String ugcId) {
        return ugcService.findPossibleActionsForUGC(ugcId, getProfileRoles());
    }

    @ExceptionHandler(PermissionsException.class)
    public String handlePermissionException(PermissionsException ex, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Permission not granted
        return ex.getMessage();
    }

    @ExceptionHandler(AttachmentErrorException.class)
    public String handleDataErrorException(AttachmentErrorException ex, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Permission not granted
        return ex.getMessage();
    }

    private Map<String, Object> parseAttibutes(HttpServletRequest request) {
        Map<String, Object> attributeMap = new HashMap<>();
        Map<String, String[]> paramMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            if (!ugcFieldSet.contains(entry.getKey())) {
                String[] values = entry.getValue();
                if (values.length > 1) {
                    attributeMap.put(entry.getKey(), values);
                } else if (values.length == 1) {
                    attributeMap.put(entry.getKey(), values[0]);
                }
            }
        }
        return (attributeMap.size() > 0)? attributeMap: null;
    }

    /**
     * Returns the tenant of the current Ticket owner.
     *
     * @return Tenant Name, Null if user is anonymous
     */
    public String getTenantName() {
        return RequestContext.getCurrent().getAuthenticationToken().getProfile().getTenantName();
    }

    private List<PublicUGC> toPublicUGCList(final List<UGC> ugs) {
        List<PublicUGC> toReturn = new ArrayList<>();
        for (UGC ugc : ugs) {
            PublicUGC publicUGC = toPublicUgc(ugc);
            toReturn.add(publicUGC);
        }
        return toReturn;
    }

    private PublicUGC toPublicUgc(final UGC ugc) {
        List<String> roles = getProfileRoles();
        String profileId = getProfileId();
        boolean isWatching = userWatchTarget(ugc.getTargetId());
        List<String> posibleActions = getPossibleActionsForUGC(ugc.getId().toString());
        return new PublicUGC(ugc, profileId, posibleActions, isWatching, roles);
    }


}
