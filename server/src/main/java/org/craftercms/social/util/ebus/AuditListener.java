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

package org.craftercms.social.util.ebus;

import org.craftercms.social.domain.audit.AuditLog;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;

/**
 *
 */
public class AuditListener implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @EventListener
    public void onAudit(final SocialEvent event) {
        AuditLog auditLog = new AuditLog(event.getSource());
        auditLog.setContextId(event.getSource() != null ? event.getSource().getContextId() : "");
        auditLog.setUserId(event.getUserId());
        auditLog.setActionName(event.getType().getName());
        applicationContext.publishEvent(auditLog);
    }

    @Override
    public void setApplicationContext(@NonNull final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
