package org.craftercms.blog.services;

public interface CrafterSocialClient {
	
	public boolean hasRootCreatePermissions(String tenant,String ticket);

}
