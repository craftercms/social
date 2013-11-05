package org.craftercms.social.repositories;

import org.bson.types.ObjectId;
import org.craftercms.social.domain.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository("notificationRepository")
public interface NotificationRepository extends MongoRepository<Notification, ObjectId>, NotificationRepositoryCustom {

}
