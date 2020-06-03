/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.services.ugc.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.craftercms.commons.collections.IterableUtils;
import org.craftercms.commons.entitlements.model.EntitlementType;
import org.craftercms.commons.entitlements.validator.EntitlementValidator;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.mongo.FileInfo;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.permissions.annotations.HasPermission;
import org.craftercms.commons.security.permissions.annotations.ProtectedResource;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.social.controllers.rest.v3.comments.exceptions.UGCNotFound;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.social.ModerationStatus;
import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.exceptions.IllegalSocialQueryException;
import org.craftercms.social.exceptions.IllegalUgcException;
import org.craftercms.social.exceptions.NotificationException;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.exceptions.UGCException;
import org.craftercms.social.repositories.UgcFactory;
import org.craftercms.social.repositories.ugc.UGCRepository;
import org.craftercms.social.security.SocialPermission;
import org.craftercms.social.security.SocialSecurityUtils;
import org.craftercms.social.services.notification.NotificationService;
import org.craftercms.social.services.system.TenantConfigurationService;
import org.craftercms.social.services.ugc.UGCService;
import org.craftercms.social.services.ugc.pipeline.UgcPipeline;
import org.craftercms.social.util.LoggerFactory;
import org.craftercms.social.util.ebus.SocialEvent;
import org.craftercms.social.util.ebus.UGCEvent;
import org.craftercms.virusscanner.api.VirusScanner;
import org.craftercms.virusscanner.impl.VirusScannerException;
import reactor.core.Reactor;
import reactor.event.Event;

import static org.craftercms.social.security.SecurityActionNames.UGC_CREATE;
import static org.craftercms.social.security.SecurityActionNames.UGC_DELETE;
import static org.craftercms.social.security.SecurityActionNames.UGC_READ;
import static org.craftercms.social.security.SecurityActionNames.UGC_UPDATE;

/**
 *
 */
@SuppressWarnings("unchecked")
public class UGCServiceImpl<T extends UGC> implements UGCService {


    private I10nLogger log = LoggerFactory.getLogger(UGCServiceImpl.class);

    private UGCRepository ugcRepository;
    private UgcPipeline pipeline;
    private Pattern invalidQueryKeys;
    private UgcFactory ugcFactory;
    private VirusScanner virusScanner;
    private Reactor reactor;
    private NotificationService notificationService;
    private List<String> arraySortFields;
    private TenantConfigurationService tenantConfigurationService;
    private ProfileService profileService;

    protected EntitlementValidator entitlementValidator;

    @Override
    @HasPermission(action = UGC_CREATE, type = SocialPermission.class)
    public UGC create(final String contextId, final String ugcParentId, final String targetId, final String
        textContent, final String subject, final Map attrs, final boolean isAnonymous) throws SocialException {

        try {
            entitlementValidator.validateEntitlement(EntitlementType.ITEM, 1);
        } catch (Exception e) {
            throw  new SocialException("Unable to complete request due to entitlement limits. Please contact your "
                + "system administrator.", e);
        }

        log.debug("logging.ugc.creatingUgc", contextId, targetId, ugcParentId, subject, attrs);
        final UGC template = new UGC(subject, textContent, targetId);

        template.setAnonymousFlag(isAnonymous);
        T newUgc = (T)ugcFactory.newInstance(template);
        newUgc.setAttributes(attrs);
        try {
            if (ObjectId.isValid(ugcParentId)) {
                setupAncestors(newUgc, ugcParentId, contextId);

            } else {
                log.debug("logging.ugc.invalidParentId");
            }
            if (StringUtils.isBlank(contextId)) {
                throw new IllegalArgumentException("context cannot be null");
            }
            pipeline.processUgc(newUgc);
            ugcRepository.save(newUgc);

            final SocialEvent<T> event = new SocialEvent<>(newUgc, SocialSecurityUtils.getCurrentProfile().getId()
                .toString(), UGCEvent.CREATE);
            event.setAttribute("baseUrl",calculateBaseUrl());
            reactor.notify(UGCEvent.CREATE.getName(), Event.wrap(event));
            setupAutoWatch(targetId, SocialSecurityUtils.getCurrentProfile(), contextId);
            log.info("logging.ugc.created", newUgc);
            return newUgc;
        } catch (MongoDataException ex) {
            log.error("logging.ugc.errorSaving", ex);
            throw new UGCException("Unable to Save UGC");
        }
    }

