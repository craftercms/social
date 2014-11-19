/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.migration.util;

/**
 */
public class MigrationException extends Exception {

    private boolean blocker;

    public MigrationException() {
        blocker=false;
    }

    public MigrationException(final String message) {
        this(message, false);
    }

    public MigrationException(final String message, final Throwable cause) {
        this(message, cause, false);
    }

    public MigrationException(final String message, final boolean blocker) {
        super(message);
        this.blocker = blocker;
    }

    public MigrationException(final String message, final Throwable cause, final boolean blocker) {
        super(message, cause);
        this.blocker = blocker;
    }

    public MigrationException(final Throwable cause, final boolean blocker) {
        super(cause);
        this.blocker = blocker;
    }

    public MigrationException(final Throwable cause) {
        this(cause, false);
    }

    public boolean isBlocker() {
        return blocker;
    }
}
