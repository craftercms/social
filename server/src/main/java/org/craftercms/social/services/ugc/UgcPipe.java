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

package org.craftercms.social.services.ugc;

import java.util.Map;

import org.craftercms.social.domain.UGC;
import org.craftercms.social.exceptions.SocialException;

/**
 * UGC Pipeline Definition.
 */
public interface UgcPipe {

    /**
     * Process the given ugc in to the pipeline
     * @param ugc UGC of going thru the pipeline
     * @param <T> Any UGC object
     * @throws SocialException If the UGC can't be process. <b>Stops the pipeline execution</b>
     */
    <T extends UGC> void process(final T ugc,Map<String,Object> params) throws SocialException;
}
