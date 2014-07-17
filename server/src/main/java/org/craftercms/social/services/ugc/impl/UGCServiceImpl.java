package org.craftercms.social.services.ugc.impl;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.craftercms.commons.collections.IterableUtils;
import org.craftercms.commons.mongo.FileInfo;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.permissions.annotations.HasPermission;
import org.craftercms.commons.security.permissions.annotations.SecuredObject;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.exceptions.IllegalSocialQueryException;
import org.craftercms.social.exceptions.IllegalUgcException;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.exceptions.UGCException;
import org.craftercms.social.repositories.UgcFactory;
import org.craftercms.social.repositories.ugc.UGCRepository;
import org.craftercms.social.security.SocialPermission;
import org.craftercms.social.services.ugc.UGCService;
import org.craftercms.social.services.ugc.pipeline.UgcPipeline;
import org.craftercms.virusscanner.api.VirusScanner;
import org.craftercms.virusscanner.impl.VirusScannerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

import static org.craftercms.social.security.SecurityActionNames.*;

/**
 *
 */
@SuppressWarnings("unchecked")
public class UGCServiceImpl<T extends UGC> implements UGCService {


    private Logger log = LoggerFactory.getLogger(UGCServiceImpl.class);

    private UGCRepository ugcRepository;
    private UgcPipeline pipeline;
    private Pattern invalidQueryKeys;
    private UgcFactory ugcFactory;
    private VirusScanner virusScanner;

    @Override
    @HasPermission(action = UGC_CREATE, type = SocialPermission.class)
    public UGC create(final String tenantId, final String ugcParentId, final String targetId,
                      final String textContent, final String subject, final Map attrs) throws SocialException {
        log.debug("Creating Ugc for tenantId {} target Id {} with parent {} subject {} with possible " + "attributes " +
            "{}", tenantId, targetId, ugcParentId, subject, attrs);
        T newUgc = (T)ugcFactory.newInstance(new UGC(subject, textContent, targetId));
        newUgc.setAttributes(attrs);
        try {
            if (ObjectId.isValid(ugcParentId)) {
                setupAncestors(newUgc, ugcParentId, tenantId);
            } else {
                log.debug("Given UGC parent Id is either null or is not a valid value");
            }
            if (StringUtils.isBlank(tenantId)) {
                throw new IllegalArgumentException("Tenant cannot be null");
            }
            pipeline.processUgc(newUgc);
            ugcRepository.save(newUgc);
            log.info("UGC {} was created", newUgc);
            return newUgc;
        } catch (MongoDataException ex) {
            log.error("Unable to save UGC ", ex);
            throw new UGCException("Unable to Save UGC");
        }
    }

    @Override
    @HasPermission(action = UGC_UPDATE, type = SocialPermission.class)
    public void setAttributes(@SecuredObject final String ugcId, final String tenantId,
                              final Map attributes) throws SocialException {
        log.debug("Adding {} to ugc {} of tenantId {}", attributes, ugcId, tenantId);
        try {
            ugcRepository.setAttributes(ugcId, tenantId, attributes);
        } catch (MongoDataException ex) {
            log.debug("Unable to add  attributes " + ugcId + " to ugc " + ugcId + " of tenantId " + tenantId, ex);
            throw new UGCException("Unable to add Attributes to UGC", ex);
        }
    }

    @Override
    @HasPermission(action = UGC_UPDATE, type = SocialPermission.class)
    public void deleteAttribute(@SecuredObject final String ugcId, final String[] attributesName,
                                final String tenantId) throws SocialException {
        log.debug("Deleting Attribute {} for ugc Id {} ", attributesName, ugcId);
        try {
            ugcRepository.deleteAttribute(ugcId, tenantId, attributesName);
        } catch (MongoDataException ex) {
            log.debug("Unable to delete attribute " + StringUtils.join(attributesName) + " for ugc " + ugcId, ex);
            throw new UGCException("Unable to delete attribute for ugc", ex);
        }
    }

