package org.craftercms.social.domain;

import java.util.ArrayList;


public class Subscriptions {
	
	private String frequency;
	private String action;
	private String format;
	private ArrayList<Target> subscription;
	
	public Subscriptions() {
		this.subscription =  new ArrayList<Target>();
	}
	
	
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
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
	public ArrayList<Target> getSubscription() {
		return subscription;
	}
	public void setSubscription(ArrayList<Target> subscription) {
		this.subscription = subscription;
	}
	
	public void addSubscription(Target target) {
		this.subscription.add(target);
	}
	
	public void removeSubscription(Target target) {
		this.subscription.remove(target);
	}
	

}
