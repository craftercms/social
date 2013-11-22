package org.craftercms.social.domain;


public class Target {
	
	private String targetId;
	
	private String targetDescription;
	
	private String targetUrl;

	public Target() {
		
	}

	public Target(String id, String description, String url) {
		this.targetId = id;
		this.targetDescription = description;
		this.targetUrl = url;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getTargetDescription() {
		return targetDescription;
	}

	public void setTargetDescription(String description) {
		this.targetDescription = description;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

}
