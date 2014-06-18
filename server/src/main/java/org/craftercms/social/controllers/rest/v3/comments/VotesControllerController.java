package org.craftercms.social.controllers.rest.v3.comments;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.services.social.VoteOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class VotesControllerController<T> extends AbstractCommentsController {

    private Logger log = LoggerFactory.getLogger(VotesControllerController.class);

    @RequestMapping(value = "{ugcId}/vote/up", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Votes Up a comment for the current logged user.",notes = "If a user already vote the " +
        "current comment down, it will be remove before counting the vote up.")
    public T voteUp(@PathVariable(value = "ugcId") @ApiParam(value = "Ugc id to vote up") final String ugcId) throws
        SocialException {
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        String userId = "testUserId"; //=ProfileUtils.getCurrentProfile().getTenant();
        return (T)socialServices.vote(ugcId, VoteOptions.VOTE_UP, userId, tenant);
    }

    @RequestMapping(value = "{ugcId}/vote/down", method = RequestMethod.POST)
         @ResponseBody
         @ApiOperation(value = "Votes Down a comment for the current logged user.",
             notes = "If a user already vote up the comment it will remove the up before counting the vote down.")
         public T voteDown(@PathVariable(value = "ugcId") @ApiParam(value = "Ugc id to vote up") final String ugcId) throws
        SocialException {
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        String userId = "testUserId"; //=ProfileUtils.getCurrentProfile().getTenant();
        return (T)socialServices.vote(ugcId, VoteOptions.VOTE_DOWN, userId, tenant);
    }

    @RequestMapping(value = "{ugcId}/vote/netural", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Votes Neutral (removes user vote for either up or down)")
    public T voteNeutral(@PathVariable(value = "ugcId") @ApiParam(value = "Ugc id to vote up") final String ugcId)
        throws
        SocialException {
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        String userId = "testUserId"; //=ProfileUtils.getCurrentProfile().getTenant();
        return (T)socialServices.vote(ugcId, VoteOptions.VOTE_NEUTRAL, userId, tenant);
    }




}
