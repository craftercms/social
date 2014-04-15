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
import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.UGCAudit;
import org.craftercms.social.domain.UGCAudit.AuditAction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


public interface UGCAuditRepository extends CrudRepository<UGCAudit> {

	UGCAudit findByProfileIdAndUgcIdAndAction(String profileId, ObjectId ugcId,
			AuditAction action) throws MongoDataException;
	
	Iterable<UGCAudit> findByUgcId(ObjectId ugcID) throws MongoDataException;

	Iterable<UGCAudit> findByUgcIdAndAction(ObjectId ugcId, AuditAction auditAction) throws MongoDataException;

	Iterable<UGCAudit> findByUgcIdAndProfileId(ObjectId ugcId, String profileId) throws MongoDataException;

	Iterable<UGCAudit> findByProfileIdAndAction(String profileId, AuditAction auditAction) throws MongoDataException;

	Iterable<UGCAudit> findByProfileId(String profileId) throws MongoDataException;


    Iterable<UGCAudit> findByLastRetrievedRow(long lastRowRetrieve, String[] actionFilters) throws MongoDataException;

    Iterable<UGCAudit> findByLastRetrievedRow(long lastRowRetrieve, int start, int end, String[] actionFilters)
        throws MongoDataException;

    long count(long lastRowRetrieve, String[] actionFilters) throws MongoDataException;

    void deleteByRow(long row);
}
