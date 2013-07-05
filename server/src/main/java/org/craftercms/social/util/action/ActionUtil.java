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
package org.craftercms.social.util.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.craftercms.social.domain.Action;

public class ActionUtil {
	
	public static final String[] ACTIONS = {"read","update","create","delete", "act_on", "moderate"};
	
	private static final List<String> READ_ROLES = new ArrayList<String>(){
		{
			add(ActionConstants.ANONYMOUS); 
			add(ActionConstants.SOCIAL_USER);
			add(ActionConstants.SOCIAL_ADMIN);
			add(ActionConstants.SOCIAL_AUTHOR);
            add(ActionConstants.OWNER);
		}
	};
	private static final List<String> CREATE_ROLES = new ArrayList<String>(){
		{
			add(ActionConstants.SOCIAL_ADMIN);
			add(ActionConstants.SOCIAL_AUTHOR);
            add(ActionConstants.OWNER);
		}
	};
	private static final List<String> UPDATE_ROLES = new ArrayList<String>(){
		{
			add(ActionConstants.SOCIAL_ADMIN);
			add(ActionConstants.OWNER);
		}
	};
	private static final List<String> DELETE_ROLES = new ArrayList<String>(){
		{
			add(ActionConstants.SOCIAL_ADMIN);
		}
	};
    private static final List<String> ACT_ON_ROLES = new ArrayList<String>(){
        {
            add(ActionConstants.SOCIAL_ADMIN);
            add(ActionConstants.SOCIAL_AUTHOR);
            add(ActionConstants.SOCIAL_USER);
            add(ActionConstants.OWNER);
        }
    };
    private static final List<String> MODERATE_ROLES = new ArrayList<String>(){
        {
            add(ActionConstants.SOCIAL_ADMIN);
            add(ActionConstants.SOCIAL_MODERATOR);
        }
    };
	
	private static final Action UPDATE_ACTION = new Action(ActionEnum.UPDATE.toString(), UPDATE_ROLES);
	private static final Action CREATE_ACTION = new Action(ActionEnum.CREATE.toString(), CREATE_ROLES);
	private static final Action DELETE_ACTION = new Action(ActionEnum.DELETE.toString(), DELETE_ROLES);
	private static final Action READ_ACTION = new Action(ActionEnum.READ.toString(), READ_ROLES);
    private static final Action ACT_ON_ACTION = new Action(ActionEnum.ACT_ON.toString(), ACT_ON_ROLES);
    private static final Action MODERATE_ACTION = new Action(ActionEnum.MODERATE.toString(), MODERATE_ROLES);
	
	private static final List<Action> DEFAULT_ACTION = new ArrayList<Action>(){
		{
			add(UPDATE_ACTION);
			add(CREATE_ACTION);
			add(DELETE_ACTION);
			add(READ_ACTION);
            add(ACT_ON_ACTION);
            add(MODERATE_ACTION);
		}
	};
	
	
	public static final List<Action> getDefaultActions() {
		return DEFAULT_ACTION;
	}

   public static List<Action> getActions(HttpServletRequest request) {
		List<Action> list = new ArrayList<Action>();
		String[] roles;
		Action action;
		Map params = request.getParameterMap();
		for (String actionName: ACTIONS) {
			roles = (String[])params.get("action_" + actionName);
			if (roles == null) { 
				continue;
			}
			action = new Action();
			action.setName(actionName);
			for (int i = 0; i < roles.length; i++) {
				action.getRoles().add(roles[i]);
			}
			list.add(action);
		}
		if (list.size() == 0) {
			return getDefaultActions();
		}
		return list;
	}
	
	
}
