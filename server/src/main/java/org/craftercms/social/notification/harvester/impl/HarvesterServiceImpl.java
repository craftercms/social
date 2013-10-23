package org.craftercms.social.notification.harvester.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.craftercms.social.domain.NotificationMessage;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.Profile;
import org.craftercms.social.domain.UGCAudit;
import org.craftercms.social.domain.UGCAuditHarvest;
import org.craftercms.social.notification.harvester.HarvesterService;
import org.craftercms.social.repositories.UGCAuditHarvestRepository;
import org.craftercms.social.repositories.ProfileRepository;
import org.craftercms.social.repositories.UGCAuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HarvesterServiceImpl implements HarvesterService {
	
	private final transient Logger log = LoggerFactory.getLogger(HarvesterServiceImpl.class);
	
	private static final String ACTION = "email";
	private static final String PERIOD = "instant";
	private static final String FORMAT = "single";
	
	@Autowired
	private UGCAuditHarvestRepository uGCAuditHarvestRepository;
	@Autowired
	private ProfileRepository profileRepository;
	@Autowired
	private UGCAuditRepository ugcAuditRepository;
	
	private String action;
	private String period;
	private String format;
	
	private String jobId = "crafterharvester";
	
	public HarvesterServiceImpl() {
		action = ACTION;
		period = PERIOD;
		format = FORMAT;
	}
	
	@Override
	public void executeJob() {
		//log.debug("*********** GREAT EXECUTED");
		System.out.println("***** executeJob 1 " );
		UGCAuditHarvest uGCAuditHarvest = uGCAuditHarvestRepository.findUGCAuditHarvestByJobId(jobId);
		System.out.println("***** executeJob 2 " + uGCAuditHarvest);
		List<UGCAudit> listUGCAudit = null;
		if (uGCAuditHarvest == null) {
			System.out.println("***** executeJob finding all ");
			listUGCAudit = ugcAuditRepository.findAll();
		} else {
			System.out.println("***** executeJob finding something ");
			listUGCAudit = ugcAuditRepository.findByLastRetrievedRow(uGCAuditHarvest.getLastRetrievedRow());
		}
		Map<String, List<UGCAudit>> targetIdUGCAuditMap = new HashMap<String, List<UGCAudit>>();
		List<UGCAudit> lua;
		if (listUGCAudit != null) {
			System.out.println("***** executeJob iterating to create the map - list ");
			for(UGCAudit current: listUGCAudit) {
				 
				lua = targetIdUGCAuditMap.get(current.getTarget());
				if (lua == null) {
					lua = new ArrayList<UGCAudit>();
					targetIdUGCAuditMap.put(current.getTarget(), lua);
				}
				lua.add(current);
			}
			System.out.println("***** executeJob iterating by targets ");
			Set<String> targets = targetIdUGCAuditMap.keySet();
			//List<Profile> profileList = this.profileRepository.findProfilesBySubscriptions(targets);
			
			
			List<Profile> pl;
			List<NotificationMessage> lnm = new ArrayList<NotificationMessage>();
			NotificationMessage nm;
			for (String target: targets) {
				System.out.println("***** executeJob iterating " + target);
				 pl = this.profileRepository.findProfilesBySubscriptions(target, action, period, format);
				 if (pl != null && pl.size() > 0) {
					 nm = new NotificationMessage();
					 nm.setProfiles(pl);
					 nm.setTarget(target);
					 nm.setuGCAudits(targetIdUGCAuditMap.get(target));
					 lnm.add(nm);
				 }
				 
			}
			System.out.println("***** testing " + lnm.size());
			// SAVE NOTIFICATIONS to new table
			updateLastNotification(uGCAuditHarvest, listUGCAudit);
		}
		
		
	}
	
	private void updateLastNotification(UGCAuditHarvest uGCAuditHarvest, List<UGCAudit> listUGCAudit) {
		if (listUGCAudit == null || listUGCAudit.size() == 0) {
			return;
		}
		if (uGCAuditHarvest == null) {
			uGCAuditHarvest = new UGCAuditHarvest();
			uGCAuditHarvest.setJobId(jobId);
		}
		uGCAuditHarvest.setLastUpdatedDate(new Date());
		uGCAuditHarvest.setLastRetrievedRow(retrieveLastUGCAudit(listUGCAudit));
		
		this.uGCAuditHarvestRepository.save(uGCAuditHarvest);
	}

	private long retrieveLastUGCAudit(List<UGCAudit> listUGCAudit) {
		return listUGCAudit.get(listUGCAudit.size() - 1).getSequence();
		//return 0l;
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

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
