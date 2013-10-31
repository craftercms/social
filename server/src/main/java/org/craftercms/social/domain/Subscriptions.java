package org.craftercms.social.domain;

import java.util.List;

public class Subscriptions {
	
	List<String> frequency;
	List<String> action;
	List<String> format;
	List<String> target;
	public List<String> getFrequency() {
		return frequency;
	}
	public void setFrequency(List<String> frequency) {
		this.frequency = frequency;
	}
	public List<String> getAction() {
		return action;
	}
	public void setAction(List<String> action) {
		this.action = action;
	}
	public List<String> getFormat() {
		return format;
	}
	public void setFormat(List<String> format) {
		this.format = format;
	}
	public List<String> getTarget() {
		return target;
	}
	public void setTarget(List<String> target) {
		this.target = target;
	}
	

}
