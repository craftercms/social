package org.craftercms.social.controllers.rest.v1;

import org.apache.commons.lang.StringUtils;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.social.domain.Subscriptions;

import org.craftercms.social.services.SubscriptionService;
import org.craftercms.social.util.SocialUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: alfonsovasquez
 * Date: 20/11/13
 * Time: 19:53
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/api/2/subscriptions")
public class SubscriptionsController {

    @Autowired
    private SubscriptionService subscriptionService;

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute
    public Subscriptions getSubscriptions()  {
        return SocialUtils.getSubscriptions(SocialUtils.getCurrentProfile());
    }

    @RequestMapping(value = "/update_settings", method = RequestMethod.POST)
    @ModelAttribute
    public void updateSettings(@RequestParam(value = "frequency", required = false) String frequency,
                               @RequestParam(value = "action", required = false) String action,
                               @RequestParam(value = "format", required = false) String format,
                               @RequestParam(value = "autoWatch", required = false) Boolean autoWatch) {
        Profile profile = SocialUtils.getCurrentProfile();
        Subscriptions subscriptions = SocialUtils.getSubscriptions(profile);

        if (subscriptions == null) {
            subscriptions = new Subscriptions();
        }

        if (StringUtils.isNotEmpty(frequency)) {
            subscriptions.setFrequency(frequency);
        }
        if (StringUtils.isNotEmpty(action)) {
            subscriptions.setAction(action);
        }
        if (StringUtils.isNotEmpty(format)) {
            subscriptions.setFormat(format);
        }
        if (autoWatch != null) {
            subscriptions.setAutoWatch(autoWatch);
        }

        subscriptionService.updateSubscriptions(profile, subscriptions);
    }

    @RequestMapping(value = "/subscribe/{targetId}", method = RequestMethod.POST)
    @ModelAttribute
    public void subscribe(@PathVariable String targetId) {
        subscriptionService.createSubscription(SocialUtils.getCurrentProfile(), targetId);
    }

    @RequestMapping(value = "/unsubscribe/{targetId}", method = RequestMethod.POST)
    @ModelAttribute
    public void unsubscribe(@PathVariable String targetId) {
        subscriptionService.deleteSubscription(SocialUtils.getCurrentProfile(), targetId);
    }

}
