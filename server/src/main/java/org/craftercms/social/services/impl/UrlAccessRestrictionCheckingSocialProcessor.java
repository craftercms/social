package org.craftercms.social.services.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.impl.processors.*;
import org.craftercms.security.utils.spring.el.AccessRestrictionExpressionRoot;

import org.craftercms.social.services.PermissionService;
import org.craftercms.social.services.TenantService;
import org.craftercms.social.services.UGCService;
import org.craftercms.social.util.support.CrafterProfileService;
import org.springframework.beans.factory.annotation.Autowired;

public class UrlAccessRestrictionCheckingSocialProcessor extends
		UrlAccessRestrictionCheckingProcessor {
	
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private UGCService ugcService;

	@Autowired
	private TenantService tenantService;

	@Autowired
	private CrafterProfileService crafterProfileService;
	
	private static final Log logger = LogFactory.getLog(UrlAccessRestrictionCheckingSocialProcessor.class);
	
	public UrlAccessRestrictionCheckingSocialProcessor() {
		super();
	}
	
	public void setUrlRestrictions(Map<String, String> restrictions) {
		super.setUrlRestrictions(restrictions);
	}
	
	protected AccessRestrictionExpressionRoot createExpressionRoot(UserProfile profile) {
		UgcSecurityExpressionRoot root = new UgcSecurityExpressionRoot(profile);
		root.setPermissionService(permissionService);
		root.setUgcService(ugcService);
		root.setTenantService(tenantService);
		root.setCrafterProfileService(crafterProfileService);
		return root;
    }

}
