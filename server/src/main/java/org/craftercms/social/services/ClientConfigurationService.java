package org.craftercms.social.services;

import java.util.Map;

/**
 * Configuration File for Clients.
 */
public interface ClientConfigurationService {
    /**
     * Returns a map of the Client configuration for this instance.
     * This configuration.
     * @return A map with the Configuration.
     */
    Map<String,Object> getClientConfiguration();
}
