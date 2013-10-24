package org.craftercms.social.domain;

import java.util.List;
import java.util.Map;

public class NotificationMessage {
	
	private String target;
	
	List<UGCAudit> uGCAudits;
	
	private List<Profile> profiles;

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

//	public Map<String, List<UGCAudit>> getTargetIdUGCMap() {
//		return targetIdUGCMap;
//	}
//
//	public void setTargetIdUGCMap(Map<String, List<UGCAudit>> targetIdUGCMap) {
//		this.targetIdUGCMap = targetIdUGCMap;
//	}

	public List<Profile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
	}

	public List<UGCAudit> getuGCAudits() {
		return uGCAudits;
	}

	public void setuGCAudits(List<UGCAudit> uGCAudits) {
		this.uGCAudits = uGCAudits;
	}

}
