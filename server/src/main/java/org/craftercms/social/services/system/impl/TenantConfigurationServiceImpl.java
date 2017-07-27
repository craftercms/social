package org.craftercms.social.services.system.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.system.ContextPreferences;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.repositories.system.ContextPreferencesRepository;
import org.craftercms.social.services.system.ContextPreferencesService;
import org.craftercms.social.services.system.TenantConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**

 */
public class TenantConfigurationServiceImpl implements TenantConfigurationService {

    private Ehcache tenantConfigCache;
    private Properties systemDefaults;
    private List<Resource> defaultLocations;
    private ContextPreferencesService contextPreferencesService;
    private Logger log = LoggerFactory.getLogger(TenantConfigurationServiceImpl.class);
    private ContextPreferencesRepository contextPreferencesRepository;

    @Override
    public <T> T getProperty(final String contextId, final String propertyName) {
        Map<String, Object> tenantConfig = null;
        T pValue = null;
        if (tenantConfigCache.isKeyInCache(contextId)) {
            tenantConfig = (Map<String, Object>)((Element)tenantConfigCache.get(contextId)).getObjectValue();
        }
        if(tenantConfig==null){
            reloadTenant(contextId);
            //Key should be in the cache since we just reload.
            Element tenantCacheConfig=tenantConfigCache.get(contextId);
            if(tenantCacheConfig!=null) {
                tenantConfig = (Map<String, Object>)tenantCacheConfig.getObjectValue();
            }
        }
        if (tenantConfig != null && tenantConfig.containsKey(propertyName)) {
            pValue = (T)tenantConfig.get(propertyName);
        } else if (systemDefaults.containsKey(propertyName)) {
            pValue = (T)systemDefaults.get(propertyName);
        } else {
            log.info("The configuration key {} does not exist as a tenant property or as a system property", propertyName);
        }
        return pValue;
    }

    @Override
    public void reloadTenant(String contextId) {
        final Map<String, Object> pref = contextPreferencesService.getContextPreferences(contextId);
        if(pref.containsKey("preferences")){
            final HashMap<String,Object> prefMap = new HashMap<>((HashMap<String,Object>)pref.get("preferences"));
            prefMap.put("contextId",contextId);
            if (pref != null) {
                tenantConfigCache.remove(contextId);
                tenantConfigCache.put(new Element(contextId, prefMap));
            } else {
                log.error("Unable to get preferences for context {} will not update", contextId);
            }
        }else{
            log.error("Context has empty  preferences {} ignoring", contextId);
        }

    }

    public void loadDefaults() throws MongoDataException, SocialException {
        systemDefaults = new Properties();
        for (Resource resource : defaultLocations) {
            if (resource.exists() && resource.isReadable()) {
                try {
                    final InputStream in = resource.getInputStream();
                    systemDefaults.load(in);
                    in.close();
                } catch (IOException ex) {
                    log.error("Unable to read file " + resource.getFilename(), ex);
                }
            }
        }
        if (systemDefaults.isEmpty()) {
            throw new IllegalStateException("System default properties are empty");
        }

        final List<Map<String, Object>> toCache = getAllPreferences();
        for (Map<String, Object> contextPref : toCache) {
            tenantConfigCache.put(new Element(contextPref.get("contextId"), contextPref));
        }
    }

    protected List<Map<String, Object>> getAllPreferences() throws SocialException {
        List<Map<String, Object>> toReturn = new ArrayList<>();
        try {
            final Iterable<ContextPreferences> preferences = contextPreferencesRepository.findAll();
            if (preferences != null) {
                final Iterator<ContextPreferences> iter = preferences.iterator();
                while (iter.hasNext()) {
                    final ContextPreferences pref = iter.next();
                    HashMap<String, Object> toAdd = new HashMap<>();
                    if (pref.getPreferences() != null) {
                        toAdd.putAll(pref.getPreferences());
                    }
                    toAdd.put("contextId", pref.getContextId());
                    toReturn.add(toAdd);
                }
            }
            return toReturn;
        } catch (MongoDataException e) {
            throw new SocialException("Unable to get preferences", e);
        }
    }

    public void setTenantConfigCache(Ehcache tenantConfigCache) {
        this.tenantConfigCache = tenantConfigCache;
    }

    public void setDefaultLocations(List<Resource> defaultLocations) {
        this.defaultLocations = defaultLocations;
    }


    public void setContextPreferencesServiceImpl(ContextPreferencesService contextPreferencesService) {
        this.contextPreferencesService = contextPreferencesService;
    }

    public void setContextPreferencesRepository(ContextPreferencesRepository contextPreferencesRepository) {
        this.contextPreferencesRepository = contextPreferencesRepository;
    }
}
