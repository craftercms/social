package org.craftercms.social.repositories;

import java.util.List;
import java.util.Set;

import org.craftercms.social.domain.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class ProfileRepositoryImpl implements ProfileRepositoryCustom {
	
	@Autowired
	private MongoTemplate profileTemplate;

	@Override
	public List<Profile> findProfilesBySubscriptions(Set<String> target) {
		Query query = new Query();
		//query.addCriteria(Criteria.where("attributes.subscriptions.target").is(target.toString().toLowerCase()));
		
		query.addCriteria(Criteria.where("attributes.subscriptions").elemMatch(
				Criteria.where("target").in(target)));
		
		
		return profileTemplate.find(query, Profile.class);
	}
	
	@Override
	public List<Profile> findProfilesBySubscriptions(String target, String action, String period, String format) {
		
//		Query query1 = new Query();
//		query1.addCriteria(Criteria.where("attributes.first-name").is("test"));
//		query1.addCriteria(Criteria.where("attributes.last-name").is("test"));
//		List lp2 = profileTemplate.find(query1, Profile.class);
		
		Query query = new Query();

		query.addCriteria(Criteria.where("subscriptions.target").in(target));
		query.addCriteria(Criteria.where("subscriptions.action").in(action));
		query.addCriteria(Criteria.where("subscriptions.period").in(period));
		query.addCriteria(Criteria.where("subscriptions.format").in(format));
		
		List<Profile> lp = profileTemplate.find(query, Profile.class);
		
		return lp;
	}

}
