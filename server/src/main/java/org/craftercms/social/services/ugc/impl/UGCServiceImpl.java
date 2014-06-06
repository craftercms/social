package org.craftercms.social.services.ugc.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.craftercms.commons.collections.IterableUtils;
import org.craftercms.commons.mongo.FileInfo;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.exceptions.IllegalSocialQueryException;
import org.craftercms.social.exceptions.IllegalUgcException;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.exceptions.UGCException;
import org.craftercms.social.repositories.UgcFactory;
import org.craftercms.social.repositories.ugc.UGCRepository;
import org.craftercms.social.services.ugc.UGCService;
import org.craftercms.social.services.ugc.pipeline.UgcPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Override
    public UGC create(final String tenantId, final String ugcParentId, final String targetId,
                      final String textContent, final String subject, final Map attrs) throws SocialException {
        log.debug("Creating Ugc for tenantId {} target Id {} with parent {} subject {} with possible "
            + "attributes {}", tenantId, targetId, ugcParentId, subject,attrs);
        T newUgc = (T)ugcFactory.newInstance(new UGC(subject, textContent, targetId));
        newUgc.setAttributes(attrs);
        try {
            if (ObjectId.isValid(ugcParentId)) {
                setupAncestors(newUgc, ugcParentId, tenantId);
            } else {
                log.debug("Given UGC parent Id is either null or is not a valid value");
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
    public void setAttributes(final String ugcId, final String tenantId, final Map attributes) throws SocialException {
        log.debug("Adding {} to ugc {} of tenantId {}", attributes, ugcId, tenantId);
        try {
            ugcRepository.setAttributes(ugcId, tenantId, attributes);
        } catch (MongoDataException ex) {
            log.debug("Unable to add  attributes " + ugcId + " to ugc " + ugcId + " of tenantId " + tenantId, ex);
            throw new UGCException("Unable to add Attributes to UGC", ex);
        }
    }

    /**
     * Generates the ancestors ordered list for the ugc.
     *
     * @param newUgc
     * @param ugcParentId
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


    @Override
    public void deleteAttribute(final String ugcId, final String[] attributesName,
                                final String tenantId) throws SocialException {
        log.debug("Deleting Attribute {} for ugc Id {}");
        try {

            ugcRepository.deleteAttribute(ugcId, tenantId, attributesName);
        } catch (MongoDataException ex) {
            log.debug("Unable to delete attribute " + attributesName + " for ugc " + ugcId, ex);
            throw new UGCException("Unable to delete attribute for ugc", ex);
        }
    }

    @Override
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
    public T update(final String ugcId, final String parentId, final String targetId, final String textContent,
                    final String subject, final String userId, final String tenantId) throws SocialException {
        log.debug("About to update UGC {}", ugcId);
        try {
            if (!ObjectId.isValid(ugcId)) {
                throw new IllegalArgumentException("Given UGC Id is not valid");
            }

            T toUpdate = (T)ugcRepository.findUGC(tenantId, ugcId);

            if (toUpdate == null) {
                throw new IllegalArgumentException("UGC with Id " + ugcId + " does not exists");
            }

            if (ObjectId.isValid(parentId)) {
                setupAncestors(toUpdate, parentId, tenantId);
            } else {
                throw new IllegalArgumentException("Given parent Id is not valid");
            }

            if (StringUtils.isNotBlank(targetId)) {
                toUpdate.setTargetId(targetId);
            }
            if (StringUtils.isNotBlank(textContent)) {
                toUpdate.setTextContent(textContent);
            }
            if (StringUtils.isNotBlank(subject)) {
                toUpdate.setTextContent(subject);
            }

            pipeline.processUgc(toUpdate);
            ugcRepository.update(ugcId, toUpdate, false, false);
            log.info("UGC {} was updated ", ugcId);
            return toUpdate;
        } catch (MongoDataException ex) {
            log.error("Unable to update UGC", ex);
            throw new UGCException("Unable to update UGC", ex);
        }
    }

    @Override
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
    public Iterable<T> readChildren(final String tenant, final String ugcId, final int limit, final int skip,
                                    final int childCount) throws UGCException {
        log.debug("Finding all UGC {} children for tenant {} starting from {} up to {} results", ugcId, tenant,
            limit, skip);
        try {
            return buildUgcTreeList(IterableUtils.toList(ugcRepository.findChildren(ugcId, tenant, limit, skip,
                childCount)));
        } catch (MongoDataException ex) {
            log.error("Unable to read ", ex);
            throw new UGCException("Unable to ", ex);
        }
    }

    @Override
    public Iterable<T> readByTargetId(final String targetId, final String tenantId) throws UGCException {
        log.debug("Finding all UGC by targetId {} for tenantId {}", targetId, tenantId);
        try {
            return buildUgcTreeList(IterableUtils.toList(ugcRepository.findByTargetId(targetId, tenantId)));
        } catch (MongoDataException ex) {
            log.error("Unable to read ", ex);
            throw new UGCException("Unable to ", ex);
        }
    }

    private T getUgcTree(final String ugcId, final int childCount, final String tenantId) throws MongoDataException {
        List<T> list = ugcRepository.findChildrenOf(ugcId, childCount, tenantId);
        return buildUgcTree(list);
    }


    @Override
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
    public FileInfo addAttachment(final String ugcId, final String tenant, final InputStream attachment,
                                  final String fileName, final String contentType) throws FileExistsException,
        UGCException {
        String internalFileName = File.separator + tenant + File.separator + ugcId + File.separator +
            fileName;
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

    @Override
    public void removeAttachment(final String ugcId, final String tenant, final String attachmentId) throws
        UGCException, FileNotFoundException {
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

    private void isQueryValid(final String query) {
        if (invalidQueryKeys.matcher(query).find()) {
            throw new IllegalSocialQueryException("Given Query '" + query + "' contains invalid selectors");
        }
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


    /**
     * <p>Given a list of results Builds A UGC Tree.</p>
     * <p>The main difference from {@link #buildUgcTree(java.util.List)} is that this method allows
     * for multiple Roots or not roots at all</p>
     *
     * @param ugs Lis of the UGS to build the tree.
     * @return A List Ugcs (Roots) all roots have there children if any.
     */
    protected List<T> buildUgcTreeList(List<T> ugs) {
        if (ugs.isEmpty()) {
            return null;
        }
        ArrayList<T> toReturn = new ArrayList<>();
        LinkedList<T> stack = new LinkedList<>();
        stack.addAll(ugs);
        while (!stack.isEmpty()) {
            T tmp = stack.pop();
            if (!findRelatives(ugs, tmp)) {
                toReturn.add(tmp);
            }
        }
        return toReturn;
    }


    /**
     * Using <i>ugcToTest</i> goes though <i>ugs</i> one by one and checks if the element
     * either it's parent or one of it's children.
     *
     * @param ugs       List of UGC to check against.
     * @param ugcToTest Ugc to check.
     * @return True if a Parent or children is found. False if is a Root (not parents , or is a leaf).
     */
    protected boolean findRelatives(List<T> ugs, T ugcToTest) {
        for (T ug : ugs) {
            if (ugcToTest.isMyParent(ug)) {
                ug.getChildren().add(ugcToTest);
                return true;
            }
            if (ug.isMyChild(ugcToTest)) {
                ugcToTest.getChildren().add(ug);
                return true;
            }
        }
        return false;
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
}
