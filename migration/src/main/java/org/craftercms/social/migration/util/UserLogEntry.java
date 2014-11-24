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

import java.util.Date;

/**

 */
public class UserLogEntry {
    private MigrationMessenger.Level level;
    private String source;
    private String message;
    private Date   date;

    public UserLogEntry(final MigrationMessenger.Level level, final String source, final String message, final Date
        date) {
        this.level = level;
        this.source = source;
        this.message = message;
        this.date = date;
    }

    public MigrationMessenger.Level getLevel() {
        return level;
    }

    public void setLevel(final MigrationMessenger.Level level) {
        this.level = level;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }
}
