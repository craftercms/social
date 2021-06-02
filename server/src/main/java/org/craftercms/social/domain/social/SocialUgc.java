/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
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

package org.craftercms.social.domain.social;

import java.util.HashSet;
import java.util.Set;

import org.craftercms.social.domain.UGC;

/**
 *
 */
public class SocialUgc extends UGC{

    private ModerationStatus moderationStatus;
    /**
     * List of profiles that like this.
     */
    private Set<String> votesUp;

    /**
     * List of profiles that like dislike.
     */
    private Set<String> votesDown;

    /**
     * List of profiles that flag this.
     */
    private Set<Flag> flags;


    public SocialUgc() {
       init();
    }

    public <T extends UGC> SocialUgc(final T base) {
        super(base);
        init();
    }

    private void init(){
        votesUp = new HashSet<>();
        votesDown = new HashSet<>();
        flags = new HashSet<>();
    }
    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(final ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public Set<String> getVotesUp() {
        return votesUp;
    }

    public void setVotesUp(final Set<String> votesUp) {
        this.votesUp = votesUp;
    }

    public Set<String> getVotesDown() {
        return votesDown;
    }

    public void setVotesDown(final Set<String> votesDown) {
        this.votesDown = votesDown;
    }

    public Set<Flag> getFlags() {
        return flags;
    }

    public void setFlags(final Set<Flag> flags) {
        this.flags = flags;
    }
}
