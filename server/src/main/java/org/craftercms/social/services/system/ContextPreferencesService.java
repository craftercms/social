package org.craftercms.social.services.system;

import java.util.Map;

import org.craftercms.social.exceptions.SocialException;

/**
 *
 */
public interface ContextPreferencesService {
    public Map findEmailPreference(final String contextId) throws SocialException;
    public String getNotificationEmailTemplate(final String contextId,final String notificationType) throws SocialException;
}
