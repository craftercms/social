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
package org.craftercms.social.controllers.rest.v1;

import java.util.List;

import org.bson.types.ObjectId;
import org.craftercms.social.domain.UGCAudit;
import org.craftercms.social.domain.UGCAudit.AuditAction;
import org.craftercms.social.services.AuditServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/2/audit")
public class AuditRestController {
	
	@Autowired
	private AuditServices auditServices;
	
	@RequestMapping(value = "/{ugcId}", method = RequestMethod.GET)
	@ModelAttribute
	public List<UGCAudit> getUGCAudit(@PathVariable String ugcId,@RequestParam(required=false,defaultValue="") String profileId){
		if(profileId.isEmpty())
			return auditServices.findUGCAudits(new ObjectId(ugcId));
		else
			return auditServices.findUGCAudits(new ObjectId(ugcId),profileId);
	}
	
	@RequestMapping(value = "/{ugcId}/{action}", method = RequestMethod.GET)
	@ModelAttribute
	public List<UGCAudit> getUGCAuditForActions(@PathVariable String ugcId,@PathVariable String action){		
		return auditServices.findUGCAudits(new ObjectId(ugcId),AuditAction.valueOf(action.toUpperCase()));
	}

	@RequestMapping(value = "/profile/{profileId}/{action}", method = RequestMethod.GET)
	@ModelAttribute
	public List<UGCAudit> getUGCAuditForUserActions(@PathVariable String profileId,@PathVariable String action){		
		return auditServices.findUGCAuditsForProfile(profileId,AuditAction.valueOf(action.toUpperCase()));
	}
	
	@RequestMapping(value = "/profile/{profileId}", method = RequestMethod.GET)
	@ModelAttribute
	public List<UGCAudit> getUGCAuditForUserActions(@PathVariable String profileId){		
		return auditServices.findUGCAuditsForProfile(profileId);
	}
	

}
