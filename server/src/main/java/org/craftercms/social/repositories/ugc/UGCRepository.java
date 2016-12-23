package org.craftercms.social.repositories.ugc;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.controllers.rest.v3.comments.exceptions.UGCNotFound;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.social.ModerationStatus;

/**
 * UGC Repository Definitions.
 */
public interface UGCRepository<T extends UGC> extends CrudRepository<T> {

    /**
     * Finds all the ugc and all its children.
     *
     * @param ugcId         Id of the ugc to find and its children.
     * @param childrenCount How many Children Levels.
     * @param contextId      Context ID of the UGCs.
     * @return List of the ugc and all its tree. (first element is the "ROOT" UGC) this is ordered by ancestors size
     * and created date (descending).
     * @throws MongoDataException
     */
    public List<T> findChildrenOf(String ugcId, final int childrenCount, final String contextId) throws
        MongoDataException;

    /**
     * Finds all UGC's of a contextId that belongs to the given target-id.
     *
     * @param targetId Target Id which the ugs belong.
     * @param contextId Context ID of the UGCs.
     * @return A Iterable of UGC that belong to the contextId and are associated to the target.
     */
    Iterable<T> findByTargetId(String targetId, String contextId) throws MongoDataException;

    /**
     * Deletes a attribute <b>Has to be full "dot notation" mode </b>
     *
     * @param ugcId         Id of the ugc to remove the attribute.
     * @param contextId     Context ID of the UGCs.
     * @param attributeName Attributes name(<b>Full dot notation</b>) to unset/delete
     * @return True if attribute is delete, false otherwise
     */
    void deleteAttribute(final String ugcId, final String contextId, final String[] attributeName) throws
        MongoDataException;

    /**
     * Find's a ugc with the given id and contextId.
     *
     * @param contextId Context ID of the UGCs.
     * @param ugcId    Id of the ugc
     * @return
     */
    T findUGC(final String contextId, final String ugcId) throws MongoDataException;

    /**
     * Adds the given attributes to the ugc.
     *
     * @param ugcId      Id of the ugc to add the attributes.
     * @param contextId  Context ID of the UGCs.
     * @param attributes Attributes to add/removeWatcher to the UGC.
     */
    void setAttributes(final String ugcId, final String contextId, final Map<String,
        Object> attributes) throws MongoDataException;


    /**
     * Deletes The Given UGC and <b>All its children</b>.
     *
     * @param ugcId    Id of the ugc
     * @param contextId Context ID of the UGCs.
     */
    void deleteUgc(final String ugcId, final String contextId) throws MongoDataException;

    /**
     * Finds Ugcs by the given user query. <b>It assumes that the query is been sanitize for any security
     * concerns</b>
     *
     * @param contextId Context ID of the UGCs.
     * @param query  Query to execute.
     * @param sort   Sort query <i>If null or empty it will be ignore</i>
     * @param start  Where to start
     * @param limit  How many results
     * @return A list of the Ugc that match the given query in the given order, empty if nothing if found.
     */
    Iterable<T> findByUserQuery(final String contextId, final String query, final String sort, final int start,
                                final int limit) throws MongoDataException;

    /**
     * Finds all the children of a given ugc.
     *
     * @param ugcId     Id of the ugc to find and its children.
     * @param contextId Context ID of the UGCs.
     * @param limit     How many results
     * @param start     where to start.
     * @param upToLevel how many sub-children levels to lookup.
     * @param targetId  Target Id which the ugs belong.
     * @return A iterate with the results.empty if noting is found.
     * @throws MongoDataException
     */
    Iterable<T> findChildren(final String ugcId, final String targetId, final String contextId, final int start,
                             final int limit, final List sortOrder, final int upToLevel) throws MongoDataException, UGCNotFound;


    Iterable<T> findByTargetId(String targetId, String contextId, int start, int limit,
                               final List<DefaultKeyValue<String, Boolean>> sortOrder,
                               final int upToLevel) throws MongoDataException;

    long countByTargetId(String contextId, String threadId, int levels) throws MongoDataException;

    long countChildrenOf(String contextId, String ugcId) throws MongoDataException;

    Iterable<T>  findByModerationStatus(ModerationStatus status, String targetId, String contextId, int start,
                                        int limit, List<DefaultKeyValue<String, Boolean>> sortOrder)
            throws MongoDataException;

    long countFindByModerationStatus(ModerationStatus status, String targetId, String contextId)
            throws MongoDataException;

    Iterable<T> findAllFlagged(String context, int start, int pageSize, List sortOrder);

    long countAllFlagged(String context, int start, int pageSize, List sortOrder);
}
