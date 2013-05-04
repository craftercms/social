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
package org.craftercms.social.util.support.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.craftercms.social.util.support.ResultParser;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

@Service("targetMapReduseParser")
public class TargetMapReduseParser implements ResultParser{
		
	@Override
	public List<String> parseList(Map<?,?> rawResults) {
		List<String> lst = new ArrayList<String>();
		BasicDBList results = ((BasicDBList)rawResults.get("results"));
		for (int i = 0; i < results.size(); i++) {
			DBObject result = (DBObject)results.get(i);
			DBObject id=(DBObject) result.get("_id");
			lst.add((String) id.get("target"));
		}
		return lst;
	}
	
}
