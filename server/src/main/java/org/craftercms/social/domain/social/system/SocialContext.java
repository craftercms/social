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

package org.craftercms.social.domain.social.system;

import java.util.UUID;

import org.craftercms.commons.mongo.Document;
import org.jongo.marshall.jackson.oid.Id;

/**
 * Context Object.
 */
@Document(collectionName = "socialContext")
public class SocialContext {

    @Id
    private String id;
    private String contextName;

    public SocialContext(final String contextName) {
        this.contextName = contextName;
        this.id= UUID.randomUUID().toString();
    }

    public SocialContext() {
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(final String contextName) {
        this.contextName = contextName;
    }
}
