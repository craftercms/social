package org.craftercms.social.util.support.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.profile.impl.domain.Tenant;
import org.craftercms.social.util.support.CrafterProfileService;
// TODO Redo this all testing
public class CrafterProfileServiceTest implements CrafterProfileService {


    @Override
    public Profile authenticateAnonymous() {
        return null;
    }

    @Override
    public String getAppToken() {
        return null;
    }

    @Override
    public Profile getProfile(final String profileId) {
        return null;
    }

    @Override
    public Profile getProfile(final String profileId, final List<String> attributes) {
        return null;
    }

    @Override
    public List<Profile> getProfilesByIds(final List<String> profileIds) {
        return null;
    }

    @Override
    public Tenant getTenant(final String tenantName) {
        return null;
    }

    @Override
    public void resetAppToken() {

    }

    @Override
    public boolean validateUserToken(final String ticket) {
        return false;
    }

    @Override
    public Profile getUserInformation(final String ticket) {
        return null;
    }

    @Override
    public void updateAttributes(final String profileId, final Map<String, Serializable> attributes) {

    }

    @Override
    public List<Profile> findProfilesBySubscriptions(final String target) {
        return null;
    }
}
