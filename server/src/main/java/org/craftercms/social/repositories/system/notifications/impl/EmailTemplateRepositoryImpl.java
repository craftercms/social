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

package org.craftercms.social.repositories.system.notifications.impl;

import org.craftercms.commons.mongo.AbstractJongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.notifications.EmailTemplate;
import org.craftercms.social.repositories.system.notifications.EmailTemplateRepository;

/**
 *
 */
public class EmailTemplateRepositoryImpl extends AbstractJongoRepository<EmailTemplate> implements
    EmailTemplateRepository {


    @Override
    public EmailTemplate findByContextAndType(final String contextId, final String templateName) throws MongoDataException {
        return findOne(getQueryFor("social.system.notification.email.byCtxIdAndName"),contextId,templateName);
    }
}
