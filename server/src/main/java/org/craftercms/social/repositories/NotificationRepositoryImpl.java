package org.craftercms.social.repositories;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	
    private static final String EVENT_ACTION = "event.action";
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Notification> findNotificationByFrequencyAndTransmitedStatus(
			String frequency, String transmittedStatus, String action, String[] eventActionFilters, Map<String,Order> notificaticationQuerySort) {
		Query query = new Query();
		query.addCriteria(Criteria.where(FREQUENCY).is(frequency).and(ACTION).is(action).and(TRANSMITED_STATUS).is(transmittedStatus));

        if(eventActionFilters != null && eventActionFilters.length > 0){
            query.addCriteria(Criteria.where(EVENT_ACTION).in(eventActionFilters));
        }
        
        setSortToQuery(query, notificaticationQuerySort);
        
		return mongoTemplate.find(query, Notification.class);
	}
	
	private void setSortToQuery(Query query, Map<String, Order> notificaticationQuerySort) {
		Iterator it = notificaticationQuerySort.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        query.sort().on((String)pairs.getKey(), (Order)pairs.getValue());
	    }
	}

	@Override
	public long countPendingsByFrequency(String frequency, String[] eventActionFilters) {
		Query query = new Query();
		query.addCriteria(Criteria.where(FREQUENCY).is(frequency).and(TRANSMITED_STATUS).is(TransmittedStatus.PENDING));

        if(eventActionFilters != null && eventActionFilters.length > 0){
            query.addCriteria(Criteria.where(EVENT_ACTION).in(eventActionFilters));
        }

        return mongoTemplate.count(query, Notification.class);
	}

}