    private String calculateBaseUrl() {
        HttpServletRequest request = RequestContext.getCurrent().getRequest();
        return request.getScheme() +"://"+request.getServerName() + ("http".equals(request
            .getScheme()) &&
            request.getServerPort() == 80 || "https".equals(request.getScheme()) &&
            request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )+request.getContextPath();
    }

    public void setNotificationServiceImpl(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    private void setupAutoWatch(final String targetId, final Profile currentProfile, final String context) throws
        NotificationException {
        final Map<String, Object> profileMap = currentProfile.getAttributes();
        if (profileMap.containsKey("defaultFrequency") && profileMap.containsKey("autoWatch")) {
            boolean autoWatch = currentProfile.getAttribute("autoWatch");
            if (autoWatch) {
                notificationService.subscribeUser(currentProfile, context + "/" + targetId, (String)currentProfile.getAttribute("defaultFrequency"));
            }
        }else if(Boolean.valueOf(tenantConfigurationService.getProperty(context,"setupAutoWatch").toString())){
            try{
                currentProfile.setAttribute("autoWatch",true);
                currentProfile.setAttribute("defaultFrequency",tenantConfigurationService.getProperty(context,
                    "defaultFrequency"));
                HashMap<String,Object> updatedMap = new HashMap<>();
                updatedMap.put("autoWatch",true);
                updatedMap.put("defaultFrequency",tenantConfigurationService.getProperty(context,"defaultFrequency"));
                Profile updatedProfile = profileService.updateAttributes(currentProfile.getId().toString(),updatedMap);
                setupAutoWatch(targetId,updatedProfile,context);
            }catch (ProfileException ex){
                log.error("logging.ugc.autowatch",currentProfile.getId().toString(),ex);
            }
        } else {
            log.debug("Profile doesn't have either defaultFrequency or autoWatch attributes set");
        }
    }

    @Override
    @HasPermission(action = UGC_UPDATE, type = SocialPermission.class)
    public void setAttributes(@ProtectedResource final String ugcId, final String contextId, final Map attributes) throws
        SocialException, UGCNotFound {
        log.debug("logging.ugc.addingAttributes", attributes, ugcId, contextId);
        try {
            T toUpdate = (T)ugcRepository.findUGC(contextId, ugcId);
            if (toUpdate == null) {
                throw new UGCNotFound("Unable to find ugc with id " + ugcId);
            }
            final Map attrs = toUpdate.getAttributes();
            attrs.putAll(attributes);
            ugcRepository.setAttributes(ugcId, contextId, attrs);
            final SocialEvent<T> event = new SocialEvent<T>(ugcId, attributes, SocialSecurityUtils.getCurrentProfile
                ().getId().toString(), UGCEvent.UPDATE_ATTRIBUTES);
            event.setAttribute("baseUrl",calculateBaseUrl());
            reactor.notify(UGCEvent.UPDATE_ATTRIBUTES.getName(), Event.wrap(event));
        } catch (MongoDataException ex) {
            log.debug("logging.ugc.unableToAddAttrs", ex, attributes, ugcId, contextId);
            throw new UGCException("Unable to add Attributes to UGC", ex);
        }
    }

    @Override
    @HasPermission(action = UGC_UPDATE, type = SocialPermission.class)
    public void deleteAttribute(@ProtectedResource final String ugcId, final String[] attributesName, final String
        contextId) throws SocialException {
        log.debug("logging.ugc.deleteAttributes", attributesName, ugcId);
        try {
            ugcRepository.deleteAttribute(ugcId, contextId, attributesName);
            final SocialEvent<T> event = new SocialEvent<T>(ugcId, SocialSecurityUtils.getCurrentProfile().getId()
                .toString(), UGCEvent.DELETE_ATTRIBUTES);
            event.setAttribute("baseUrl",calculateBaseUrl());
            reactor.notify(UGCEvent.DELETE_ATTRIBUTES.getName(), Event.wrap(event));
        } catch (MongoDataException ex) {
            log.debug("logging.ugc.unableToDelAttrs", ex, attributesName, ugcId);
            throw new UGCException("Unable to delete attribute for ugc", ex);
        }
    }

    @Override
    @HasPermission(action = UGC_DELETE, type = SocialPermission.class)
    public boolean deleteUgc(final String ugcId, final String contextId) throws SocialException {
        log.debug("logging.ugc.deleteUgc", ugcId);
        try {
            ugcRepository.deleteUgc(ugcId, contextId);
            final SocialEvent<T> event = new SocialEvent<T>(ugcId, SocialSecurityUtils.getCurrentProfile().getId()
                .toString(), UGCEvent.DELETE);
            event.setAttribute("baseUrl",calculateBaseUrl());

            reactor.notify(UGCEvent.DELETE.getName(), Event.wrap(event));
        } catch (MongoDataException ex) {
            log.error("logging.ugc.deleteUgcError", ex, ugcId, contextId);
            throw new UGCException("Unable to delete UGC", ex);
        }
        return false;
    }

    @Override
    @HasPermission(action = UGC_UPDATE, type = SocialPermission.class)
    public UGC update(@ProtectedResource final String ugcId, final String body, final String subject, final String
        contextId, final Map attributes) throws SocialException, UGCNotFound {
        log.debug("logging.ugc.updateUgc", ugcId);
        try {
            final Profile currentProfile = SocialSecurityUtils.getCurrentProfile();
            boolean moderateByMail = Boolean.parseBoolean(tenantConfigurationService.getProperty(contextId,
                "moderateByMailEnable").toString());

            if (!ObjectId.isValid(ugcId)) {
                throw new IllegalArgumentException("Given UGC Id is not valid");
            }
            T toUpdate = (T)ugcRepository.findUGC(contextId, ugcId);
            if (toUpdate == null) {
                throw new IllegalArgumentException("UGC with Id " + ugcId + " does not exist");
            }
            if (StringUtils.isNotBlank(body)) {
                toUpdate.setBody(body);
            }
            if (StringUtils.isNotBlank(subject)) {
                toUpdate.setBody(subject);
            }
            pipeline.processUgc(toUpdate);
            if(moderateByMail && !SocialSecurityUtils.isProfileModeratorOrAdmin(currentProfile,contextId)){
                if(toUpdate instanceof SocialUgc){
                    ((SocialUgc)toUpdate).setModerationStatus(ModerationStatus.UNMODERATED);
                }
            }
            ugcRepository.update(ugcId, toUpdate, false, false);
            final SocialEvent<T> event = new SocialEvent<>(toUpdate, SocialSecurityUtils.getCurrentProfile().getId()
                .toString(), UGCEvent.UPDATE);
            event.setAttribute("baseUrl",calculateBaseUrl());
            reactor.notify(UGCEvent.UPDATE.getName(), Event.wrap(event));
            if (attributes != null && !attributes.isEmpty()) {
                toUpdate.getAttributes().putAll(attributes);
                //ToDo This should be one query, problem is with deep attributes !!
                setAttributes(toUpdate.getId().toString(), contextId, toUpdate.getAttributes());
                reactor.notify(UGCEvent.UPDATE_ATTRIBUTES, Event.wrap(attributes));
            }
            log.info("logging.ugc.updatedUgc", ugcId);
            return toUpdate;
        } catch (MongoDataException ex) {
            log.error("logging.ugc.unableToUpdateUgc", ex);
            throw new UGCException("Unable to removeWatcher UGC", ex);
        }
    }

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public T read(final String ugcId, final boolean includeChildren, final int childCount, final String contextId)
        throws UGCException {
        try {
            if (includeChildren) {
                return getUgcTree(ugcId, childCount, contextId);
            } else {
                return (T)ugcRepository.findUGC(contextId, ugcId);
            }
        } catch (MongoDataException e) {
            log.error("logging.ugc.unableToRead");
            throw new UGCException("Unable to find ugc by name");
        }
    }

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public Iterable<T> readByTargetId(final String targetId, final String contextId) throws UGCException {
        log.debug("logging.ugc.findingByTarget", targetId, contextId);
        try {
            return buildUgcTreeList(IterableUtils.toList(ugcRepository.findByTargetId(targetId, contextId)), Integer
                .MAX_VALUE);
        } catch (MongoDataException ex) {
            log.error("logging.ugc.unableRead", ex);
            throw new UGCException("Unable to ", ex);
        }
    }

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public Iterable<T> search(final String contextId, final String query, final String sort, final int start, final
    int limit) throws UGCException {
        log.debug("Finding all ugc of context {} with user query {} sorted by {} skipping {} and with a limit of {}",
            contextId, query, sort, start, limit);
        isQueryValid(query);
        try {
            return ugcRepository.findByUserQuery(contextId, query, sort, start, limit);
        } catch (MongoDataException ex) {
            log.error("Unable to find User with given query" + query + " sorted by " + sort, ex);
            throw new UGCException("Unable to find Ugc with user query ", ex);
        }
    }

    @Override
    @HasPermission(action = UGC_UPDATE, type = SocialPermission.class)
    public FileInfo addAttachment(@ProtectedResource final String ugcId, final String contextId, final InputStream
        attachment, final String fileName, final String contentType) throws FileExistsException, UGCException {
        String internalFileName = File.separator + contextId + File.separator + ugcId + File.separator +
            fileName;
        try {
            checkForVirus(attachment);
        } catch (IOException | VirusScannerException ex) {
            log.error("logging.ugc.errorScanVirus", ex);
            return null;
        }

        try {
            UGC ugc = ugcRepository.findUGC(contextId, ugcId);
            if (ugc == null) {
                throw new IllegalUgcException("UGC with given Id does not exist");
            }
            FileInfo info = ugcRepository.saveFile(attachment, internalFileName, contentType);
            try {
                info.setFileName(new URLCodec().decode(fileName));
            } catch (DecoderException e) {
                info.setFileName(fileName);
            }
            info.setAttribute("owner", ugcId);
            ugc.getAttachments().add(info);
            ugcRepository.update(ugcId, ugc);
            reactor.notify(UGCEvent.ADD_ATTACHMENT.getName(), Event.wrap(new SocialEvent<>(ugcId, new InputStream[]
                {new CloseShieldInputStream(attachment)})));
            return info;
        } catch (MongoDataException e) {
            log.error("logging.ugc.unableToSaveAttachment", e, internalFileName);
            throw new UGCException("Unable to save File to UGC");
        }
    }

    private void checkForVirus(final InputStream attachment) throws IOException {
        virusScanner.scan(new CloseShieldInputStream(attachment));
    }

    @Override
    @HasPermission(action = UGC_UPDATE, type = SocialPermission.class)
    public void removeAttachment(@ProtectedResource final String ugcId, final String contextId, final String
        attachmentId) throws UGCException, FileNotFoundException {
        try {
            UGC ugc = ugcRepository.findUGC(contextId, ugcId);
            if (ugc == null) {
                throw new IllegalUgcException("UGC with given Id does not exist");
            }
            if (!ObjectId.isValid(attachmentId)) {
                throw new IllegalArgumentException("Given Attachment id is not valid");
            }
            ObjectId attachmentOid = new ObjectId(attachmentId);
            FileInfo info = ugcRepository.getFileInfo(attachmentOid);
            if (!info.getStoreName().startsWith(File.separator + contextId)) {
                throw new IllegalSocialQueryException("Given Attachment does not belong to the given context");
            }
            ugc.getAttachments().remove(info);
            ugcRepository.deleteFile(attachmentOid);
            ugcRepository.update(ugcId, ugc);
            reactor.notify(UGCEvent.DELETE_ATTACHMENT.getName(), Event.wrap(new SocialEvent<>(ugcId, attachmentId,
                UGCEvent.DELETE_ATTACHMENT)));
        } catch (MongoDataException e) {
            log.error("logging.ugc.attachmentToRemove", e, attachmentId);
            throw new UGCException("Unable to save File to UGC");
        }
    }

    @Override
    @HasPermission(action = UGC_UPDATE, type = SocialPermission.class)
    public FileInfo updateAttachment(@ProtectedResource final String ugcId, final String contextId, final String
        attachmentId, final InputStream newAttachment) throws UGCException, FileNotFoundException {
        if (!ObjectId.isValid(ugcId)) {
            throw new IllegalArgumentException("Given Ugc Id is not valid");
        }
        if (!ObjectId.isValid(attachmentId)) {
            throw new IllegalArgumentException("Given UGC Id is not valid");
        }
        try {
            T ugc = (T)ugcRepository.findUGC(contextId, ugcId);
            if (ugc == null) {
                throw new IllegalUgcException("Given UGC Id does not exist");
            }
            FileInfo oldInfo = ugcRepository.getFileInfo(new ObjectId(attachmentId));
            FileInfo newInfo = ugcRepository.updateFile(new ObjectId(attachmentId), newAttachment, oldInfo
                .getStoreName(), oldInfo.getContentType(), true);
            ugc.getAttachments().add(newInfo);
            ugc.getAttachments().remove(oldInfo);
            ugcRepository.update(ugcId, ugc);
            reactor.notify(UGCEvent.DELETE_ATTACHMENT.getName(), Event.wrap(new SocialEvent<>(ugcId, attachmentId,
                UGCEvent.DELETE_ATTACHMENT)));
            reactor.notify(UGCEvent.ADD_ATTACHMENT.getName(), Event.wrap(new SocialEvent<>(ugcId, new InputStream[]
                {new CloseShieldInputStream(newAttachment)}), UGCEvent.ADD_ATTACHMENT));
            return newInfo;
        } catch (MongoDataException e) {
            log.error("logging.ugc.attachmentError");
            throw new UGCException("Unable to removeWatcher Attachment");
        } catch (FileExistsException e) {
            log.error("logging.ugc.attachmentNotFound", attachmentId);
            throw new UGCException("Unable to find attachment with given id", e);
        }
    }

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public FileInfo readAttachment(final String ugcId, final String contextId, final String attachmentId) throws
        FileNotFoundException, UGCException {
        if (!ObjectId.isValid(ugcId)) {
            throw new IllegalArgumentException("Given Ugc Id is not valid");
        }
        if (!ObjectId.isValid(attachmentId)) {
            throw new IllegalArgumentException("Given attachment Id is not valid");
        }
        try {
            UGC ugc = ugcRepository.findUGC(contextId, ugcId);
            if (ugc == null) {
                throw new IllegalUgcException("UGC with given Id does not exist");
            }
            ObjectId attachmentOid = new ObjectId(attachmentId);
            FileInfo info = ugcRepository.readFile(attachmentOid);
            if (!info.getStoreName().startsWith(File.separator + contextId)) {
                throw new IllegalSocialQueryException("Given Attachment does not belong to the given context");
            }
            return info;
        } catch (MongoDataException ex) {
            log.error("logging.ugc.attachmentNotFound", ex, attachmentId);
            throw new UGCException("Unable to read file", ex);
        }
    }

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public List<T> read(final String targetId, final String contextId, final int start, final int limit, final List
        sortOrder, final int upToLevel, final int childrenPerLevel) throws UGCException {
        try {
            List<T> list = IterableUtils.toList(ugcRepository.findByTargetId(targetId, contextId, start, limit,
                sortOrder,upToLevel));
            list= sortByArrays(list,sortOrder);
            return buildUgcTreeList(list, childrenPerLevel);
        } catch (MongoDataException e) {
            log.error("logging.ugc.unableToRead");
            throw new UGCException("Unable to find ugc by target");
        }
    }

    private List<T> sortByArrays(final List<T> list, final List<DefaultKeyValue<String, Boolean>> sortOrder) {
        if(sortOrder==null || list==null){
            return list;
        }
        for (final DefaultKeyValue<String, Boolean> sort: sortOrder) {
            if(arraySortFields.contains(sort.getKey())){
                Collections.sort(list, new Comparator<T>() {
                    @Override
                    public int compare(final T o1, final T o2) {
                        try {
                            Collection<Object> arrayToCompare1= (Collection<Object>)PropertyUtils.getProperty(o1,sort.getKey());
                            Collection<Object> arrayToCompare2= (Collection<Object>)PropertyUtils.getProperty(o2,sort.getKey());
                            int orderOfSort=sort.getValue() ? 1:-1;//true ASC,false DESC
                            // I'm sorry for this:D
                            if (arrayToCompare1.size() < arrayToCompare2.size()) {
                                return -1*orderOfSort;
                            } else if (arrayToCompare1.size() > arrayToCompare2.size()) {
                                return 1*orderOfSort;
                            } else {
                                return 0;
                            }
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                            ClassCastException e) {
                          log.error("Unable to sort by "+sort.getKey(),e);
                            return 0;
                        }
                    }
                });
            }
        }
        return list;
    }

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public List<T> readChildren(final String ugcId, final String targetId, final String contextId, final int start,
                                final int limit, final List sortOrder, final int upToLevel, final int
        childrenPerLevel) throws UGCException, UGCNotFound {
        log.debug("logging.ugc.readChildren", ugcId, contextId, limit, start);
        try {
            return buildUgcTreeList(IterableUtils.toList(ugcRepository.findChildren(ugcId, targetId, contextId,
                start, limit, sortOrder, upToLevel)), childrenPerLevel);
        } catch (MongoDataException ex) {
            log.error("logging.ugc.unableToRead", ex);
            throw new UGCException("Unable to ", ex);
        }
    }

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public UGC read(final String ugcId, final String contextId) throws UGCException {
        try {
            return ugcRepository.findUGC(contextId, ugcId);
        } catch (MongoDataException e) {
            log.error("logging.ugc.unableToReadP", e, ugcId, contextId);
            throw new UGCException("Unable to find UGC with given ID and context");
        }
    }

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public long count(final String threadId, final String contextId) throws UGCException {
        try {
            return ugcRepository.countByTargetId(contextId, threadId, 0);
        } catch (MongoDataException e) {
            log.error("logging.ugc.unableToCount");
            throw new UGCException("Unable to count UGC by target and context", e);
        }
    }

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public long countChildren(final String ugcId, final String contextId) throws UGCException {
        try {
            return ugcRepository.countChildrenOf(contextId, ugcId);
        } catch (MongoDataException ex) {
            log.error("logging.ugc.unableToCount", ex);
            throw new UGCException("Unable to count children of Ugc");
        }
    }

    private void isQueryValid(final String query) {
        if (invalidQueryKeys.matcher(query).find()) {
            throw new IllegalSocialQueryException("Given Query '" + query + "' contains invalid selectors");
        }
    }

    /**
     * <p>Given a list of results Builds A UGC Tree.</p>
     * <p>The main difference from {@link #buildUgcTree(java.util.List)} is that this method allows
     * for multiple Roots or not roots at all</p>
     *
     * @param ugs              List of the UGS to build the tree.
     * @param childrenPerLevel Levels of Children.
     * @return A List Ugcs (Roots) all roots have there children if any.
     */
    protected List<T> buildUgcTreeList(List<T> ugs, final int childrenPerLevel) {
        if (ugs.isEmpty()) {
            return null;
        }
        ArrayList<T> toReturn = new ArrayList<>();
        LinkedList<T> stack = new LinkedList<>();
        stack.addAll(ugs);
        while (!stack.isEmpty()) {
            T tmp = stack.pop();
            if (!findRelatives(ugs, tmp, childrenPerLevel)) {
                toReturn.add(tmp);
            }
        }
        return toReturn;
    }

    /**
     * Using <i>ugcToTest</i> goes though <i>ugs</i> one by one and checks if the element
     * either it's parent or one of it's children.
     *
     * @param ugs              List of UGC to check against.
     * @param ugcToTest        Ugc to check.
     * @param childrenPerLevel
     * @return True if a Parent or children is found. False if is a Root (not parents , or is a leaf).
     */
    protected boolean findRelatives(List<T> ugs, T ugcToTest, final int childrenPerLevel) {
        for (T ug : ugs) {
            if (ugcToTest.isMyParent(ug)) {
                if (ug.getChildren().size() < childrenPerLevel) {
                    ug.getChildren().add(ugcToTest);
                    return true;
                }
            }
            if (ug.isMyChild(ugcToTest)) {
                if (ugcToTest.getChildren().size() < childrenPerLevel) {
                    ugcToTest.getChildren().add(ug);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Generates the ancestors ordered list for the ugc.
     *
     * @param newUgc      UGC to setup ancestors.
     * @param ugcParentId Parent Id.
     * @throws MongoDataException
     * @throws UGCException
     */
    private void setupAncestors(final UGC newUgc, final String ugcParentId, final String contextId) throws
        MongoDataException, UGCException {
        UGC parent = ugcRepository.findUGC(contextId, ugcParentId);
        if (parent == null) {
            throw new UGCException("Parent UGC does not exist");
        }
        ObjectId parentId = new ObjectId(ugcParentId);
        ArrayDeque<ObjectId> ancestors = parent.getAncestors().clone();

        if (ancestors.isEmpty() || !ancestors.getLast().equals(parentId)) {
            ancestors.addLast(parentId);
            newUgc.setAncestors(ancestors);
        }
    }

    public void setReactor(Reactor reactor) {
        this.reactor = reactor;
    }

    public void setUGCRepositoryImpl(UGCRepository UGCRepositoryImpl) {
        ugcRepository = UGCRepositoryImpl;
    }

    public void setPipeline(UgcPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public void setInvalidQueryKeys(final String invalidQueryKeysPattern) {
        invalidQueryKeys = Pattern.compile(invalidQueryKeysPattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    }

    public void setArraySortFields(final String arraySortFields) {
        this.arraySortFields = Arrays.asList(arraySortFields.split(","));
    }

    public void setSocialUgcFactory(UgcFactory ugcFactory) {
        this.ugcFactory = ugcFactory;
    }

    public void setVirusScanner(final VirusScanner virusScanner) {
        this.virusScanner = virusScanner;
    }

    /**
     * <p>Given a list of results Builds A UGC Tree.</p>
     * <p><b>The first element of the list will be taken as the root of the tree</b></p>
     *
     * @param ugs List of the UGS to build the tree. First Element will be the root.
     * @return A Ugc with its children array filled (and its children...).
     */
    protected T buildUgcTree(List<T> ugs) {
        if (ugs.isEmpty()) {
            return null;
        }
        LinkedList<T> stack = new LinkedList<>();
        stack.addAll(ugs);
        T root = stack.pop();
        root.setChildren(new ArrayDeque());
        while (!stack.isEmpty()) {
            T tmp = stack.pop();
            if (tmp.isMyParent(root)) {
                root.getChildren().add(tmp);
            } else {
                findMyParent(root.getChildren(), tmp);
            }
        }
        return root;
    }

    /**
     * Finds the parent of the orphanChild in the list of possible parents.
     * It will also go recursively to the children's children until there is nothing left.
     * <b>It is possible that it will never find a parent (the orphanChild will be disposed silently</b>
     *
     * @param possibleParents Possible parent of orphanChild
     * @param orphanChild     UGC to find it's parent.
     */
    protected void findMyParent(final Collection<T> possibleParents, final UGC orphanChild) {
        for (T child : possibleParents) {
            if (orphanChild.isMyParent(child)) {
                child.getChildren().add(orphanChild);
                return;
            }
        }
        for (T child : possibleParents) {
            findMyParent(child.getChildren(), orphanChild);
        }
    }

    private T getUgcTree(final String ugcId, final int childCount, final String contextId) throws MongoDataException {
        List<T> list = ugcRepository.findChildrenOf(ugcId, childCount, contextId);
        return buildUgcTree(list);
    }

    public void setTenantConfigurationService(final TenantConfigurationService tenantConfigurationService) {
        this.tenantConfigurationService = tenantConfigurationService;
    }

    public void setProfileService(final ProfileService profileService) {
        this.profileService = profileService;
    }

    public void setEntitlementValidator(final EntitlementValidator entitlementValidator) {
        this.entitlementValidator = entitlementValidator;
    }

}
