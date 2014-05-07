package org.craftercms.social.exceptions;

import org.craftercms.commons.mongo.MongoDataException;

/**
 * Thrown when a SecurityProfile Operation Fails
 */
public class SecurityProfileException extends SocialException {
    private static final long serialVersionUID = 2745355670351287046L;

    public SecurityProfileException(final String message) {
        super(message);
    }

    public SecurityProfileException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
