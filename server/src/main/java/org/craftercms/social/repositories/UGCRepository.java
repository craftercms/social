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
import org.craftercms.social.domain.UGC;
import org.craftercms.social.exceptions.SecurityProfileException;
import org.craftercms.social.exceptions.UGCException;
import org.springframework.data.mongodb.repository.Query;

/**
 * UGC Data Operations.
 */
public interface UGCRepository extends CrudRepository<UGC> {

    /**
     * Find Ugc with the given Ids.
     * @param ids Ids to look for.
     * @return A Iterable of  UGS with the given Id's.
     * @throws MongoDataException If ugc can be found due a data error.
     */
    Iterable<UGC> findByIds(ObjectId[] ids) throws MongoDataException;

    /**
     *  Finds all Child Ugs for the given parent.
     * @param parentId Parent of the UGS.
     * @return  A list of all UGC that have the given parent Id as it's parent.
     * @throws MongoDataException If ugc can be found due a data error.
     */
    Iterable<UGC> findByParentId(ObjectId parentId) throws MongoDataException;

    /**
     * Find all UGcs with the given tenant,target and moderation status.
     * Also Skips Results and Orders (Order before skip)
     * @param tenant Tenant of the UGc.
     * @param target Target of the UGC
     * @param moderationStatusArr Moderation Status of the UGC
     * @param page Page wanted.
     * @param pageSize Page Size
     * @param sortField Sort field.
     * @param sortOrder <b>True</b> for ascending order <b>False</b> for descending.
     * @return A List of UGCs that matches the given criteria.
     * @throws MongoDataException If ugc can be found due a data error.
     */
     Iterable<UGC> findUGCs(String tenant, String target, String[] moderationStatusArr, int page, int pageSize,
                                  String sortField, boolean sortOrder) throws MongoDataException;

    /**
     * Find all UGC that are not a children of any other UGC (Roots)
     * @param tenant Tenant of the UGc.
     * @param target Target of the UGC
     * @return A List of UGCs that are not children and match the tenant and target.
     * @throws MongoDataException If ugc can be found due a data error.
     */
    Iterable<UGC> findTenantAndTargetIdAndParentIsNull(String tenant, String target) throws MongoDataException;

    /**
     * Find a UGC by Id and moderation Status
     * @param id Id of the UGC
     * @param moderationStatus Posible Moderation status of the UGC
     * @return A UGC with given Id and in a given moderation status.
     * @throws MongoDataException If ugc can be found due a data error.
     */
    UGC findUGC(ObjectId id, String[] moderationStatus) throws MongoDataException;

    /**
     * Find all Ugc for the given tenant and target.<i>Sorts and Pages result.</i>
     * @param tenant Tenant of the UGc.
     * @param target Target of the UGC
     * @param page Page wanted.
     * @param pageSize Page Size
     * @param sortField Sort field.
     * @param sortOrder <b>True</b> for ascending order <b>False</b> for descending.
     * @return
     */
    Iterable<UGC> findByTenantTargetPaging(String tenant, String target, int page, int pageSize, String sortField,
                                       boolean sortOrder) throws MongoDataException;

    /**
     * Gets all the possible  actions that a the given Role can do to a given UGC base on that's UGC Security Profile.
     * @param ugcId UGC id.
     * @param roles Roles to check.
     * @return A List of Actions that a given Roles can do to a UGC.
     * @throws MongoDataException If ugc can be found due a data error.
     * @throws SecurityProfileException If there is a problem checking the security Profile.
     */
    Iterable<String> findPossibleActionsForUGC(String ugcId, List<String> roles) throws MongoDataException, SecurityProfileException;

    Iterable<UGC> findByParentId(ObjectId parentId, String[] moderationStatus, String sortField, boolean sortOrder) throws MongoDataException;

    Iterable<UGC> findByTenantAndSort(String tenant, String sortField, boolean sortOrder) throws MongoDataException;

    Iterable<UGC> findByModerationStatusAndTenantAndTargetId(String[] moderationStatus, String tenant,
                                                             String targetId,
                                                             boolean isOnlyRoot) throws MongoDataException;

    Iterable<UGC> findByTenantAndTargetIdRegex(String tenant, String targetIdRegex, int page, int pageSize,
                                               String sortField, boolean sortOrder) throws MongoDataException;

}
