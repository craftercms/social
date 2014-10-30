package org.craftercms.social.util.serialization;

import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.jackson.mvc.annotations.InjectValueFactory;
import org.craftercms.profile.api.Profile;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.exceptions.NotificationException;
import org.craftercms.social.security.SocialSecurityUtils;
import org.craftercms.social.services.notification.NotificationService;
import org.craftercms.social.util.LoggerFactory;
import org.craftercms.social.util.ProfileUtils;
import org.craftercms.social.util.profile.ProfileAggregator;

/**
 *
 */
public class SocialInjectValueFactory implements InjectValueFactory {

    private ProfileAggregator profileAggregator;
    private NotificationService notificationService;
    private I10nLogger log = LoggerFactory.getLogger(SocialInjectValueFactory.class);


    @Override
    public <T> T getObjectFor(final Class<T> declaringClass, final Object basePropertyValue, final String
        originalProperty, final Object object) {
        if (UGC.class.isAssignableFrom(object.getClass())) {
            if (declaringClass.equals(Profile.class)) {
                final Profile profile = profileAggregator.getProfile((String)basePropertyValue);
                if(((UGC)object).isAnonymousFlag()){
                    anonymizeProfile((UGC)object);
                    return (T)ProfileUtils.getAnonymousProfile();
                }else{
                    return (T)profile;
                }
            } else if ((declaringClass.equals(boolean.class) || declaringClass.equals(Boolean.class)) &&
                originalProperty.equals("targetId")) {
                try {
                    final Profile profile = SocialSecurityUtils.getCurrentProfile();
                    if (!profile.getUsername().equalsIgnoreCase(SocialSecurityUtils.ANONYMOUS)) {
                        return (T)(Boolean)notificationService.isBeenWatch(basePropertyValue.toString(), profile
                            .getId().toString());
                    } else {
                        return (T)Boolean.FALSE;
                    }
                } catch (NotificationException e) {
                    log.error("logging.system.notification.aggregation", e, SocialSecurityUtils.getCurrentProfile()
                        .getId());
                }
            }
        }
        return null;
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
}
