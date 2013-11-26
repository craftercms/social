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

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.bson.types.ObjectId;
import org.craftercms.social.services.SupportDataAccess;
import org.craftercms.social.services.UGCService;
import org.craftercms.social.util.web.Attachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/api/2")
public class UGCSupportRestController {

	@Autowired
	private transient UGCService ugcService;
	
	@Autowired
    private SupportDataAccess supportDataAccess;

	@RequestMapping(value = "/get_attachment/{attachmentId}", method = RequestMethod.GET)
	public void getAttachment(@PathVariable String attachmentId,
			HttpServletResponse response) throws IOException {
		try {
    		ObjectId id = new ObjectId(attachmentId); 
			Attachment file = supportDataAccess.getAttachment(id);
			if (file !=null) {
	    		response.setContentType(file.getContentType());
				response.setContentLength((int) file.getLength());
				response.setHeader("Content-Disposition", "attachment; filename="
						+ file.getFilename());
				ugcService.streamAttachment(id, response.getOutputStream());
	    		
			} else {
				response.sendError(HttpStatus.SC_NOT_FOUND, "Attachment id{} not found: " + attachmentId);
			}
		} catch (Exception e) {
			
			response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR,e.getMessage());
		}
	}
	
}
