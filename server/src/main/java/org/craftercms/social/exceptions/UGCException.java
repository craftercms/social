package org.craftercms.social.exceptions;

/**
 * Thrown when a UGC operation Fail.
 */
public class UGCException extends SocialException {
    private static final long serialVersionUID = -1753727093451538876L;

    public UGCException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UGCException(final String message) {
        super(message);
    }
}

