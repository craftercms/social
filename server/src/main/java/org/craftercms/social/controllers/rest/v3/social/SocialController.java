package org.craftercms.social.controllers.rest.v3.social;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

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
@Api(value = "Social Service",basePath = "/api/3/ugc",description = "")
public class SocialController<T extends SocialUgc> {
    @Autowired
    private SocialServices socialServices;

    @RequestMapping(value = "{ugcId}/vote/{vote}",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "{ugcId}/vote/{vote}",httpMethod = "POST")
    public T vote(@PathVariable(value = "ugcId") final String ugcId,@PathVariable(value = "vote") final
                  VoteOptions voteOption) throws SocialException {
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        String userId = "testUserId"; //=ProfileUtils.getCurrentProfile().getTenant();
        return (T)socialServices.vote(ugcId,voteOption,userId,tenant);
    }


    @RequestMapping(value = "{ugcId}/moderate/{moderateStatus}",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "{ugcId}/vote/{vote}",httpMethod = "POST")
    public T moderate(@PathVariable(value = "ugcId") final String ugcId,@PathVariable(value = "moderateStatus") final
    SocialUgc.ModerationStatus moderationStatus) throws SocialException {
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        String userId = "testUserId"; //=ProfileUtils.getCurrentProfile().getTenant();
        return (T)socialServices.moderate(ugcId,moderationStatus,userId,tenant);
    }


}
