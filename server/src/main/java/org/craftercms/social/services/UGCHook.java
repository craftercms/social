package org.craftercms.social.services;


import org.craftercms.social.domain.UGC;
import org.craftercms.profile.impl.domain.Profile;

public interface UGCHook {

    /**
     * This method is intended to be used when a new UGC is been added
     *
     * @param ugc the UGC that has been added
     * @param profile the profile associated with the ugc
     */
    public void onNewUGC(UGC ugc, Profile profile);

    /**
     * This method is intended be use to be used when a new UGC is being added
     * to execute extra actions
     *
     * @param ugc the UGC that has been added
     * @param profile the profile associated with the ugc
     */
    public void onNewChildUGC(UGC ugc, Profile profile);

}
