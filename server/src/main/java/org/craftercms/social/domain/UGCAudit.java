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

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ugcaudit")
@XmlRootElement
public class UGCAudit {

	public enum AuditAction {
		LIKE("like"), DISLIKE("dislike"), FLAG("flag"), CREATE("create"), UPDATE("update"), DELETE("delete"), MODERATE("moderate");
		private String name;

		private AuditAction(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private ObjectId id;

	private ObjectId ugcId;

    private String tenant;

	private String profileId;

	private AuditAction action;
	
	private String reason;
	
	public UGCAudit() {
		this(null,null,null,null,null);
	}

	public UGCAudit(ObjectId ugcId, String tenant, String profileId,
			AuditAction action, String reason) {
		super();
        this.tenant = tenant;
		this.ugcId = ugcId;
		this.profileId = profileId;
		this.action = action;
		this.reason = reason;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getUgcId() {
		return ugcId;
	}

	public void setUgcId(ObjectId ugcId) {
		this.ugcId = ugcId;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public AuditAction getAction() {
		return action;
	}

	public void setAction(AuditAction action) {
		this.action = action;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	@XmlElement
	public Date getDateCreated() {
		return new Date(id.getTime());
	}

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }


}