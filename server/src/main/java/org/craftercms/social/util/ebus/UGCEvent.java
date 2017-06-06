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
