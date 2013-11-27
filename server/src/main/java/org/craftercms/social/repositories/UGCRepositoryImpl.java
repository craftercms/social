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
package org.craftercms.social.repositories;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.craftercms.security.api.RequestContext;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.services.PermissionService;
import org.craftercms.social.util.UGCConstants;
import org.craftercms.social.util.action.ActionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;

public class UGCRepositoryImpl implements UGCRepositoryCustom {

    private static final String TENANT = "tenant";
    private static final String TARGET_ID = "targetId";
    private static final String PARENT_ID = "parentId";
    private static final String MODERATION_STATUS = "moderationStatus";
    private Logger log = LoggerFactory.getLogger(UGCRepository.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String ID = "_id";

    @Autowired
    private PermissionService permissionService;

    @Override
    public List<UGC> findTenantAndTargetIdAndParentIsNull(String tenant, String target, ActionEnum action) {
        Query query = this.permissionService.getQuery(action, RequestContext.getCurrent().getAuthenticationToken()
            .getProfile());
        query.addCriteria(Criteria.where(TENANT).is(tenant));
        query.addCriteria(Criteria.where(TARGET_ID).is(target));
        query.addCriteria(Criteria.where(PARENT_ID).is(null));


        return mongoTemplate.find(query, UGC.class);
    }


    @Override
    public List<UGC> findByModerationStatusAndTenantAndTargetId(String[] moderationStatus, String tenant,
                                                                String targetId, boolean isOnlyRoot) {
        Query query = new Query();
        if (tenant != null) {
            query.addCriteria(Criteria.where(TENANT).is(tenant));
        }
        if (targetId != null) {
            query.addCriteria(Criteria.where(TARGET_ID).is(targetId));
        }
        if (isOnlyRoot) {
            query.addCriteria(Criteria.where(PARENT_ID).is(null));
        }
        if (moderationStatus != null) {
            query.addCriteria(Criteria.where(MODERATION_STATUS).in(moderationStatus));
        }


        return mongoTemplate.find(query, UGC.class);
    }

    @Override
    public List<UGC> findByTenantAndTargetIdRegex(String tenant, String targetIdRegex, int page, int pageSize,
                                                  ActionEnum action, String sortField, String sortOrder) {
        Query q = this.permissionService.getQuery(action, RequestContext.getCurrent().getAuthenticationToken()
            .getProfile());
        if (StringUtils.isBlank(targetIdRegex)) {
            throw new IllegalArgumentException("Regex can't be null or empty");
        }
        if (StringUtils.isBlank(tenant)) {
            throw new IllegalArgumentException("Tenant can't be null or empty");
        }
        if (page > 0 && pageSize > 0) {
            int start = getStart(page, pageSize);
            int end = pageSize;
            q.skip(start);
            q.limit(end);

        }
        q.addCriteria(Criteria.where(TARGET_ID).regex(targetIdRegex, "ig"));
        q.addCriteria(Criteria.where(TENANT).is(tenant));
        if (sortOrder.equalsIgnoreCase(UGCConstants.SORT_ORDER_DESC)) {
            q.sort().on(sortField, Order.DESCENDING);
        } else {
            q.sort().on(sortField, Order.ASCENDING);
        }
        log.debug("Getting UGC using {}", q.toString());
        return mongoTemplate.find(q, UGC.class);
    }


    @Override
    public List<UGC> findUGCs(String tenant, String target, String[] moderationStatusArr, ActionEnum action,
                              int page, int pageSize, String sortField, String sortOrder) {
        Query query = this.permissionService.getQuery(action, RequestContext.getCurrent().getAuthenticationToken()
            .getProfile());

        includeDefaultUGCFields(query);
        if (tenant != null) {
            query.addCriteria(Criteria.where(TENANT).is(tenant));
        }
        if (target != null) {
            query.addCriteria(Criteria.where(TARGET_ID).is(target));
        }
        if (moderationStatusArr != null) {
            query.addCriteria(Criteria.where(MODERATION_STATUS).in(moderationStatusArr));
        }

        if (page != -1 && pageSize != -1) {
            int start = getStart(page, pageSize);
            int end = pageSize;
            query.skip(start);
            query.limit(end);
        }

        if (sortOrder.equalsIgnoreCase(UGCConstants.SORT_ORDER_DESC)) {
            query.sort().on(sortField, Order.DESCENDING);
        } else {
            query.sort().on(sortField, Order.ASCENDING);
        }
        return mongoTemplate.find(query, UGC.class);
    }

    @Override
    public UGC findUGC(ObjectId id, ActionEnum action, String[] moderationStatusArr) {
        Query query = this.permissionService.getQuery(action, RequestContext.getCurrent().getAuthenticationToken()
            .getProfile());
        query.addCriteria(Criteria.where(ID).is(id));
        includeDefaultUGCFields(query);

        if (moderationStatusArr != null) {
            query.addCriteria(Criteria.where(MODERATION_STATUS).in(moderationStatusArr));
        }
        List<UGC> list = mongoTemplate.find(query, UGC.class);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    private void includeDefaultUGCFields(Query query) {
        query.fields().include(UGCConstants.ATTRIBUTES);
        query.fields().include(UGCConstants.FIELD_ID);
        query.fields().include(UGCConstants.PARENT_ID);
        query.fields().include(UGCConstants.TEXT_CONTENT);
        query.fields().include(UGCConstants.ATTACHMENT_ID);
        query.fields().include(UGCConstants.ACTIONS);
        query.fields().include(UGCConstants.SUBJECT);

        query.fields().include(UGCConstants.CREATED_BY);
        query.fields().include(UGCConstants.LAST_MODIFIED_BY);
        query.fields().include(UGCConstants.OWNER);
        query.fields().include(UGCConstants.CREATED_DATE);
        query.fields().include(UGCConstants.LAST_MODIFIED_DATE);
        query.fields().include(UGCConstants.MODERATION_STATUS);

        query.fields().include(UGCConstants.PROFILE_ID);
        query.fields().include(UGCConstants.TENANT);
        query.fields().include(UGCConstants.TARGET_ID);
        query.fields().include(UGCConstants.TARGET_URL);
        query.fields().include(UGCConstants.TARGET_DESCRIPTION);
        query.fields().include(UGCConstants.ANONYMOUS_FLAG);

        query.fields().include(UGCConstants.TIMES_MODERATED);

        query.fields().include(UGCConstants.LIKES);
        query.fields().include(UGCConstants.DISLIKES);
        query.fields().include(UGCConstants.FLAGS);


    }


    @Override
    public List<UGC> findByTenantTargetPaging(String tenant, String target, int page, int pageSize,
                                              ActionEnum action, String sortField, String sortOrder) {
        Query query = this.permissionService.getQuery(action, RequestContext.getCurrent().getAuthenticationToken()
            .getProfile());

        includeDefaultUGCFields(query);
        if (tenant != null) {
            query.addCriteria(Criteria.where(TENANT).is(tenant));
        }
        if (target != null) {
            query.addCriteria(Criteria.where(TARGET_ID).is(target));
        }
        int start = getStart(page, pageSize);
        int end = pageSize;
        query.skip(start);
        query.limit(end);

        if (sortOrder.equalsIgnoreCase(UGCConstants.SORT_ORDER_DESC)) {
            query.sort().on(sortField, Order.DESCENDING);
        } else {
            query.sort().on(sortField, Order.ASCENDING);
        }
        return mongoTemplate.find(query, UGC.class);
    }

    @Override
    public List<UGC> findByTenantAndSort(String tenant, ActionEnum action, String sortField, String sortOrder) {
        Query query = this.permissionService.getQuery(action, RequestContext.getCurrent().getAuthenticationToken()
            .getProfile());

        includeDefaultUGCFields(query);
        if (tenant != null) {
            query.addCriteria(Criteria.where(TENANT).is(tenant));
        }
        if (sortOrder.equalsIgnoreCase(UGCConstants.SORT_ORDER_DESC)) {
            query.sort().on(sortField, Order.DESCENDING);
        } else {
            query.sort().on(sortField, Order.ASCENDING);
        }
        return mongoTemplate.find(query, UGC.class);
    }

    @Override
    public List<String> findPossibleActionsForUGC(final String ugcId, final List<String> roles){
        // Current Version of Spring data does not support Aggregation got to Driver.
        DB mongoDriver = mongoTemplate.getDb();
        DBCollection ugcCollection = mongoDriver.getCollection(UGCConstants.UGC_COLLECTION_NAME);

        DBObject unwind = new BasicDBObject("$unwind","$actions");
        BasicDBObject matchOptions = new BasicDBObject();
        matchOptions.put("_id",new ObjectId(ugcId));
        BasicDBList rolesList = new BasicDBList();
        rolesList.addAll(roles);
        matchOptions.put("actions.roles",new BasicDBObject("$in",rolesList ));
        DBObject match = new BasicDBObject("$match",matchOptions);
        DBObject push = new BasicDBObject("$push","$actions.name");
        DBObject groupOptions = new BasicDBObject();
        groupOptions.put("actions",push);
        groupOptions.put("_id","$_id");
        DBObject group=new BasicDBObject("$group",groupOptions);
        AggregationOutput aggregationResult = ugcCollection.aggregate(unwind, match, group);
        List<String> list = new ArrayList<String>();
        for(DBObject result : aggregationResult.results()){
            BasicDBList actions = (BasicDBList)result.get("actions");
            if(actions!=null){
                for (Object action : actions) {
                     list.add(action.toString());
                }
                return list;
            }
        }
        return list;
    }

    @Override
    public List<UGC> findByParentIdWithReadPermission(ObjectId parentId, ActionEnum action,
                                                      String[] moderationStatus, String sortField, String sortOrder) {
        Query query = this.permissionService.getQuery(action, RequestContext.getCurrent().getAuthenticationToken()
            .getProfile());
        query.addCriteria(Criteria.where(PARENT_ID).is(parentId));

        includeDefaultUGCFields(query);
        if (moderationStatus != null) {
            query.addCriteria(Criteria.where(MODERATION_STATUS).in(moderationStatus));
        }
        if (sortOrder.equalsIgnoreCase(UGCConstants.SORT_ORDER_DESC)) {
            query.sort().on(sortField, Order.DESCENDING);
        } else {
            query.sort().on(sortField, Order.ASCENDING);
        }
        return mongoTemplate.find(query, UGC.class);
    }

    private int getStart(int page, int pageSize) {
        if (page <= 0) {
            return 0;
        }
        return (page - 1) * pageSize;
    }

}
