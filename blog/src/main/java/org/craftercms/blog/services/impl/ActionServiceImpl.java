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
package org.craftercms.blog.services.impl;

import java.util.ArrayList;

import org.craftercms.blog.model.Action;
import org.craftercms.blog.services.ActionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ActionServiceImpl implements ActionService {
	
	private ArrayList<Action> actions;
	private String readRoles;
	private String createRoles;
	private String updateRoles;
	private String deleteRoles;
	
   	@Override
	public ArrayList<Action> getActions(String id) {
   		if (actions != null) { 
   			return actions;
   		}
		ArrayList<String> actionRoles = new ArrayList<String>();
		String[] reads = this.readRoles.split(",");
		for (String readRole: reads) {
			actionRoles.add(readRole.trim());
		}
		Action readAction = new Action("READ",actionRoles);
		
		
		actionRoles = new ArrayList<String>();
		String[] updates = this.updateRoles.split(",");
		for (String role: updates) {
			actionRoles.add(role.trim());
		}
		Action editAction = new Action("UPDATE",actionRoles);
		
		actionRoles = new ArrayList<String>();
		String[] creates = this.createRoles.split(",");
		for (String role: creates) {
			actionRoles.add(role.trim());
		}
		Action createAction = new Action("CREATE",actionRoles);
		
		actionRoles = new ArrayList<String>();
		String[] deletes = this.deleteRoles.split(",");
		for (String role: deletes) {
			actionRoles.add(role.trim());
		}
		Action deleteAction = new Action("DELETE",actionRoles);
		
		actions = new ArrayList<Action>();
			
		actions.add(editAction);
		actions.add(createAction);
		actions.add(deleteAction);
		actions.add(readAction);
			
		return actions;
	}
	
	/*** ***/
	@Value("${read.roles}")
	public void setReadRoles(String readRole) {
		this.readRoles = readRole;
	}
	@Value("${create.roles}")
	public void setCreateRoles(String createRole) {
		this.createRoles = createRole;
	}
	
	@Value("${update.roles}")
	public void setUpdateRoles(String updateRole) {
		this.updateRoles = updateRole;
	}
	@Value("${delete.roles}")
	public void setDeleteRoles(String deleteRole) {
		this.deleteRoles = deleteRole;
	}
	
	

}
