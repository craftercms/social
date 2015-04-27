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
