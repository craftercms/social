package org.craftercms.social.controllers.rest.v3.social;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

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
@Api(value = "Social Service", basePath = "/api/3/ugc", description = "Social specific services")
public class SocialController<T extends SocialUgc> {
    @Autowired
    private SocialServices socialServices;

    @RequestMapping(value = "{ugcId}/vote/{vote}", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "{ugcId}/vote/{vote}", notes = "This service guarantees that a user cannot be at the same " +
        "in" + "  opposite queue at the same time if this is detected a user will be remove from and added to the new" +
        " status")
    public T vote(@PathVariable(value = "ugcId") final String ugcId, @ApiParam("Type of action a user will perform " +
        "on" + " the Ugc Possible options are (VOTE_UP,UNVOTE_UP,VOTE_DOWN,UNVOTE_DOWN)") @PathVariable(value =
        "vote") final VoteOptions voteOption) throws SocialException {
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        String userId = "testUserId"; //=ProfileUtils.getCurrentProfile().getTenant();
        return (T)socialServices.vote(ugcId, voteOption, userId, tenant);
    }

    @RequestMapping(value = "{ugcId}/moderate/{moderateStatus}", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Change the moderation status of a UGC", notes = "The possible values are  UNMODERATED, " +
        "" + "PENDING, APPROVED, SPAM, TRASH. Notice that once a UGC is in a THRASH moderation status can't be change")
    public T moderate(@ApiParam("Id of the Ugc") @PathVariable(value = "ugcId") final String ugcId,
                      @ApiParam("New Status to set ot the ugc UNMODERATED, PENDING, APPROVED, SPAM, " +
                          "TRASH ") @PathVariable(value = "moderateStatus")
    final SocialUgc.ModerationStatus moderationStatus) throws SocialException {
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        String userId = "testUserId"; //=ProfileUtils.getCurrentProfile().getTenant();
        return (T)socialServices.moderate(ugcId, moderationStatus, userId, tenant);
    }


}
