package org.craftercms.social.services.impl;


import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.social.domain.Subscriptions;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.services.SubscriptionService;
import org.craftercms.social.services.UGCHook;
import org.craftercms.social.util.SocialUtils;
import org.craftercms.social.util.support.CrafterProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UGCHookImpl implements UGCHook {

    @Autowired
    private SubscriptionService subscriptionService;

    /**
     * If auto watch is enable the profile will be subscript to the new UGC,
     * if it is already subscript it will update the subscription
     * @param ugc the UGC that has been added
     * @param profile the profile associated with the ugc
     */
    @Override
    public void onNewUGC(UGC ugc, Profile profile) {
        Subscriptions subscriptions = SocialUtils.getSubscriptions(profile);
        if(subscriptions != null && subscriptions.isAutoWatch()) {
            subscriptionService.createSubscription(profile, ugc.getTargetId());
        }
    }

    /**
     * If auto watch is enable the profile will be subscript to the new child UGC,
     * if it is already subscript it will update the subscription
     * @param ugc the UGC that has been added
     * @param profile the profile associated with the ugc
     */
    @Override
    public void onNewChildUGC(UGC ugc, Profile profile) {
        this.onNewUGC(ugc,profile);
    }


}
