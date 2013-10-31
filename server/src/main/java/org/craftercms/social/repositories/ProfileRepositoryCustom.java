package org.craftercms.social.repositories;

import java.util.List;
import java.util.Set;

import org.craftercms.social.domain.Profile;

public interface ProfileRepositoryCustom {
	
	List<Profile> findProfilesBySubscriptions(Set<String> target);
	List<Profile> findProfilesBySubscriptions(String target, String action, String period, String format);
	//List findProfilesBySubscriptions(String target, String action, String period, String format);

}
