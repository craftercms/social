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

import java.util.List;

import org.bson.types.ObjectId;
import org.craftercms.social.domain.UGC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;

public class UGCRepositoryImpl implements UGCRepositoryCustom {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private static final String ID = "_id";

	@Override
	public List<UGC> findTenantAndTargetIdAndParentIsNull(String tenant, String target,
			Query query) {
		query.addCriteria(Criteria.where("tenant").is(tenant));
        query.addCriteria(Criteria.where("targetId").is(target));
        query.addCriteria(Criteria.where("parentId").is(null));
        
		
		return mongoTemplate.find(query, UGC.class);
	}
	
	@Override
	public List<UGC> findUGCs(String tenant, String target,
			String[] moderationStatusArr, String[] roles, boolean sortChronological, Query query) {
		if (tenant !=null) {
			query.addCriteria(Criteria.where("tenant").is(tenant));
		}
        if (target !=null) {
        	query.addCriteria(Criteria.where("targetId").is(target));
        }
        if (moderationStatusArr !=null) {
        	query.addCriteria(Criteria.where("moderationStatus").in(moderationStatusArr));
        }

		if (sortChronological) {
			query.sort().on(ID, Order.DESCENDING);
		} else {
			query.sort().on(ID, Order.ASCENDING);
		}
		
		return mongoTemplate.find(query, UGC.class);
	}

	@Override
	public UGC findUGC(ObjectId id, Query query) {
		query.addCriteria(Criteria.where("id").is(id));
		
		List<UGC> list = mongoTemplate.find(query, UGC.class);
		if (list!=null && list.size()>0) { 
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public List<UGC> findByTenantTargetPaging(String tenant, String target,
			int page, int pageSize, boolean sortChronological, Query query) {
		query.addCriteria(Criteria.where("tenant").is(tenant));
		query.addCriteria(Criteria.where("targetId").is(target));
		int start = getStart(page, pageSize);
		int end = pageSize;
		query.skip(start);
		query.limit(end);
		
		if (sortChronological) {
			query.sort().on("_id", Order.DESCENDING);
		} else {
			query.sort().on("_id", Order.ASCENDING);
		}
		
		return mongoTemplate.find(query, UGC.class);
	}
	
	@Override
	public List<UGC> findByParentIdWithReadPermission(ObjectId parentId, Query query) {
		query.addCriteria(Criteria.where("parentId").is(parentId));
		
		return mongoTemplate.find(query, UGC.class);
	}
	
	
	private int getStart(int page, int pageSize) {
		if (page <=0) {
			return 0;
		}
		return (page-1) * pageSize;
	}

}
