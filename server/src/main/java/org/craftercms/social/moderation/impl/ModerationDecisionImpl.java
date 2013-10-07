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

import java.util.List;

import org.craftercms.social.domain.UGC;
import org.craftercms.social.moderation.ModerationDecision;
import org.craftercms.social.moderation.ModerationFilter;


/**
 * Using the ModerationFilters decides if a UGC have to be moderate or not <br/>
 * The neither ModerationDecisionImpl or the Filters modified the UGC <br/>
 * The decision is taken base on the first
 * 
 * @author cortiz
 * 
 */
public class ModerationDecisionImpl implements ModerationDecision {

	private List<ModerationFilter> filters;
	
	private int maxFlagsBeforeTrash = 10;
		
	public ModerationDecisionImpl(List<ModerationFilter> filters) {
		super();
		this.filters = filters;
	}
	
	/* (non-Javadoc)
	 * @see org.craftercms.social.moderation.ModerationDecision#needModeration(org.craftercms.social.domain.UGC)
	 */
	@Override
	public boolean needModeration(final UGC ugc) {
		boolean needsModeration = false;
		for (ModerationFilter filter : filters) {
			if (filter.needModeration(ugc)) {
				needsModeration = true;
				break;
			}
		}
		return needsModeration;
	}

	@Override
	public boolean isTrash(UGC ugc) {
		return ugc.getFlagCount() >= maxFlagsBeforeTrash;
	}
	
	@Override
	public void setMaxFlagsBeforeTrash(int maxFlags) {
		maxFlagsBeforeTrash = maxFlags;
	}

}
