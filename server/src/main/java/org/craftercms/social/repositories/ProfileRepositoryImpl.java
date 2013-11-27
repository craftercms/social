package org.craftercms.social.repositories;

import java.util.List;
import java.util.Set;

import org.craftercms.social.domain.Profile;
import org.craftercms.social.domain.Subscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class ProfileRepositoryImpl implements ProfileRepositoryCustom {
	
	@Autowired
	private MongoTemplate profileTemplate;

	@Override
	public List<Profile> findProfilesBySubscriptions(String target) {
		Query query = new Query();
		query.addCriteria(Criteria.where("attributes." + Subscriptions.ATTRIBUTE_TARGETS).is(target));
		return profileTemplate.find(query, Profile.class);
	}

}
