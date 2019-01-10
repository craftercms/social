/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
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

package org.craftercms.social.repositories.system.notifications;

import java.util.List;
import java.util.Map;

import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.notifications.ProfileWatchOptions;
import org.craftercms.social.domain.notifications.ThreadsToNotify;
import org.craftercms.social.domain.notifications.WatchedThread;
import org.craftercms.social.exceptions.NotificationException;
import org.craftercms.social.exceptions.SocialException;

/**
 *
 */
public interface WatchedThreadsRepository extends CrudRepository<WatchedThread> {
     void removeWatcher(final String thread, final String userId) throws NotificationException;
     void addWatcher(final String thread, final String userId, final String frequency) throws NotificationException ;
     WatchedThread isUserSubscribe(final String threadId, final String profileId) throws MongoDataException;
     Iterable<WatchedThread> findAllWithWatchers() throws NotificationException;
     List<ThreadsToNotify> findProfilesToSend(final String type) throws NotificationException;
     List<Map> findUserWatchedThreads(final String profileId) throws SocialException;

}
