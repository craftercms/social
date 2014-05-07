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
package org.craftercms.social.notification.harvester.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.social.domain.Event;
import org.craftercms.social.domain.Notification;
import org.craftercms.social.domain.Notification.TransmittedStatus;
import org.craftercms.social.domain.Subscriptions;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.UGCAudit;
import org.craftercms.social.exceptions.AuditException;
import org.craftercms.social.notification.harvester.BaseHarvesterService;
import org.craftercms.social.repositories.NotificationRepository;
import org.craftercms.social.repositories.UGCAuditRepository;
import org.craftercms.social.repositories.UGCRepository;
import org.craftercms.social.util.PageManagement;
import org.craftercms.social.util.support.CrafterProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * UGCAudit harvester implementation.
 * <p/>
 * This harvester reads ugcAudit entries since the last read, and creates entries in the notifications collection.
 */
@Component
public class UGCAuditHarvesterServiceImpl extends BaseHarvesterService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private CrafterProfileService crafterProfileService;
    @Autowired
    private UGCAuditRepository ugcAuditRepository;
    @Autowired
    private UGCRepository ugcRepository;

    private int pageSize;

    private PageManagement pageManagement;

    public UGCAuditHarvesterServiceImpl() {
        this.pageSize = DEFAULT_PAGE_SIZE;
        this.pageManagement = new PageManagement();
        this.actionFilters = new ArrayList<String>();
    }

    @Override
    public void doHarvestInternal(Map<String, ?> harvesterProperties) throws AuditException {
        long lastRowRetrieved = getLastRowRetrieved((Map<String, ? super Serializable>)harvesterProperties);

        // GET Audits using last retrieved row
        boolean isDone = initPageManagement(lastRowRetrieved);
        //page = 0;
        Map<String, List<UGCAudit>> targetIdUGCAuditMap;
        Iterable<UGCAudit> listUGCAudit;
        Map<String, Profile> actionOwnersCache = new HashMap<String, Profile>();

        while (!isDone) {
            listUGCAudit = findUGCAuditList(lastRowRetrieved, pageManagement.getStart(), pageManagement.getEnd());
            if (listUGCAudit != null) {
                log.debug("Audit harvester found audits");

                // Sets audits into a map -> target / Audits
                targetIdUGCAuditMap = flatAuditsByTarget(listUGCAudit);

                createNotifications(actionOwnersCache, targetIdUGCAuditMap);

                //update the last row retrieved and check if there will be more entries to process
                isDone = updateLastRowAndPageManagement(convertToList(listUGCAudit), (Map<String,
                    ? super Serializable>)harvesterProperties);

            } else {
                isDone = true;
            }
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    private void createNotifications(Map<String, Profile> actionOwnersCache, Map<String,
        List<UGCAudit>> targetIdUGCAuditMap) throws AuditException {
        Set<String> targets = targetIdUGCAuditMap.keySet();
        List<Profile> pl;

        for (String target : targets) {
            // Gets subscribers by a target, an action, a frequency and format
            pl = this.crafterProfileService.findProfilesBySubscriptions(target);

            if (log.isDebugEnabled()) {
                log.debug("Audit harvester found " + pl.size() + " subscribers for target " + target);
            }

            // saves notifications
            createNotifications(pl, targetIdUGCAuditMap.get(target), actionOwnersCache);

        }
    }

    private boolean updateLastRowAndPageManagement(List<UGCAudit> listUGCAudit, Map<String,
        ? super Serializable> harvesterProperties) {
        boolean isDone = false;
        updateLastRowRetrieved(listUGCAudit, (Map<String, ? super Serializable>)harvesterProperties);
        if (pageManagement.isLastPage()) {
            isDone = true;
        } else {

            pageManagement.next();
        }
        return isDone;
    }

    private Long getLastRowRetrieved(Map<String, ? super Serializable> harvesterProperties) {
        Long lastRowRetrieved = null;
        if (harvesterProperties == null || harvesterProperties.get(LAST_ROW_RETRIEVED) == null) {
            if (harvesterProperties == null) {
                harvesterProperties = new HashMap<String, Serializable>();
            }
            lastRowRetrieved = new Long(0);
            harvesterProperties.put(LAST_ROW_RETRIEVED, lastRowRetrieved);
        } else {
            lastRowRetrieved = (Long)harvesterProperties.get(LAST_ROW_RETRIEVED);
        }
        return lastRowRetrieved;
    }

    private Map<String, List<UGCAudit>> flatAuditsByTarget(Iterable<UGCAudit> listUGCAudit) {
        List<UGCAudit> lua;
        Map<String, List<UGCAudit>> targetIdUGCAuditMap = new HashMap<String, List<UGCAudit>>();
        //flatten audits by target
        for (UGCAudit current : listUGCAudit) {
            lua = targetIdUGCAuditMap.get(current.getTarget().getTargetId());
            if (lua == null) {
                lua = new ArrayList<>();
                targetIdUGCAuditMap.put(current.getTarget().getTargetId(), lua);
            }
            lua.add(current);
        }
        return targetIdUGCAuditMap;
    }

    private boolean initPageManagement(long lastRowRetrieve) throws AuditException {
        boolean isDone = false;

        pageManagement.setStart(0);
        pageManagement.setPageSize(this.pageSize);
        long total = 0;
        try {
            total = this.ugcAuditRepository.count(lastRowRetrieve, this.getActionFiltersAsStringArray());
        } catch (MongoDataException e) {
            log.error("Unable to count Audit UGC's", e);
            throw new AuditException("Unable to count UGCs", e);
        }
        pageManagement.setTotal(total);
        if (total == 0) {
            isDone = true;
        }

        return isDone;
    }

    /**
     * Persist notifications
     *
     * @param pl     Susbcriptors for each event
     * @param audits The events
     */
    private void createNotifications(List<Profile> pl, List<UGCAudit> audits, Map<String,
        Profile> actionOwnersCache) throws AuditException {
        if (pl == null || pl.size() == 0) {
            return;
        }

        for (UGCAudit currentAudit : audits) {
            UGC ugc = ugcRepository.findOne(currentAudit.getUgcId().toString());
            if (ugc != null) {
                UGC.ModerationStatus modStatus = ugc.getModerationStatus();
                if (modStatus != UGC.ModerationStatus.SPAM && modStatus != UGC.ModerationStatus.TRASH) {
                    for (Profile profile : pl) {
                        if (log.isDebugEnabled()) {
                            log.debug("Audit harvester creating notification event ROW " + currentAudit.getRow() +
                                " for the subscriber: " + profile.getUserName());
                        }

                        createNotification(profile, currentAudit, actionOwnersCache);
                    }
                }
            }
        }
    }

    private void createNotification(Profile profile, UGCAudit currentAudit, Map<String,
        Profile> actionOwnersCache) throws AuditException {
        if (currentAudit.getProfileId() != null && !currentAudit.getProfileId().equalsIgnoreCase("anonymous")) {
            Notification notification = new Notification();
            // ACTION of the current JOB
            notification.setAction(Subscriptions.getFromAttributes(profile.getAttributes()).getAction());

            notification.setCreatedDate(new Date());
            notification.setRow(currentAudit.getRow());

            //FORMAT of the current JOB
            notification.setFormat(Subscriptions.getFromAttributes(profile.getAttributes()).getFormat());

            //FORMAT of the current JOB
            notification.setFrequency(Subscriptions.getFromAttributes(profile.getAttributes()).getFrequency());
            notification.setSubscriberUsername(profile.getUserName());

            notification.setSubscriberEmail(profile.getEmail());
            notification.setSubscriberId(new ObjectId(profile.getId()));
            notification.setTransmitedStatus(TransmittedStatus.PENDING);

            notification.setEvent(createEvent(currentAudit, getActionAuditOwner(actionOwnersCache, currentAudit)));

            try {
                this.notificationRepository.save(notification);
            } catch (MongoDataException e) {
                log.error("Unable to save Audit UGc", e);
                throw new AuditException("Unable to Save AuditUGC", e);
            }
        }
    }

    private Profile getActionAuditOwner(Map<String, Profile> actionOwnersCache, UGCAudit currentAudit) {
        Profile p = actionOwnersCache.get(currentAudit.getProfileId());

        if (p == null) {
            Profile currentProfile = this.crafterProfileService.getProfile(currentAudit.getProfileId());
            p = new Profile();
            if (currentProfile != null) {
                p.setUserName(currentProfile.getUserName());
                p.setTenantName(currentProfile.getTenantName());
                p.setEmail(currentProfile.getEmail());
                p.setId(currentProfile.getId());
            }
            actionOwnersCache.put(currentAudit.getProfileId(), p);
        }

        return p;
    }

    private Event createEvent(UGCAudit currentAudit, Profile actionOwner) {
        Event event = new Event();
        event.setAction(currentAudit.getAction());
        event.setProfile(actionOwner);
        event.setTarget(currentAudit.getTarget());
        event.setUgcId(currentAudit.getUgcId());
        event.setTenantName(currentAudit.getTenant());
        event.setAuditDate(currentAudit.getCreatedDate());
        return event;
    }

    private Iterable<UGCAudit> findUGCAuditList(long lastRowRetrieved, int page, int pageSize) throws AuditException {
        try {
            return ugcAuditRepository.findByLastRetrievedRow(lastRowRetrieved, page, pageSize,
                this.getActionFiltersAsStringArray());
        } catch (MongoDataException e) {
            log.error("Unable to find Audit UGCs", e);
            throw new AuditException("Unable to find audit UGC", e);
        }
    }


    private void updateLastRowRetrieved(List<UGCAudit> listUGCAudit, Map<String,
        ? super Serializable> harvesterProperties) {
        if (listUGCAudit != null && listUGCAudit.size() > 0) {
            Long lastRowRetrived = listUGCAudit.get(listUGCAudit.size() - 1).getRow();
            harvesterProperties.put(LAST_ROW_RETRIEVED, lastRowRetrived);
        }
    }
    // This is done since I have no idea how harvester works
    // This should be remove
    private List<UGCAudit> convertToList(Iterable<UGCAudit> auditIterator) {
        ArrayList<UGCAudit> auditList = new ArrayList<>();
        for (UGCAudit ugcAudit : auditIterator) {
            auditList.add(ugcAudit);
        }
        return auditList;
    }

}
