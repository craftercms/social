/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.entitlement;

import java.util.Arrays;
import java.util.List;

import org.craftercms.commons.entitlements.model.Entitlement;
import org.craftercms.commons.entitlements.model.EntitlementType;
import org.craftercms.commons.entitlements.model.Module;
import org.craftercms.commons.entitlements.usage.EntitlementUsageProvider;
import org.craftercms.social.repositories.SocialContextRepository;
import org.craftercms.social.repositories.ugc.UGCRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import static org.craftercms.commons.entitlements.model.Module.SOCIAL;

/**
 * Implementation of {@link EntitlementUsageProvider} for Crafter Social module.
 *
 * @author joseross
 */
public class SocialEntitlementUsageProvider implements EntitlementUsageProvider {

    private static final Logger logger = LoggerFactory.getLogger(SocialEntitlementUsageProvider.class);

    /**
     * Current instance of {@link SocialContextRepository}.
     */
    protected SocialContextRepository socialContextRepository;

    /**
     * Current instance of {@link UGCRepository}.
     */
    protected UGCRepository ugcRepository;

    @Required
    public void setSocialContextRepository(final SocialContextRepository socialContextRepository) {
        this.socialContextRepository = socialContextRepository;
    }

    @Required
    public void setUgcRepository(final UGCRepository ugcRepository) {
        this.ugcRepository = ugcRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Module getModule() {
        return SOCIAL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Entitlement> getCurrentUsage() {
        Entitlement sites = new Entitlement();
        sites.setType(EntitlementType.SITE);
        Entitlement items = new Entitlement();
        items.setType(EntitlementType.ITEM);

        try {
            items.setValue((int) ugcRepository.count());
            sites.setValue((int) socialContextRepository.count());
        } catch (Exception e) {
            logger.error("Error fetching data", e);
        }
        return Arrays.asList(items, sites);
    }
    
}
