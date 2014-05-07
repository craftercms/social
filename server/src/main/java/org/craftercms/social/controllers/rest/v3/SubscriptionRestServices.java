package org.craftercms.social.controllers.rest.v3;

import org.craftercms.social.domain.Subscriptions;
import org.craftercms.social.exceptions.SocialException;

/**
 * Defines all Rest Services for Subscriptions.
 * <p>Implementers Must
 * <ul>
 * <li>Audit all Calls</li>
 * <li>Check that this call are made with an authenticated user and the the UGC belongs to the user's tenant.</li>
 * </ul>
 * </p>
 */
public interface SubscriptionRestServices {


    /**
     * Returns all current logged user subscriptions
     * @return A Iterable of all user subscriptions.Null if user does not have any subscriptions.
     * @throws SocialException If unable to get user subscriptions.
     */
    Iterable<Subscriptions> getSubscriptions() throws SocialException;

    /**
     * Updates current user subscriptions settings.
     * @param frequency Frequency to send notification to the user.
     * @param deliveryMethod    Delivery Method of the notification.
     * @param format Format of the notification.
     * @param autoWatch Notification auto watch.
     * @return True if available to save , false other wise.
     * @throws org.craftercms.social.exceptions.SocialException If unable to save due a internal error.
     */
    boolean updateSettings(final String frequency,final String deliveryMethod, final String format , boolean autoWatch);

    /**
     * Subscribes a user to the given target Id.
     * @param targetId Target id to subscribe the user.
     * @return True if available to save , false other wise.
     * @throws org.craftercms.social.exceptions.SocialException If unable to save due a internal error.
     */
    boolean subscribe(final String targetId) throws SocialException;

    /**
     * Unsubscribes a user for the given target Id.
     * @param targetId Target id to subscribe the user.
     * @return True if available to save , false other wise.
     * @throws org.craftercms.social.exceptions.SocialException If unable to save due a internal error.
     */
    boolean unSubscribe(final String targetId) throws SocialException;
}
