package org.craftercms.social.services.impl;

import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.social.domain.Subscriptions;
import org.craftercms.social.services.SubscriptionService;
import org.craftercms.social.util.SocialUtils;
import org.craftercms.social.util.support.CrafterProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: alfonsovasquez
 * Date: 20/11/13
 * Time: 15:03
 * To change this template use File | Settings | File Templates.
 */
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private CrafterProfileService crafterProfileService;

    @Override
    public void updateSubscriptions(Profile profile, Subscriptions subscriptions) {
        SocialUtils.setSubscriptions(profile, subscriptions);

        crafterProfileService.updateAttributes(profile.getId(), convertToSerializableMap(profile.getAttributes()));
    }

    @Override
    public void createSubscription(Profile profile, String targetId) {
        Subscriptions subscriptions = SocialUtils.getSubscriptions(profile);

        if (subscriptions == null) {
            subscriptions = new Subscriptions();

            SocialUtils.setSubscriptions(profile, subscriptions);
        }

        if (!subscriptions.getTargets().contains(targetId)) {
            subscriptions.getTargets().add(targetId);
        }

        crafterProfileService.updateAttributes(profile.getId(), convertToSerializableMap(profile.getAttributes()));
    }

    @Override
    public void deleteSubscription(Profile profile, String targetId) {
        Subscriptions subscriptions = SocialUtils.getSubscriptions(profile);

        if (subscriptions == null) {
            subscriptions.getTargets().remove(targetId);
        }

        crafterProfileService.updateAttributes(profile.getId(), convertToSerializableMap(profile.getAttributes()));
    }

    // TODO: Remove this piece of HORRIBLE code. Right now it's here because I didn't find another way to convert a map
    // of objects to a map of serializable
    private Map<String, Serializable> convertToSerializableMap(Map<String, Object> map) {
        Map<String, Serializable> serializableMap = new HashMap<String, Serializable>();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            serializableMap.put(entry.getKey(), (Serializable) entry.getValue());
        }

        return serializableMap;
    }

}
