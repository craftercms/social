/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.collections.IterableUtils;
import org.craftercms.profile.api.Profile;
import org.craftercms.social.controllers.rest.v3.comments.exceptions.UGCNotFound;
import org.craftercms.social.domain.social.Flag;
import org.craftercms.social.domain.social.ModerationStatus;
import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.exceptions.IllegalUgcException;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.exceptions.UGCException;
import org.craftercms.social.security.SocialSecurityUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 */
@Controller
public class CommentsController<T extends SocialUgc> extends AbstractCommentsController {

    private Logger log = LoggerFactory.getLogger(CommentsController.class);

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public T create(@RequestParam final String body,
                    @RequestParam final String thread,
                    @RequestParam final String parent,
                    @RequestParam(required = false, defaultValue = "false", value = "anonymous") final boolean anonymous,
                    @RequestParam(required = false, defaultValue = "", value = "subject") final String subject,
                    @RequestParam(required = false, defaultValue = "{}") final String attributes, MultipartFile attachment)
            throws SocialException, MissingServletRequestParameterException, IOException {
        Map<String, Object> attributesMap = null;

        if (!StringUtils.isBlank(attributes)) {
            attributesMap = parseAttributes(attributes);
        }
        T newUgc = (T)ugcService.create(context(), parent, thread, body, subject, attributesMap, checkAnonymous
            (anonymous));

        if (attachment != null) {
            ugcService.addAttachment(newUgc.getId().toString(), context(), attachment.getInputStream(), attachment
                .getOriginalFilename(), getContentType(attachment.getOriginalFilename()));
        }
        return newUgc;
    }



    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    @ResponseBody
    public T update(@PathVariable("id") final String id,
                    @RequestParam final String body,
                    @RequestParam(required = false, defaultValue = "{}") final String attributes)
            throws SocialException, MissingServletRequestParameterException, UGCNotFound {
        Map<String, Object> attributesMap = null;
        if (!StringUtils.isBlank(attributes)) {
            attributesMap = parseAttributes(attributes);
        }
        return (T)ugcService.update(id, body, "", context(), attributesMap);
    }

