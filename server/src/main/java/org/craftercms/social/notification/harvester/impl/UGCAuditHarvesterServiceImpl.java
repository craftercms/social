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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.craftercms.social.domain.Event;
import org.craftercms.social.domain.Notification;
import org.craftercms.social.domain.Notification.TransmittedStatus;
import org.craftercms.social.domain.Profile;
import org.craftercms.social.domain.UGCAudit;
import org.craftercms.social.notification.harvester.BaseHarvesterService;
import org.craftercms.social.repositories.NotificationRepository;
import org.craftercms.social.repositories.ProfileRepository;
import org.craftercms.social.repositories.UGCAuditRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * UGCAudit harvester implementation.
 *
 * This harvester reads ugcAudit entries since the last read, and creates entries in the notifications collection.
 */
@Component
public class UGCAuditHarvesterServiceImpl extends BaseHarvesterService {


    // These should be dynamically calculated
    static final String ACTION = "email";
    static final String FREQUENCY = "instant";
    static final String FORMAT = "single";

    // TODO: These need to be passed in as parameters and are specific to the implementation
    static final String APPLICATION_ID = "crafter-social";
    static final String COLLECTION_NAME = "uGCAudit";
    static final String DEFAULT_JOB_ID = "crafter-social-harvester";

	@Autowired
	private NotificationRepository notificationRepository;
	@Autowired
	private ProfileRepository profileRepository;
	@Autowired
	private UGCAuditRepository ugcAuditRepository;

	
	private String action;
	private String frequency;
	private String format;

	
	public UGCAuditHarvesterServiceImpl() {

        // TODO: collectionName
//		jobId = DEFAULT_JOB_ID;
//		action = ACTION;
//		frequency = FREQUENCY;
//		format = FORMAT;
//		this.applicationId = APPLICATION_ID;
//		this.collectionName = COLLECTION_NAME;
	}
	
	@Override
	public void doHarvestInternal(Map<String, ?> harvesterProperties) {

        // GET Audits using last retrieved row
		List<UGCAudit> listUGCAudit = findUGCAuditList(getLastRowRetrieved(harvesterProperties));
		if (listUGCAudit != null && listUGCAudit.size() > 0) {
			log.debug("Harvester JOB found audits " + listUGCAudit.size());
			// Sets audits into a map -> target / Audits
			Map<String, List<UGCAudit>> targetIdUGCAuditMap = flatAuditsByTarget(listUGCAudit);
			
			Set<String> targets = targetIdUGCAuditMap.keySet();
			List<Profile> pl;
			
			for (String target: targets) {
				// Gets subscribers by a target, an action, a frequency and format
				pl = this.profileRepository.findProfilesBySubscriptions(target, action, frequency, format);
				log.debug("Harvester JOB found " + pl.size() + " subscribers for target " + target);
				
				// saves notifications
				createNotifications(pl, targetIdUGCAuditMap.get(target));
				 
			}

		}

        // TODO: Set the updated last row retrieved in the properties
		
	}

    private Long getLastRowRetrieved(Map<String, ?> harvesterProperties) {
        Long lastRowRetrieved = null;
        if (harvesterProperties == null || harvesterProperties.get(LAST_ROW_RETRIEVED) == null) {
            lastRowRetrieved = new Long(0);
        } else {
            lastRowRetrieved =  (Long) harvesterProperties.get(LAST_ROW_RETRIEVED);
        }
        return lastRowRetrieved;
    }

    private Map<String, List<UGCAudit>> flatAuditsByTarget(List<UGCAudit> listUGCAudit) {
		List<UGCAudit> lua;
		Map<String, List<UGCAudit>> targetIdUGCAuditMap = new HashMap<String, List<UGCAudit>>();
		//flatten audits by target
		for(UGCAudit current: listUGCAudit) {
			lua = targetIdUGCAuditMap.get(current.getTarget());
			if (lua == null) {
				lua = new ArrayList<UGCAudit>();
				targetIdUGCAuditMap.put(current.getTarget().getId(), lua);
			}
			lua.add(current);
		}
		return targetIdUGCAuditMap;
	}

	/**
	 * Persist notifications
	 * 
	 * @param pl Susbcriptors for each event
	 * 
	 * @param audits The events
	 */
	private void createNotifications(List<Profile> pl, List<UGCAudit> audits) {
		if (pl == null || pl.size() == 0) {
			return;
		}
		for (UGCAudit currentAudit: audits) {
			for (Profile profile: pl) {
				createNotification(profile, currentAudit);
			}
		}
	}

	private Notification createNotification(Profile profile,
			UGCAudit currentAudit) {
		Notification notification = new Notification();
		// ACTION of the current JOB
		notification.setAction(this.action);
		//notification.setAction(profile.getSubscriptions().getAction().get(0));
		notification.setCreatedDate(new Date());
		//notification.setFormat(profile.getSubscriptions().getFormat().get(0));
		//FORMAT of the current JOB
		notification.setAction(this.format);
		
		//notification.setFrequency(profile.getSubscriptions().getFrequency().get(0));
		//FORMAT of the current JOB
		notification.setFrequency(this.frequency);
		
		notification.setSubscriberEmail(profile.getEmail());
		notification.setSubscriberId(profile.getId());
		notification.setTransmitedStatus(TransmittedStatus.PENDING);
		notification.setEvent(createEvent(profile,currentAudit));
		
		this.notificationRepository.save(notification);
		
		return notification;
	}

	private Event createEvent(Profile profile, UGCAudit currentAudit) {
		Event event = new Event();
		event.setAction(currentAudit.getAction());
		event.setProfile(profile);
		event.setTarget(currentAudit.getTarget());
		return event;
	}

	private List<UGCAudit> findUGCAuditList(long lastRowRetrieved) {
		List<UGCAudit> listUGCAudit = null;
		//if (harvestStatus == null) {                  // TODO: Discuss when this would happen
		//	listUGCAudit = ugcAuditRepository.findAll();
		//} else {
			listUGCAudit = ugcAuditRepository.findByLastRetrievedRow(lastRowRetrieved);
		//}
		return  listUGCAudit;
	}


    @Override
    protected Class getCollectionClassName() {
        return UGCAuditRepository.class;
    }

	private long retrieveLastUGCAudit(List<UGCAudit> listUGCAudit) {
		return listUGCAudit.get(listUGCAudit.size() - 1).getRow();
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
