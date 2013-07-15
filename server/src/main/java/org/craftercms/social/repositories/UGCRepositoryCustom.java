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

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Query;
import java.util.List;

import org.bson.types.ObjectId;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.UGC.ModerationStatus;

public interface UGCRepositoryCustom {
	
	public List<UGC> findUGCs(String tenant, String target,
			String[] moderationStatusArr,String[] roles, boolean sortChronological, Query query);

	List<UGC> findTenantAndTargetIdAndParentIsNull(String tenant,
			String target, Query query);
	
	UGC findUGC(ObjectId id, Query q, String[] moderationStatusArr);

	List<UGC> findByTenantTargetPaging(String tenant, String target,
			int page, int pageSize, boolean sortChronological, Query query);
	
	List<UGC> findByParentIdWithReadPermission(ObjectId parentId, Query query, String[] moderationStatus);

}
