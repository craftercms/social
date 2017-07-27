/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.social.moderation;

import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.social.SocialUgc;

public interface ModerationDecision {
	
	/**
	 * Makes the decision if a {@link UGC} has to be moderated
	 * based on {@link ModerationFilter#needModeration(SocialUgc)} result
	 * returns True if at least one of {@link ModerationFilter} returns true
	 * @param ugc UGC to test
	 * @return True if at least one {@link ModerationFilter#needModeration(SocialUgc)}  returns true;
	 */
	boolean needModeration(final SocialUgc ugc);

	/**
	 * Set {@link UGC} as Trash
	 * @param ugc the {@link UGC} to be trashed
	 * @return true if it can be set in the trash.
	 */
	boolean isTrash(SocialUgc ugc);

	/**
	 * Sets the number of user flags threshold before the {@link UGC} is sent to the trash.
	 * @param maxFlags Maximum number of flags (unique flags).
	 */
	void setMaxFlagsBeforeTrash(int maxFlags);
}