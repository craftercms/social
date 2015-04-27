package org.craftercms.social.services.system;

/**
 * Created by cortiz on 4/16/15.
 */
public interface TenantConfigurationService {

    String HIDDEN_UGC_STATUS = "hiddenUgcStatus";

    <T> T getProperty(final String contextId, final String propertyName);

    void reloadTenant(String contextId);
}
