package org.craftercms.social.domain;

public class Target {
	
	private String id;
	private String description;
	private String url;
	public Target() {
		
	}
	public Target(String id, String description, String url) {
		this.id = id;
		this.description = description;
		this.url = url;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	

}
