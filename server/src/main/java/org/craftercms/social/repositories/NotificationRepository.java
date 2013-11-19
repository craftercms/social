package org.craftercms.social.repositories;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.craftercms.social.domain.Notification;
import org.craftercms.social.util.action.ActionConstants;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository("notificationRepository")
public interface NotificationRepository extends MongoRepository<Notification, ObjectId>, NotificationRepositoryCustom {
	
}
