package org.craftercms.social.util.support.impl;

import java.io.Serializable;
import java.util.*;

import org.craftercms.profile.api.ProfileClient;
import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.profile.impl.domain.Tenant;
import org.craftercms.profile.exceptions.AppAuthenticationException;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.social.util.support.CrafterProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

public class CrafterProfileServiceImpl implements CrafterProfileService {
	private Object lock = new Object();

	private Logger log = LoggerFactory.getLogger(CrafterProfileService.class);
	
	private static final String NOT_GET_APP_TOKEN = "could not get an AppToken";
	
	@Autowired
	@Qualifier("crafterProfile")
	private ProfileClient client;

	public static final String ROLES = "craftersocial-role";

	@Value("#{socialSettings['security.crafterprofile.appUsername']}")
	private String appUsername;
	@Value("#{socialSettings['security.crafterprofile.appPassword']}")
	private String appPassword;

	@Value("#{socialSettings['security.crafterprofile.tenant.name']}")
	private String crafterProfileAppTenantName;

	private String appToken = null;

	private static final String ANONYMOUS_USER = "anonymous";
	private static final String ANONYMOUS_PASSWORD = "anonymous";

	public CrafterProfileServiceImpl() {
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
				log.error(NOT_GET_APP_TOKEN, e);
			}
		}

		return appToken;
	}

	public boolean validateUserToken(String ticket) {
		try {
			return client.isTicketValid(getAppToken(), ticket);
		} catch (AppAuthenticationException e) {
			try {
				synchronized (lock) {
					init();
				}
			} catch (AppAuthenticationFailedException e1) {
				log.error(NOT_GET_APP_TOKEN, e);
			}
			return client.isTicketValid(appToken, ticket);
		}
	}

	public Profile getUserInformation(String ticket) {
		try {
			return client.getProfileByTicketWithAttributes(getAppToken(),
					ticket, Arrays.asList(new String[] {}));
		} catch (AppAuthenticationException e) {
			try {
				synchronized (lock) {
					init();
				}
			} catch (AppAuthenticationFailedException e1) {
				log.error(NOT_GET_APP_TOKEN, e);
			}
			return client.getProfileByTicketWithAttributes(getAppToken(),
					ticket, Arrays.asList(new String[] {}));
		}
	}

    public List<Profile> getProfilesByIds(List<String> profileIds) {
		if (profileIds == null || profileIds.size() == 0) {
			return null;
		}
		List<Profile> list = null;
		try {
			list = client.getProfilesWithAllAttributes(getAppToken(), profileIds);
		} catch (AppAuthenticationException e) {
			try {
				synchronized (lock) {
					init();
				}
			} catch (AppAuthenticationFailedException e1) {
				log.error(NOT_GET_APP_TOKEN, e);
			}
			list = client.getProfiles(appToken, profileIds);
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
		if (profileId == null || profileId.equals("") || profileId.equalsIgnoreCase("anonymous")) {
			return ProfileConstants.ANONYMOUS;
		}

		try {
			return client.getProfile(getAppToken(), profileId);
		} catch (AppAuthenticationException e) {
			try {
				synchronized (lock) {
					init();
				}
			} catch (AppAuthenticationFailedException e1) {
				log.error(NOT_GET_APP_TOKEN, e);
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
		} catch (AppAuthenticationException e) {
			try {
				synchronized (lock) {
					init();
				}
			} catch (AppAuthenticationFailedException e1) {
				log.error(NOT_GET_APP_TOKEN, e);
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

			client.getTicket(token, ANONYMOUS_USER,
					ANONYMOUS_PASSWORD, crafterProfileAppTenantName);
		} catch (Exception e) {
			log.error("could not get ticket for anonymous", e);
			return null;
		}

		Profile profile = client.getProfileByUsernameWithAllAttributes(token,
				ANONYMOUS_USER, crafterProfileAppTenantName);

		return profile;
	}


	@Override
	public Profile getProfile(String profileId, List<String> attributes) {
		if (profileId == null || profileId.equals("") || profileId.equalsIgnoreCase("anonymous")) {
			return ProfileConstants.ANONYMOUS;
		} else if (attributes==null) {
			return getProfile(profileId);
		}

		try {
			return client.getProfileWithAttributes(getAppToken(), profileId, attributes);
		} catch (AppAuthenticationException e) {
			try {
				synchronized (lock) {
					init();
				}
			} catch (AppAuthenticationFailedException e1) {
				log.error(NOT_GET_APP_TOKEN, e);
			}
			return client.getProfileWithAttributes(getAppToken(), profileId, attributes);
		}
	}

    @Override
    public void updateAttributes(String profileId, Map<String, Serializable> attributes) {
        if (profileId == null || profileId.equals("") || profileId.equalsIgnoreCase("anonymous")) {
            return;
        } else if (attributes == null) {
            attributes = new HashMap<String, Serializable>();
        }

        try {
            client.updateAttributesForProfile(getAppToken(), profileId, attributes);
        } catch (AppAuthenticationException e) {
            try {
                synchronized (lock) {
                    init();
                }
            } catch (AppAuthenticationFailedException e1) {
                log.error(NOT_GET_APP_TOKEN, e);
            }
            client.setAttributesForProfile(getAppToken(), profileId, attributes);
        }
    }

}
