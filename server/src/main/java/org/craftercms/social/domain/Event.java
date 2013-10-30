package org.craftercms.social.domain;

import java.util.Date;

import org.craftercms.social.domain.UGCAudit.AuditAction;

public class Event {
	
	private Target target;
	
	private AuditAction action;
	
	private Profile profile;
	
	private Object ugcId;
	
	private Date auditDate;
	
	private String tenantName;
	
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

	public Object getUgcId() {
		return ugcId;
	}

	public void setUgcId(Object ugcId) {
		this.ugcId = ugcId;
	}

	public Date getAuditDate() {
		return auditDate;
	}

	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

}
