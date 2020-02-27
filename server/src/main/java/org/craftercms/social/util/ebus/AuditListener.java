/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
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

import org.craftercms.commons.audit.Audit;
import org.craftercms.commons.audit.AuditModel;
import org.craftercms.commons.ebus.annotations.EListener;
import org.craftercms.commons.ebus.annotations.EventHandler;
import org.craftercms.commons.ebus.annotations.EventSelectorType;
import org.craftercms.social.domain.audit.AuditLog;
import reactor.core.Reactor;
import reactor.event.Event;

/**
 *
 */
@EListener
public class AuditListener {

    private Reactor auditReactor;

    @EventHandler(
        event = ".*",
        ebus = SocialEventConstants.SOCIAL_REACTOR_NAME,
        type = EventSelectorType.REGEX)
    public void onAudit(final Event<? extends SocialEvent> socialEvent) {
        SocialEvent event=socialEvent.getData();
        AuditLog auditLog=new AuditLog(event.getSource());
        auditLog.setContextId(event.getSource().getContextId());
        auditLog.setUserId(event.getUserId());
        auditLog.setActionName(socialEvent.getKey().toString());
        auditReactor.notify(Audit.AUDIT_EVENT,Event.wrap(auditLog));
    }

    public void setAuditReactor(final Reactor auditReactor) {
        this.auditReactor = auditReactor;
    }
}
