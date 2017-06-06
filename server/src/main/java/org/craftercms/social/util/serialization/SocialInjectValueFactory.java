package org.craftercms.social.util.serialization;

import java.util.Arrays;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.jackson.mvc.annotations.InjectValueFactory;
import org.craftercms.profile.api.Profile;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.exceptions.NotificationException;
import org.craftercms.social.security.SecurityActionNames;
import org.craftercms.social.security.SocialSecurityUtils;
import org.craftercms.social.services.notification.NotificationService;
import org.craftercms.social.util.LoggerFactory;
import org.craftercms.social.util.ProfileUtils;
import org.craftercms.social.util.profile.ProfileAggregator;

/**
 *
 */
public class SocialInjectValueFactory implements InjectValueFactory {

    protected ProfileAggregator profileAggregator;
    protected NotificationService notificationService;
    protected I10nLogger log = LoggerFactory.getLogger(SocialInjectValueFactory.class);
    protected String ignoreAnonymousFlagRoles;


    @Override
    public <T> T getObjectFor(final Class<T> declaringClass, final Object basePropertyValue, final String
        originalProperty, final Object object) {
        if (UGC.class.isAssignableFrom(object.getClass())) {
            if (declaringClass.equals(Profile.class)) {
                final Profile profile = profileAggregator.getProfile((String)basePropertyValue);
                if(((UGC)object).isAnonymousFlag() && !ignoreAnonymousFlag()){
                    anonymizeProfile((UGC)object);
                    return (T)ProfileUtils.getAnonymousProfile();
                }else{
                    return (T)profile;
                }
            }
        }
        return null;
    }

    private boolean ignoreAnonymousFlag() {
        final Profile currentUser = SocialSecurityUtils.getCurrentProfile();
        if( currentUser==null || currentUser.getRoles().isEmpty() ||
            currentUser.getUsername().equalsIgnoreCase(SocialSecurityUtils.ANONYMOUS)) {
            return false;
        }
        return CollectionUtils.containsAny(currentUser.getRoles(), Arrays.asList(ignoreAnonymousFlagRoles.split(",")));

    }

    protected void anonymizeProfile(final UGC object) {
        if(object.getCreatedBy().equals(object.getLastModifiedBy())){
            object.setLastModifiedBy("");
        }
        object.setCreatedBy("");
    }

    public void setProfileAggregator(final ProfileAggregator profileAggregator) {
        this.profileAggregator = profileAggregator;
    }

    public void setNotificationServiceImpl(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void setIgnoreAnonymousFlagRoles(final String ignoreAnonymousFlagRoles) {
        this.ignoreAnonymousFlagRoles = ignoreAnonymousFlagRoles;
    }
}
