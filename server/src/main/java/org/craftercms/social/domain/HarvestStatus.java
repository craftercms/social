package org.craftercms.social.domain;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("harvestStatus")
@XmlRootElement
public class HarvestStatus {
	@Id 
	private String id;
	
	private String collectionName;
	
	private long lastRowRetrieved;
	
	private String status;
	
	private String applicationId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public long getLastRowRetrieved() {
		return lastRowRetrieved;
	}

	public void setLastRowRetrieved(long lastRowRetrieved) {
		this.lastRowRetrieved = lastRowRetrieved;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	

}
