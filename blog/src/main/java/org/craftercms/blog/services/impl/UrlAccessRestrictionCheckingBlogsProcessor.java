package org.craftercms.blog.services.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.blog.services.BlogService;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.impl.processors.*;
import org.craftercms.security.utils.spring.el.AccessRestrictionExpressionRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class UrlAccessRestrictionCheckingBlogsProcessor extends UrlAccessRestrictionCheckingProcessor {
	
	private CrafterSocialRestClientImpl crafterSocialRestClientImpl;
	
	private BlogService blogService;
	
	private static final Log logger = LogFactory.getLog(UrlAccessRestrictionCheckingBlogsProcessor.class);
	
	public UrlAccessRestrictionCheckingBlogsProcessor() {
		super();
	}
	
	protected AccessRestrictionExpressionRoot createExpressionRoot(UserProfile profile) {
		AccessRestrictionExpressionBlogRoot root =  new AccessRestrictionExpressionBlogRoot(profile);
		root.setBlogService(blogService);
		root.setCrafterSocialRestClient(crafterSocialRestClientImpl);
		if (logger.isDebugEnabled()) {
            logger.debug("Creating expression ROOT for username: " + profile.getUserName());
        }
		return root;
    }

	public CrafterSocialRestClientImpl getCrafterSocialRestClientImpl() {
		return crafterSocialRestClientImpl;
	}

	public void setCrafterSocialRestClientImpl(
			CrafterSocialRestClientImpl crafterSocialRestClientImpl) {
		this.crafterSocialRestClientImpl = crafterSocialRestClientImpl;
	}

	public BlogService getBlogService() {
		return blogService;
	}

	public void setBlogService(BlogService blogService) {
		this.blogService = blogService;
	}

}