    @RequestMapping(value = "{id}/update", method = RequestMethod.POST)
    @ResponseBody
    public T updatePost(@PathVariable("id") final String id,
                        @RequestParam final String body,
                        @RequestParam(required = false, defaultValue = "{}") final String attributes)
            throws SocialException, MissingServletRequestParameterException, UGCNotFound {
        return this.update(id, body, attributes);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public boolean delete(@PathVariable("id") final String id) throws SocialException {
        ugcService.deleteUgc(id, context());
        return true;
    }

    @RequestMapping(value = "{id}/delete", method = RequestMethod.POST)
    @ResponseBody
    public boolean deletePost(@PathVariable("id") final String id) throws SocialException {
        return this.delete(id);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @ResponseBody
    public T read(@PathVariable("id") final String id) throws SocialException {
        return (T)ugcService.read(id, context());
    }


    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public Iterable<T> read(
                            @RequestParam final String search,
                            @RequestParam final String sortBy,
                            @RequestParam int start,
                            @RequestParam int limit) throws  SocialException {
        return ugcService.search(context(),search,sortBy,start,limit);
    }



    @RequestMapping(value = "{id}/attributes", method = {RequestMethod.POST, RequestMethod.PUT}, consumes =
        {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @ResponseBody
    public boolean addAttributes(@NotBlank @PathVariable(value = "id") final String id,
                                 @RequestParam final Map<String, Object> attributes) throws SocialException, UGCNotFound {
        log.debug("Request for deleting form  UGC {} attributes {}", id, attributes);
        attributes.remove("context");
        ugcService.setAttributes(id, context(), attributes);
        return true;//Always true unless exception.
    }


    @RequestMapping(value = "{id}/attributes", method = RequestMethod.DELETE)
    @ResponseBody
    public boolean removeAttributes(@PathVariable(value = "id") final String id,
                                    @RequestParam final String attributes) throws SocialException {
        log.debug("Request for deleting form  UGC {} attributes {}", id, attributes);
        ugcService.deleteAttribute(id, attributes.split(","), context());
        return true;//Always true unless exception.
    }

    @RequestMapping(value = "{id}/attributes/delete", method = RequestMethod.POST)
    @ResponseBody
    public boolean removeAttributesPost(@PathVariable(value = "id") final String id,
                                        @RequestParam final String attributes) throws SocialException {
       return this.removeAttributes(id, attributes);
    }

    @RequestMapping(value = "{id}/flags", method = RequestMethod.POST)
    @ResponseBody
    public T flagUgc(@PathVariable(value = "id") final String id,
                     @RequestParam final String reason) throws SocialException {
        return (T)socialServices.flag(id, context(), reason, userId());
    }


    @RequestMapping(value = "{id}/flags", method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Flag> flagUgc(@PathVariable(value = "id") final String id) throws
        SocialException {
        T ugc = (T)ugcService.read(id, context());
        if (ugc == null) {
            throw new IllegalUgcException("Given UGC does not exist for context");
        }
        return ugc.getFlags();
    }

    @RequestMapping(value = "{id}/flags/{flagId}", method = {RequestMethod.POST, RequestMethod.DELETE})
    @ResponseBody
    public boolean unflagUgc(@PathVariable(value = "id") final String id,
                             @PathVariable(value = "flagId") final String flagId) throws SocialException {
        return socialServices.unFlag(id, flagId, userId(), context());
    }

    @RequestMapping(value = "{id}/moderate", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public T moderate(@PathVariable final String id,
                      @RequestParam final ModerationStatus status) throws SocialException {

        return (T)socialServices.moderate(id, status, userId(), context());
    }



    @RequestMapping(value = "moderation/{status}", method = RequestMethod.GET)
    @ResponseBody
    public Iterable<T> byStatus(@PathVariable("status") final ModerationStatus status,
                                @RequestParam(defaultValue = "", required = false) final String thread,
                                @RequestParam(required = false, defaultValue = "0") final int pageNumber,
                                @RequestParam(required = false, defaultValue = ThreadsController.MAX_INT) final int pageSize,
                                @RequestParam(required = false) final List<String> sortBy,
                                @RequestParam(required = false) final
    List<SocialSortOrder> sortOrder) throws UGCException {
        int start = 0;
        if (pageNumber > 0 && pageSize > 0) {
            start = ThreadsController.getStart(pageNumber, pageSize);
        }

        return IterableUtils.toList(socialServices.findByModerationStatus(status, thread, context(), start, pageSize,
            ThreadsController.getSortOrder(sortBy, sortOrder)));
    }


    @RequestMapping(value = "flagged", method = RequestMethod.GET)
    @ResponseBody
    public Iterable<T> flagged(@RequestParam(required = false, defaultValue = "0") final int pageNumber,
                               @RequestParam(required = false, defaultValue = ThreadsController.MAX_INT) final int pageSize,
                               @RequestParam(required = false) final List<String> sortBy,
                               @RequestParam(required = false) final List<SocialSortOrder> sortOrder) throws UGCException {
        int start = 0;
        if (pageNumber > 0 && pageSize > 0) {
            start = ThreadsController.getStart(pageNumber, pageSize);
        }

        return IterableUtils.toList(socialServices.findAllFlaggedUgs(context(), start, pageSize, ThreadsController
            .getSortOrder(sortBy, sortOrder)));
    }

    @RequestMapping(value = "flagged/count", method = RequestMethod.GET)
    @ResponseBody
    public long flaggedCount(@RequestParam(required = false, defaultValue = "0") final int pageNumber,
                             @RequestParam(required = false, defaultValue = ThreadsController.MAX_INT) final int pageSize,
                             @RequestParam(required = false) final List<String> sortBy,
                             @RequestParam(required = false) final List<SocialSortOrder> sortOrder)
        throws UGCException {
        int start = 0;
        if (pageNumber > 0 && pageSize > 0) {
            start = ThreadsController.getStart(pageNumber, pageSize);
        }

        return socialServices.countAllFlaggedUgs(context(), start, pageSize, ThreadsController.getSortOrder(sortBy,
            sortOrder));
    }


    @RequestMapping(value = "moderation/{status}/count", method = RequestMethod.GET)
    @ResponseBody
    public long byStatusCount(@PathVariable("status") final ModerationStatus status,
                              @RequestParam(defaultValue = "", required = false) final String thread) throws UGCException {
        return socialServices.countByModerationStatus(status, thread, context());
    }


    protected boolean checkAnonymous(final boolean anonymous) {
        final Profile profile = SocialSecurityUtils.getCurrentProfile();
        Object isAlwaysAnonymous = profile.getAttribute("isAlwaysAnonymous");
        if (isAlwaysAnonymous == null) {
            return anonymous;
        } else {
            return ((Boolean)isAlwaysAnonymous).booleanValue();
        }

    }

}
