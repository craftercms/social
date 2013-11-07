package org.craftercms.social.repositories;

import java.util.List;
import java.util.Set;

import org.craftercms.social.domain.Profile;

public interface ProfileRepositoryCustom {
	
	List<Profile> findProfilesBySubscriptions(String target);

}
