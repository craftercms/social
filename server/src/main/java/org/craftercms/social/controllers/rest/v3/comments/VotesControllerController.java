/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.controllers.rest.v3.comments;

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

    @RequestMapping(value = "{id}/votes/up", method = RequestMethod.POST)
    @ResponseBody
    public T voteUp(@PathVariable(value = "id") final String id) throws
        SocialException {
        String context = context();
        String userId = userId();
        return (T)socialServices.vote(id, VoteOptions.VOTE_UP,userId, context);
    }

    @RequestMapping(value = "{id}/votes/down", method = RequestMethod.POST)
    @ResponseBody
    public T voteDown(@PathVariable(value = "id") final String id) throws
        SocialException {
        String context = context();
        String userId = userId();
        return (T)socialServices.vote(id, VoteOptions.VOTE_DOWN, userId, context);
    }

    @RequestMapping(value = "{id}/votes/neutral", method = RequestMethod.POST)
    @ResponseBody
    public T voteNeutral(@PathVariable(value = "id") final String id) throws SocialException {
        String context = context();
        String userId = userId();
        return (T)socialServices.vote(id, VoteOptions.VOTE_NEUTRAL, userId, context);
    }



}
