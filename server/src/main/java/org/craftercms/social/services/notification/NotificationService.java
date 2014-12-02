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
package org.craftercms.social.services.notification;

import java.util.List;
import java.util.Map;

import org.craftercms.profile.api.Profile;
import org.craftercms.social.domain.notifications.WatchedThread;
import org.craftercms.social.exceptions.NotificationException;
import org.craftercms.social.exceptions.SocialException;

/**
 *
 */
public interface NotificationService {

    public static final String WEEKLY = "weekly";
    public static final String DAILY = "daily";
    public static final String INSTANT = "instant";

    void subscribeUser(final Profile profile, final String threadId, final String type) throws NotificationException;

    void notify(final String type);

    void unSubscribeUser(final String userId, String threadId) throws NotificationException;

    boolean isBeenWatch(final String threadId, final String profileId) throws NotificationException;

    List<Map> getUserSubscriptions() throws SocialException;

    void changeSubscription(Profile p, String threadId, String frequency) throws NotificationException;
}
