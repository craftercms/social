package org.craftercms.social.services.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.social.domain.Subscriptions;
import org.craftercms.social.services.SubscriptionService;
import org.craftercms.social.util.support.CrafterProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        Map changedAttributes = new HashMap<String, Serializable>();
        changedAttributes = Subscriptions.setInAttributes(subscriptions, changedAttributes);

        Map attributes = profile.getAttributes();
        if (attributes == null) {
            profile.setAttributes(changedAttributes);
        } else {
            attributes.putAll(changedAttributes);
        }

        crafterProfileService.updateAttributes(profile.getId(), changedAttributes);
    }

    @Override
    public void createSubscription(Profile profile, String targetId) {
        Map attributes = profile.getAttributes();
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
            profile.setAttributes(attributes);
        }

        List<String> targets = Subscriptions.getTargetsAsList(attributes);
        if (targets == null) {
            targets = new ArrayList<String>();
        }

        if (!targets.contains(targetId)) {
            targets.add(targetId);
        }

        attributes.put(Subscriptions.ATTRIBUTE_TARGETS, targets);

        Map changedAttributes = new HashMap<String, Serializable>(1);
        changedAttributes.put(Subscriptions.ATTRIBUTE_TARGETS, targets);

        crafterProfileService.updateAttributes(profile.getId(), changedAttributes);
    }

    @Override
    public void deleteSubscription(Profile profile, String targetId) {
        Map attributes = profile.getAttributes();
        if (MapUtils.isNotEmpty(attributes)) {
            List<String> targets = (List<String>) attributes.get(Subscriptions.ATTRIBUTE_TARGETS);
            if (CollectionUtils.isNotEmpty(targets)) {
                if (targets.remove(targetId)) {
                    attributes.put(Subscriptions.ATTRIBUTE_TARGETS, targets);

                    Map changedAttributes = new HashMap<String, Serializable>(1);
                    changedAttributes.put(Subscriptions.ATTRIBUTE_TARGETS, targets);

                    crafterProfileService.updateAttributes(profile.getId(), changedAttributes);
                }
            }
        }
    }

}
