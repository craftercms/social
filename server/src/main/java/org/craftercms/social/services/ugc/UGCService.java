package org.craftercms.social.services.ugc;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.apache.commons.io.FileExistsException;
import org.craftercms.commons.mongo.FileInfo;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.exceptions.UGCException;

/**
 * Defines all Rest Services for UGCs.
 * <p>Implementers Must
 * <ul>
 * <li>Audit all Calls</li>
 * <li>Check that this call are made with an authenticated user and the the UGC belongs to the user's contextId.</li>
 * </ul>
 * </p>
 */
public interface UGCService<T extends UGC> {

    /**
     * <p>Creates an UGC.</p>
     * <p>Implementers must check if the current user is allow to create UGC for that contextId</p>
     *
     * @param contextId   Context ID of the UGC
     * @param ugcParentId Parent Id of the UGC <i>If not null or empty will be the parent of the UGC else it wont have
     *                    any parent</i>.
     * @param targetId    Target Id of the UGC.
     * @param textContent Actual content of the UGC Must be cleanup to prevent XSS.
     * @param subject     Subject of the UGC.
     * @param attrs
     * @return A new Public (secure) UGC.
     * @throws org.craftercms.social.exceptions.SocialException If UGC can't be created.
     * @throws java.lang.IllegalArgumentException               If given parent UGC does not exist.
     */
    public T create(final String contextId, final String ugcParentId, final String targetId, final String textContent,
                    final String subject, final Map<String, Object> attrs) throws SocialException;


    /**
     * <p>Sets an attribute to the given UGC <i>Creates if does not exist</i></p>
     * <p>Implementers must check if the current user is allow to update UGC and that the user belongs to the same
     * ugc contextId</p>.
     *
     * @param ugcId      Id of the Ugc to add Attribute.
     * @param contextId  Context ID of the UGC.
     * @param attributes Attributes to set.<b>Nested attributes Must be nested with in the map</b>
     * @return A new Public (secure) UGC.
     * @throws org.craftercms.social.exceptions.SocialException if attribute can be set.
     * @throws java.lang.IllegalArgumentException               If given UGC does not exist.*
     */
    void setAttributes(final String ugcId, final String contextId, Map<String, Object> attributes)
        throws SocialException;

    /**
     * Deletes a attribute of the given UGC.
     * <p>Implementers must check if the current user is allow to update UGC and that the user belongs to the same
     * ugc contextId</p>.
     *
     * @param ugcId    id Id of the Ugc to add Attribute.
     * @param name     Attributes Name of the attribute to delete.
     * @param contextId Context ID of the UGC
     * @return A new Public (secure) UGC.
     * @throws org.craftercms.social.exceptions.SocialException if attribute can be deleted.
     * @throws java.lang.IllegalArgumentException               If given UGC does not exist.*
     */
    public void deleteAttribute(final String ugcId, final String[] name, final String contextId) throws SocialException;

    /**
     * <p>Deletes a UGC <i>If the Ugc is a parent it will delete the whole tree</i></p>
     * <p>Implementers must check if the current user is allow to delete UGC and that the user belongs to the same
     * ugc contextId.t</p>.
     * x
     *
     * @param ugcId    UGC id to delete.
     * @param contextId Context ID of the UGC
     * @return True if UGC (and tree) can be deleted , false other wise.
     * @throws org.craftercms.social.exceptions.SocialException if ugc (and or tree) can be deleted)
     * @throws java.lang.IllegalArgumentException               If given UGC does not exist.*
     */
    boolean deleteUgc(final String ugcId, final String contextId) throws SocialException;

