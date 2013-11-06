package org.craftercms.social.repositories;

import java.util.List;

import org.craftercms.social.domain.Notification;
import org.craftercms.social.domain.Notification.TransmittedStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;

public class NotificationRepositoryImpl implements NotificationRepositoryCustom {
	
	private static final String TRANSMITED_STATUS = "transmitedStatus";
	private static final String ACTION = "action";
	private static final String FREQUENCY = "frequency";
	private static final String CREATED_DATE = "createdDate";
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Notification> findNotificationByFrequencyAndTransmitedStatus(
			String frequency, String transmittedStatus, String action, int start, int end) {
		Query query = new Query();
		query.addCriteria(Criteria.where(FREQUENCY).is(frequency).and(ACTION).is(action).and(TRANSMITED_STATUS).is(transmittedStatus));
		query.sort().on(CREATED_DATE, Order.DESCENDING);
		query.skip(start);
        query.limit(end > start? (end - start + 1): 0);
		return mongoTemplate.find(query, Notification.class);
	}
	
	@Override
	public long countPendingsByFrequency(
			String frequency) {
		Query query = new Query();
		query.addCriteria(Criteria.where(FREQUENCY).is(frequency).and(TRANSMITED_STATUS).is(TransmittedStatus.PENDING));
		return mongoTemplate.count(query, Notification.class);
	}

}
