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

import org.bson.types.ObjectId;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.UGC.ModerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("uGCRepository")
public interface UGCRepository extends MongoRepository<UGC, ObjectId>, UGCRepositoryCustom {

	List<UGC> findByModerationStatusAndTenant(ModerationStatus moderationStatus, String tenant);

	List<UGC> findByModerationStatusAndTenantAndTargetId(ModerationStatus moderationStatus, String tenant, String target);
	
	List<UGC> findByTenantAndTargetId(String tenant, String target);

    Page<UGC> findByTenantAndTargetId(String tenant, String target, Pageable pageable);

    List<UGC> findByTenantAndTargetIdAndParentIdIsNull(String tenant, String target);

	@Query("{_id:{$in:?0}}")
	List<UGC> findByIds(ObjectId[] ids);
	
	List<UGC> findByParentId(ObjectId parentId);
}