    /**
     * Updates the given UGC with the given information. Also it will update
     * <p>Implementers must check if the current user is allow to update UGC and that the user belongs to the same
     * ugc contextId.</p>.
     *
     * @param ugcId    Id of the Ugc to update.
     * @param body     new Text Content (empty of null to leave current value).
     * @param subject  new subject (empty of null to leave current value).
     * @param contextId
     * @return the updated Public (secure) UGC.
     * @throws SocialException                    If the UGC can be updated.
     * @throws java.lang.IllegalArgumentException If given UGC does not exist.*
     */
    public T update(final String ugcId, final String body, final String subject, final String contextId,
                    final Map<String, Object> attributes) throws SocialException;


    /**
     * Gets a UGC with the given Id.
     * <p>Implementers must check if the current user is allow to read UGC and that the user belongs to the same
     * ugc contextId.</p>.
     *
     * @param ugcId           Id of the desire UGC
     * @param includeChildren True to include the children of this UGC.
     * @param childCount      Amount of children to be include.<i>negative Numbers to all</i>
     * @param contextId        contextId owner of the UGC
     * @return The UGC (and its children).
     * @throws java.lang.IllegalArgumentException               If includeChildren is set to true and childCount is
     *                                                          set to 0
     * @throws org.craftercms.social.exceptions.SocialException If is unable to get the UGC.
     */
    public T read(final String ugcId, final boolean includeChildren, final int childCount,
                  final String contextId) throws UGCException;

    <T extends UGC> Iterable<T> readByTargetId(String targetId, String contextId) throws UGCException;

    /**
     * Finds All UGC that match the given criteria.
     *
     * @param contextId Context ID of the UGC
     * @param query  Query Map.
     * @param sort   Sort Map.
     * @param start  Where to start.
     * @param limit  How many results to return.
     * @return A list of all UGC that match the given criteria , if sort map is empty is unsorted
     */
    Iterable<T> search(final String contextId, final String query, final String sort, final int start,
                       final int limit) throws UGCException;

    /**
     * Adds and Attachment Information to the given UGC.
     *
     * @param ugcId      Id of the desire UGC to attach a file.
     * @param contextId  Context ID of the UGC
     * @param attachment Attachment to add.
     */
    FileInfo addAttachment(final String ugcId, final String contextId, final InputStream attachment,
                           final String fileName, final String contentType) throws FileExistsException, UGCException;

    /**
     * Deletes an attachment of the given UGC.
     *
     * @param ugcId        UGC id to delete the attachment.
     * @param contextId    Context ID of the UGC
     * @param attachmentId attachment Id to delete.
     * @throws java.io.FileNotFoundException                        If file is not found
     * @throws org.craftercms.social.exceptions.IllegalUgcException If the given UGC id does not exists.
     * @throws org.craftercms.social.exceptions.UGCException        If unable to delete the attachment or update the
     *                                                              UGC.
     */
    void removeAttachment(String ugcId, String contextId, String attachmentId) throws UGCException, FileNotFoundException;

    FileInfo updateAttachment(String ugcId, String contextId, String attachmentId, InputStream newAttachment) throws
        UGCException, FileNotFoundException;

    FileInfo readAttachment(String ugcId, String contextId, String attachmentId) throws FileNotFoundException,
        UGCException;

    List<T> read(String targetId, String contextId, int start, int limit,
                 List<DefaultKeyValue<String, Boolean>> sortOrder, final int upToLevel, final int childrenPerLevel)
        throws UGCException;

    public List<T> readChildren(final String ugcId, final String targetId, final String contextId,
                                    final int start, final int limit, final List sortOrder, final int upToLevel,
                                    final int childrenPerLevel) throws UGCException;
    /**
     * Finds a single UGC.
     *
     * @param ugcId  Id of the Ugc.
     * @param contextId Context ID of the UGC
     * @return The ugc with the given Id ,null if not found.
     */
    T read(String ugcId, String contextId) throws UGCException;

    /**
     * Counts all the First Level ugc of a target.
     * @param threadId Id ot the target.
     * @param contextId Context ID of the UGC
     * @return A count of all possible first level comments.
     */
    long count(String threadId, String contextId) throws UGCException;

    long countChildren(String ugcId, String contextId) throws UGCException;
}
