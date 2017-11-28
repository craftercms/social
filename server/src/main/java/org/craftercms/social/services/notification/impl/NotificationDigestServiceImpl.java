/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
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

package org.craftercms.social.services.notification.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import freemarker.cache.TemplateLoader;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.profile.api.Profile;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.services.notification.NotificationDigestService;
import org.craftercms.social.services.system.ContextPreferencesService;
import org.craftercms.social.services.system.EmailService;
import org.craftercms.social.util.LoggerFactory;
import org.craftercms.social.util.SocialFreemarkerLoader;
import org.craftercms.social.util.profile.ProfileAggregator;
import org.slf4j.Logger;

/**
 *
 */
public class NotificationDigestServiceImpl implements NotificationDigestService {

    public static final String DEFAULT_LOADPATH = "classpath:/crafter/social/notifications";

    private I10nLogger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private Logger log = org.slf4j.LoggerFactory.getLogger(NotificationDigestServiceImpl.class);
    private ProfileAggregator profileAggregator;
    private TemplateLoader socialFreemarkerLoader;
    private Configuration cfg;
    private EmailService emailService;
    private String systemDefaultLocale;
    private Map<String,Object> modelExt;
    private ContextPreferencesService preferencesService;

    public void setProfileAggregatorImpl(ProfileAggregator profileAggregator) {
        this.profileAggregator = profileAggregator;
    }

    @Override
    public void digest(final List<HashMap> auditDigest, final String profileId, final String type) {
        Profile toSend = profileAggregator.getProfile(profileId);
        if (toSend != null) {
            init();
            final HashMap<String, Object> dataModel = new HashMap<>(modelExt);
            //TODO Fix this for multi context users!
            List<String> contexts= new ArrayList<>();
            dataModel.put("profile", toSend);
            for (HashMap hashMap : auditDigest) {
                if(!contexts.contains(hashMap.get("contextId"))) {
                    contexts.add(hashMap.get("contextId").toString());
                }
            }
            for (String contextId : contexts) {
                try {
                    StringWriter writer = new StringWriter();
                    dataModel.put("digest", auditDigest);
                    final Map<String, Object> preferences = preferencesService.getContextPreferences(contextId);
                    final String timezoneId=((HashMap<String,Object>)preferences.get("preferences")).get("timezone").toString();
                    cfg.setTimeZone(TimeZone.getTimeZone(timezoneId));
                    Template template = cfg.getTemplate(contextId + "/" + type,getProfileLocale(toSend
                        .getAttribute("notificationLocale")));
                    dataModel.put("baseUrl", preferences.get("baseUrl"));
                    final Environment env = template.createProcessingEnvironment(dataModel, writer);
                    env.process();
                    writer.flush();
                    emailService.sendEmail(toSend,writer,null, contextId);
                    log.info("Notification Email send to {} for ctxId {}",toSend.getEmail(),contextId);
                } catch (IOException | TemplateException | SocialException ex) {
                    logger.error("logging.system.notification.errorLoadingTemplate", ex);
                }
            }

        } else {
            logger.error("Unable to send notification to profile {} it does not exist", profileId);
        }
    }


    private Locale getProfileLocale(final Object notificationLocale) {
        if(notificationLocale==null){
            return new Locale(systemDefaultLocale);
        }else{
            return new Locale(notificationLocale.toString());
        }

    }

    public void init() {
        cfg = new Configuration(Configuration.VERSION_2_3_21);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setOutputEncoding("UTF-8");

        cfg.setTemplateLoader(socialFreemarkerLoader);
    }


    public void setSocialFreemarkerLoader(TemplateLoader socialFreemarkerLoader) {
        this.socialFreemarkerLoader = socialFreemarkerLoader;
    }

    public void setEmailService(final EmailService emailService) {
        this.emailService = emailService;
    }

    public void setSystemDefaultLocale(final String systemDefaultLocale) {
        this.systemDefaultLocale = systemDefaultLocale;
    }

    public void setPreferencesService(final ContextPreferencesService preferencesService) {
        this.preferencesService = preferencesService;
    }

    public void setModelExt(Map modelExt) {
        this.modelExt=modelExt;
    }



}
