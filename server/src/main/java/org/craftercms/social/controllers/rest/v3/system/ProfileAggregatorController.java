package org.craftercms.social.controllers.rest.v3.system;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.security.permissions.annotations.HasPermission;
import org.craftercms.social.security.SocialPermission;
import org.craftercms.social.util.profile.ProfileAggregator;
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
@RequestMapping("/api/3/system/profile")
@Api(value = "System Profile",description = "Clears profile cache,Only for Social Admins or Super Admins.")
public class ProfileAggregatorController {

    @Autowired
    private ProfileAggregator profileAggregator;

    @RequestMapping(value = "/clear", method = {RequestMethod.DELETE,RequestMethod.GET})
    @ApiOperation("Clear Profile Aggregator Cache")
    @ResponseBody
    @HasPermission(action = "ClearCache",type = SocialPermission.class)
    public boolean clearProfileCache(@ApiParam(value = "List of Id to be remove from Social Profiles Cache")
                                         @RequestParam(required = false, defaultValue = "") final String profileIds) {
        if (StringUtils.isBlank(profileIds)) {
            profileAggregator.clearProfileCache();
        } else {
            String[] ids = profileIds.split(",");
            if (ids.length != 0) {
               profileAggregator.clearProfileCache(Arrays.asList(ids));
            } else {
                return false;
            }
        }
        return true;
    }
}
