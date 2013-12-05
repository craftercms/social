package org.craftercms.social.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.craftercms.social.services.ClientConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * Default Implementation of ClientConfigurationService.
 */
public class ClientConfigurationServiceImpl implements ClientConfigurationService {

    private HashMap<String, Object> configuration;
    private List<Resource> configurationFiles;
    private Logger log = LoggerFactory.getLogger(ClientConfigurationServiceImpl.class);

    @Override
    public Map<String, Object> getClientConfiguration() {
        return this.configuration;
    }

    /**
     * Reads and populates the configuration map.
     */
    public void init() {
        this.configuration = new HashMap<String, Object>();
        for (Resource configurationFile : configurationFiles) {
            if (configurationFile.exists()) {
                try {
                    InputStream file = configurationFile.getInputStream();
                    Properties properties = new Properties();
                    properties.load(file);
                    file.close();
                    copyPropertiesToMap(properties);
                } catch (IOException e) {
                    log.error("Unable to load File " + configurationFile.getFilename(), e);
                }
            }
        }
    }

    /**
     * Copies the read property file to the map.
     *
     * @param properties
     */
    private void copyPropertiesToMap(final Properties properties) {
        for (Object key : properties.keySet()) {
            Map<String, Object> pivot = null;
            for (StringTokenizer tokenizer = new StringTokenizer(key.toString(), "."); tokenizer.hasMoreTokens(); ) {
                String token = tokenizer.nextToken();
                if (tokenizer.hasMoreTokens()) {
                    pivot = (Map<String, Object>)configuration.get(token);
                    if (pivot == null) {
                        pivot = new HashMap<String, Object>();
                        configuration.put(token, pivot);
                    }
                } else {
                    if (pivot != null) {
                        pivot.put(token, properties.get(key.toString()));
                    } else {
                        configuration.put(key.toString(), properties.get(key.toString()));
                    }

                }
            }
        }
    }

    public void setConfigurationFiles(final List<Resource> configurationFiles) {
        this.configurationFiles = configurationFiles;
    }
}
