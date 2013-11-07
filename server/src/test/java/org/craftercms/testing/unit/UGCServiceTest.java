package org.craftercms.testing.unit;

import org.bson.types.ObjectId;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.authentication.AuthenticationToken;
import org.craftercms.social.domain.Target;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.UGC.ModerationStatus;
import org.craftercms.social.domain.UGCAudit;
import org.craftercms.social.domain.UGCAudit.AuditAction;
import org.craftercms.social.exceptions.AttachmentErrorException;
import org.craftercms.social.exceptions.PermissionDeniedException;
import org.craftercms.social.moderation.ModerationDecision;
import org.craftercms.social.repositories.UGCAuditRepository;
import org.craftercms.social.repositories.UGCRepository;
import org.craftercms.social.services.*;
import org.craftercms.social.services.impl.UGCServiceImpl;
import org.craftercms.social.services.impl.VirusScannerServiceImpl;
import org.craftercms.social.util.action.ActionEnum;
import org.craftercms.social.util.action.ActionUtil;
import org.craftercms.social.util.support.CrafterProfile;
import org.craftercms.social.util.web.Attachment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { RequestContext.class})
public class UGCServiceTest {
//	@Test //TODO: DISABLING test to make the build up
//	public void test() {
//		
//	}
	
	@Mock
	private PermissionService permissionService;
	@Mock
	private CounterService counterService;
	@Mock
	private TenantService tenantService;
	
	@Mock
	private CrafterProfile crafterProfileService;
	@Mock
	private UGCAuditRepository auditRepository;
	@Mock
	private UGCRepository repository;
	@Mock
	private ModerationDecision moderationDecisionManager;
	@Mock
    private SupportDataAccess supportDataAccess;

	@InjectMocks
	private UGCServiceImpl ugcServiceImpl;
	
	private static final String VALID_ID = 	 "520278180364146bdbd42d1f";
	private static final String ROOT_ID = 	 "520278180364146bdbd42d16";
	private static final String PROFILE_ID = "5202b88203643ac2849709bc";
	private static final String ATTACHMENT_ID = "5202b88203643ac2849709ac";
	private static final String SORT_FIELD = "createdDate";
	private static final String SORT_ORDER = "DESC";
	
	private Profile currentProfile;
	private UGC currentUGC;
	private UGC parentUGC;

	private List<UGC> ul;
	private List<UGCAudit> la;
	private List<String> moderateRootRoles;
	
	@Before
	public void startup() {
		mockStatic(RequestContext.class);
		when(RequestContext.getCurrent()).thenReturn(getCurrentRequestContext());


		currentProfile = getProfile();
		currentUGC = getUGC();
		ul = new ArrayList<UGC>();
		ul.add(currentUGC);
		UGCAudit audit = getAudit();
		la = new ArrayList<UGCAudit>();
		la.add(getAudit());
		moderateRootRoles = new ArrayList<String>();
		moderateRootRoles.add("tester");
		
		Attachment attachment = new Attachment("image/png", 412, "mypicture.png");
		
		when(crafterProfileService.getProfile(PROFILE_ID)).thenReturn(currentProfile);
		when(repository.findOne(new ObjectId(VALID_ID))).thenReturn(currentUGC);
		when(repository.findOne(new ObjectId(ROOT_ID))).thenReturn(currentUGC);
		when(repository.findUGCs("","", new String[]{""}, ActionEnum.READ, 0, 0, SORT_FIELD,SORT_ORDER)).thenReturn(ul);
		when(repository.findUGC(new ObjectId(VALID_ID), ActionEnum.READ,new String[]{""})).thenReturn(currentUGC);
		when(repository.findByIds(Mockito.<ObjectId[]>any())).thenReturn(ul);
		when(repository.findByTenantTargetPaging("test","testing",1,10,ActionEnum.READ,SORT_FIELD,SORT_ORDER)).thenReturn(ul);
		when(repository.findTenantAndTargetIdAndParentIsNull(Mockito.<String>any(),Mockito.<String>any(),Mockito.<ActionEnum>any())).thenReturn(ul);
		when(repository.save(Mockito.<UGC>any())).thenReturn(currentUGC);
		when(permissionService.getQuery(ActionEnum.READ, currentProfile)).thenReturn(getQuery());
		when(permissionService.allowed(Mockito.<ActionEnum>any(), Mockito.<UGC>any(), Mockito.<Profile>any())).thenReturn(true);
		when(counterService.getNextSequence(Mockito.<String>any())).thenReturn(1l);
		when(auditRepository.findByProfileIdAndAction(PROFILE_ID, AuditAction.CREATE)).thenReturn(la);
		when(auditRepository.findByProfileIdAndUgcIdAndAction(PROFILE_ID, new ObjectId(VALID_ID),AuditAction.CREATE)).thenReturn(audit);
		when(tenantService.getRootModeratorRoles("test")).thenReturn(moderateRootRoles);
		when(supportDataAccess.getAttachment(Mockito.<ObjectId>any())).thenReturn(attachment);
		
	}
	
