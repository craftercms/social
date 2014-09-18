package org.craftercms.social.domain.audit;

import java.util.Date;
import java.util.UUID;

import de.undercouch.bson4jackson.types.ObjectId;
import org.craftercms.commons.audit.AuditModel;
import org.craftercms.commons.mongo.Document;
import org.craftercms.social.domain.UGC;
import org.jongo.marshall.jackson.oid.Id;

/**
 *
 */
@Document(collectionName = "audit")
public class AuditLog extends AuditModel{

    private String contextId;
    private String userId;
    private String actionName;

    public <T extends UGC> AuditLog(T payload) {
        this.id= UUID.randomUUID().toString();
        this.setAuditDate(new Date());
        this.setPayload(payload);
    }

    @Override
    @Id
    public String getId() {
        return super.getId();
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(final String contextId) {
        this.contextId = contextId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(final String actionName) {
        this.actionName = actionName;
    }
}
