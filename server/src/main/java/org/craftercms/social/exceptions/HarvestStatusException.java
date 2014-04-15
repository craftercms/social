package org.craftercms.social.exceptions;

/**
 * Thrown when a Harvest Status operation fails.
 */
public class HarvestStatusException extends Throwable {
    private static final long serialVersionUID = -6718541372669996164L;

    public HarvestStatusException(final String message, final Throwable ex) {
        super(message, ex);
    }
}
