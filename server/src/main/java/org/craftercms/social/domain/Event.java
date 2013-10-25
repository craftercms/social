package org.craftercms.social.domain;

import org.craftercms.social.domain.UGCAudit.AuditAction;

public class Event {
	
	private Target target;
	
	private AuditAction action;
	
	private Profile profile;

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public AuditAction getAction() {
		return action;
	}

	public void setAction(AuditAction action) {
		this.action = action;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

}
