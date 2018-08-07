package org.craftercms.social.controllers.rest.v3.comments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.craftercms.profile.api.Profile;
import org.craftercms.social.controllers.rest.v3.comments.exceptions.UGCNotFound;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.exceptions.UGCException;
import org.craftercms.social.security.SocialSecurityUtils;
import org.craftercms.social.services.notification.NotificationService;
import org.craftercms.social.services.ugc.UGCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 */
@Controller
@RequestMapping("/api/3/threads")
public class ThreadsController {

    public static final String MAX_INT = "666";
    @Autowired
    private UGCService ugcService;

    @Autowired
    private NotificationService notificationService;


    @RequestMapping(value = "{id}/comments", method = RequestMethod.GET)
    @ResponseBody
    public Thread thread(@PathVariable final String id,
                         @RequestParam(required = false, defaultValue = MAX_INT) final int recursive,
                         @RequestParam(required = false, defaultValue = "0") final int pageNumber,
                         @RequestParam(required = false, defaultValue = MAX_INT) final int pageSize,
                         @RequestParam final int childrenCount,
                         @RequestParam(required = false) final List<String> sortBy,
                         @RequestParam(required = false) final List<SocialSortOrder> sortOrder) throws SocialException {
        int start = 0;
        if (pageNumber > 0 && pageSize > 0) {
            start = getStart(pageNumber, pageSize);
        }
        Thread thread = new Thread();
        final int upToLevel;
        if (recursive < 0) {
            upToLevel = Integer.MAX_VALUE;
        } else {
            upToLevel = recursive;
        }
        thread.setComments(ugcService.read(id, SocialSecurityUtils.getContext(), start, pageSize, getSortOrder
            (sortBy, sortOrder), upToLevel, childrenCount));
        if (SocialSecurityUtils.getCurrentProfile().getUsername().equalsIgnoreCase(SocialSecurityUtils.ANONYMOUS)) {
            thread.setWatched(false);
        } else {
            thread.setWatched(notificationService.isBeenWatch(SocialSecurityUtils.getContext() + "/" + id,
                SocialSecurityUtils.getCurrentProfile().getId().toString()));
        }
        thread.setPageNumber(pageNumber);
        thread.setPageSize(pageSize);
        thread.setTotal(ugcService.count(id, SocialSecurityUtils.getContext()));

        return thread;
    }

    @RequestMapping(value = "{id}/comments/{commentId}/children", method = RequestMethod.GET)
    @ResponseBody
    public Thread comments(@PathVariable final String id,
                           @PathVariable final String commentId,
                           @RequestParam(required = false, defaultValue = MAX_INT) final int recursive,
                           @RequestParam(required = false, defaultValue = "0") final int pageNumber,
                           @RequestParam(required = false, defaultValue = MAX_INT) final int pageSize,
                           @RequestParam(required = false, defaultValue = MAX_INT) final int childrenCount,
                           @RequestParam(required = false) final List<String> sortBy,
                           @RequestParam(required = false) final List<SocialSortOrder> sortOrder) throws SocialException {
        int start = 0;
        if (pageNumber > 0 && pageSize > 0) {
            start = getStart(pageNumber, pageSize);
        }
        Thread thread = new Thread();
        final int upToLevel;
        if (recursive < 0) {
            upToLevel = Integer.MAX_VALUE;
        } else {
            upToLevel = recursive;
        }
        try {
            thread.setComments(ugcService.readChildren(commentId, id, SocialSecurityUtils.getContext(), start, pageSize,
                getSortOrder(sortBy, sortOrder), upToLevel, childrenCount));
        } catch (UGCNotFound ugcNotFound) {
            ugcNotFound.printStackTrace();
        }
        thread.setWatched(notificationService.isBeenWatch(SocialSecurityUtils.getContext() + "/" + id,
            SocialSecurityUtils.getCurrentProfile().getId().toString()));
        thread.setPageNumber(pageNumber);
        thread.setPageSize(pageSize);
        thread.setTotal(ugcService.countChildren(commentId, SocialSecurityUtils.getContext()));
        thread.setTotal(ugcService.count(id, SocialSecurityUtils.getContext()));
        return thread;
    }

    @RequestMapping(value = "{id}/subscribe", method = RequestMethod.POST)
    @ResponseBody
    public boolean subscribe(@PathVariable final String id, @RequestParam(required = false, defaultValue = "") final
    String frequency, @RequestParam final String context) throws UGCException {
        Profile p = SocialSecurityUtils.getCurrentProfile();
        if (!p.getUsername().equals(SocialSecurityUtils.ANONYMOUS)) {
            notificationService.subscribeUser(p, context + "/" + id, frequency);
            return true;
        }
        return false;
    }

    @RequestMapping(value = "{id}/subscribe", method = RequestMethod.PUT)
    @ResponseBody
    public boolean changeSubscribe(@PathVariable final String id, @RequestParam(required = false, defaultValue = "")
    final String frequency, @RequestParam final String context) throws UGCException {
        Profile p = SocialSecurityUtils.getCurrentProfile();
        if (!p.getUsername().equals(SocialSecurityUtils.ANONYMOUS)) {
            notificationService.changeSubscription(p, context + "/" + id, frequency);
            return true;
        }
        return false;
    }

    @RequestMapping(value = "{id}/subscribe/update", method = RequestMethod.POST)
    @ResponseBody
    public boolean changeSubscribePost(@PathVariable final String id, @RequestParam(required = false, defaultValue = "")
    final String frequency, @RequestParam final String context) throws UGCException {
       return this.changeSubscribe(id, frequency, context);
    }

    @RequestMapping(value = "/subscriptions", method = RequestMethod.GET)
    @ResponseBody
    public List<Map> subscriptions(@RequestParam final String context) throws SocialException {
        return notificationService.getUserSubscriptions();
    }


    @RequestMapping(value = "{id}/unsubscribe", method = {RequestMethod.POST,RequestMethod.DELETE})
    @ResponseBody
    public boolean unSubscribe(@PathVariable final String id, @RequestParam final String context) throws UGCException {
        Profile p = SocialSecurityUtils.getCurrentProfile();
        if (!p.getUsername().equals(SocialSecurityUtils.ANONYMOUS)) {
            notificationService.unSubscribeUser(p.getId().toString(), context + "/" + id);
            return true;
        }
        return false;
    }


    public static List<DefaultKeyValue<String, Boolean>> getSortOrder(final List<String> sortFields, final
    List<SocialSortOrder> sortOrder) {
        if (CollectionUtils.isEmpty(sortFields)) {
            return Arrays.asList(new DefaultKeyValue<>("createdBy",SocialSortOrder.DESC.value()));
        }
        List<DefaultKeyValue<String, Boolean>> toReturn = new ArrayList<>();
        for (int i = 0; i < sortFields.size(); i++) {
            DefaultKeyValue<String, Boolean> mapSort;
            if (CollectionUtils.isEmpty(sortOrder) || i >= sortOrder.size()) {
                mapSort = new DefaultKeyValue(sortFields.get(i), SocialSortOrder.DESC.value());
            } else {
                mapSort = new DefaultKeyValue(sortFields.get(i), sortOrder.get(i).value());
            }
            toReturn.add(mapSort);
        }
        return toReturn;
    }

    public static int getStart(int page, int pageSize) {
        if (page <= 0) {
            return 0;
        }
        return (page - 1) * pageSize;
    }

    //    if (page > 0 && pageSize > 0) {
    //        int start = getStart(page, pageSize);
    //        int end = pageSize;
    //        q.skip(start);
    //        q.limit(end);
    //
    //    }
}
