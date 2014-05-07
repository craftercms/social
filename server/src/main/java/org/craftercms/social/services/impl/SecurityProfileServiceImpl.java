package org.craftercms.social.services.impl;

import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.Actions;
import org.craftercms.social.exceptions.SecurityProfileException;
import org.craftercms.social.repositories.SecurityProfileRepository;
import org.craftercms.social.services.SecurityProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Implementation Of SecurityProfileService.
 */
public class SecurityProfileServiceImpl implements SecurityProfileService {

    private SecurityProfileRepository securityProfileRepository;
    private Logger log = LoggerFactory.getLogger(SecurityProfileServiceImpl.class);

    @Override
    public Iterable<String> findActionsFor(final String securityProfile) throws SecurityProfileException {
        log.debug("Getting actions for {}", securityProfile);
        try {
            return securityProfileRepository.findActionsFor(securityProfile);
        } catch (MongoDataException ex) {
            log.error("Unable to get Actions for security profile " + securityProfile, ex);
            throw new SecurityProfileException("Unable to find security Profile", ex);
        }
    }

    @Override
    public Actions getDefaultSecurityProfile() throws SecurityProfileException {
        try {
            return securityProfileRepository.findDefault();
        } catch (MongoDataException e) {
            log.error("Unable to find default security Profile", e);
            throw new SecurityProfileException("Unable to find default security Profile");
        }
    }

    public void setSecurityProfileRepository(final SecurityProfileRepository securityProfileRepository) {
        this.securityProfileRepository = securityProfileRepository;
    }
}
