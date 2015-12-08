/*
 * Copyright (C) 2007-${year} Crafter Software Corporation.
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.services.ugc.pipeline;

import java.util.Map;

import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.moderation.ModerationDecision;
import org.craftercms.social.services.ugc.UgcPipe;

/**
 *
 */
public class ModerationPipe implements UgcPipe{

    private ModerationDecision moderationDecision;

    @Override
    public <T extends UGC> void process(final T ugc,Map<String,Object> params) throws SocialException {
        if(ugc instanceof SocialUgc)
            moderationDecision.needModeration((SocialUgc)ugc);
    }


    public void setModerationDecision(final ModerationDecision moderationDecision) {
        this.moderationDecision = moderationDecision;
    }
}
