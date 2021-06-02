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

import static org.craftercms.social.security.SecurityActionNames.UGC_CREATE;
import static org.craftercms.social.security.SecurityActionNames.UGC_DELETE;
import static org.craftercms.social.security.SecurityActionNames.UGC_FLAG;
import static org.craftercms.social.security.SecurityActionNames.UGC_MODERATE;
import static org.craftercms.social.security.SecurityActionNames.UGC_READ;
import static org.craftercms.social.security.SecurityActionNames.UGC_UNFLAG;
import static org.craftercms.social.security.SecurityActionNames.UGC_UPDATE;

public enum UGCEvent {
    UPDATE(UGC_UPDATE), MODERATE(UGC_MODERATE), UNFLAG(UGC_UNFLAG), FLAG(UGC_FLAG), CREATE(UGC_CREATE),
    DELETE(UGC_DELETE), READ(UGC_READ), VOTE("vote"), UPDATE_ATTRIBUTES("updateAttributes"),
    DELETE_ATTRIBUTES("deleteAttributes"), ADD_ATTACHMENT("addAttachment"), DELETE_ATTACHMENT("deleteAttachment");
    private String name;

    UGCEvent(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
