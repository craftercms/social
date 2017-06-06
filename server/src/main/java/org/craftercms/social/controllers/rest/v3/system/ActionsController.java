package org.craftercms.social.controllers.rest.v3.system;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

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
@Api(value = "Security Actions",basePath ="/api/3/system/actions",
    description = "Services to Admin Security Actions")
public class ActionsController {

    @Autowired
    private SecurityActionsService actionsService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Gets all Security Actions for current context.")
    public Iterable<SocialSecurityAction> getCurrentActions() {
        return IterableUtils.toList(actionsService.get(SocialSecurityUtils.getContext()));
    }

    @RequestMapping(method = {RequestMethod.PUT,RequestMethod.POST})
    @ResponseBody
    @ApiOperation(value = "Updates the given action name with the Roles", notes = "Notice that this is not a partial "
        + "" + "removeWatcher of roles, this will replace the current action Roles with the new ones (send)")
    public SocialSecurityAction update(@RequestParam("actionName") final String actionName,
                                       @RequestParam() final String roles) throws SocialException {
        return actionsService.update(SocialSecurityUtils.getContext(), actionName, Arrays.asList(roles.split(",")));
    }


}
