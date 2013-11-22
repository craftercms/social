package org.craftercms.testing.unit;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.security.api.RequestContext;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.UGC.ModerationStatus;
import org.craftercms.social.services.UGCService;
import org.craftercms.social.services.impl.PermissionServiceImpl;
import org.craftercms.social.util.action.ActionConstants;
import org.craftercms.social.util.action.ActionEnum;
import org.craftercms.social.util.action.ActionUtil;
import org.craftercms.social.util.support.CrafterProfileService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { RequestContext.class})
public class PermissionServiceTest {
	
	@Mock
	private UGCService ugcService;
	
	@Mock
	private CrafterProfileService crafterProfileService;
	
	@InjectMocks
	private PermissionServiceImpl permissionServiceImpl;
	
	private static final String UGC_ID = 	 "520278180364146bdbd42d1f";
	private static final String PROFILE_ID = "5202b88203643ac2849709bc";
	private static final String EMAIL = "craftercms@email.com";
	
	private UGC currentUGC;
	private Profile currentProfile;
	
	private List<UGC> ul;
	
	@Before
	public void startup() {
		currentUGC = getUGC();
		currentProfile = getProfile();
		ul = new ArrayList<UGC>();
		ul.add(currentUGC);
		
	}
	
	@Test
	public void testAllowed() {
		when(ugcService.findById(Mockito.<ObjectId>any())).thenReturn(currentUGC);
		when(crafterProfileService.getProfile(PROFILE_ID)).thenReturn(currentProfile);
		
		boolean allowed = permissionServiceImpl.allowed(ActionEnum.CREATE, new ObjectId(UGC_ID), PROFILE_ID);
		assertTrue(allowed);
	}
	
	@Test
	public void testAllowed_1() {
		when(ugcService.findById(Mockito.<ObjectId>any())).thenReturn(currentUGC);
		when(crafterProfileService.getProfile(PROFILE_ID)).thenReturn(currentProfile);
		
		boolean allowed = permissionServiceImpl.allowed(ActionEnum.CREATE, currentUGC, PROFILE_ID);
		assertTrue(allowed);
	}
	
	@Test
	public void testAllowed_2() {
		when(ugcService.findById(Mockito.<ObjectId>any())).thenReturn(currentUGC);
		when(crafterProfileService.getProfile(PROFILE_ID)).thenReturn(currentProfile);
		
		boolean allowed = permissionServiceImpl.allowed(ActionEnum.CREATE, currentUGC, currentProfile);
		assertTrue(allowed);
	}
	@Test
	public void testNotAllowed() {
		when(ugcService.findById(Mockito.<ObjectId>any())).thenReturn(currentUGC);
		when(crafterProfileService.getProfile(PROFILE_ID)).thenReturn(currentProfile);
		
		boolean allowed = permissionServiceImpl.allowed(ActionEnum.DELETE, currentUGC, currentProfile);
		assertFalse(allowed);
	}
	@Test
	public void testCheckGrantedPermission() {
		when(ugcService.findById(Mockito.<ObjectId>any())).thenReturn(currentUGC);
		when(crafterProfileService.getProfile(PROFILE_ID)).thenReturn(currentProfile);
		
		List<UGC> l = permissionServiceImpl.checkGrantedPermission(ActionEnum.DELETE, ul, PROFILE_ID);
		assertNotNull(l);
		assertTrue(l.size()==0);
	}
	
	private UGC getUGC() {
		UGC ugc= new UGC();
		ugc.setCreatedBy("test");
		ugc.setCreatedDate(new Date());
		ugc.setFlagCount(0);
		ugc.setId(new ObjectId(UGC_ID));
		ugc.setLastModifiedBy("test");
		ugc.setLastModifiedDate(new Date());
		ugc.setLikeCount(0);
		ugc.setModerationStatus(ModerationStatus.UNMODERATED);
		ugc.setOffenceCount(0);
		ugc.setOwner("test");
		ugc.setProfile(getProfile());
		ugc.setProfileId(PROFILE_ID);
		ugc.setTargetId("testing");
		ugc.setTenant("test");
		ugc.setTextContent("Testing Content");
		ugc.setTimesModerated(0);
		ugc.setAttachmentId(new ObjectId[]{});
		ugc.setActions(ActionUtil.getDefaultActions());
		return ugc;
	}

	private Profile getProfile() {
		Map<String,Object> attributes = new HashMap<String, Object>();
		Profile p = new Profile(PROFILE_ID, "test", "test", true, new Date(), new Date(), attributes, EMAIL,true);
		List<String> roles = new ArrayList<String>(){
			{
			
				add(ActionConstants.SOCIAL_AUTHOR);
	         
			}
		};
		p.setRoles(roles);
		return p;
	}

}
