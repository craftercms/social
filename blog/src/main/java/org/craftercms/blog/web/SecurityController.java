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
package org.craftercms.blog.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.UserProfile;

import org.springframework.web.servlet.ModelAndView;


@Controller
public class SecurityController {
	
	private final Logger log = LoggerFactory
			.getLogger(SecurityController.class);
	
	@RequestMapping("/blog-console/login")
	public ModelAndView loginFromBlogConsole(Model model, @RequestParam(required=false) String message) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("../login");
		return mav;
	}
	
	@RequestMapping("/login")
	public String login(Model model, @RequestParam(required=false) String message) {
		return "login";
	}
	
	@RequestMapping("/logout")
	public String logout() {
		return "redirect:/logout";
	}
	
}
