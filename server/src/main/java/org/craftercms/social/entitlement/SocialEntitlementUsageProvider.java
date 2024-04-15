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

package org.craftercms.social.entitlement;

import java.util.Arrays;
import java.util.List;

import org.craftercms.commons.entitlements.exception.UnsupportedEntitlementException;
import org.craftercms.commons.entitlements.model.EntitlementType;
import org.craftercms.commons.entitlements.model.Module;
import org.craftercms.commons.entitlements.usage.EntitlementUsageProvider;
import org.craftercms.social.repositories.SocialContextRepository;
import org.craftercms.social.repositories.ugc.UGCRepository;

import static org.craftercms.commons.entitlements.model.Module.SOCIAL;

/**
 * Implementation of {@link EntitlementUsageProvider} for Crafter Social module.
 *
 * @author joseross
 */
public class SocialEntitlementUsageProvider implements EntitlementUsageProvider {

    /**
     * Current instance of {@link SocialContextRepository}.
     */
    protected SocialContextRepository socialContextRepository;

    /**
     * Current instance of {@link UGCRepository}.
     */
    protected UGCRepository ugcRepository;

    public SocialEntitlementUsageProvider(final SocialContextRepository socialContextRepository,
                                          final UGCRepository ugcRepository) {
        this.socialContextRepository = socialContextRepository;
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
    public List<EntitlementType> getSupportedEntitlements() {
        return Arrays.asList(EntitlementType.SITE, EntitlementType.ITEM);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doGetEntitlementUsage(final EntitlementType type) throws Exception {
        switch (type) {
            case SITE:
                return (int) socialContextRepository.count();
            case ITEM:
                return (int) ugcRepository.count();
            default:
                throw new UnsupportedEntitlementException(SOCIAL, type);
        }
    }
    
}
