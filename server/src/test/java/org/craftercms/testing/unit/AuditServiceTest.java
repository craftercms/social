package org.craftercms.testing.unit;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.security.api.RequestContext;
import org.craftercms.social.domain.UGCAudit;
import org.craftercms.social.domain.UGCAudit.AuditAction;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.repositories.UGCAuditRepository;
import org.craftercms.social.services.impl.AuditServicesImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RequestContext.class})
public class AuditServiceTest {
    @Mock
    private UGCAuditRepository repository;
    @InjectMocks
    private AuditServicesImpl auditServiceImpl;
    private UGCAudit audit;
    private List<UGCAudit> la;

    private static final String UGC_ID = "520278180364146bdbd42d1f";
    private static final String PROFILE_ID = "5202b88203643ac2849709bc";

    @Before
    public void startup() throws SocialException, MongoDataException {

        audit = getAudit();
        la = new ArrayList<>();
        la.add(audit);
        when(repository.findByUgcId(Mockito.<ObjectId>any())).thenReturn(la);
        when(repository.findByUgcIdAndProfileId(Mockito.<ObjectId>any(), Mockito.<String>any())).thenReturn(la);
        when(repository.findByUgcIdAndAction(Mockito.<ObjectId>any(), Mockito.<AuditAction>any())).thenReturn(la);
        when(repository.findByProfileId(Mockito.<String>any())).thenReturn(la);
        when(repository.findByProfileIdAndAction(Mockito.<String>any(), Mockito.<AuditAction>any())).thenReturn(la);
    }

    @Test
    public void testFindUGCAudits() throws SocialException {
        Iterable<UGCAudit> la = auditServiceImpl.findUGCAudits(new ObjectId(UGC_ID));
        assertNotNull(la);
    }

    @Test
    public void testFindUGCAuditsWithProfileId() throws SocialException {
        Iterable<UGCAudit> la = auditServiceImpl.findUGCAudits(new ObjectId(UGC_ID), PROFILE_ID);
        assertNotNull(la);

    }

    @Test
    public void testFindUGCAuditsWithAuditAction() throws SocialException {
        Iterable<UGCAudit> la = auditServiceImpl.findUGCAudits(new ObjectId(UGC_ID), AuditAction.DISLIKE);
        assertNotNull(la);
    }

    @Test
    public void testFindUGCAuditsWithProfileIdOnly() throws SocialException {
        Iterable<UGCAudit> la = auditServiceImpl.findUGCAuditsForProfile(PROFILE_ID);
        assertNotNull(la);
    }

    @Test
    public void testFindUGCAuditsWithProfile() throws SocialException {
        Iterable<UGCAudit> la = auditServiceImpl.findUGCAuditsForProfile(PROFILE_ID, AuditAction.DISLIKE);
        assertNotNull(la);
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
