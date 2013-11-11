package org.craftercms.social.util.support.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.impl.domain.Attribute;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.profile.impl.domain.Schema;
import org.craftercms.profile.impl.domain.Tenant;
import org.craftercms.social.domain.Action;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.util.action.ActionConstants;
import org.craftercms.social.util.action.ActionEnum;
import org.craftercms.social.util.support.CrafterProfile;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class CrafterProfileTest implements CrafterProfile {

	protected static final String TENANT_TEST = "craftercms";
	protected static final String TENANT_TEST_EMAIL = "craftercms@email.com";
	protected static final String TARGET_TEST = "craftercms";
	protected static final String ADMIN_USER = "admin";
	protected static final String ADMIN_PASS = "admin";
	protected static final String BASIC_USER = "basicuser";
	protected static final String BASIC_PASS = "basicuser";
	protected static final String ADMIN_ID = "adminid";
	protected static final String BASIC_ID = "basicid";
	protected static final String CONTENT = "content";
	protected static final String APP_TOKE = "mockapptoken";

	private static final List<String>READ_ROLES = new ArrayList<String>() {
		{
			add(ActionConstants.ANONYMOUS);
			add(ActionConstants.SOCIAL_USER);
			add(ActionConstants.SOCIAL_ADMIN);
			add(ActionConstants.SOCIAL_AUTHOR);
			add(ActionConstants.OWNER);
		}
	};
	private static final List<String>CREATE_ROLES = new ArrayList<String>() {
		{
			add(ActionConstants.SOCIAL_ADMIN);
			add(ActionConstants.SOCIAL_AUTHOR);
			add(ActionConstants.OWNER);
		}
	};
	private static final List<String>UPDATE_ROLES = new ArrayList<String>() {
		{
			add(ActionConstants.SOCIAL_ADMIN);
			add(ActionConstants.SOCIAL_AUTHOR);
			add(ActionConstants.OWNER);
		}
	};
	private static final List<String>DELETE_ROLES = new ArrayList<String>() {
		{
			add(ActionConstants.SOCIAL_ADMIN);
			add(ActionConstants.SOCIAL_AUTHOR);
		}
	};
	private static final List<String>ACT_ON_ROLES = new ArrayList<String>() {
		{
			add(ActionConstants.SOCIAL_ADMIN);
			add(ActionConstants.SOCIAL_AUTHOR);
			add(ActionConstants.SOCIAL_USER);
			add(ActionConstants.OWNER);
		}
	};
	private static final List<String>MODERATE_ROLES = new ArrayList<String>() {
		{
			add(ActionConstants.SOCIAL_ADMIN);
			add(ActionConstants.SOCIAL_MODERATOR);
		}
	};

	private static final List<String>SOCIAL_ROLES = new ArrayList<String>() {
		{
			add(ActionConstants.SOCIAL_ADMIN);
			add(ActionConstants.SOCIAL_MODERATOR);
			add(ActionConstants.SOCIAL_AUTHOR);
			add(ActionConstants.SOCIAL_USER);
		}
	};

	private static final Action UPDATE_ACTION = new Action(ActionEnum.UPDATE.toString(), UPDATE_ROLES);
	private static final Action CREATE_ACTION = new Action(ActionEnum.CREATE.toString(), CREATE_ROLES);
	private static final Action DELETE_ACTION = new Action(ActionEnum.DELETE.toString(), DELETE_ROLES);
	private static final Action READ_ACTION = new Action(ActionEnum.READ.toString(), READ_ROLES);
    private static final Action ACT_ON_ACTION = new Action(ActionEnum.ACT_ON.toString(), ACT_ON_ROLES);
    private static final Action MODERATE_ACTION = new Action(ActionEnum.MODERATE.toString(), MODERATE_ROLES);

	private String appToken = null;
	private static final String APP_TOKEN = "testing";

	public CrafterProfileTest() {
		super();

	}

	protected void init() throws AppAuthenticationFailedException {
		appToken = APP_TOKEN;
	}

	public String getAppToken() {
		return appToken;
	}

	public boolean validateUserToken(String ticket) {
		return true;
	}

	public Profile getUserInformation(String ticket) {
		return createAdminUser();
	}

    @Override
    public Profile createOrUpdateSubscription(String profileId, String targetId, String targetDescription, String targetUrl) {
        // TODO createOrUpdateSubscription test implementation
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Profile> getProfilesByIds(List<String> profileIds) {

		List<Profile> list = new ArrayList();
		list.add(createAdminUser());
		return list;
	}

	/**
	 * Get the profile based on the ID
	 * 
	 * @param profileId
	 * @return
	 */
	public Profile getProfile(String profileId) {
		
		return createAdminUser();

	}

	/**
	 * Get the tenant based on the ID
	 * 
	 * @param tenantName
	 * @return
	 */
	public Tenant getTenant(String tenantName) {
		return createTenant();
	}

	public void resetAppToken() {
		this.appToken = null;
	}

	public Profile authenticateAnonymous() {
		return createBasicUser();
	}

	protected Tenant createTenant() {
		Tenant t = new Tenant();
		List<String> domains = new ArrayList<String>();
		domains.add("localhost");
		List<Attribute> a = new ArrayList<Attribute>();
		Schema s = new Schema();
		s.setAttributes(a);

		t.setDomains(domains);
		t.setId(BASIC_ID);
		t.setRoles(SOCIAL_ROLES);
		t.setSchema(s);
		t.setTenantName(TENANT_TEST);
		return t;
	}

	protected Profile createBasicUser() {
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("first-name", BASIC_USER);
		attributes.put("last-name", BASIC_USER);
		List<String> roles = new ArrayList<String>();
		roles.add("SOCIAL_USER");
		return new Profile(BASIC_ID, BASIC_USER, BASIC_PASS, true, new Date(),
				new Date(), attributes, roles, TENANT_TEST, TENANT_TEST_EMAIL, true);

	}

	protected Profile createAdminUser() {
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("first-name", ADMIN_PASS);
		attributes.put("last-name", ADMIN_PASS);
		List<String> roles = new ArrayList<String>();
		roles.add("SOCIAL_ADMIN");
		return new Profile(ADMIN_ID, ADMIN_USER, ADMIN_PASS, true, new Date(),
				new Date(), attributes, roles, TENANT_TEST,TENANT_TEST_EMAIL, true);
	}

	protected UGC createBasicUGC(Profile p) {
		ArrayList<Action> actions = new ArrayList<Action>();
		actions.add(READ_ACTION);
		actions.add(UPDATE_ACTION);
		actions.add(CREATE_ACTION);
		actions.add(DELETE_ACTION);
		actions.add(ACT_ON_ACTION);
		actions.add(MODERATE_ACTION);
		UGC ugc = new UGC(CONTENT, p.getId(), TENANT_TEST, TARGET_TEST, null, null, null);

		ugc.setActions(actions);

		return ugc;

	}

	private void initSecurity() {
		SecurityContextHolder.getContext().setAuthentication(
				getAuthToken(createAdminUser()));
	}

	private Authentication getAuthToken(Profile userProfile) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if (userProfile.getRoles() != null) {
			for (String r : userProfile.getRoles()) {
				authorities.add(new SimpleGrantedAuthority(r.toUpperCase()));
			}
		}

		return new SocialAuthToken(authorities, userProfile);
	}

	static class SocialAuthToken extends AbstractAuthenticationToken {

		private static final long serialVersionUID = 1142799805748917666L;
		private Profile profile;

		public SocialAuthToken(
				Collection<? extends GrantedAuthority> authorities,
				Profile profile) {
			super(authorities);
			this.profile = profile;
		}

		@Override
		public Object getCredentials() {
			return null;
		}

		@Override
		public Object getPrincipal() {
			return profile.getId();
		}

		@Override
		public String getName() {
			return profile.getUserName();
		}

		@Override
		public boolean isAuthenticated() {
			return true;
		}

		@Override
		public Object getDetails() {
			return profile;
		}
	}

}
