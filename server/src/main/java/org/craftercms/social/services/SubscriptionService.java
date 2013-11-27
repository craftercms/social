package org.craftercms.social.services;

import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.social.domain.Subscriptions;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alfonsovasquez
 * Date: 20/11/13
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 */
public interface SubscriptionService {

    void updateSubscriptions(Profile profile, Subscriptions subscriptions);

    void createSubscription(Profile profile, String targetId);

    void deleteSubscription(Profile profile, String targetId);

}
