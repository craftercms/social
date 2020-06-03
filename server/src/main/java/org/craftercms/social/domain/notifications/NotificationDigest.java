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

package org.craftercms.social.domain.notifications;

import java.util.List;

import org.craftercms.social.domain.UGC;
import org.jongo.marshall.jackson.oid.Id;

/**
 *
 */
public class NotificationDigest<T extends UGC> {

    @Id
    private String actionType;

    private List<T> ugcForAction;


    public String getActionType() {
        return actionType;
    }

    public void setActionType(final String actionType) {
        this.actionType = actionType;
    }

    public List<T> getUgcForAction() {
        return ugcForAction;
    }

    public void setUgcForAction(final List<T> ugcForAction) {
        this.ugcForAction = ugcForAction;
    }
}
