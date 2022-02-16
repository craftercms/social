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

package org.craftercms.social.services.ugc.pipeline;

import java.util.List;
import java.util.Map;

import org.craftercms.social.domain.UGC;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.services.ugc.UgcPipe;

/**
 * Created by cortiz on 5/29/14.
 */
public class UgcPipeline {

    private List<UgcPipe> pipeList;


    public <T extends UGC> void processUgc(T ugc) throws SocialException {
        processUgc(ugc,null);
    }
    public <T extends UGC> void processUgc(T ugc,Map<String,Object> params) throws SocialException {
        for (UgcPipe ugcPipe : pipeList) {
            ugcPipe.process(ugc,params);
        }
    }

    public void setPipeList(final List<UgcPipe> pipeList) {
        this.pipeList = pipeList;
    }
}