    @Override
    @HasPermission(action = UGC_DELETE, type = SocialPermission.class)
    public boolean deleteUgc(final String ugcId, final String tenantId) throws SocialException {
        log.debug("About to delete ugc with id {}", ugcId);
        try {
            ugcRepository.deleteUgc(ugcId, tenantId);
        } catch (MongoDataException ex) {
            log.error("Unable to delete UGC with id " + ugcId + " Of tenant " + tenantId, ex);
            throw new UGCException("Unable to delete UGC", ex);
        }
        return false;
    }

    @Override
    @HasPermission(action = UGC_UPDATE, type = SocialPermission.class)
    public UGC update(@SecuredObject final String ugcId, final String body, final String subject,
                      final String tenantId, final Map attributes) throws SocialException {
        log.debug("About to update UGC {}", ugcId);
        try {
            if (!ObjectId.isValid(ugcId)) {
                throw new IllegalArgumentException("Given UGC Id is not valid");
            }

            T toUpdate = (T)ugcRepository.findUGC(tenantId, ugcId);

            if (toUpdate == null) {
                throw new IllegalArgumentException("UGC with Id " + ugcId + " does not exists");
            }

            if (StringUtils.isNotBlank(body)) {
                toUpdate.setBody(body);
            }
            if (StringUtils.isNotBlank(subject)) {
                toUpdate.setBody(subject);
            }

            pipeline.processUgc(toUpdate);
            ugcRepository.update(ugcId, toUpdate, false, false);

            if (attributes != null) {
                //ToDo This should be one query, problem is with deep attributes !!
                setAttributes(toUpdate.getId().toString(), tenantId, attributes);
            }
            log.info("UGC {} was updated ", ugcId);
            return toUpdate;
        } catch (MongoDataException ex) {
            log.error("Unable to update UGC", ex);
            throw new UGCException("Unable to update UGC", ex);
        }
    }

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public T read(final String ugcId, final boolean includeChildren, final int childCount,
                  final String tenantId) throws UGCException {

        try {
            if (includeChildren) {
                return getUgcTree(ugcId, childCount, tenantId);
            } else {
                return (T)ugcRepository.findUGC(tenantId, ugcId);
            }
        } catch (MongoDataException e) {
            log.error("Unable to find UGC by its Id");
            throw new UGCException("Unable to find ugc by name");
        }
    }

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public Iterable<T> readByTargetId(final String targetId, final String tenantId) throws UGCException {
        log.debug("Finding all UGC by targetId {} for tenantId {}", targetId, tenantId);
        try {
            return buildUgcTreeList(IterableUtils.toList(ugcRepository.findByTargetId(targetId, tenantId)),
                Integer.MAX_VALUE);
        } catch (MongoDataException ex) {
            log.error("Unable to read ", ex);
            throw new UGCException("Unable to ", ex);
        }
    }

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public Iterable<T> search(final String tenant, final String query, final String sort, final int start,
                              final int limit) throws UGCException {
        log.debug("Finding all ugc of tenant {} with user query {} sorted by {} skipping {} and with a limit of {}",
            tenant, query, sort, start, limit);
        isQueryValid(query);
        try {
            return ugcRepository.findByUserQuery(tenant, query, sort, start, limit);
        } catch (MongoDataException ex) {
            log.error("Unable to find User with given query" + query + " sorted by " + sort, ex);
            throw new UGCException("Unable to find Ugc with user query ", ex);
        }
    }

    @Override
    @HasPermission(action = UGC_UPDATE, type = SocialPermission.class)
    public FileInfo addAttachment(@SecuredObject final String ugcId, final String tenant,
                                  final InputStream attachment, final String fileName,
                                  final String contentType) throws FileExistsException, UGCException {
        String internalFileName = File.separator + tenant + File.separator + ugcId + File.separator +
            fileName;
        try {
            checkForVirus(attachment);
        } catch (IOException | VirusScannerException ex) {
            log.error("Unable to scan virus not saving attachment", ex);
            return null;
        }

        try {
            UGC ugc = ugcRepository.findUGC(tenant, ugcId);
            if (ugc == null) {
                throw new IllegalUgcException("UGC with given Id does not exist");
            }
            FileInfo info = ugcRepository.saveFile(attachment, internalFileName, contentType);
            ugc.getAttachments().add(info);
            ugcRepository.update(ugcId, ugc);
            return info;
        } catch (MongoDataException e) {
            log.error("Unable to save File " + internalFileName, e);
            throw new UGCException("Unable to save File to UGC");
        }
    }

