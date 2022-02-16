/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.repositories.system;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.social.domain.audit.AuditLog;
import org.craftercms.social.exceptions.SocialException;

/**
 * Created by Carlos Ortiz on 8/21/14.
 */
public interface AuditRepository extends CrudRepository<AuditLog>{
    void deleteByIds(List<String> ids) throws SocialException;

    List<AuditLog> getByDate(String context, Date from, Date to) throws SocialException;

    List<AuditLog> getByDate(Date from, Date to) throws SocialException;

    Iterable<AuditLog> getNotificationsToSend(final String threadId, Date from, Date to) throws
        SocialException;

    List<HashMap> getNotificationDigest(String threadId, Date from, Date to, List<String> profilesToExclude) throws SocialException;

}
