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

package org.craftercms.social.services.ugc.pipeline;

import java.util.Date;
import java.util.Map;

import org.craftercms.profile.api.Profile;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.security.SocialSecurityUtils;
import org.craftercms.social.services.ugc.UgcPipe;

/**
 * Created by cortiz on 5/29/14.
 */
public class MetadataPipe implements UgcPipe {

    @Override
    public <T extends UGC> void process(final T ugc,Map<String,Object> params) throws SocialException {
        if(ugc.getCreatedBy()==null){
            ugc.setCreatedBy(SocialSecurityUtils.getCurrentProfile().getId().toString());
        }
        if (ugc.getCreatedDate() == null) {
            ugc.setCreatedDate(new Date());
        }
        ugc.setLastModifiedDate(new Date());
        if(params!=null && params.containsKey("modifierProfile")){
            Profile profile = (Profile)params.get("modifierProfile");
            ugc.setLastModifiedBy(profile.getId().toString());
        }else {
            ugc.setLastModifiedBy(SocialSecurityUtils.getCurrentProfile().getId().toString());
        }
        ugc.setContextId(SocialSecurityUtils.getContext());
        ugc.setChildren(null);
    }
}
