package org.craftercms.social.controllers.rest.v3.social;

import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.services.social.SocialServices;
import org.craftercms.social.services.social.VoteOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/3/social")
public class SocialController<T extends SocialUgc> {
    @Autowired
    private SocialServices socialServices;

    @RequestMapping(value = "{ugcId}/vote/{vote}",method = RequestMethod.POST)
    @ResponseBody
    public T vote(@PathVariable(value = "ugcId") final String ugcId,@PathVariable(value = "vote") final
                  VoteOptions voteOption) throws SocialException {
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        String userId = "testUserId"; //=ProfileUtils.getCurrentProfile().getTenant();
        return (T)socialServices.vote(ugcId,voteOption,userId,tenant);
    }


}