    private void checkForVirus(final InputStream attachment) throws IOException {
        virusScanner.scan(attachment);
        attachment.reset();
    }

    @Override
    @HasPermission(action = UGC_UPDATE, type = SocialPermission.class)
    public void removeAttachment(@SecuredObject final String ugcId, final String tenant,
                                 final String attachmentId) throws UGCException, FileNotFoundException {
        try {
            UGC ugc = ugcRepository.findUGC(tenant, ugcId);
            if (ugc == null) {
                throw new IllegalUgcException("UGC with given Id does not exist");
            }
            if (!ObjectId.isValid(attachmentId)) {
                throw new IllegalArgumentException("Given Attachment id is not valid");
            }
            ObjectId attachmentOid = new ObjectId(attachmentId);
            FileInfo info = ugcRepository.getFileInfo(attachmentOid);
            if (!info.getFileName().startsWith(File.separator + tenant)) {
                throw new IllegalSocialQueryException("Given Attachment does not belong to the given tenant");
            }
            ugc.getAttachments().remove(info);
            ugcRepository.deleteFile(attachmentOid);
            ugcRepository.update(ugcId, ugc);
        } catch (MongoDataException e) {
            log.error("Unable to remove File " + attachmentId, e);
            throw new UGCException("Unable to save File to UGC");
        }
    }

