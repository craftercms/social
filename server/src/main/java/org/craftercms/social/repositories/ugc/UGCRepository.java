package org.craftercms.social.repositories.ugc;

import java.util.List;
import java.util.Map;

import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.UGC;

/**
 * UGC Repository Definitions.
 */
public interface UGCRepository<T extends UGC> extends CrudRepository<T> {

    /**
     * Finds all the ugc and all its children.
     *
     * @param ugcId         Id of the ugc to find and its children.
     * @param childrenCount How many Children Levels.
     * @param tenantId      tenantId Owner of the UGCs.
     * @return List of the ugc and all its tree. (first element is the "ROOT" UGC) this is ordered by ancestors size
     * and created date (descending).
     * @throws MongoDataException
     */
    public List<T> findChildrenOf(String ugcId, final int childrenCount, final String tenantId) throws
        MongoDataException;

    /**
     * Finds all UGC's of a tenantId that belongs to the given target-id.
     *
     * @param targetId Target Id which the ugs belong.
     * @param tenantId tenantId Owner of the UGCs.
     * @return A Iterable of UGC that belong to the tenantId and are associated to the target.
     */
    Iterable<T> findByTargetId(String targetId, String tenantId) throws MongoDataException;

    /**
     * Deletes a attribute <b>Has to be full "dot notation" mode </b>
     *
     * @param ugcId         Id of the ugc to remove the attribute.
     * @param tenantId      tenantId Owner of the UGCs.
     * @param attributeName Attributes name(<b>Full dot notation</b>) to unset/delete
     * @return True if attribute is delete, false otherwise
     */
    void deleteAttribute(final String ugcId, final String tenantId, final String[] attributeName) throws
        MongoDataException;

    /**
     * Find's a ugc with the given id and tenantId.
     *
     * @param tenantId tenantId Owner of the UGCs.
     * @param ugcId    Id of the ugc
     * @return
     */
    T findUGC(final String tenantId, final String ugcId) throws MongoDataException;

    /**
     * Adds the given attributes to the ugc.
     *
     * @param ugcId      Id of the ugc to add the attributes.
     * @param tenantId   tenantId Owner of the UGC.
     * @param attributes Attributes to add/update to the UGC.
     */
    void setAttributes(final String ugcId, final String tenantId, final Map<String,
        Object> attributes) throws MongoDataException;


    /**
     * Deletes The Given UGC and <b>All its children</b>.
     *
     * @param ugcId    Id of the ugc
     * @param tenantId tenantId Owner of the UGCs
     */
    void deleteUgc(final String ugcId, final String tenantId) throws MongoDataException;

    /**
     * Finds tenants Ugcs by the given user query. <b>It assumes that the query is been sanitize for any security
     * concerns</b>
     *
     * @param tenant Tenant Owner of the Ugc to look for.
     * @param query  Query to execute.
     * @param sort   Sort query <i>If null or empty it will be ignore</i>
     * @param start  Where to start
     * @param limit  How many results
     * @return A list of the Ugc that match the given query in the given order, empty if nothing if found.
     */
    Iterable<T> findByUserQuery(final String tenant, final String query, final String sort, final int start,
                                final int limit) throws MongoDataException;

    /**
     * Finds all the children of a given ugc.
     *
     * @param ugcId  Id of the ugc to find and its children.
     * @param tenant Tenant Owner of the Ugc to look for.
     * @param limit  How many results
     * @param skip   where to start.
     * @return A iterate with the results.empty if noting is found.
     * @throws MongoDataException
     */
    Iterable findChildren(String ugcId, String tenant, int limit, int skip) throws MongoDataException;
}
