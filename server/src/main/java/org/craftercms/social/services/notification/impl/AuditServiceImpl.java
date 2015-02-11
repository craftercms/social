/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.services.notification.impl;

import java.util.Date;
import java.util.List;

import org.craftercms.commons.audit.AuditService;
import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.audit.AuditLog;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.repositories.system.AuditRepository;
import org.craftercms.social.security.SocialSecurityUtils;
import org.craftercms.social.util.LoggerFactory;

/**
 * Audit Service Social Impl;
 */
public class AuditServiceImpl extends AuditService<AuditLog> {

    private AuditRepository auditRepository;
    private I10nLogger log = LoggerFactory.getLogger(AuditServiceImpl.class);

    @Override
    public AuditLog getAuditLog(final String id) {
        return null;
    }

    @Override
    protected void persistAudit(final AuditLog auditModel) {
        try {
            auditRepository.save(auditModel);
        } catch (MongoDataException e) {
            log.error("logging.system.auditErrorSaving", e, auditModel);
        }
    }

    @Override
    protected void deleteAudits(final List<String> auditId) {
        try {
            auditRepository.deleteByIds(auditId);
        } catch (SocialException e) {
            log.error("logging.system.unableToDeleteAudit", e, auditId);
        }
    }

    @Override
    public List<AuditLog> getAuditLogs(final Date from) {
        return getAuditLogs(from, new Date());
    }

    @Override
    public List<AuditLog> getAuditLogs(final Date from, final Date to) {
        try {
            return auditRepository.getByDate(from,to);
        } catch (SocialException ex) {
            log.error("logging.system.unableToFindAudits", ex, from, to);
        }
        return null;
    }

    public void setAuditRepository(final AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }
}
