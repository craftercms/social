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

package org.craftercms.social.services.system;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import com.google.common.cache.Cache;
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

    private Cache<String, Object> emailConfigCache;
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
        emailConfigCache.invalidate(preferenceCacheKey);
        emailConfigCache.invalidate(javaMailCacheKey);
        getEmailSettings(contextId);
        getSender(contextId);
    }

    protected Map<String, Object> getEmailSettings(final String contextId) throws SocialException {
        final String cacheKey = contextId + "-preferences";
        final Map<String, Object> config = (Map<String, Object>) emailConfigCache.getIfPresent(cacheKey);
        if (config != null) {
            return config;
        } else {
            final Map<String, Object> toReturn = contextPreferences.findEmailPreference(contextId);
            emailConfigCache.put(cacheKey, toReturn);
            return toReturn;
        }
    }

    public JavaMailSender getSender(final String contextId) throws SocialException {
        final String cacheKey = contextId + "-javaMail";
        final JavaMailSender config = (JavaMailSender) emailConfigCache.getIfPresent(cacheKey);
        if (config != null) {
            return config;
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
            emailConfigCache.put(cacheKey, toReturn);
            return toReturn;
        } else {
            throw new SocialException("Email is not configured for context " + cacheKey);
        }
    }

    public void setEmailConfigCache(Cache<String, Object> emailConfigCache) {
        this.emailConfigCache = emailConfigCache;
    }

    public void setContextPreferences(final ContextPreferencesService contextPreferences) {
        this.contextPreferences = contextPreferences;
    }
}
