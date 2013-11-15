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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.bson.types.ObjectId;
import org.craftercms.social.controllers.rest.v1.to.UGCRequest;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.social.util.Hierarchical;
import org.craftercms.social.util.serialization.StringObjectMapConverter;
import org.springframework.data.annotation.Transient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("ugc")
@XmlRootElement
public class UGC implements Hierarchical<UGC> {
	public enum ModerationStatus {
		UNMODERATED, PENDING, APPROVED, SPAM, TRASH
	}
	
	public static final String[] ModerationStatusString = {
		"UNMODERATED", "PENDING", "APPROVED", "SPAM", "TRASH"
	};

	public static final String COLLECTION_NAME = null;

	private ObjectId id;
	private ObjectId parentId;
	private String textContent;
	@Transient
	private List<AttachmentModel> attachmentsList;
	private ObjectId[] attachmentId;
	private List<Action> actions;

    private String createdBy;
    private String lastModifiedBy;
    private String owner;
    private Date createdDate;
    private Date lastModifiedDate;
	
	private ModerationStatus moderationStatus;
	private int timesModerated;
	private int likeCount;
	private int offenceCount;
	private int flagCount;
	private String profileId;
    private String tenant;
	private String targetId;
	private String targetUrl;
	private String targetDescription;
	private boolean anonymousFlag;
	@Transient
	private transient List<UGC> children;
	@Transient
	private int extraChildCount;
	@Transient
	private transient Profile profile = null;
	
	@XmlElement(name="attributes")
	@XStreamAlias("attributes")
	@XStreamConverter(StringObjectMapConverter.class)
	private Map<String, Object> attributes = null;

	public UGC() {
		this(null, null, null, null, null, null,null,null);
	}

	public UGC(ObjectId parentId, String textContent, ObjectId[] attachmentId, String profileId, String tenant, String targetId,
			Map<String, Object> attributes, String targetUrl, String targetDescription) {
		super();
		this.parentId = parentId;
		this.textContent = textContent;
		if (attachmentId == null) {
			this.attachmentId = null;
		} else {
			this.attachmentId = attachmentId.clone();
		}
		this.profileId = profileId;
        this.tenant = tenant;
		this.targetId = targetId;
		this.attributes = attributes;
		this.children = new ArrayList<UGC>();
		this.targetDescription = targetDescription;
		this.targetUrl = targetUrl;
		
	}

	public UGC(String textContent, String profileId, String tenant, String target, Map<String, Object> attributeMap, String targetUrl, String targetDescription) {
		this(null, textContent, null, profileId, tenant, target, attributeMap, targetUrl, targetDescription);
	}

	public UGC(String textContent, String profileId, String tenant, String target, ObjectId parentId, Map<String, Object> attributeMap, String targetUrl, String targetDescription) {
		this(parentId, textContent, null, profileId, tenant, target, attributeMap, targetUrl, targetDescription);
	}

    public UGC(UGCRequest ugcRequest, String profileId) {
        this(ugcRequest.getTextContent(), profileId, ugcRequest.getTenant(), ugcRequest.getTargetId(),
                ugcRequest.getParentId() == null ? null : new ObjectId(ugcRequest.getParentId()), ugcRequest.getAttributes(),
                ugcRequest.getTargetUrl(), ugcRequest.getTargetDescription());

        this.setActions(ugcRequest.getActions());
        this.setAttributes(this.getAttributes());

    }

	@Override
	public void addChild(UGC child) {
		children.add(child);
	}

	@Override
	@XmlElement
	public List<UGC> getChildren() {
		return children;
	}

	@Override
	public int getChildCount() {
		return children.size();
	}

	@XmlElement
	@Override
	public int getExtraChildCount() {
		return extraChildCount;
	}

	@Override
	public void incExtraChildCount() {
		extraChildCount++;
	}

	@Override
	public void incExtraChildCountBy(int count) {
		extraChildCount += count;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getParentId() {
		return parentId;
	}

	public void setParentId(ObjectId parentId) {
		this.parentId = parentId;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public ObjectId[] getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(ObjectId[] attachmentId) {
		if (attachmentId==null) {
			this.attachmentId = null;
		} else {
			this.attachmentId = attachmentId.clone();
		}
	}

	public ModerationStatus getModerationStatus() {
		return moderationStatus;
	}

	public void setModerationStatus(ModerationStatus moderationStatus) {
		this.moderationStatus = moderationStatus;
	}

	public int getTimesModerated() {
		return timesModerated;
	}

	public void setTimesModerated(int timesModerated) {
		this.timesModerated = timesModerated;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public int getOffenceCount() {
		return offenceCount;
	}

	public void setOffenceCount(int offenceCount) {
		this.offenceCount = offenceCount;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	@XmlElement
	public Date getDateAdded() {
		return new Date(id.getTime());
	}

	public int getFlagCount() {
		return flagCount;
	}

	public void setFlagCount(int flagCount) {
		this.flagCount = flagCount;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	@Override
	public String toString() {
		return String.format("UGC [id=%s, parentId=%s, textContent=%s, attachmentId=%s, moderationStatus=%s,"
				+ " timesModerated=%s, likeCount=%s, offenceCount=%s, profileId=%s, targetId=%s]", id, parentId, textContent,
                attachmentId!=null ? attachmentId.length : 0, moderationStatus, timesModerated, likeCount, offenceCount, profileId, targetId);
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

	public List<AttachmentModel> getAttachmentsList() {
		return attachmentsList;
	}

	public void setAttachmentsList(List<AttachmentModel> attachmentsList) {
		this.attachmentsList = attachmentsList;
	}

	public boolean isAnonymousFlag() {
		return anonymousFlag;
	}

	public void setAnonymousFlag(boolean isAnonymous) {
		this.anonymousFlag = isAnonymous;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public String getTargetDescription() {
		return targetDescription;
	}

	public void setTargetDescription(String targetDescription) {
		this.targetDescription = targetDescription;
	}
}
