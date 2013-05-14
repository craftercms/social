package org.craftercms.blog.services.impl;

import java.util.Map;

import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.utils.spring.el.AccessRestrictionExpressionRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.craftercms.blog.services.BlogService;
import org.craftercms.blog.services.impl.CrafterSocialRestClientImpl;

public class AccessRestrictionExpressionBlogRoot extends AccessRestrictionExpressionRoot {
	
	private CrafterSocialRestClientImpl crafterSocialRestClientImpl;
	
	private BlogService blogService;
	
	public AccessRestrictionExpressionBlogRoot(UserProfile profile) {
        super(profile);
    }
	
	
	public boolean hasSocialAdminAndCreateRole() {
		RequestContext context = RequestContext.getCurrent();
		if (isAuthenticated() && crafterSocialRestClientImpl.hasRootCreatePermissions(
				blogService.getTenant(), 
						context.getAuthenticationToken().getTicket())) {
			return true;
		}
		return false;
		
	}


	public void setBlogService(BlogService blogService2) {
		this.blogService = blogService2;
		
	}


	public void setCrafterSocialRestClient(
			CrafterSocialRestClientImpl crafterSocialRestClientImpl2) {
		this.crafterSocialRestClientImpl = crafterSocialRestClientImpl2;
		
	}
}
