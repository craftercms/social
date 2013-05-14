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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.craftercms.blog.model.BlogListForm;
import org.craftercms.blog.services.BlogService;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.RestException;
import org.craftercms.security.api.RequestContext;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.ModelAndView;


@Controller
public class BlogController {
	
	@Autowired
	private BlogService blogService;
	
	private static final String CONSOLE_URI = "blog-console";
	
	@RequestMapping(value = {"/blog-console/blog_entries","/blog-console/*","blog-console/*","/blog-console","/blog_entries","blog_entries"})
	public ModelAndView getBlogs(Model model, @RequestParam(required=false) String message,
			HttpServletRequest request) throws Exception{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("blog_entries");
		
		RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        return mav;
		
	}
	
	@RequestMapping(value = {"blog-console/entries_published","entries_published","/index"}, method = RequestMethod.GET)
	public String getEntriesPublished(Model model, @RequestParam(required=false) String message,
			HttpServletRequest request) throws Exception{

		if (isPublishedEntriesView(request.getRequestURI())) {
			return "entries_published";
		} else {
			return "redirect:/entries_published";
		}
		
	}
	
	@RequestMapping(value = {"/*"}, method = RequestMethod.GET) //It's new
	public ModelAndView getPublishedEntries(Model model, @RequestParam(required=false) String message) throws Exception{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("index");
		RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        return mav;
		
	}
	
	@RequestMapping( value = {"/get_config","/blog-console/get_config"}, method = RequestMethod.GET)
	@ModelAttribute
	public BlogListForm getBlogListForm() {
		return blogService.getBlogListForm();
	}
	
	@ExceptionHandler(AppAuthenticationFailedException.class)
	public String logoutException() {
		return "redirect:/logout";
	}
	
	@ExceptionHandler(RestException.class)
	public String Exception() {
		return "redirect:/logout";
	}
	
	private String getPublishedEntriesView(String requestURI) {
		String view = "entries_published";
		if (requestURI.contains(CONSOLE_URI)) {
			view = "/entries_published";
		}
		
		return view;
	}
	
	private boolean isPublishedEntriesView(String requestURI) {
		boolean isView = true;
		if (requestURI.contains(CONSOLE_URI)) {
			isView = false;
		}
		
		return isView;
	}
	
	private String getBlogEntriesView(String requestURI) {
		String view = "blog_entries";
		if (requestURI.contains(CONSOLE_URI)) {
			view = "../blog_entries";
		}
		
		return view;
	}

}
