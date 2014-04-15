package org.craftercms.social.exceptions;

/**
 * Thrown when a Audit Error occurs.
 */
public class AuditException extends SocialException {
    private static final long serialVersionUID = 9181708502989148913L;

    public AuditException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
