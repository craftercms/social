package org.craftercms.social.repositories;

import java.util.List;

import org.craftercms.social.domain.*;

public interface NotificationRepositoryCustom {
	
	public List<Notification> findNotificationByFrequencyAndTransmitedStatus(String frequency, 
				String transmittedStatus);

	long countPendingsByFrequency(String frequency);

	List<Notification> findNotificationByFrequencyAndTransmitedStatus(
			String frequency, String transmittedStatus, int start, int end);

}
