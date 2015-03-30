package org.craftercms.social.services.system;

import java.util.Map;

import org.craftercms.social.exceptions.SocialException;

/**
 *
 */
public interface ContextPreferencesService {
    Map findEmailPreference(final String contextId) throws SocialException;
    String getNotificationEmailTemplate(final String contextId,final String notificationType) throws SocialException;

    Map<String, Object> getContextPreferences(String contextId);

    boolean saveContextPreference(String contextId,Map<String,Object> preferences);

    boolean saveEmailTemplate(String context, String type, final String template) throws SocialException;

    String getEmailTemplate(String context, String emailTemplateType) throws  SocialException;
}
