package org.craftercms.social.repositories;

import java.util.List;
import java.util.Map;

import org.craftercms.social.domain.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Order;

public interface NotificationRepositoryCustom {
	
	long countPendingsByFrequency(String frequency, String[] eventActionFilters);

	List<Notification> findNotificationByFrequencyAndTransmitedStatus(
			String frequency, String transmittedStatus, String action, String[] eventActionFilters, Map<String,Sort.Direction> notificaticationQuerySort);

}
