package org.craftercms.social.exceptions;

/**
 * Thrown when a invalid UGC is given.
 */
public class IllegalUgcException extends RuntimeException {
    private static final long serialVersionUID = -7326727603948598166L;

    public IllegalUgcException(final String message) {
        super(message);
    }
}
