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
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.craftercms.blog.util.BlogsUtil;

@Service
public class BlogService {
	
	private String appBlogName;
    private String appTenantName;
	
	@Autowired
	private ProfileRestClientImpl profileRestClient;
	
	@Autowired
	private ActionService actionService;
	
	public BlogListForm getBlogListForm() {
		RequestContext context = RequestContext.getCurrent();
		UserProfile user = context.getAuthenticationToken().getProfile();
		BlogListForm blogListForm = new BlogListForm();
		blogListForm.setTenant(this.appTenantName);
		blogListForm.setTicket(getTicket(user, context));
		blogListForm.setTarget(this.appBlogName);
		blogListForm.setActions(actionService.getActions(this.appBlogName));
		
		if (!isAnonymousUser(user)) {
			blogListForm.setAuthenticate(true);
		}
		return blogListForm;
	}
	
	@Value("${crafter.comments.app.blog.name}")
	public void setCrafterSocialAppBlogName(String blogName) {
		this.appBlogName = blogName;
	}

    @Value("${crafter.profile.app.tenant.name}")
    public void setAppTenantName(String appTenantName) {
        this.appTenantName = appTenantName;
    }
    
    public String getTenant() {
    	return appTenantName;
    }
	
	private String getTicket(UserProfile user, RequestContext context) {
		String ticket = "";
		
		if (user != null) {
			String value = context.getAuthenticationToken().getTicket();
			if (value != null) {
				ticket = value;
			}
		} 
		return ticket;
	}
	private boolean isAnonymousUser(UserProfile u) {
		
		if (u != null && u.getUserName().equalsIgnoreCase(BlogsUtil.ANONYMOUS)) {
			return true;
		} 
		return false;
	}
}
