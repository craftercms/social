package org.craftercms.testing.unit;

import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.security.api.RequestContext;
import org.craftercms.social.domain.Tenant;
import org.craftercms.social.exceptions.TenantException;
import org.craftercms.social.repositories.TenantRepository;
import org.craftercms.social.services.impl.TenantServiceImpl;
import org.craftercms.social.util.action.ActionConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RequestContext.class})
public class TenantServiceTest {

    private static final String TENANT_ID = "520278180364146bdbd42d1f";
    @Mock
    private TenantRepository tenantRepository;
    @InjectMocks
    private TenantServiceImpl tenantService;

    private Tenant currentTenant;

    @Before
    public void startup() throws MongoDataException {
        currentTenant = getTenant();

        when(tenantRepository.findTenantByTenantName("test")).thenReturn(currentTenant);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {

                return null;
            }
        }).when(tenantRepository).save(Mockito.any(Tenant.class));
    }


    @Test
    public void testGetRootCreateRoles() throws TenantException {
        List<String> roles = tenantService.getRootCreateRoles("test");
        assertNotNull(roles);
        assertTrue(roles.size() > 0);
    }

    @Test
    public void testGetTenantByName() throws TenantException {
        Tenant t = tenantService.getTenantByName("test");
        assertNotNull(t);

    }

    @Test
    public void testSaveTenant() throws TenantException {
        Tenant t = tenantService.setTenant("tenant", Arrays.asList(ActionConstants.SOCIAL_ADMIN,
            ActionConstants.SOCIAL_AUTHOR));
        assertNotNull(t);

    }

    @Test
    public void testDeleteTenant() throws TenantException, MongoDataException {
        tenantService.deleteTenant("test");
        Mockito.verify(tenantRepository).removeById(Mockito.anyString());

    }

    private Tenant getTenant() {
        Tenant t = new Tenant();
        t.setId(new ObjectId(TENANT_ID));
        t.setTenantName("test");
        t.setRoles(Arrays.asList(ActionConstants.SOCIAL_ADMIN, ActionConstants.SOCIAL_AUTHOR));
        return t;
    }

}
