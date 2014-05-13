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

import org.craftercms.social.domain.UGCAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;

public class UGCAuditRepositoryImpl implements UGCAuditRepositoryCustom {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private static final String ROW = "row";
    private static final String ACTION = "action";
	
	@Override
	public List<UGCAudit> findByLastRetrievedRow(long lastRowRetrieve, String[] actionFilters) {
		Query query = new Query();
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC,ROW)));
		query.addCriteria(Criteria.where(ROW).gt(lastRowRetrieve));

        if(actionFilters != null){
            for(String actionFilter : actionFilters){
                query.addCriteria(Criteria.where(ACTION).is(actionFilter));
            }
        }

		return mongoTemplate.find(query, UGCAudit.class);
	}
	
	@Override
	public List<UGCAudit> findByLastRetrievedRow(long lastRowRetrieve, int start, int end, String[] actionFilters) {
		Query query = new Query();
		
		query.addCriteria(Criteria.where(ROW).gt(lastRowRetrieve));


        if(actionFilters != null && actionFilters.length > 0){
            query.addCriteria(Criteria.where(ACTION).in(actionFilters));
        }
		
		query.skip(start);
        query.limit(end > start? (end - start + 1): 0);
		
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC,ROW)));
	
		return mongoTemplate.find(query, UGCAudit.class);
	}
	
	@Override
	public long count(long lastRowRetrieve, String[] actionFilters) {
		Query query = new Query();
		
		query.addCriteria(Criteria.where(ROW).gt(lastRowRetrieve));

        if(actionFilters != null && actionFilters.length > 0){
            query.addCriteria(Criteria.where(ACTION).in(actionFilters));
        }
		
		return mongoTemplate.count(query, UGCAudit.class);
	}
	



}
