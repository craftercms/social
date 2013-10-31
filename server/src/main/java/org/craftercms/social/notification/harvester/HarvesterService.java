package org.craftercms.social.notification.harvester;

public interface HarvesterService {
	
	String HARVESTER_STATUS_IDLE = "idle";
	String HARVESTER_STATUS_RUNNING = "running";
	
	String ACTION = "email";
	String FREQUENCY = "instant";
	String FORMAT = "single";
	
	String APPLICATION_ID = "crafter-social";
	String COLLECTION_NAME = "uGCAudit";
	String DEFAULT_JOB_ID = "crafter-social-harvester";
	
	public void harvest();

}
