package org.craftercms.social.controllers.rest.v3.system;

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
public class ProfileAggregatorController {

    @Autowired
    private ProfileAggregator profileAggregator;

    @RequestMapping(value = "/clear", method = {RequestMethod.DELETE,RequestMethod.GET})
    @ResponseBody
    @HasPermission(action = "ClearCache",type = SocialPermission.class)
    public boolean clearProfileCache(@RequestParam(required = false, defaultValue = "") final String profileIds) {
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
