package org.craftercms.social.repositories;

import java.util.List;

import org.craftercms.social.domain.*;

public interface NotificationRepositoryCustom {
	
	long countPendingsByFrequency(String frequency, String[] eventActionFilters);

	List<Notification> findNotificationByFrequencyAndTransmitedStatus(
			String frequency, String transmittedStatus, String action, int start, int end, String[] eventActionFilters);

}
