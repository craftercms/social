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
import org.craftercms.social.domain.UGCAudit.AuditAction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UGCAuditRepository extends MongoRepository<UGCAudit,ObjectId> {

	UGCAudit findByProfileIdAndUgcIdAndAction(String profileId, ObjectId ugcId,
			AuditAction action);
	
	List<UGCAudit> findByUgcId(ObjectId ugcID);

	List<UGCAudit> findByUgcIdAndAction(ObjectId ugcId, AuditAction auditAction);

	List<UGCAudit> findByUgcIdAndProfileId(ObjectId ugcId, String profileId);

	List<UGCAudit> findByProfileIdAndAction(String profileId,
			AuditAction auditAction);

	List<UGCAudit> findByProfileId(String profileId);

}
