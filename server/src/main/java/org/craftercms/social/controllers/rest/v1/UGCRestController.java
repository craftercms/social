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
import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.UGC.ModerationStatus;
import org.craftercms.social.exceptions.PermissionDeniedException;
import org.craftercms.social.services.UGCService;
import org.craftercms.social.util.HierarchyGenerator;
import org.craftercms.social.util.HierarchyList;
import org.craftercms.social.util.action.ActionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	private static final List<String> ugcFieldList = Arrays.asList(new String[] { "parentId", "textContent", "attachmentId","anonymousFlag", "targetUrl","targetDescription",
			"moderationStatus", "profileId", "tenant", "target", "ticket", "attachments", "action_read", "action_create", "action_update",
			"action_delete", "action_act_on", "action_moderate"});
	private static final Set<String> ugcFieldSet = new HashSet<String>(ugcFieldList);
	
	@RequestMapping(value = "/moderation/{moderationStatus}/target", method = RequestMethod.GET)
	@ModelAttribute
	public List<UGC> findByModerationStatusAndTargetId(
			@PathVariable final String moderationStatus,
            @RequestParam final String tenant,
            @RequestParam final String target,
            @RequestParam(required=false,defaultValue="0")int page,
			@RequestParam(required=false,defaultValue="0")int pageSize,
			@RequestParam(required=false,defaultValue="createdDate")String sortField,
			@RequestParam(required=false,defaultValue="DESC") String sortOrder,
			final HttpServletResponse response) throws IOException {
        log.debug("Get Request for UGC with status %s", moderationStatus);
		return ugcService.findByModerationStatusAndTargetId(ModerationStatus
				.valueOf(moderationStatus.toUpperCase()), tenant, target, page, pageSize, sortField, sortOrder);
	}

	@RequestMapping(value = "/moderation/{moderationStatus}", method = RequestMethod.GET)
	@ModelAttribute
	public List<UGC> findByModerationStatus(
			@PathVariable final String moderationStatus,
            @RequestParam final String tenant,
            @RequestParam(required=false,defaultValue="0")int page,
			@RequestParam(required=false,defaultValue="0")int pageSize,
			@RequestParam(required=false,defaultValue="createdDate")String sortField,
			@RequestParam(required=false,defaultValue="DESC") String sortOrder,
			final HttpServletResponse response) throws IOException {
        log.debug("Get Request for UGC with status %s", moderationStatus);
		return ugcService.findByModerationStatus(ModerationStatus
				.valueOf(moderationStatus.toUpperCase()), tenant,page, pageSize, sortField, sortOrder);
	}
	
	@RequestMapping(value = "/target", method = RequestMethod.GET)
	@ModelAttribute
	public HierarchyList<UGC> getTargetUgcs(@RequestParam final String tenant, @RequestParam final String target,
			@RequestParam(required=false,defaultValue="99")int rootCount,
			@RequestParam(required=false,defaultValue="99")int childCount,
			@RequestParam(required=false,defaultValue="0")int page,
			@RequestParam(required=false,defaultValue="0")int pageSize,
			@RequestParam(required=false,defaultValue="createdDate")String sortField,
			@RequestParam(required=false,defaultValue="DESC") String sortOrder){
        if(page>=0 && pageSize>0){
			return HierarchyGenerator.generateHierarchy(ugcService.findByTargetValidUGC(tenant, target, getProfileId(), page,pageSize, sortField, sortOrder),null,rootCount,childCount);
		}else{
			return HierarchyGenerator.generateHierarchy(ugcService.findByTargetValidUGC(tenant, target, getProfileId(), sortField, sortOrder),null,rootCount,childCount);
		}
	}
	
	@RequestMapping(value = "/moderation/{tenantName}/all", method = RequestMethod.GET)
	@ModelAttribute
	public List<UGC> getUgcsByTenant(@PathVariable final String tenantName, 
			@RequestParam(required=false,defaultValue="0")int page,
			@RequestParam(required=false,defaultValue="0")int pageSize,
			@RequestParam(required=false,defaultValue="createdDate")String sortField,
			@RequestParam(required=false,defaultValue="DESC") String sortOrder){
        if(page >= 0 && pageSize > 0){
			return ugcService.findUGCsByTenant(tenantName, page,pageSize, sortField, sortOrder);
		}else{
			return ugcService.findUGCsByTenant(tenantName, sortField, sortOrder);
		}
	}
	
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	@ModelAttribute
	public int getCount(@RequestParam final String tenant,
                        @RequestParam final String target){
		return ugcService.getTenantTargetCount(tenant, target);
	}
	
	@RequestMapping(value = "/moderation/{ugcId}/status", method = RequestMethod.POST)
	@ModelAttribute
	public UGC updateModerationStatus(@PathVariable final String ugcId,
			@RequestParam final String moderationStatus,
            @RequestParam final String tenant,
			final HttpServletResponse response) throws IOException, PermissionDeniedException {
        UGC u = ugcService.updateModerationStatus(new ObjectId(ugcId),
				ModerationStatus.valueOf(moderationStatus.toUpperCase()), tenant, getProfileId());
		return u;
	}
	
	@RequestMapping(value = "/moderation/update/status", method = RequestMethod.POST)
	@ModelAttribute
	public List<UGC> updateModerationStatus(@RequestParam (required = false) List<String> ids,
			@RequestParam final String moderationStatus,
            @RequestParam final String tenant,
			final HttpServletResponse response) throws IOException, PermissionDeniedException {
        return ugcService.updateModerationStatus(ids,
				ModerationStatus.valueOf(moderationStatus.toUpperCase()), tenant);
	}
	
	@RequestMapping(value = "/get_ugc/{ugcId}", method = RequestMethod.GET)
	public UGC findByUGCId(@PathVariable String ugcId,
				@RequestParam final String tenant,
				@RequestParam(required=false,defaultValue="createdDate")String sortField,
				@RequestParam(required=false,defaultValue="DESC") String sortOrder,
				HttpServletResponse response) throws IOException {		
        return ugcService.findUGCAndChildren(new ObjectId(ugcId), tenant, getProfileId(), sortField, sortOrder);
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ModelAttribute
	public UGC addUGC(@RequestParam(required = true) String target,
            @RequestParam(required = true) String tenant,
			@RequestParam(required = false) String parentId,
			@RequestParam(required = false) String targetUrl,
			@RequestParam(required = false) String targetDescription,
			@RequestParam(required = false) String textContent,
			@RequestParam(required = false) MultipartFile[] attachments,
			@RequestParam(required = false) Boolean anonymousFlag,
			HttpServletRequest request) throws PermissionDeniedException{
		if (anonymousFlag == null) {
			anonymousFlag = false;
		}
		/** Pre validations **/
		if (target == null && parentId == null) {
			throw new IllegalArgumentException(
					"Target or Parent Id Must a valid not empty String");
		}
		if (target != null && parentId == null) {
			return ugcService.newUgc(new UGC(textContent, getProfileId(), tenant, target, parseAttibutes(request), targetUrl, targetDescription), attachments,
                            ActionUtil.getActions(request), tenant, getProfileId(), anonymousFlag);
		} else {
			return ugcService
					.newChildUgc(new UGC(textContent, getProfileId(), tenant, target, new ObjectId(parentId), parseAttibutes(request), targetUrl, targetDescription), attachments,
                            ActionUtil.getActions(request), tenant, getProfileId(), anonymousFlag);
		}
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ModelAttribute
	public UGC updateUGC(HttpServletRequest request, 
			@RequestParam(required = true)String ugcId,
            @RequestParam(required = true) String tenant,
            @RequestParam(required = false) String target,
            @RequestParam(required = false) String targetUrl,
			@RequestParam(required = false) String targetDescription,
            @RequestParam(required = false) String parentId,
			@RequestParam(required = false)String textContent,
			@RequestParam(required = false) MultipartFile[] attachments) throws PermissionDeniedException{
        return ugcService.updateUgc(new ObjectId(ugcId), tenant, target, getProfileId(),
                parentId==null?null:new ObjectId(parentId), textContent, attachments,targetUrl, targetDescription);
	}
	
	@RequestMapping(value = "/delete/{ugcId}", method = RequestMethod.POST)
	@ModelAttribute
	public void deleteUGC(HttpServletRequest request, 
			@PathVariable final String ugcId,
            @RequestParam(required = true) final String tenant) throws PermissionDeniedException{
        ugcService.deleteUgc(new ObjectId(ugcId), tenant, getProfileId());
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ModelAttribute
	public void deleteUGC(HttpServletRequest request, 
			@RequestParam List<String> ugcIds,
            @RequestParam(required = true) String tenant) throws PermissionDeniedException{
        ugcService.deleteUgc(ugcIds, tenant, getProfileId());
	}
	
	@RequestMapping(value = "/like/{ugcId}", method = RequestMethod.POST)
	@ModelAttribute
	public UGC likeUGC(@PathVariable() String ugcId,
           @RequestParam(required = true) final String tenant) {
		return ugcService.likeUGC(new ObjectId(ugcId), tenant, getProfileId());
	}

	@RequestMapping(value = "/flag/{ugcId}", method = RequestMethod.POST)
	@ModelAttribute
	public UGC flagUGC(@PathVariable() String ugcId,
                       @RequestParam final String tenant,
                       @RequestParam final String reason) {
		return ugcService.flagUGC(new ObjectId(ugcId), reason, tenant, getProfileId());
	}
	
	@RequestMapping(value = "/dislike/{ugcId}", method = RequestMethod.POST)
	@ModelAttribute
	public UGC dislikeUGC(@PathVariable() String ugcId,
              @RequestParam final String tenant) {
		return ugcService.dislikeUGC(new ObjectId(ugcId), tenant, getProfileId());
	}

	@RequestMapping(value = "/{ugcId}/set_attibutes", method = RequestMethod.POST)
	@ModelAttribute
	public void setAttributes(@RequestParam final String ticket, @RequestParam final String tenant,
                  @PathVariable String ugcId, HttpServletRequest request) {
		Map<String, Object> attributeMap = parseAttibutes(request);
		ugcService.setAttributes(new ObjectId(ugcId), attributeMap, tenant, getProfileId());
	}

	private String getProfileId(){
		return RequestContext.getCurrent().getAuthenticationToken().getProfile().getId();
	}
	
	@ExceptionHandler(PermissionDeniedException.class)
	public String handlePermissionException(PermissionDeniedException ex, HttpServletResponse response) {
		response.setHeader("Content-Type", "application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Permission not granted
        return ex.getMessage();
	}
	
	private Map<String, Object> parseAttibutes(HttpServletRequest request) {
		Map<String, Object> attributeMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = request.getParameterMap();
		for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
			if (!ugcFieldSet.contains(entry.getKey())) {
				String[] values = (String[]) entry.getValue();
				if (values.length > 1) {
					attributeMap.put(entry.getKey(), values);
				} else if (values.length == 1) {
					attributeMap.put(entry.getKey(), values[0]);
				}
			}
		}
		return (attributeMap.size() > 0) ? attributeMap : null;
	}
}
