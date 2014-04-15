package org.craftercms.social.exceptions;

/**
 * Base Exception for Social
 */
public class SocialException extends Exception {
    private static final long serialVersionUID = -909381167140649351L;

    public SocialException(final String message) {
        super(message);
    }

    public SocialException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
