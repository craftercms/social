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
package org.craftercms.social.domain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.craftercms.commons.jackson.mvc.annotations.Exclude;
import org.craftercms.commons.jackson.mvc.annotations.InjectValue;
import org.craftercms.commons.mongo.Document;
import org.craftercms.commons.mongo.FileInfo;
import org.craftercms.profile.api.Profile;
import org.jongo.marshall.jackson.oid.Id;
import org.springframework.beans.BeanUtils;

@SuppressWarnings("unchecked")
@Document(collectionName = UGC.COLLECTION_NAME)
public class UGC<T extends UGC> {

    public static final String COLLECTION_NAME = "ugc";

    @Id
    private ObjectId id;
    private ArrayDeque<ObjectId> ancestors;
    private String targetId;
    @Exclude
    private String contextId;
    private String subject;
    private String body;
    private ObjectId[] attachmentId;
    private String createdBy;
    private String lastModifiedBy;
    private Date createdDate;
    private Date lastModifiedDate;
    private boolean anonymousFlag;
    private Map<String, Object> attributes;
    private ArrayDeque<T> children;
    private List<FileInfo> attachments;
    @InjectValue(useProperty = "createdBy")
    private Profile user;

    public UGC() {
        ancestors = new ArrayDeque<>();
        children = new ArrayDeque<>();
        attachments = new ArrayList<>();
        user = null;
    }

    public UGC(T base) {
        this();
        BeanUtils.copyProperties(base, this);
}

    public UGC(final String subject, final String body, final String targetId) {
        this();
        this.subject = subject;
        this.body = body;
        this.targetId = targetId;
    }

    public UGC(final String subject, final String body, final String targetId, final ArrayDeque<ObjectId> ancestors) {
        this();
        this.subject = subject;
        this.body = body;
        this.targetId = targetId;
        this.ancestors = ancestors;
    }


    public ObjectId getId() {
        return id;
    }

    public void setId(final ObjectId id) {
        this.id = id;
    }

    public ArrayDeque<ObjectId> getAncestors() {
        return ancestors;
    }

    public void setAncestors(final ArrayDeque<ObjectId> ancestors) {
        this.ancestors = ancestors;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(final String targetId) {
        this.targetId = targetId;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(final String contextId) {
        this.contextId = contextId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public ObjectId[] getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(final ObjectId[] attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(final Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public boolean isAnonymousFlag() {
        return anonymousFlag;
    }

    public void setAnonymousFlag(final boolean anonymousFlag) {
        this.anonymousFlag = anonymousFlag;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(final Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public ArrayDeque<T> getChildren() {
        return children;
    }

    public void setChildren(final ArrayDeque<T> children) {
        this.children = children;
    }

    public List<FileInfo> getAttachments() {
        return attachments;
    }

    public void setAttachments(final List<FileInfo> attachments) {
        this.attachments = attachments;
    }

    public Profile getUser() {
        return user;
    }

    public void setUser(final Profile user) {
        this.user = user;
    }

    public boolean isMyParent(final T ugc) {
        if (ancestors.isEmpty()) {
            return false;
        } else {
            return ancestors.getLast().equals(ugc.getId());
        }
    }

    public <T extends UGC> boolean isMyChild(final T ug) {
        if (ug.getAncestors().isEmpty()) {
            return false;
        }
        return ug.getAncestors().getLast().equals(this.id);
    }

    @Override
    public String toString() {

        return "UGC{" +
            "id=" + id +
            ", ancestors=" + StringUtils.join(ancestors) +
            ", targetId='" + targetId + '\'' +
            ", subject='" + subject + '\'' +
            ", createdBy='" + createdBy + '\'' +
            ", lastModifiedBy='" + lastModifiedBy + '\'' +
            ", createdDate=" + createdDate +
            ", lastModifiedDate=" + lastModifiedDate +
            ", anonymousFlag=" + anonymousFlag +
            ", attributes=" + attributes +
            '}';
    }


}
