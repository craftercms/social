/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
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

package org.craftercms.social.domain.system;

import java.util.Map;

import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.Document;
import org.jongo.marshall.jackson.oid.Id;

/**
 *
 */
@Document(collectionName = "preferences")
public class ContextPreferences {

    @Id
    private String contextId;
    private Map<String,Object> preferences;


    public ContextPreferences() {
    }

    public ContextPreferences(final String contextId, final Map<String, Object> preferences) {
        this.contextId = contextId;
        this.preferences = preferences;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(final String contextId) {
        this.contextId = contextId;
    }

    public Map<String, Object> getPreferences() {
        return preferences;
    }

    public void setPreferences(final Map<String, Object> preferences) {
        this.preferences = preferences;
    }

    @Override
    public String toString() {
        return "ContextPreferences{" +
            "contextId='" + contextId + '\'' +
            ", preferences=" + preferences +
            '}';
    }
}
