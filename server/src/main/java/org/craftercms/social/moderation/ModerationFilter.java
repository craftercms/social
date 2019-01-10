/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
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

import org.craftercms.social.domain.social.SocialUgc;

/**
 * Filter Interface for Moderation Decision manager
 * @author cortiz
 */
public interface ModerationFilter {
	/**
	 * Decide if a UGC have to be moderate
	 * @param ugc Ugc to check
	 * @return True if the UGC Need to be moderate
	 * 		   False Otherwise
	 */
	public boolean needModeration(SocialUgc ugc);
	/**
	 * Get's the name of the Modetation Filter
	 * @return the name of the Filter
	 */
	public String getName();
	
}
