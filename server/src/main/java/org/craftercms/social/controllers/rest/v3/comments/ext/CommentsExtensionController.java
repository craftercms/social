package org.craftercms.social.controllers.rest.v3.comments.ext;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.cache.TemplateLoader;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.VerificationToken;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.exceptions.UGCException;
import org.craftercms.social.services.notification.NotificationService;
import org.craftercms.social.services.social.SocialServices;
import org.craftercms.social.services.ugc.UGCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.wordnik.swagger.annotations.Api;

@Controller
@RequestMapping("/api/3/comments/extension")
@Api(value = "Comment Services Extension", basePath = "/api/3/comments/extension",
    description = "Comments services Extension")
public class CommentsExtensionController {

    private static final String APPROVER_RESULT_TEMPLATE_NAME = "APPROVER_RESULT_TEMPLATE";
    @Autowired
    protected SocialServices socialServices;

    @Autowired
    protected NotificationService notificationService;

    @Autowired
    protected UGCService ugcService;

    @Autowired
    @Qualifier("social.util.email.notificationFreemarkerLoader")
    protected TemplateLoader templateLoader;

    @Autowired
    @Qualifier("social.notification.emailModelExt")
    private Map<String, Object> extMap;

    @Autowired
    private ProfileService profileService;

    @Value("${studio.social.system.defaultLocale}")
    private String systemDefaultLocale;

    private Configuration cfg;

    private Logger logger = LoggerFactory.getLogger(CommentsExtensionController.class);


    @RequestMapping(value = "approve/{id}/{tokenId}", method = RequestMethod.GET)
    @ResponseBody
    public void approveComment(@PathVariable("id") final String ugcId, @PathVariable("tokenId") final String
        profileTokenId, HttpServletResponse response, HttpServletRequest request, @RequestParam(required = true,
        value = "context") final String context) throws IOException, TemplateException, SocialException, ProfileException {
        HashMap<String, Object> dataModel = new HashMap<>(extMap);
        dataModel.put("ugc", ugcId);
        dataModel.put("token", profileTokenId);
        dataModel.put("request", request);
        Profile profile = null;
        final PrintWriter writer = response.getWriter();
        try {
            final VerificationToken token = profileService.getVerificationToken(profileTokenId);
            if (token != null) {
                profile = profileService.getProfile(token.getProfileId());
            }
        } catch (ProfileException ex) {
            logger.error("Error getting the profile with given token", ex);
        }
        UGC ugc=ugcService.read(ugcId, context);
        if(ugc==null){
            response.getWriter().println("UGC not found");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (profile != null) {
            dataModel.put("expireToken", false);
            dataModel.put("profile", profile);
            dataModel.putAll(socialServices.approveComment(ugc, profile));
            try {
                ugc.setUser(profileService.getProfile(ugc.getCreatedBy()));
            } catch (ProfileException ex) {
                final Profile dummy = new Profile();
                dummy.setAttribute("displayName", "User Not found");
                ugc.setUser(dummy);
            }

        } else {
            dataModel.put("expireToken", true);
        }
        dataModel.put("ugc", ugc);
        Template template = cfg.getTemplate(context + "/" + APPROVER_RESULT_TEMPLATE_NAME, getProfileLocale(profile));
        final Environment env = template.createProcessingEnvironment(dataModel, writer);
        env.process();
        writer.flush();
        response.setContentType(MimeTypeUtils.TEXT_HTML_VALUE);
        response.setStatus(200);
    }


    @PostConstruct
    public void init() {
        cfg = new Configuration(Configuration.VERSION_2_3_21);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setOutputEncoding("UTF-8");
        cfg.setTemplateLoader(this.templateLoader);
    }


    private Locale getProfileLocale(final Profile profile) {
        if (profile == null) {
            return new Locale(systemDefaultLocale);
        } else {
            final Subject notificationLocale = profile.getAttribute("notificationLocale");
            if (notificationLocale == null) {
                return new Locale(systemDefaultLocale);
            } else {
                return new Locale(notificationLocale.toString());
            }
        }

    }
}
