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

package org.craftercms.social.domain.social;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public enum ModerationStatus {
    UNMODERATED, PENDING, APPROVED, SPAM, TRASH;
    private static Logger logger = LoggerFactory.getLogger(ModerationStatus.class);
    public static List<ModerationStatus> listOfModerationStatus(final String listOfModerationStatus){
        List<ModerationStatus> toReturn = new ArrayList<>();
        if(StringUtils.isNoneBlank(listOfModerationStatus)) {
            final String[] toConvert = listOfModerationStatus.split(",");
            for (String moderationStatus : toConvert) {
                try {
                    toReturn.add(ModerationStatus.valueOf(moderationStatus));
                }catch (IllegalArgumentException ex){
                    logger.error("Unable to transform {} to a valid moderation status",moderationStatus);
                }
            }
        }
        /** Don't troll with the result! **/
        return Collections.unmodifiableList(toReturn);
    }

}
