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

package org.craftercms.social.repositories.ugc.impl;

import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.util.JSONParseException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.craftercms.commons.collections.IterableUtils;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.controllers.rest.v3.comments.exceptions.UGCNotFound;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.social.ModerationStatus;
import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.exceptions.IllegalUgcException;
import org.craftercms.social.repositories.SocialJongoRepository;
import org.craftercms.social.repositories.TreeUGC;
import org.craftercms.social.repositories.ugc.UGCRepository;
import org.craftercms.social.repositories.ugc.support.BaseTreeUgc;
import org.craftercms.social.services.system.TenantConfigurationService;
import org.craftercms.social.services.system.impl.TenantConfigurationServiceImpl;
import org.jongo.Aggregate;
import org.jongo.Find;
import org.jongo.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class UGCRepositoryImpl<T extends UGC> extends SocialJongoRepository implements UGCRepository {

    private Logger log = LoggerFactory.getLogger(UGCRepositoryImpl.class);
    private TenantConfigurationService tenantConfigurationService;

    /**
     * Creates a instance of a Jongo Repository.
     */
    public UGCRepositoryImpl() throws Exception {
    }

    @Override
    public Iterable<UGC> findAll() throws MongoDataException {
        throw new UnsupportedOperationException("Disable for security reasons , use findAllUgc(String,String)");
    }    //TODO cortiz Fix this To many queries, Jongo Aggregation limitation for now wait for release 1.1 or make a
    // patch

    @Override
    public List<T> findChildrenOf(final String ugcId, final int childrenCount,
                                  final String contextId) throws MongoDataException {
        try {
            String pt1 = getQueryFor("social.ugc.getTree1");
            String pt2 = getQueryFor("social.ugc.getTree2");
            String pt3 = getQueryFor("social.ugc.getTree3");
            String pt4 = getQueryFor("social.ugc.getTree4");
            String pt5 = getQueryFor("social.ugc.getTree5");
            String pt6 = getQueryFor("social.ugc.getTree6");
            String pt7 = getQueryFor("social.ugc.getTree7");
            String pt8 = getQueryFor("social.ugc.getTree8");
            String pt9 = getQueryFor("social.ugc.getTree9");
            String pt10 = getQueryFor("social.ugc.getTree10");

            if (!ObjectId.isValid(ugcId)) {
                throw new IllegalArgumentException("Given UGC id is not valid");
            }

            ObjectId id = new ObjectId(ugcId);
            Aggregate aggregation = getCollection().aggregate(pt1);
            aggregation.and(pt2, contextId, Arrays.asList(id), id);
            aggregation.and(pt3).and(pt4).and(pt5).and(pt6).and(pt7).and(pt8);
            aggregation.and(pt9, childrenCount);
            aggregation.and(pt10);

            return toUgcList(IterableUtils.toList(aggregation.as(super.ugcFactory.getTreeClass())));
        } catch (Exception ex) {
            log.error("Unable to ", ex);
            throw new MongoDataException("Unable to find children of given UGC", ex);
        }
    }

    // Always find UGC by id AND contextId
    @Override
    public UGC findById(final String id) throws MongoDataException {
        throw new UnsupportedOperationException("Disable for security reasons , use findUgc(String,String)");
    }

    @Override
    public Iterable<T> findByTargetId(final String targetId, final String contextId) throws MongoDataException {
        String query = getQueryFor("social.ugc.byTargetId");
        return (Iterable<T>)find(query, contextId, targetId);
    }

    @Override
    public void deleteAttribute(final String ugcId, final String contextId, final String[] attributesName) throws
        MongoDataException {
        String query = getQueryFor("social.ugc.byContextAndId");
        String delete = getQueryFor("social.ugc.deleteAttribute");
        try {
            if (!ObjectId.isValid(ugcId)) {
                throw new IllegalArgumentException("Given UGC Id is not valid");
            }
            Map<String, Integer> map = new HashMap<>();
            for (String attributeName : attributesName) {
                map.put("attributes." + attributeName, -1);
            }
            getCollection().update(query, new ObjectId(ugcId), contextId).with(delete, map);
        } catch (MongoException ex) {
            log.error("Unable to delete attribute " + attributesName + " for UGC " + ugcId + "of contextId " +
                contextId, ex);
            throw new MongoDataException("Unable to delete attribute of a ugc", ex);
        }

    }

    @Override
    public T findUGC(final String contextId, final String ugcId) throws MongoDataException {
        String query = getQueryFor("social.ugc.byContextAndId");
        T toReturn=null;
        if (ObjectId.isValid(ugcId)) {
            toReturn=(T)findOne(query, new ObjectId(ugcId), contextId);
        } else {
            throw new IllegalArgumentException("Given UGC " + ugcId + " is invalid");
        }
        return toReturn;
    }

    @Override
    public void setAttributes(final String ugcId, final String contextId,
                              final Map attributes) throws MongoDataException {
        String query = getQueryFor("social.ugc.byContextAndId");
        String update = getQueryFor("social.ugc.addAttributes");
        try {
            if (!ObjectId.isValid(ugcId)) {
                throw new IllegalArgumentException("Given UGC Id is not valid");
            }
            getCollection().update(query, new ObjectId(ugcId), contextId).with(update, attributes);
        } catch (MongoException ex) {
            log.error("Unable to delete attribute " + attributes + " for UGC " + ugcId + "of contextId " + contextId, ex);
            throw new MongoDataException("Unable to delete attribute of a ugc", ex);
        }
    }


    @Override
    public void deleteUgc(final String ugcId, final String contextId) throws MongoDataException {
        String delete = getQueryFor("social.ugc.byIds");
        if (!ObjectId.isValid(ugcId)) {
            throw new IllegalArgumentException("Given UGC Id is not valid");
        }
        //Good Candidate for java8
        Collection<ObjectId> toDelete = CollectionUtils.collect(findChildrenOf(ugcId, Integer.MAX_VALUE, contextId),
            new Transformer<UGC, ObjectId>() {
                @Override
                public ObjectId transform(final UGC input) {
                    return input.getId();
                }
            }
        );
        log.debug("Deleting UGC's {}", toDelete);
        remove(delete, toDelete);
    }

    @Override
    public Iterable<T> findByUserQuery(final String contextId, final String query, final String sort, final int start,
                                       final int limit) throws MongoDataException {
        try {
            StringBuffer realQuery = new StringBuffer(query);
            realQuery.insert(1, "contextId:\"" + contextId + "\", ");
            Find find = getCollection().find(realQuery.toString());
            if (!StringUtils.isBlank(sort)) {
                find.sort(sort);
            }
            return (Iterable<T>)find.skip(start).limit(limit).as(clazz);
        } catch (MongoException | JSONParseException ex) {
            log.error("Unable to Find UGC with given User query " + query + "sorted by " + sort, ex);
            throw new MongoDataException("Unable to find ugcs by user query", ex);
        }
    }


    @Override
    public Iterable<T> findChildren(final String ugcId, final String targetId, final String contextId,
                                    final int start, final int limit, final List sortOrder,
                                    final int upToLevel) throws MongoDataException, UGCNotFound {
        try {
            if (!ObjectId.isValid(ugcId)) {
                throw new IllegalArgumentException("Given UGC id is not valid");
            }
            T parent = findUGC(contextId,ugcId);
            if(parent==null){
                throw new IllegalUgcException("Ugc does not exist for given id and context");
            }else if (parent instanceof SocialUgc && (((SocialUgc)parent).getModerationStatus()== ModerationStatus.TRASH || ((SocialUgc)parent).getModerationStatus()== ModerationStatus.SPAM)){
                throw new UGCNotFound("Parent UGC is on Spam or thrash");
            }
            ArrayDeque<ObjectId> ancestors = parent.getAncestors().clone();
            ancestors.addLast(parent.getId());
            String query = getQueryFor("social.ugc.byContextTargetAncestorsExact");
            String hiddenStatus=tenantConfigurationService.getProperty(contextId, TenantConfigurationService
                .HIDDEN_UGC_STATUS);
            Find find = getCollection().find(query, contextId, targetId, ancestors,ModerationStatus.listOfModerationStatus(hiddenStatus));
            return getUgcsToFind(find, targetId, contextId, start, limit, sortOrder, (ancestors.size()+upToLevel));
        } catch (MongoException ex) {
            log.error("Unable to find children of " + ugcId, ex);
            throw new MongoDataException("Unable to find children of given UGC", ex);
        }
    }

    @Override
    public Iterable<T> findByTargetId(final String targetId, final String contextId, final int start, final int limit,
                                      final List sortOrder, final int upToLevel) throws MongoDataException {
        try {
            String query = getQueryFor("social.ugc.byTargetIdRootLvl");
            String hiddenStatus=tenantConfigurationService.getProperty(contextId, TenantConfigurationService
                .HIDDEN_UGC_STATUS);

            Find find = getCollection().find(query, contextId, targetId
                ,ModerationStatus.listOfModerationStatus(hiddenStatus));

            return getUgcsToFind(find, targetId, contextId, start, limit, sortOrder, upToLevel);
        } catch (MongoException ex) {
            log.error("Unable to Find UGC's " + targetId + "children", ex);
            throw new MongoDataException("Unable to find ugcs by user query", ex);
        }
    }


    private Iterable<T> getUgcsToFind(final Find initialFind, final String targetId, final String contextId,
                                      final int start, final int limit, final List sortOrder,
                                      final int upToLevel) throws MongoDataException {
        if (CollectionUtils.isEmpty(sortOrder)) {
            initialFind.sort(getQueryFor("social.ugc.defaultSort"));
        } else {
            initialFind.sort(createSortQuery(sortOrder));
        }
        initialFind.projection("{_id:1}");
        List<ObjectId> listOfIds = IterableUtils.toList(initialFind.skip(start).limit(limit).map(new ResultHandler<ObjectId>() {
            @Override
            public ObjectId map(final DBObject result) {
                return (ObjectId)result.get("_id");
            }
        }));
        String finalQuery = getQueryFor("social.ugc.byTargetIdRootNLvl");

        finalQuery = finalQuery.replaceAll("%@", String.valueOf(upToLevel));
        String hiddenStatus=tenantConfigurationService.getProperty(contextId, TenantConfigurationService
            .HIDDEN_UGC_STATUS);

        final Find finalMongoQuery = getCollection().find(finalQuery, targetId, contextId, ModerationStatus.listOfModerationStatus(hiddenStatus),listOfIds, listOfIds);
        if (CollectionUtils.isEmpty(sortOrder)) {
            finalMongoQuery.sort(getQueryFor("social.ugc.defaultSort"));
        } else {
            finalMongoQuery.sort(createSortQuery(sortOrder));
        }
        return finalMongoQuery.as(clazz);
    }

    @Override
    public long countByTargetId(final String contextId, final String threadId,
                                final int levels) throws MongoDataException {
        try {
            String hiddenStatus=tenantConfigurationService.getProperty(contextId, TenantConfigurationService
                .HIDDEN_UGC_STATUS);
            return count(getQueryFor("social.ugc.byTargetIdWithFixLvl"), contextId, threadId, levels, ModerationStatus.listOfModerationStatus(hiddenStatus));
        } catch (MongoException ex) {
            log.error("Unable to count ugc for context " + contextId + "and target " + threadId, ex);
            throw new MongoDataException("Unable to count ugc for context and target", ex);
        }
    }

    @Override
    public long countChildrenOf(final String contextId, final String ugcId) throws MongoDataException {
        try {
            if (!ObjectId.isValid(ugcId)) {
                throw new IllegalArgumentException("Given UGC id is not valid");
            }
            T parent = findUGC(contextId, ugcId);
            ArrayDeque<ObjectId> ancestors = parent.getAncestors().clone();
            ancestors.addLast(parent.getId());

            return count(getQueryFor("social.ugc.byContextAncestorsExact"), contextId, ancestors, ancestors.size());
        } catch (MongoException ex) {
            log.error("Unable to count ugc for context " + contextId + "and id " + ugcId, ex);
            throw new MongoDataException("Unable to count ugc for context and target", ex);
        }
    }

    @Override
    public Iterable<T> findByModerationStatus(final ModerationStatus status, final String targetId,
                                              final String contextId, final int start, final int limit,
                                              final List sortOrder)
            throws MongoDataException {
        Find query;
        if (StringUtils.isBlank(targetId)) {
            query = getCollection().find(getQueryFor("social.ugc.byModerationStatus"), status, contextId);
        } else {
            query = getCollection().find(getQueryFor("social.ugc.byModerationStatusAndTargetId"), status, contextId, targetId);
        }

        return query.sort(createSortQuery(sortOrder)).skip(start).limit(limit).as(clazz);
    }

    @Override
    public long countFindByModerationStatus(final ModerationStatus status, final String targetId,
                                            final String contextId) throws MongoDataException {
        if (StringUtils.isBlank(targetId)) {
            return count(getQueryFor("social.ugc.byModerationStatus"), status, contextId);
        } else {
            return count(getQueryFor("social.ugc.byModerationStatusAndTargetId"), status, contextId, targetId);
        }
    }

    @Override
    public Iterable<T> findAllFlagged(final String context, final int start, final int pageSize, final List
        sortOrder) {
        String query = getQueryFor("social.ugc.byFlaggedStatus");
        Find f = getCollection().find(query,context, ModerationStatus.TRASH);
        if (CollectionUtils.isEmpty(sortOrder)) {
            f.sort(getQueryFor("social.ugc.defaultSort"));
        } else {
            f.sort(createSortQuery(sortOrder));
        }
        f.skip(start).limit(pageSize);
        return f.as(clazz);
    }

    @Override
    public long countAllFlagged(final String context, final int start, final int pageSize, final List
        sortOrder) {
        String query = getQueryFor("social.ugc.byFlaggedStatus");
        return  getCollection().count(query, context, ModerationStatus.TRASH);

    }

    protected List<T> toUgcList(final List<BaseTreeUgc> as) {
        ArrayList<T> ugcList = new ArrayList<>(as.size());
        for (TreeUGC a : as) {
            ugcList.add((T)a.getUGC());
        }
        return ugcList;
    }


    public void setTenantConfigurationServiceImpl(TenantConfigurationService tenantConfigurationService) {
        this.tenantConfigurationService=tenantConfigurationService;
    }
}
