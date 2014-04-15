package org.craftercms.social.repositories;

import java.util.List;

import com.mongodb.MongoException;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.craftercms.commons.mongo.JongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationRepositoryImpl extends JongoRepository<Notification> implements NotificationRepository {

    private Logger log = LoggerFactory.getLogger(NotificationRepositoryImpl.class);

    /**
     * Creates a instance of a Jongo Repository.
     */
    public NotificationRepositoryImpl() throws MongoDataException {
    }


    @Override
    public Iterable<Notification> findNotificationByFrequencyAndTransmittedStatus(String frequency,
                                                                                  String transmittedStatus,
                                                                                  String action,
                                                                                  String[] eventActionFilters,
                                                                                  List<DefaultKeyValue<String,
                                                                                      Boolean>>
                                                                                      notificationQuerySort) throws
        MongoDataException {
        try {
            String query = getQueryFor("social.notification.byFreqActionStatus");
            return getCollection().find(query, frequency, action, transmittedStatus).sort(createSortQuery
                (notificationQuerySort)).as(Notification.class);
        } catch (MongoException ex) {
            log.error("Unable to find Notifications by given params frequency {}.transmitedStatus {} , action {} , " +
                    "" + "eventActionFilters {} and sorted by notificaticationQuerySort {}", frequency,
                transmittedStatus, action, eventActionFilters, notificationQuerySort
            );
            throw new MongoDataException("Unable to find Notifications by given params", ex);
        }
    }
}
