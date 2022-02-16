/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.domain.notifications;

import org.jongo.marshall.jackson.oid.Id;

/**
 *
 */
public class ProfileWatchOptions {

    @Id
    private String profileId;
    private String frequency;

    public ProfileWatchOptions() {
    }

    public ProfileWatchOptions(final String profileId, final String frequency) {
        this.profileId = profileId;
        this.frequency = frequency;
    }

    public ProfileWatchOptions(final String profileId) {
        this.profileId = profileId;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(final String profileId) {
        this.profileId = profileId;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(final String frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "ProfileWatchOptions{" +
            "profileId='" + profileId + '\'' +
            ", frequency='" + frequency + '\'' +
            '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ProfileWatchOptions that = (ProfileWatchOptions)o;

        if (!profileId.equals(that.profileId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = profileId != null? profileId.hashCode(): 0;
        result = 31 * result + (frequency != null? frequency.hashCode(): 0);
        return result;
    }
}
