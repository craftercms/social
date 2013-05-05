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
package org.craftercms.blog.services;

import org.craftercms.blog.model.BlogListForm;
import org.craftercms.profile.impl.ProfileRestClientImpl;
import org.craftercms.crafterprofile.user.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class BlogService {
	
	private String appBlogName;
    private String appTenantName;
	
	@Autowired
	private ProfileRestClientImpl profileRestClient;
	
	@Autowired
	private ActionService actionService;
	
	public BlogListForm getBlogListForm() {
		
		BlogListForm blogListForm = new BlogListForm();
		blogListForm.setTenant(this.appTenantName);
		blogListForm.setTicket(getTicket());
		blogListForm.setTarget(this.appBlogName);
		blogListForm.setActions(actionService.getActions(this.appBlogName));
		Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!isAnonymousUser()) {
			blogListForm.setAuthenticate(true);
		}
		return blogListForm;
	}
	
	@Value("${crafter.social.app.blog.name}")
	public void setCrafterSocialAppBlogName(String blogName) {
		this.appBlogName = blogName;
	}

    @Value("${crafter.profile.app.tenant.name}")
    public void setAppTenantName(String appTenantName) {
        this.appTenantName = appTenantName;
    }
	
	private String getTicket() {
		Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (user == null || user instanceof String) {
			return "";
		} else {
			UserProfile profile = (UserProfile)user;
			return profile.getTicket();
		}
	}
	private boolean isAnonymousUser() {
		Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (user == null || (user instanceof String)) {
			return true;
		}
		return false;
	}
}