	@Test
	public void testFindById() {
		UGC ugc = ugcServiceImpl.findById(new ObjectId(VALID_ID));
		assertNotNull(ugc);
	}

	@Test
	public void testFindByModerationStatus() {
		mockStatic(RequestContext.class);
		
		when(RequestContext.getCurrent()).thenReturn(getCurrentRequestContext());

		List<UGC> l = ugcServiceImpl.findByModerationStatus(ModerationStatus.UNMODERATED, "test", 0, 0, SORT_FIELD,SORT_ORDER);
		assertNotNull(l);
		assertNotNull(l.size() > 0);
		
	}
	@Test
	public void testFindByModerationStatusAndTargetId() {
		mockStatic(RequestContext.class);
		
		when(RequestContext.getCurrent()).thenReturn(getCurrentRequestContext());
		
		List<UGC> l = ugcServiceImpl.findByModerationStatusAndTargetId(ModerationStatus.UNMODERATED, "test", "testing", 0, 0, SORT_FIELD,SORT_ORDER);
		assertNotNull(l);
		assertNotNull(l.size() > 0);
		
	}
	
	@Test
	public void testFindByTarget() {
		mockStatic(RequestContext.class);
		
		when(RequestContext.getCurrent()).thenReturn(getCurrentRequestContext());
		
		List<UGC> l = ugcServiceImpl.findByTarget("test", "testing", 0, 0, SORT_FIELD,SORT_ORDER);
		assertNotNull(l);
		assertNotNull(l.size() > 0);
		
	}
	@Test
	public void testFindByTargetValidUGC() {
		mockStatic(RequestContext.class);
		
		when(RequestContext.getCurrent()).thenReturn(getCurrentRequestContext());
		
		List<UGC> l = ugcServiceImpl.findByTargetValidUGC("test","testing",PROFILE_ID,SORT_FIELD,SORT_ORDER);
		assertNotNull(l);
		assertNotNull(l.size() > 0);
		
	}
	@Test
	public void testFindByTargetValid_UGC() {
		mockStatic(RequestContext.class);
		
		when(RequestContext.getCurrent()).thenReturn(getCurrentRequestContext());
		
		List<UGC> l = ugcServiceImpl.findByTargetValidUGC("test","testing",PROFILE_ID,1,10,SORT_FIELD,SORT_ORDER);
		assertNotNull(l);
		assertNotNull(l.size() > 0);
		
	}
	@Test
	public void testFindUGCAndChildren() {
		mockStatic(RequestContext.class);
		
		when(RequestContext.getCurrent()).thenReturn(getCurrentRequestContext());
		when(repository.findUGC(new ObjectId(VALID_ID), ActionEnum.READ, new String[] {
			ModerationStatus.APPROVED.toString(), ModerationStatus.UNMODERATED.toString(),
			ModerationStatus.PENDING.toString(), ModerationStatus.TRASH.toString()})).thenReturn(currentUGC);
		UGC ugc = ugcServiceImpl.findUGCAndChildren(new ObjectId(VALID_ID), "test", PROFILE_ID, SORT_FIELD,SORT_ORDER);
		assertNotNull(ugc);
		
	}
	@Test
	public void testInitUGCAndChildren() {
		mockStatic(RequestContext.class);
		
		when(RequestContext.getCurrent()).thenReturn(getCurrentRequestContext());
		
		UGC ugc = ugcServiceImpl.initUGCAndChildren(currentUGC, currentProfile, new String[]{"UNMODERATED"}, SORT_FIELD,SORT_ORDER);
		assertNotNull(ugc);
		
	}
	@Test
	public void testLikeUGC() {
		mockStatic(RequestContext.class);
		
		when(RequestContext.getCurrent()).thenReturn(getCurrentRequestContext());
		
		UGC ugc = ugcServiceImpl.likeUGC(new ObjectId(VALID_ID), "test",PROFILE_ID);
		assertNotNull(ugc);
		
	}
	@Test
	public void testDiskeUGC() {
		mockStatic(RequestContext.class);
		
		when(RequestContext.getCurrent()).thenReturn(getCurrentRequestContext());
		
		UGC ugc = ugcServiceImpl.dislikeUGC(new ObjectId(VALID_ID), "test",PROFILE_ID);
		assertNotNull(ugc);
		
	}
	@Test
	public void testFlagUGC() {
		mockStatic(RequestContext.class);
		
		when(RequestContext.getCurrent()).thenReturn(getCurrentRequestContext());
		
		UGC ugc = ugcServiceImpl.flagUGC(new ObjectId(VALID_ID), "testing", "test",PROFILE_ID);
		assertNotNull(ugc);
		
	}
	
