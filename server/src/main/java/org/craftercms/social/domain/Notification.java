package org.craftercms.social.domain;

import java.util.Date;

import org.bson.types.ObjectId;

public class Notification {
	
	public enum TransmittedStatus {
		PROCESSING("processing"), PROCESSED("processed"), PENDING("pending");
		private String name;

		private TransmittedStatus(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}
	
	private ObjectId id;
	
	private String frequency;
	
	private String action;
	
	private String format;
	
	private Date createdDate;
	
	private ObjectId subscriberId;
	
	private String subscriberEmail;
	
	private Event event;
	
	private TransmittedStatus transmitedStatus;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public ObjectId getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(ObjectId subscriberId) {
		this.subscriberId = subscriberId;
	}

	public String getSubscriberEmail() {
		return subscriberEmail;
	}

	public void setSubscriberEmail(String subscriberEmail) {
		this.subscriberEmail = subscriberEmail;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public TransmittedStatus getTransmitedStatus() {
		return transmitedStatus;
	}

	public void setTransmitedStatus(TransmittedStatus transmitedStatus) {
		this.transmitedStatus = transmitedStatus;
	}

}
