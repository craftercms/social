package org.craftercms.social.controllers.rest.v3.system;

import java.util.Arrays;

import org.craftercms.commons.collections.IterableUtils;
import org.craftercms.social.domain.social.system.SocialSecurityAction;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.security.SocialSecurityUtils;
import org.craftercms.social.services.system.SecurityActionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 */
@Controller
@RequestMapping("/api/3/system/actions")
public class ActionsController {

    @Autowired
    private SecurityActionsService actionsService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Iterable<SocialSecurityAction> getCurrentActions() {
        return IterableUtils.toList(actionsService.get(SocialSecurityUtils.getContext()));
    }

    @RequestMapping(method = {RequestMethod.PUT,RequestMethod.POST})
    @ResponseBody
    public SocialSecurityAction update(@RequestParam("actionName") final String actionName,
                                       @RequestParam() final String roles) throws SocialException {
        return actionsService.update(SocialSecurityUtils.getContext(), actionName, Arrays.asList(roles.split(",")));
    }


}
