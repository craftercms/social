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

package org.craftercms.social.services.system;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.craftercms.profile.api.Profile;
import org.craftercms.social.exceptions.SocialException;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * Created by Carlos Ortiz on 10/28/14.
 */
public class EmailService {

    private Ehcache emailConfigCache;
    private ContextPreferencesService contextPreferences;

    public void sendEmail(final Profile toSend, final StringWriter writer, final String subject, final String contextId)
        throws SocialException {
        Map<String, Object> emailSettings = getEmailSettings(contextId);
        JavaMailSender sender = getSender(contextId);
        MimeMessage message = sender.createMimeMessage();
        String realSubject=subject;
        if(StringUtils.isBlank(realSubject)){
            realSubject=generateSubjectString(emailSettings.get("subject").toString());
        }
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toSend.getEmail());
            helper.setReplyTo(emailSettings.get("replyTo").toString());
            helper.setFrom(emailSettings.get("from").toString());
            helper.setSubject(realSubject);

            helper.setPriority(NumberUtils.toInt(emailSettings.get("priority").toString(),4));
            helper.setText(writer.toString(), true);
            message.setHeader("Message-ID", String.format("[%s]-%s-%s-%s", RandomStringUtils.randomAlphanumeric(5),contextId,
                realSubject,toSend.getId()));
            sender.send(message);
        } catch (MessagingException e) {
            throw new SocialException("Unable to send Email to " + toSend.getEmail(), e);
        }
    }

    private String generateSubjectString(final String message) {
        //TODO Find a pretty way to do this
        return message;
    }

    public void updateContextEmailCache(final String contextId) throws SocialException {
        final String preferenceCacheKey = contextId + "-preferences";
        final String javaMailCacheKey = contextId + "-javaMail";
        emailConfigCache.remove(preferenceCacheKey);
        emailConfigCache.remove(preferenceCacheKey);
        getEmailSettings(contextId);
        getSender(contextId);
    }

    protected Map<String, Object> getEmailSettings(final String contextId) throws SocialException {
        final String cacheKey = contextId + "-preferences";
        final Element config = emailConfigCache.get(cacheKey);
        if (config != null) {
            return (Map<String, Object>)config.getObjectValue();
        } else {
            final Map<String, Object> toReturn = contextPreferences.findEmailPreference(contextId);
            emailConfigCache.put(new Element(cacheKey, toReturn));
            return toReturn;
        }
    }

    public JavaMailSender getSender(final String contextId) throws SocialException {
        final String cacheKey = contextId + "-javaMail";
        final Element config = emailConfigCache.get(cacheKey);
        if (config != null) {
            return (JavaMailSender)config.getObjectValue();
        } else {
            return loadConfig(contextId + "-javaMail", getEmailSettings(contextId));
        }
    }

    private JavaMailSender loadConfig(final String cacheKey, final Map<String, Object> emailPreferences) throws
        SocialException {
        if (emailPreferences != null) {
            JavaMailSenderImpl toReturn = new JavaMailSenderImpl();
            toReturn.setDefaultEncoding(emailPreferences.get("encoding").toString());

            toReturn.setHost(emailPreferences.get("host").toString());
            toReturn.setPort(NumberUtils.toInt(emailPreferences.get("port").toString(), 25));
            final Properties javaMailProps = new Properties();
            if (Boolean.parseBoolean(emailPreferences.get("auth").toString())) {
                toReturn.setUsername(emailPreferences.get("username").toString());
                toReturn.setPassword(emailPreferences.get("password").toString());
                javaMailProps.put("mail.smtp.auth", "true");
            }
            if (Boolean.parseBoolean(emailPreferences.get("tls").toString())) {
                javaMailProps.put("mail.smtp.starttls.enable", "true");
            }
            toReturn.setJavaMailProperties(javaMailProps);
            emailConfigCache.put(new Element(cacheKey, toReturn));
            return toReturn;
        } else {
            throw new SocialException("Email is not configured for context " + cacheKey);
        }
    }

    public void setEmailConfigCache(Ehcache emailConfigCache) {
        this.emailConfigCache = emailConfigCache;
    }

    public void setContextPreferences(final ContextPreferencesService contextPreferences) {
        this.contextPreferences = contextPreferences;
    }
}
