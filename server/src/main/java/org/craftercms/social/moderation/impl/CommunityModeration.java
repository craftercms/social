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
package org.craftercms.social.moderation.impl;

import org.craftercms.social.domain.UGC;
import org.craftercms.social.moderation.ModerationFilter;

public class CommunityModeration implements ModerationFilter {

	private int comunityOffenceMaxPercentage;

	@Override
	public boolean needModeration(UGC ugc) {
		    //To prevent division by 0
		    int likes = (ugc.getLikeCount() == 0)?1:ugc.getLikeCount();
			int percentage = ((ugc.getOffenceCount() * 100) / (likes + ugc.getOffenceCount()));
			if (percentage >= comunityOffenceMaxPercentage) {
				return true;
			} else {
				return false;
			}
	}

	@Override
	public String getName() {
		return "Comunity Moderation Filter";
	}

	public void setComunityOffenceMaxPercentage(int comunityOffenceMaxPercentage) {
		this.comunityOffenceMaxPercentage = comunityOffenceMaxPercentage;
	}
}
