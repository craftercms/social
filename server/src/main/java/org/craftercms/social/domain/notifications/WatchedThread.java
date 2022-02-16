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

package org.craftercms.social.domain.notifications;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.mongo.Document;
import org.craftercms.social.util.LoggerFactory;
import org.jongo.marshall.jackson.oid.Id;

/**
 *
 */
@Document(collectionName = "watchList")
public class WatchedThread {
    @Id
    private String threadId;
    private Set<ProfileWatchOptions> watchers;
    @JsonIgnore
    private I10nLogger log = LoggerFactory.getLogger(WatchedThread.class);

    public WatchedThread() {
        watchers = new HashSet<>();
    }

    public void addWatcher(final String profileId, final String frequency) {
        final ProfileWatchOptions toAdd = new ProfileWatchOptions(profileId, frequency);
        if (!watchers.contains(toAdd)) {
            watchers.add(toAdd);
            log.debug("logging.system.notification.userAddedWatching", profileId, threadId, frequency);
        } else {
            log.debug("logging.system.notification.userAlreadyWatching", profileId, threadId, frequency);
        }
    }

    public void removeWatcher(final String profileId, final String frequency) {
        if (watchers.remove(new ProfileWatchOptions(profileId, frequency))) {
            log.debug("logging.system.notification.userRemoveWatching", profileId, threadId, frequency);
        } else {
            log.debug("logging.system.notification.userUnableToRemove", profileId, threadId, frequency);
        }
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(final String threadId) {
        this.threadId = threadId;
    }

    public Set<ProfileWatchOptions> getWatchers() {
        return watchers;
    }

    public void setWatchers(final Set<ProfileWatchOptions> watchers) {
        this.watchers = watchers;
    }

    @Override
    public String toString() {
        return "WatchedThreads{" +
            "threadId=" + threadId +
            ", watchers=" + watchers +
            '}';
    }
}
