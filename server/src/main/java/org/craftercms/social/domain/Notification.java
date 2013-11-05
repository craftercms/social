package org.craftercms.social.domain;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

/*@CompoundIndexes({
    @CompoundIndex(name = "frequency_transmitedStatus_row_idx", def = "{'frequency': 1, 'transmitedStatus': 1,'row': 1}", unique = true)
    ,
    @CompoundIndex(name = "tenantName_idx", def = "{'tenantName': 1}" )
})*/
public class Notification {
	
	public enum TransmittedStatus {
		PROCESSING("PROCESSING"), PROCESSED("PROCESSED"), PENDING("PENDING");
		private String name;

		private TransmittedStatus(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}
	
	private long row;
	
	private ObjectId id;
	
	private String frequency;
	
	private String action;
	
	private String format;
	
	private Date createdDate;
	
	private ObjectId subscriberId;
	
	private String subscriberEmail;
	
	private String subscriberUsername;
	
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

	public String getSubscriberUsername() {
		return subscriberUsername;
	}

	public void setSubscriberUsername(String subscriberUsername) {
		this.subscriberUsername = subscriberUsername;
	}

	public long getRow() {
		return row;
	}

	public void setRow(long row) {
		this.row = row;
	}

}
