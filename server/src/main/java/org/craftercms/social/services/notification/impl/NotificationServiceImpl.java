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

package org.craftercms.social.services.notification.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.permissions.annotations.HasPermission;
import org.craftercms.social.domain.audit.AuditLog;
import org.craftercms.social.domain.notifications.WatchedThread;
import org.craftercms.social.exceptions.NotificationException;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.repositories.system.AuditRepository;
import org.craftercms.social.repositories.system.AuditRepositoryImpl;
import org.craftercms.social.repositories.system.notifications.WatchedThreadsRepository;
import org.craftercms.social.repositories.system.notifications.WatchedThreadsRepositoryImpl;
import org.craftercms.social.security.SocialPermission;
import org.craftercms.social.services.notification.NotificationService;
import org.craftercms.social.util.LoggerFactory;

import static org.craftercms.social.security.SecurityActionNames.UGC_READ;

/**
 *
 */
public class NotificationServiceImpl implements NotificationService {

    private AuditRepository auditRepository;
    private WatchedThreadsRepository watchedThreadsRepository;
    private I10nLogger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public void subscribeUser(final String userId, final String threadId, final String frequency) throws
        NotificationException {
        try {
            WatchedThread thread = watchedThreadsRepository.findByStringId(threadId);
            if (thread == null) {
                log.debug("logging.system.notification.creatingSubscription", threadId);
                thread = new WatchedThread();
                thread.setThreadId(threadId);
                watchedThreadsRepository.save(thread);
            }
            log.debug("logging.system.notification.adding", userId, threadId, frequency);
            watchedThreadsRepository.addWatcher(thread.getThreadId(), userId, frequency);
        } catch (MongoDataException e) {
            throw new NotificationException("Unable to subscribe User", e);
        }
    }


    @Override
    @HasPermission(action = UGC_READ, type = SocialPermission.class)
    public void unSubscribeUser(final String userId, final String threadId) throws NotificationException {
        try {
            WatchedThread thread = watchedThreadsRepository.findByStringId(threadId);
            if (thread != null) {
                watchedThreadsRepository.removeWatcher(thread.getThreadId(), userId);
            }
            log.debug("logging.system.notification.remove", userId, threadId);
        } catch (MongoDataException e) {
            throw new NotificationException("Unable to subscribe User", e);
        }
    }

    @Override
    public boolean isBeenWatch(final String ugcId, final String profileId) throws NotificationException {
        try {
            WatchedThread thread = watchedThreadsRepository.isUserSubscribe(ugcId, profileId);
            return thread != null;
        } catch (MongoDataException e) {
            throw new NotificationException("Unable to Check if user is subscribe", e);
        }
    }

    @Override
    public void notify(final String type) {
        final Date from = getStartDateByType(type);
        final Date to = new Date();
        try {
            List<AuditLog> toNotify = auditRepository.getByDate(from, to);


        }catch (SocialException ex){
            log.error("Unable to send notifications",ex);
        }

    }

    protected Date getStartDateByType(final String type) {
        Calendar cal= Calendar.getInstance();
        if (type.equalsIgnoreCase(NotificationService.WEEKLY)) {
            cal.add(Calendar.WEEK_OF_YEAR,-1);
            return cal.getTime();
        } else if (type.equalsIgnoreCase(NotificationService.DAILY)) {
            cal.add(Calendar.DAY_OF_MONTH,-1);
            return cal.getTime();
        } else {
            return null;
        }
    }


    public void setAuditRepository(AuditRepositoryImpl auditRepository) {
        this.auditRepository = auditRepository;
    }

    public void setWatchedThreadsRepository(WatchedThreadsRepositoryImpl watchedThreadsRepository) {
        this.watchedThreadsRepository = watchedThreadsRepository;
    }


}
