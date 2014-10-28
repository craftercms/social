/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.repositories.system.notifications.impl;


import com.mongodb.MongoException;

import java.util.List;

import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.AbstractJongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.notifications.ThreadsToNotify;
import org.craftercms.social.domain.notifications.WatchedThread;
import org.craftercms.social.exceptions.NotificationException;
import org.craftercms.social.repositories.system.notifications.WatchedThreadsRepository;
import org.jongo.Aggregate;

/**
 *
 */
public class WatchedThreadsRepositoryImpl extends AbstractJongoRepository<WatchedThread> implements WatchedThreadsRepository {

    @Override
    public void removeWatcher(final String thread, final String userId) throws NotificationException {
        try {
            final String pullQuery = getQueryFor("social.notifications.removeUserWatch");
            final String find = getQueryFor("social.notifications.findById");
            getCollection().findAndModify(find, thread).with(pullQuery, userId).as(clazz);
        } catch (MongoException ex) {
            throw new NotificationException("Unable to removeWatcher Watched Thread", ex);
        }
    }

    @Override
    public void addWatcher(final String thread, final String userId, final String frequency) throws
        NotificationException {
        try {
            final String addQuery = getQueryFor("social.notifications.addUserWatch");
            final String find = getQueryFor("social.notifications.findById");
            getCollection().findAndModify(find, thread).with(addQuery, userId, frequency).as(clazz);
        } catch (MongoException ex) {
            throw new NotificationException("Unable to add Watcher Watched Thread", ex);
        }
    }

    @Override
    public WatchedThread isUserSubscribe(final String threadId, final String profileId) throws MongoDataException {
        final String query = getQueryFor("social.notifications.isBeenWatched");
        return findOne(query, threadId, profileId);
    }

    @Override
    public Iterable<WatchedThread> findAllWithWatchers() throws NotificationException {
        try {
            final String query = getQueryFor("social.notifications.byWatchersNotEmpty");
            return find(query);
        } catch (MongoDataException ex) {
            throw new NotificationException("Unable to find threads with watchers", ex);
        }
    }

    @Override
    public List<ThreadsToNotify> findProfilesToSend(final String type) throws
        NotificationException {
        try{
            String aggregationQuerypt1 = getQueryFor("social.notification.getProfilePt1");
            String aggregationQuerypt2 = getQueryFor("social.notification.getProfilePt2");
            String aggregationQuerypt3 = getQueryFor("social.notification.getProfilePt3");
            String aggregationQuerypt4 = getQueryFor("social.notification.getProfilePt4");
            final Aggregate aggregation = getCollection().aggregate(aggregationQuerypt1);
            aggregation.and(aggregationQuerypt2).and(aggregationQuerypt3,type).and(aggregationQuerypt4);
            return aggregation.as(ThreadsToNotify.class);
        }catch (MongoException ex){
            throw new NotificationException("Unable to find Profiles to notify", ex);
        }
    }

}