	@Test
	public void testGetAttachment() {
		mockStatic(RequestContext.class);
		
		when(RequestContext.getCurrent()).thenReturn(getCurrentRequestContext());
		
		Attachment a = ugcServiceImpl.getAttachment(new ObjectId(ATTACHMENT_ID));
		assertNotNull(a);
		
	}
	@Test
	public void testGetTenantTargetCount() {
		mockStatic(RequestContext.class);
		
		when(RequestContext.getCurrent()).thenReturn(getCurrentRequestContext());
		
		int count = ugcServiceImpl.getTenantTargetCount("test","testing");
		assertTrue(count > 0);
		
	}
	@Test
	public void testNewChild() {
		mockStatic(RequestContext.class);
		
		when(RequestContext.getCurrent()).thenReturn(getCurrentRequestContext());
		UGC u  = null;
		try {
            currentUGC.setActions(ActionUtil.getDefaultActions());
            currentUGC.setTenant("test");
            currentUGC.setProfileId(PROFILE_ID);
            currentUGC.setAnonymousFlag(false);
			u = ugcServiceImpl.newUgc(currentUGC);
		} catch (PermissionDeniedException pde) {
			fail(pde.getMessage());
		} catch (AttachmentErrorException dee) {
            fail(dee.getMessage());
        }
        assertNotNull(u);
		
	}
	
