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
package org.craftercms.social.moderation.impl;

import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.moderation.ModerationFilter;

public class CommunityModeration implements ModerationFilter {

	private int comunityOffenceMaxPercentage;

	@Override
	public boolean needModeration(SocialUgc ugc) {
		    //To prevent division by 0
		    int likes = (ugc.getVotesUp().size() == 0)?1:ugc.getVotesUp().size();
			int percentage = ((ugc.getVotesDown().size() * 100) / (likes + ugc.getVotesDown().size()));
			return percentage >= comunityOffenceMaxPercentage;
	}

	@Override
	public String getName() {
		return "Comunity Moderation Filter";
	}

	public void setComunityOffenceMaxPercentage(int comunityOffenceMaxPercentage) {
		this.comunityOffenceMaxPercentage = comunityOffenceMaxPercentage;
	}
}
