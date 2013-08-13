package org.craftercms.testing.unit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.craftercms.security.api.RequestContext;
import org.craftercms.social.domain.Tenant;
import org.craftercms.social.repositories.TenantRepository;
import org.craftercms.social.services.impl.TenantServiceImpl;
import org.craftercms.social.util.action.ActionConstants;
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
public class TenantServiceTest {
	
	private static final String TENANT_ID = "520278180364146bdbd42d1f";
	@Mock
	private TenantRepository tenantRepository;
	@InjectMocks
	private TenantServiceImpl tenantService;
	
	private Tenant currentTenant;
	
	@Before
	public void startup() {
		currentTenant = getTenant();
		
		when(tenantRepository.findTenantByTenantName("test")).thenReturn(currentTenant);
		when(tenantRepository.save(Mockito.<Tenant>any())).thenReturn(currentTenant);
	}
	
	@Test
	public void testGetRootCreateRoles() {
		List<String> roles = tenantService.getRootCreateRoles("test");
		assertNotNull(roles);
		assertTrue(roles.size() > 0);
	}
	@Test
	public void testGetTenantByName() {
		Tenant t = tenantService.getTenantByName("test");
		assertNotNull(t);
		
	}
	@Test
	public void testSaveTenant() {
		List<String> l = new ArrayList<String>(){
			{
				add(ActionConstants.SOCIAL_ADMIN);
				add(ActionConstants.SOCIAL_AUTHOR);
			}
		};
		Tenant t = tenantService.setTenant("tenant", l);
		assertNotNull(t);
		
	}
	@Test
	public void testDeleteTenant() {
		tenantService.deleteTenant("test");
		Mockito.verify(tenantRepository).delete(Mockito.<Tenant>any());
		
	}
	
	private Tenant getTenant() {
		Tenant t = new Tenant();
		t.setId(new ObjectId(TENANT_ID));
		t.setTenantName("test");
		List<String> l = new ArrayList<String>(){
			{
				add(ActionConstants.SOCIAL_ADMIN);
				add(ActionConstants.SOCIAL_AUTHOR);
			}
		};
		t.setRoles(l);
		return t;
	}

}
