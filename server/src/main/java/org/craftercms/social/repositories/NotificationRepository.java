package org.craftercms.social.repositories;

import java.util.List;

import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.Notification;

public interface NotificationRepository extends CrudRepository<Notification> {

    Iterable<Notification> findNotificationByFrequencyAndTransmittedStatus(String frequency,
                                                                           String transmittedStatus, String action,
                                                                           String[] eventActionFilters,
                                                                           List<DefaultKeyValue<String,
                                                                               Boolean>> notificationQuerySort) throws MongoDataException;

}
