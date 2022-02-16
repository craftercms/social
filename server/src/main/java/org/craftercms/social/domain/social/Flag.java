/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.domain.social;

import org.bson.types.ObjectId;
import org.craftercms.commons.jackson.mvc.annotations.SecureProperty;
import org.jongo.marshall.jackson.oid.Id;

/**
 *
 */
public class Flag {

    @Id
    private ObjectId id;
    private String reason;
    @SecureProperty(role = {"SOCIAL_ADMIN","SOCIAL_MODERATOR","OWNER"})
    private String userId;

    public Flag() {
    }

    public Flag(final String reason, final String userId) {
        this.reason = reason;
        this.userId = userId;
        this.id=new ObjectId();
    }

    public Flag(final ObjectId id) {
        this.id = id;
    }

    public Flag(final ObjectId id, final String userId) {
        this.id = id;
        this.userId = userId;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(final ObjectId id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Flag{" +
            "id=" + id +
            ", reason='" + reason + '\'' +
            ", userId='" + userId + '\'' +
            '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Flag flag = (Flag)o;

        if (!id.equals(flag.id)) {
            return false;
        }
        return userId.equals(flag.userId);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + userId.hashCode();
        return result;
    }
}
