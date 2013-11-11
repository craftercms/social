package org.craftercms.social.services.impl;


import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.profile.impl.domain.Target;
import org.craftercms.social.domain.Subscriptions;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.services.UGCHook;
import org.craftercms.social.util.support.CrafterProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UGCHookImpl implements UGCHook {

    @Autowired
    private CrafterProfile crafterProfile;

    /**
     * If auto watch is enable the profile will be subscript to the new UGC,
     * if it is already subscript it will update the subscription
     * @param ugc the UGC that has been added
     * @param profile the profile associated with the ugc
     */
    @Override
    public void onNewUGC(UGC ugc, Profile profile) {

        boolean autoWatch = profile.getSubscriptions().isAutoWath();

        if(autoWatch){
            crafterProfile.createOrUpdateSubscription(ugc.getProfileId(), ugc.getTargetId(), ugc.getTargetDescription(), ugc.getTargetUrl());
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
