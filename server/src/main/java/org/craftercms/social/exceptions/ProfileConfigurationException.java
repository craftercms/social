package org.craftercms.social.exceptions;

/**
 * Thrown when a Profile is not Configure to work with social.
 */
public class ProfileConfigurationException extends RuntimeException {
    private static final long serialVersionUID = 1654156376938138754L;

    public ProfileConfigurationException(final String message) {
        super(message);
    }
}
