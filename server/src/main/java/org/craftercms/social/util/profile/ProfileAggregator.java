package org.craftercms.social.util.profile;

import java.util.List;

import org.craftercms.profile.api.Profile;

/**
 *
 */
public interface ProfileAggregator {
    void clearProfileCache(List<String> profileIds);

    void clearProfileCache();

    Profile getProfile(String profileId);
}
