package org.craftercms.social.util;

import org.craftercms.commons.i10n.I10nLogger;

/**
 * Created by Carlos Ortiz on 8/4/14.
 */
public final class LoggerFactory {

    private LoggerFactory(){}

    public static I10nLogger getLogger(Class<?> clazz){
        return new I10nLogger(clazz,"crafter/social/messages/logging");
    }
}
