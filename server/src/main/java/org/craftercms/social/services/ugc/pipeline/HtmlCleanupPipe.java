/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
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

package org.craftercms.social.services.ugc.pipeline;

import java.util.Map;

import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.social.Flag;
import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.services.ugc.UgcPipe;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Cleans up the body of the UGC to prevent XSS
 */
public class HtmlCleanupPipe implements UgcPipe{
    private final Whitelist whitelist = Whitelist.relaxed().addTags("div","em");
    @Override
    public <T extends UGC> void process(final T ugc,Map<String,Object> params) throws SocialException {
        ugc.setBody(cleanup(ugc.getBody()));
        if(ugc instanceof SocialUgc) {
            for (Flag flag : ((SocialUgc)ugc).getFlags()){
                flag.setReason(cleanup(flag.getReason()));
            }
        }
    }

    /**
     * Does the actual cleanup.
     * @param toCleanup Text to cleanup.
     * @return cleanup text.
     */
    private String cleanup(final String toCleanup){
        return Jsoup.clean(toCleanup,whitelist);
    }
}