	@Test
	public void testNewChildUgc() {
		mockStatic(RequestContext.class);
		
		when(RequestContext.getCurrent()).thenReturn(getCurrentRequestContext());
		UGC u  = null;
		try {
			currentUGC.setParentId(new ObjectId(ROOT_ID));
            currentUGC.setActions(ActionUtil.getDefaultActions());
            currentUGC.setTenant("test");
            currentUGC.setProfileId(PROFILE_ID);
            currentUGC.setAnonymousFlag(false);
			u = ugcServiceImpl.newChildUgc(currentUGC);
		} catch (PermissionDeniedException pde) {
			fail(pde.getMessage());
        } catch (AttachmentErrorException dee) {
            fail(dee.getMessage());
        }
		assertNotNull(u);
		
	}
	@Test
	public void testSetAttributes() {
		mockStatic(RequestContext.class);
		
		when(RequestContext.getCurrent()).thenReturn(getCurrentRequestContext());

		Map<String,Object> attributeMap = new HashMap<String, Object>();
		attributeMap.put("article", "Content");
		ugcServiceImpl.setAttributes(currentUGC.getId(), attributeMap, "test", PROFILE_ID);
	}
	@Test
	public void testUpdateModerationStatus() {
		mockStatic(RequestContext.class);
		
		when(RequestContext.getCurrent()).thenReturn(getCurrentRequestContext());
		
		Map<String,Object> attributeMap = new HashMap<String, Object>();
		attributeMap.put("article", "Content");
		UGC ugc = ugcServiceImpl.updateModerationStatus(currentUGC.getId(), ModerationStatus.APPROVED,"test", PROFILE_ID);
		assertNotNull(ugc);
	}
	@Test
	public void testUpdateUGC() {
		mockStatic(RequestContext.class);
		
		when(RequestContext.getCurrent()).thenReturn(getCurrentRequestContext());
		
		Map<String,Object> attributeMap = new HashMap<String, Object>();
		attributeMap.put("article", "Content");
		UGC ugc = null;
		try {
			ugc = ugcServiceImpl.updateUgc(currentUGC.getId(), "test", "testing", PROFILE_ID, null, "Content", null, null, null);
		} catch(PermissionDeniedException pde) {
			fail(pde.getMessage());
        } catch (AttachmentErrorException dee) {
            fail(dee.getMessage());
        }

		assertNotNull(ugc);
	}
//	@Test
//	public void testFindByProfileAction() {
//		List<UGC> l = ugcServiceImpl.findByProfileAction(PROFILE_ID, AuditAction.CREATE);
//		assertNotNull(l);
//		assertNotNull(l.size() > 0);
//	}
//findTargetsForModerationStatus -> MAPREDUCE	
	// getTargets -> MAPREDUCE
	//streamAttachment
	
	private UGC getUGC() {
		UGC ugc= new UGC();
		ugc.setCreatedBy("test");
		ugc.setCreatedDate(new Date());
		ugc.setFlagCount(0);
		ugc.setId(new ObjectId(VALID_ID));
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
		return ugc;
	}

	private Profile getProfile() {
		Map<String,Object> attributes = new HashMap<String, Object>();
		Profile p = new Profile(PROFILE_ID, "test", "test", true, new Date(), new Date(), attributes,"", true);
		return p;
	}
	
	private Query getQuery() {
		String[] roles = new String[]{"tester"};
		Query query = new Query();
		query.addCriteria(Criteria.where("actions").elemMatch(
				Criteria.where("name").is("read")
					.and("roles").in(roles)));
		return query;
	}
	
	private RequestContext getCurrentRequestContext() {
		AuthenticationToken at = new AuthenticationToken();
		UserProfile us = new UserProfile(getProfile());
		at.setProfile(us);
		RequestContext rc = new RequestContext();
		rc.setTenantName("test");
		rc.setAuthenticationToken(at);
		return rc;
	}
	
	private UGCAudit getAudit() {
		UGCAudit a = new UGCAudit();
		a.setAction(AuditAction.CREATE);
		a.setProfileId(PROFILE_ID);
		a.setReason("");
		a.setTenant("test");
		Target t = new Target();
		t.setTargetId("targetId");
		t.setTargetDescription("targetdescription");
		t.setTargetUrl("targeturl");
		a.setTarget(t);
		//a.setId(new ObjectId("5202b88203643ac2849709bc"));
		a.setRow(10l);
		a.setUgcId(new ObjectId(VALID_ID));
		return a;
	}
}