package org.craftercms.social.notification.harvester.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.craftercms.social.domain.Event;
import org.craftercms.social.domain.HarvestStatus;
import org.craftercms.social.domain.Notification;
import org.craftercms.social.domain.Notification.TransmittedStatus;
import org.craftercms.social.domain.Profile;
import org.craftercms.social.domain.UGCAudit;
import org.craftercms.social.notification.harvester.HarvesterService;
import org.craftercms.social.repositories.HarvestStatusRepository;
import org.craftercms.social.repositories.NotificationRepository;
import org.craftercms.social.repositories.ProfileRepository;
import org.craftercms.social.repositories.UGCAuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HarvesterServiceImpl implements HarvesterService {
	
	private final transient Logger log = LoggerFactory.getLogger(HarvesterServiceImpl.class);
	
	@Autowired
	private HarvestStatusRepository harvestStatusRepository;
	@Autowired
	private NotificationRepository notificationRepository;
	@Autowired
	private ProfileRepository profileRepository;
	@Autowired
	private UGCAuditRepository ugcAuditRepository;
	
	private String action;
	private String frequency;
	private String format;
	
	private String applicationId;
	private String collectionName;
	
	private String jobId;
	
	public HarvesterServiceImpl() {
		jobId = DEFAULT_JOB_ID;
		action = ACTION;
		frequency = FREQUENCY;
		format = FORMAT;
		this.applicationId = APPLICATION_ID;
		this.collectionName = COLLECTION_NAME;
	}
	
	@Override
	public void harvest() {
		log.debug("Starting harvester JOB " + jobId);
		// GET Harvest status
		HarvestStatus harvestStatus = harvestStatusRepository.findHarvestStatusByJobId(jobId);
		// Update Harvest status to RUNNING
		updateLastNotification(harvestStatus, null, HARVESTER_STATUS_RUNNING);
		// GET Audits using last retrieved row
		List<UGCAudit> listUGCAudit = findUGCAuditList(harvestStatus);
		if (listUGCAudit != null && listUGCAudit.size() > 0) {
			log.debug("Harvester JOB found audits " + listUGCAudit.size());
			// Sets audits into a map -> target / Audits
			Map<String, List<UGCAudit>> targetIdUGCAuditMap = flatAuditsByTarget(listUGCAudit);
			
			Set<String> targets = targetIdUGCAuditMap.keySet();
			List<Profile> pl;
			
			for (String target: targets) {
				// Gets Susbcriptors by a target, an action, a frequency and format
				pl = this.profileRepository.findProfilesBySubscriptions(target, action, frequency, format);
				log.debug("Harvester JOB found " + pl.size() + " subscribers for target " + target);
				
				// saves notifications
				createNotifications(pl, targetIdUGCAuditMap.get(target));
				 
			}
			// Update Harvest status to IDLE and update the last row retrieve
			updateLastNotification(harvestStatus, listUGCAudit, HARVESTER_STATUS_IDLE);
		}
		
		
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

	private List<UGCAudit> findUGCAuditList(HarvestStatus harvestStatus) {
		List<UGCAudit> listUGCAudit = null;
		if (harvestStatus == null) {
			listUGCAudit = ugcAuditRepository.findAll();
		} else {
			listUGCAudit = ugcAuditRepository.findByLastRetrievedRow(harvestStatus.getLastRowRetrieved());
		}
		return  listUGCAudit;
	}

	private void updateLastNotification(HarvestStatus harvestStatus, List<UGCAudit> listUGCAudit, String status) {
		if (harvestStatus == null) {
			harvestStatus = new HarvestStatus();
			harvestStatus.setId(jobId);
			harvestStatus.setApplicationId(applicationId);
			harvestStatus.setCollectionName(collectionName);
		}
		
		
		harvestStatus.setStatus(status);
		if (listUGCAudit != null && listUGCAudit.size() > 0) {
			harvestStatus.setLastRowRetrieved(retrieveLastUGCAudit(listUGCAudit));
		}
		
		this.harvestStatusRepository.save(harvestStatus);
	}

	private long retrieveLastUGCAudit(List<UGCAudit> listUGCAudit) {
		return listUGCAudit.get(listUGCAudit.size() - 1).getRow();
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
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

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

}