    @Override
    @HasPermission(action = UGC_UPDATE, type = SocialPermission.class)
    public FileInfo updateAttachment(@SecuredObject final String ugcId, final String tenant,
                                     final String attachmentId, final InputStream newAttachment) throws UGCException,
        FileNotFoundException {
        if (!ObjectId.isValid(ugcId)) {
            throw new IllegalArgumentException("Given Ugc Id is not valid");
        }
        if (!ObjectId.isValid(attachmentId)) {
            throw new IllegalArgumentException("Given UGC Id is not valid");
        }
        try {
            T ugc = (T)ugcRepository.findUGC(tenant, ugcId);
            if (ugc == null) {
                throw new IllegalUgcException("Given UGC Id does not exists");
            }
            FileInfo oldInfo = ugcRepository.getFileInfo(attachmentId);
            ugc.getAttachments().remove(oldInfo);
            FileInfo newInfo = ugcRepository.updateFile(new ObjectId(attachmentId), newAttachment,
                oldInfo.getFileName(), oldInfo.getContentType(), true);
            ugc.getAttachments().add(newInfo);
            ugcRepository.update(ugcId, ugc);
            return newInfo;
        } catch (MongoDataException e) {
            log.error("Unable to update Attachment");
            throw new UGCException("Unable to update Attachment");
        } catch (FileExistsException e) {
            log.error("Unable to find attachment with Id {}", attachmentId);
            throw new UGCException("Unable to find attachment with given id", e);
        }


    }

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public FileInfo readAttachment(final String ugcId, final String tenant, final String attachmentId) throws
        FileNotFoundException, UGCException {
        if (!ObjectId.isValid(ugcId)) {
            throw new IllegalArgumentException("Given Ugc Id is not valid");
        }
        if (!ObjectId.isValid(attachmentId)) {
            throw new IllegalArgumentException("Given attachment Id is not valid");
        }
        try {
            UGC ugc = ugcRepository.findUGC(tenant, ugcId);
            if (ugc == null) {
                throw new IllegalUgcException("UGC with given Id does not exist");
            }
            ObjectId attachmentOid = new ObjectId(attachmentId);
            FileInfo info = ugcRepository.readFile(attachmentOid);
            if (!info.getFileName().startsWith(File.separator + tenant)) {
                throw new IllegalSocialQueryException("Given Attachment does not belong to the given tenant");
            }
            return info;
        } catch (MongoDataException ex) {
            log.error("Unable to read file with id " + attachmentId, ex);
            throw new UGCException("Unable to read file", ex);
        }
    }

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public List<T> read(final String targetId, final String tenantId, final int start, final int limit,
                        final List sortOrder, final int upToLevel, final int childrenPerLevel) throws UGCException {
        try {
            Iterable<T> list = ugcRepository.findByTargetId(targetId, tenantId, start, limit, sortOrder, upToLevel);
            return buildUgcTreeList(IterableUtils.toList(list), childrenPerLevel);
        } catch (MongoDataException e) {
            log.error("Unable to find UGC by its targetId");
            throw new UGCException("Unable to find ugc by target");
        }
    }

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public List<T> readChildren(final String ugcId, final String targetId, final String tenantId, final int start,
                                final int limit, final List sortOrder, final int upToLevel,
                                final int childrenPerLevel) throws UGCException {
        log.debug("Finding all UGC {} children for tenant {} starting from {} up to {} results", ugcId, tenantId,
            limit, start);
        try {
            return buildUgcTreeList(IterableUtils.toList(ugcRepository.findChildren(ugcId, targetId, tenantId, start,
                limit, sortOrder, upToLevel)), childrenPerLevel);
        } catch (MongoDataException ex) {
            log.error("Unable to read ", ex);
            throw new UGCException("Unable to ", ex);
        }
    }

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public UGC read(final String ugcId, final String tenant) throws UGCException {
        try {
            return ugcRepository.findUGC(tenant, ugcId);
        } catch (MongoDataException e) {
            log.error("Unable to find UGC with id " + ugcId + " for tenant " + tenant, e);
            throw new UGCException("Unable to find UGC with given ID and tenant");
        }
    }

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public long count(final String threadId, final String tenant) throws UGCException {
        try {
            return ugcRepository.countByTargetId(tenant, threadId, 0);
        } catch (MongoDataException e) {
            log.error("Unable to count comments by tenant and ugc");
            throw new UGCException("Unable to count UGC by target and tenant", e);
        }
    }

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public long countChildren(final String ugcId, final String tenantId) throws UGCException {
        try {
            return ugcRepository.countChildrenOf(tenantId, ugcId);
        } catch (MongoDataException ex) {
            log.error("Unable to count children of ugc " + ugcId, ex);
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
     * @param ugs              Lis of the UGS to build the tree.
     * @param childrenPerLevel
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
    private void setupAncestors(final UGC newUgc, final String ugcParentId,
                                final String tenantId) throws MongoDataException, UGCException {
        UGC parent = ugcRepository.findUGC(tenantId, ugcParentId);
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

    public void setUGCRepositoryImpl(UGCRepository UGCRepositoryImpl) {
        ugcRepository = UGCRepositoryImpl;
    }

    public void setPipeline(UgcPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public void setInvalidQueryKeys(final String invalidQueryKeysPattern) {
        invalidQueryKeys = Pattern.compile(invalidQueryKeysPattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    }

    public void setSocialUgcFactory(UgcFactory ugcFactory) {
        this.ugcFactory = ugcFactory;
    }

    /**
     * <p>Given a list of results Builds A UGC Tree.</p>
     * <p><b>The first element of the list will be taken as the root of the tree</b></p>
     *
     * @param ugs Lis of the UGS to build the tree. First Element will be the root.
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
     * <b>Is possible that it will never finds a parent (the orphanChild will be dispose silently</b>
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

    private T getUgcTree(final String ugcId, final int childCount, final String tenantId) throws MongoDataException {
        List<T> list = ugcRepository.findChildrenOf(ugcId, childCount, tenantId);
        return buildUgcTree(list);
    }

    public void setVirusScanner(final VirusScanner virusScanner) {
        this.virusScanner = virusScanner;
    }
}
