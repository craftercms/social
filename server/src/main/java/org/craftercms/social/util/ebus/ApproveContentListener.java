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

package org.craftercms.social.util.ebus;

import freemarker.cache.TemplateLoader;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.craftercms.commons.ebus.annotations.EListener;
import org.craftercms.commons.ebus.annotations.EventHandler;
import org.craftercms.commons.ebus.annotations.EventSelectorType;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.SortOrder;
import org.craftercms.profile.api.VerificationToken;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.services.system.ContextPreferencesService;
import org.craftercms.social.services.system.EmailService;
import org.craftercms.social.services.system.TenantConfigurationService;
import org.slf4j.Logger;
import reactor.event.Event;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 *
 */
@EListener
public class ApproveContentListener {

    private static final String APPROVER_EMAIL_TEMPLATE_NAME = "APPROVEREMAIL";
    private ProfileService profileService;
    private TenantConfigurationService tenantConfigurationService;
    private Logger logger = org.slf4j.LoggerFactory.getLogger(ApproveContentListener.class);
    private TemplateLoader socialFreemarkerLoader;
    private Configuration cfg;
    private String systemDefaultLocale;
    private HashMap<String,Object> modelExt;
    private EmailService emailService;
    private ContextPreferencesService contextPreferencesService;

    @EventHandler(
        event = "ugc.create",
        ebus = SocialEventConstants.SOCIAL_REACTOR_NAME,
        type = EventSelectorType.REGEX)
    public void onAudit(final Event<? extends SocialEvent> socialEvent) {
        SocialEvent event = socialEvent.getData();

        UGC ugc = event.getSource();
        boolean moderateByMail = Boolean.parseBoolean(tenantConfigurationService.getProperty(event.getSource()
            .getContextId(), "moderateByMailEnable").toString());
        String moderateRole = tenantConfigurationService.getProperty(event.getSource().getContextId(),
            "moderateByMailRole");
        String emailSubject  = tenantConfigurationService.getProperty(event.getSource().getContextId(),
            "moderateByMailSubject");
        if (moderateByMail) {
            try {
                final Profile profile = profileService.getProfile(event.getUserId());
                ugc.setUser(profile);
                if (profile != null) {
                    final List<Profile> toSendEmail = profileService.getProfilesByQuery(profile.getTenant(),
                        "{\"attributes" + ".socialContexts" + ".id\":\"" + ugc.getContextId() + "\",\"attributes" +
                            "" + ".socialContexts" + ".roles\":{$in:[\""+moderateRole+"\"]},enabled:true,\"attributes"
                            + ".socialContexts.id\":\""+ugc.getContextId()+"\"}",
                        "createdOn", SortOrder.ASC, 0, 999);
                    logger.debug("To Send emails {}", toSendEmail);

                    buildEmailToApprover(toSendEmail,ugc,emailSubject,(String)event.getAttribute("baseUrl"));
                }// Profile Should be null , this UGC is just been created , user MUST exist!
            } catch (ProfileException e) {
                logger.error("Unable to get profiles information!", e);
            }

        }
    }

    private void buildEmailToApprover(final List<Profile> toSendEmail,UGC ugc,String emailSubject,final String baseUrl
        ) {

        for (Profile profile : toSendEmail) {
            try {
                HashMap<String,Object> dataModel = new HashMap(modelExt);
                final VerificationToken id = profileService.createVerificationToken(profile.getId().toString());
                dataModel.put("profile",profile);
                dataModel.put("ugc",ugc);
                dataModel.put("verificationToken",id);
                dataModel.put("baseUrl",baseUrl);
                final Map<String, Object> contextPreferences = contextPreferencesService.getContextPreferences(ugc
                    .getContextId());
                final TimeZone timezone = TimeZone.getTimeZone(((HashMap<String,Object>)contextPreferences.get("preferences")).get("timezone").toString());
                cfg.setTimeZone(timezone);
                StringWriter writer = new StringWriter();
                Template template=cfg.getTemplate(ugc.getContextId()+"/"+APPROVER_EMAIL_TEMPLATE_NAME,
                    getProfileLocale(profile.getAttribute("notificationLocale")));
                final Environment env = template.createProcessingEnvironment(dataModel, writer);
                env.process();
                writer.flush();
                emailService.sendEmail(profile,writer,emailSubject,ugc.getContextId());
            }catch (ProfileException ex){
                logger.error("Unable to generate Verification Token",ex);
            }catch (TemplateException | IOException ex){
                logger.error("Unable to generate email template",ex);
            } catch (SocialException ex) {
                logger.error("Unable to Send email ",ex);
            }catch (Throwable ex){
                logger.error("Unable to send email due a unknown exception",ex);
            }

        }
    }

    private Locale getProfileLocale(final Object notificationLocale) {
        if(notificationLocale==null){
            return new Locale(systemDefaultLocale);
        }else{
            return new Locale(notificationLocale.toString());
        }

    }


    public void init(){
        cfg = new Configuration(Configuration.VERSION_2_3_21);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setOutputEncoding("UTF-8");
        cfg.setTemplateLoader(socialFreemarkerLoader);
    }

    public void setProfileClient(final ProfileService profileService) {
        this.profileService = profileService;
    }

    public void setTenantConfigurationServiceImpl(TenantConfigurationService tenantConfigurationService) {
        this.tenantConfigurationService = tenantConfigurationService;
    }


    public void setSocialFreemarkerLoader(TemplateLoader socialFreemarkerLoader) {
        this.socialFreemarkerLoader=socialFreemarkerLoader;
    }

    public void setSystemDefaultLocale(final String systemDefaultLocale) {
        this.systemDefaultLocale = systemDefaultLocale;
    }

    public void setModelExt(final HashMap<String, Object> modelExt) {
        this.modelExt = modelExt;
    }


    public void setEmailService(EmailService emailService) {
        this.emailService=emailService;
    }

    public void setContextPreferencesService(final ContextPreferencesService contextPreferencesService) {
        this.contextPreferencesService = contextPreferencesService;
    }
}
