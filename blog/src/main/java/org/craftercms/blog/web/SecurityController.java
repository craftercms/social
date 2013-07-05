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

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.ModelAndView;


@Controller
public class SecurityController {
	
	@RequestMapping("/blog-console/login")
	public ModelAndView loginFromBlogConsole(Model model, @RequestParam(required=false) String message) {
		System.out.println(" ****** /blog-console/login ");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("../login");
		return mav;
	}
	
	@RequestMapping("/login")
	public String login(Model model, @RequestParam(required=false) String message) {
		
		System.out.println(" /login ");
		return "login";
	}
	
	@RequestMapping("/logout")
	public String logout() {
		return "redirect:/logout";
	}

}
