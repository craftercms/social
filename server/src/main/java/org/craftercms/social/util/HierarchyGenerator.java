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
package org.craftercms.social.util;

import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;

public class HierarchyGenerator {

	public static <T extends Hierarchical<T>> HierarchyList<T> generateHierarchy(List<T> objList, ObjectId parentId, int rootCount, int childCount) {
		HierarchyList<T> hierarchy = new HierarchyList<T>();
		HashMap<Object, T> map = new HashMap<Object, T>();
		if (objList != null && objList.size() > 0) {
			for (T obj : objList) {
				map.put(obj.getId(), obj);

				if (obj.getParentId() == parentId) {
					if (hierarchy.size() <= rootCount) {
						hierarchy.add(obj);
					} else {
						hierarchy.incExtraCount();
					}
				}
			}

			for (T obj : objList) {
				if (obj.getParentId() != parentId) {
					T parent = map.get(obj.getParentId());
					if (parent != null) {
						if (parent.getChildCount() <= childCount) {
							parent.addChild(obj);
						} else {
							parent.incExtraChildCount();
						}
					}

				}
			}

		}

		return hierarchy;
	}
}
