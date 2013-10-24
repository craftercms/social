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
import org.craftercms.social.domain.UGCAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;

public class UGCAuditRepositoryImpl implements UGCAuditRepositoryCustom {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private static final String SEQUENCE = "sequence";
	
	@Override
	public List<UGCAudit> findByLastRetrievedRow(long lastRowRetrieve) {
		Query query = new Query();
		query.sort().on(SEQUENCE, Order.DESCENDING);
		query.addCriteria(Criteria.where(SEQUENCE).gt(lastRowRetrieve));
		//query.skip(lastRowRetrieve);
		return mongoTemplate.find(query, UGCAudit.class);
	}


}
