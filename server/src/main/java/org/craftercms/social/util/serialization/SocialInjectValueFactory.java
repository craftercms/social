package org.craftercms.social.util.serialization;

import org.craftercms.commons.jackson.mvc.annotations.InjectValueFactory;
import org.craftercms.profile.api.Profile;
import org.craftercms.social.util.profile.ProfileAggregator;

/**
 *
 */
public class SocialInjectValueFactory implements InjectValueFactory {

    private ProfileAggregator profileAggregator;

    @Override
    public <T> T getObjectFor(final Class<T> declaringClass, final Object basePropertyValue, final Object object) {
        if(declaringClass.equals(Profile.class)){
            return (T)profileAggregator.getProfile((String)basePropertyValue);
        }
        return null;
    }

    public void setProfileAggregator(final ProfileAggregator profileAggregator) {
        this.profileAggregator = profileAggregator;
    }
}
