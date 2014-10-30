package org.craftercms.social.repositories.system;

import com.mongodb.MongoException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.craftercms.commons.collections.IterableUtils;
import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.mongo.AbstractJongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.audit.AuditLog;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.services.notification.AuditServiceImpl;
import org.craftercms.social.util.LoggerFactory;
import org.craftercms.social.util.profile.ProfileAggregator;
import org.jongo.Aggregate;

/**
 * Audit Repository.
 */
public class AuditRepositoryImpl extends AbstractJongoRepository<AuditLog> implements AuditRepository {

    private I10nLogger log = LoggerFactory.getLogger(AuditServiceImpl.class);
    private ProfileAggregator profileAggregator;

    @Override
    public void deleteByIds(final List<String> ids) throws SocialException {

        String query = getQueryFor("social.system.audit.deleteByIds");
        log.debug("logging.system.auditAboutToDelete", ids);
        try {
            remove(query, ids);
        } catch (MongoDataException e) {
            throw new SocialException("Unable to delete Audits", e);
        }
    }

    @Override
    public List<AuditLog> getByDate(final String context, final Date from, final Date to) throws SocialException {
        String query = getQueryFor("social.system.audit.byDateRangeCTX");
        log.debug("logging.system.findingAuditsByCTX", context, from, to);
        try {
            return IterableUtils.toList(find(query, context, from, to));
        } catch (MongoDataException e) {
            throw new SocialException("Unable to find Log Audits", e);
        }
    }

    @Override
    public List<AuditLog> getByDate(final Date from, final Date to) throws SocialException {
        String query = getQueryFor("social.system.audit.byDateRange");
        log.debug("logging.system.findingAuditsBy", from, to);
        try {
            return IterableUtils.toList(find(query, from, to));
        } catch (MongoDataException e) {
            throw new SocialException("Unable to find Log Audits", e);
        }
    }

    @Override
    public Iterable<AuditLog> getNotificationsToSend(final String threadId, final Date from, final Date to) throws
        SocialException {
        String query = getQueryFor("social.system.audit.byDateRange");
        log.debug("social.notification.notificationToSend", threadId, from, to);
        try {
            return IterableUtils.toList(find(query, from, to));
        } catch (MongoDataException e) {
            throw new SocialException("Unable to find Log Audits", e);
        }

    }

    @Override
    public List<HashMap> getNotificationDigest(final String id, final Date from, final Date to, List<String>
        profilesToExclude) throws SocialException {
        try {
            final String querypt1 = getQueryFor("social.notification.audit.getNotificationDigestPt1");
            final String querypt2 = getQueryFor("social.notification.audit.getNotificationDigestPt2");
            final String[] idParts = id.split("/");
            final Aggregate agregation = getCollection().aggregate(querypt1, idParts[1], idParts[0],
                profilesToExclude, from, to);
            final List<HashMap> preResults = agregation.and(querypt2).as(HashMap.class);
            for (HashMap preResult : preResults) {
                List<HashMap> ugcList = (List<HashMap>)preResult.get("ugc");
                for (HashMap ugc : ugcList) {
                    if (ugc.containsKey("lastModifiedBy") && ugc.containsKey("createdBy")) {
                        ugc.put("createdBy", profileAggregator.getProfile(ugc.get("createdBy").toString()));
                        ugc.put("lastModifiedBy", profileAggregator.getProfile(ugc.get("lastModifiedBy").toString()));
                    }
                }
            }
            return preResults;
        } catch (MongoException ex) {
            throw new SocialException("Unable to Generate Notification Digest ", ex);
        }

    }


    public void setProfileAggregatorImpl(ProfileAggregator profileAggregator) {
        this.profileAggregator = profileAggregator;
    }
}
