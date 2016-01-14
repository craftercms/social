/*
 * Copyright (C) 2007-2015 Crafter Software Corporation.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.controllers.rest.v3.comments;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileAttachment;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.security.SocialSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.wordnik.swagger.annotations.Api;

@Controller
@RequestMapping("/api/3/profile")
@Api(value = "Comment Services", basePath = "/api/3/profile", description = "Comments services")
public class ProfileHelperController {

    public static final String AVATAR = "avatar";
    @Autowired
    protected ProfileService profileService;

    @RequestMapping(value = "/avatar/{profileId}", method = RequestMethod.POST)
    @ResponseBody
    public Profile getProfileAvatar(MultipartHttpServletRequest request, HttpServletResponse response,
                                    @PathVariable("profileId") String profileId) throws IOException,
        ProfileException {
        final Profile profile = SocialSecurityUtils.getCurrentProfile();
        if (profile != null && !profile.getUsername().equalsIgnoreCase(SocialSecurityUtils.ANONYMOUS)) {
            final Iterator<String> files = request.getFileNames();
            String fileName = null;
            if (files.hasNext()) {
                fileName = files.next();
            }
            if (!StringUtils.isBlank(fileName)) {
                final MultipartFile avatar = request.getFile(fileName);
                profileService.addProfileAttachment(profile.getId().toString(), AVATAR + "." + FilenameUtils
                    .getExtension(avatar.getOriginalFilename()).toLowerCase(), avatar.getInputStream());
            }
            return profile;
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
    }

    @RequestMapping(value = "/avatar/{profileId}", method = RequestMethod.GET)
    public void getProfileAvatar(HttpServletResponse response, @PathVariable("profileId") String profileId,
                                 HttpServletRequest request) throws IOException, SocialException {
        InputStream input = null;
        boolean imageFound = false;
        try {
            try {
                if (ObjectId.isValid(profileId)) {
                    final ProfileAttachment information = profileService.getProfileAttachmentInformation(profileId,
                        AVATAR);
                    if (information != null) {
                        response.setContentType(information.getContentType());
                        response.setContentLength((int)information.getFileSizeBytes());
                        response.setHeader("Cache-Control","max-age=3600");
                        response.setStatus(HttpServletResponse.SC_OK);
                        input = profileService.getProfileAttachment(AVATAR, profileId);
                        IOUtils.copy(input, response.getOutputStream());
                        imageFound = true;
                    }
                }
            } catch (ProfileException ex) {
                imageFound=false;
            }
            if (!imageFound) {
                response.sendRedirect(request.getContextPath()+"/resources/silhouette.png");
                }
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }
}
