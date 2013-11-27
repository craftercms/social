package org.craftercms.testing.unit;

import org.craftercms.social.repositories.UGCAuditRepository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.craftercms.security.api.RequestContext;
import org.craftercms.social.domain.UGCAudit;
import org.craftercms.social.domain.UGCAudit.AuditAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.craftercms.social.services.impl.AuditServicesImpl;
@RunWith(PowerMockRunner.class)
@PrepareForTest( { RequestContext.class})
public class AuditServiceTest {
	@Mock
	private UGCAuditRepository repository;
	@InjectMocks
	private AuditServicesImpl  auditServiceImpl;
	private UGCAudit audit;
	private List<UGCAudit> la;
	
	private static final String UGC_ID = 	 "520278180364146bdbd42d1f";
	private static final String PROFILE_ID = "5202b88203643ac2849709bc";
	
	@Before
	public void startup() {
		
		audit = getAudit();
		la = new ArrayList<UGCAudit>();
		la.add(audit);
		when(repository.findByUgcId(Mockito.<ObjectId>any())).thenReturn(la);
		when(repository.findByUgcIdAndProfileId(Mockito.<ObjectId>any(), Mockito.<String>any())).thenReturn(la);
		when(repository.findByUgcIdAndAction(Mockito.<ObjectId>any(), Mockito.<AuditAction>any())).thenReturn(la);
		when(repository.findByProfileId(Mockito.<String>any())).thenReturn(la);
		when(repository.findByProfileIdAndAction(Mockito.<String>any(), Mockito.<AuditAction>any())).thenReturn(la);
	}
	
	@Test
	public void testFindUGCAudits() {
		List<UGCAudit> la = auditServiceImpl.findUGCAudits(new ObjectId(UGC_ID));
		assertNotNull(la);
		assertTrue(la.size()>0);
	}
	
	@Test
	public void testFindUGCAuditsWithProfileId() {
		List<UGCAudit> la = auditServiceImpl.findUGCAudits(new ObjectId(UGC_ID),PROFILE_ID);
		assertNotNull(la);
		assertTrue(la.size()>0);
	}
	@Test
	public void testFindUGCAuditsWithAuditAction() {
		List<UGCAudit> la = auditServiceImpl.findUGCAudits(new ObjectId(UGC_ID),AuditAction.DISLIKE);
		assertNotNull(la);
		assertTrue(la.size()>0);
	}
	@Test
	public void testFindUGCAuditsWithProfileIdOnly() {
		List<UGCAudit> la = auditServiceImpl.findUGCAuditsForProfile(PROFILE_ID);
		assertNotNull(la);
		assertTrue(la.size()>0);
	}
	@Test
	public void testFindUGCAuditsWithProfile() {
		List<UGCAudit> la = auditServiceImpl.findUGCAuditsForProfile(PROFILE_ID, AuditAction.DISLIKE);
		assertNotNull(la);
		assertTrue(la.size()>0);
	}
	
	private UGCAudit getAudit() {
		UGCAudit a = new UGCAudit();
		a.setAction(AuditAction.CREATE);
		a.setProfileId(PROFILE_ID);
		a.setReason("");
		a.setTenant("test");
		//a.setId(new ObjectId("5202b88203643ac2849709bc"));
		a.setRow(10l);
		a.setUgcId(new ObjectId(UGC_ID));
		return a;
	}
	

}
