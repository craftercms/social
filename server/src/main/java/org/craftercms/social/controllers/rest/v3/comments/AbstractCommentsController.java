package org.craftercms.social.controllers.rest.v3.comments;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordnik.swagger.annotations.Api;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.exception.AuthenticationRequiredException;
import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.security.SocialSecurityUtils;
import org.craftercms.social.services.notification.NotificationService;
import org.craftercms.social.services.social.SocialServices;
import org.craftercms.social.services.ugc.UGCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Generic Information about all Comments related Rest Services
 */
@Controller
@RequestMapping("/api/3/comments")
@Api(value = "Comment Services", basePath = "/api/3/comments", description = "Comments services")
public class AbstractCommentsController<T extends SocialUgc> {

    @Autowired
    protected SocialServices socialServices;

    @Autowired
    protected NotificationService notificationService;

    @Autowired
    protected UGCService ugcService;


    /**
     * Parse the json String to a map.
     *
     * @param attributes Json String to parse.
     * @return A map with the values of the JSON String
     * @throws org.springframework.web.bind.MissingServletRequestParameterException If String can't be parsed.
     */
    protected Map<String, Object> parseAttributes(final String attributes) throws

        MissingServletRequestParameterException {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory(); // since 2.1 use mapper.getFactory() instead


        try {
            JsonParser jp = factory.createParser(attributes);
            return mapper.readValue(jp, HashMap.class);
        } catch (IOException e) {
            throw new MissingServletRequestParameterException("attributes", "Json");
        }
    }

    /**
     * Gets current context.
     *
     * @return current context, never null.
     */
    protected String context() {
        return SocialSecurityUtils.getContext();
    }

    /**
     * Current user id.
     *
     * @return Current User Id, Empty if a user is not logged.
     */
    protected String userId() {
        ObjectId id = getCurrentProfile().getId();
        if (id == null) {
            // This is if user is not there or anonymous.
            throw new AuthenticationRequiredException("Missing or expired auth token");
        }
        return id.toString();
    }

    /**
     * Gets Current User profile.
     *
     * @return Profile of current Logged User.
     */
    protected Profile getCurrentProfile() {
        return SocialSecurityUtils.getCurrentProfile();
    }

    /**
     * Gets the content type of the file based on the file extension.
     *
     * @param filename File name to check.
     * @return Content Type of the file based on filename.
     */
    protected String getContentType(String filename) {
        return new MimetypesFileTypeMap().getContentType(filename);
    }
}
