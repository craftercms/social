package org.craftercms.social.domain;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.bson.types.ObjectId;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("uGCAuditHarvest")
@XmlRootElement
public class UGCAuditHarvest {
	private long lastRetrievedRow;
	private String jobId;
	private String state;
	private Date lastUpdatedDate;
	public long getLastRetrievedRow() {
		return lastRetrievedRow;
	}
	public void setLastRetrievedRow(long lastRetrievedRow) {
		this.lastRetrievedRow = lastRetrievedRow;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}
	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

}
