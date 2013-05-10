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
package org.craftercms.social.util.support;

import java.util.Arrays;
import java.util.List;

import org.craftercms.profile.api.ProfileClient;
import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.AppAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("crafterProfileService")
public class CrafterProfile {
	private Object lock = new Object();
	
	Logger log = LoggerFactory.getLogger(CrafterProfile.class);
	@Autowired
	@Qualifier("crafterProfile")
	private ProfileClient client;

	public static String ROLES = "craftersocial-role";

	@Value("#{socialSettings['security.crafterprofile.appUsername']}")
	private String appUsername;
	@Value("#{socialSettings['security.crafterprofile.appPassword']}")
	private String appPassword;
	
	@Value("#{socialSettings['security.crafterprofile.tenant.name']}")
	private String crafterProfileAppTenantName;

	private String appToken = null;
	
	private static final String ANONYMOUS_USER = "anonymous"; 
	private static final String ANONYMOUS_PASSWORD = "anonymous"; 

	public CrafterProfile() {
		super();
	}

	protected void init() throws AppAuthenticationFailedException {
		appToken = client.getAppToken(appUsername, appPassword);
		log.debug("CRAFTER APPTOKEN {}", appToken);
	}

	public String getAppToken() {
		if (appToken == null) {
			try {
				synchronized (lock) {
					if (appToken == null) {
						init();
					}
				}
			} catch (AppAuthenticationFailedException e) {
				log.error("could not get an AppToken", e);
			}
		}
		
		return appToken;
	}
	
	public boolean validateUserToken(String ticket) {
		try {
			return client.isTicketValid(getAppToken(), ticket);
		} catch(AppAuthenticationException e) {
			try {
				synchronized (lock) {
					init();
				}
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return client.isTicketValid(appToken, ticket);
		}
	}

	public Profile getUserInformation(String ticket) {
		try {
			return client.getProfileByTicketWithAttributes(getAppToken(), ticket, Arrays.asList(new String[]{}));
		} catch(AppAuthenticationException e) {
			try {
				synchronized (lock) {
					init();
				}
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return client.getProfileByTicketWithAttributes(getAppToken(), ticket, Arrays.asList(new String[]{}));
		}
	}

	public List<Profile> getProfilesByIds(List<String> profileIds) {
		if (profileIds == null || profileIds.size() == 0) {
			return null;
		}
		List<Profile> list =  null;
		try {
			list =  client.getProfiles(getAppToken(), profileIds);
		} catch(AppAuthenticationException e) {
			try {
				synchronized (lock) {
					init();
				}
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			list =  client.getProfiles(appToken, profileIds);
		}
		
		return list;
	}


    /**
     * Get the profile based on the ID
     *
     * @param profileId
     * @return
     */
	public Profile getProfile(String profileId) {
		if (profileId == null || profileId.equals("")) {
			return  ProfileConstants.ANONYMOUS;
		}
		
		try {
			return client.getProfile(getAppToken(), profileId);
		} catch(AppAuthenticationException e) {
			try {
				synchronized (lock) {
					init();
				}
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return client.getProfile(getAppToken(), profileId);
		}
		
	}

    /**
     * Get the tenant based on the ID
     *
     * @param tenantName
     * @return
     */
    public Tenant getTenant(String tenantName) {
    	try {
	        return client.getTenantByName(getAppToken(), tenantName);
	    } catch(AppAuthenticationException e) {
			try {
				synchronized (lock) {
					init();
				}
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return client.getTenantByName(getAppToken(), tenantName);
		}
    }
	
	public void resetAppToken() {
		this.appToken = null;
	}

	public Profile authenticateAnonymous() {
		String token = null;
		
		try {
			token = client.getAppToken(appUsername, appPassword);
		
			String ticket = client.getTicket(token, ANONYMOUS_USER, ANONYMOUS_PASSWORD,crafterProfileAppTenantName);
		} catch (Exception e) {
			log.error("could not get ticket for anonymous", e);
			return null;
		}
		
		Profile profile = client.getProfileByUsernameWithAllAttributes(token, ANONYMOUS_USER, crafterProfileAppTenantName);
		
		return profile;
	}

}
