package org.craftercms.social.exceptions;

/**
 * Thrown when a Tenant operation fails.
 */
public class TenantException extends SocialException {
    private static final long serialVersionUID = 8870293390992069787L;

    public TenantException(final String message, final Throwable ex) {
        super(message, ex);
    }
}
