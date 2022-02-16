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

package org.craftercms.social.domain.audit;

import org.craftercms.commons.audit.AuditModel;
import org.craftercms.commons.mongo.Document;
import org.craftercms.social.domain.UGC;
import org.jongo.marshall.jackson.oid.Id;

/**
 *
 */
@Document(collectionName = "audit")
public class AuditLog extends AuditModel {

    private String contextId;
    private String userId;
    private String actionName;

    public <T extends UGC> AuditLog(T payload) {
        super();
        this.setPayload(payload);
    }

    @Override
    @Id
    public String getId() {
        return super.getId();
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(final String contextId) {
        this.contextId = contextId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(final String actionName) {
        this.actionName = actionName;
    }
}
