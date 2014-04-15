package org.craftercms.social.exceptions;

import org.craftercms.commons.mongo.MongoDataException;

/**
 * Throw when a $
 */
public class CounterException extends SocialException {
    private static final long serialVersionUID = 5733881170221662624L;

    public CounterException(final String message, final Throwable ex) {
        super(message,ex);
    }
}
