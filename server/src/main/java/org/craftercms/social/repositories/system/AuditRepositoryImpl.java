package org.craftercms.social.repositories.system;

import java.util.Date;
import java.util.List;

import org.craftercms.commons.collections.IterableUtils;
import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.mongo.AbstractJongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.audit.AuditLog;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.services.notification.AuditServiceImpl;
import org.craftercms.social.util.LoggerFactory;

/**
 * Audit Repository.
 */
public class AuditRepositoryImpl extends AbstractJongoRepository<AuditLog> implements AuditRepository {

    private I10nLogger log = LoggerFactory.getLogger(AuditServiceImpl.class);

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
        log.debug("logging.system.findingAuditsBy" , from, to);
        try {
            return IterableUtils.toList(find(query, from, to));
        } catch (MongoDataException e) {
            throw new SocialException("Unable to find Log Audits", e);
        }
    }
}
