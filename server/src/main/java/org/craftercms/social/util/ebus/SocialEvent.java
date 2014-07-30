package org.craftercms.social.util.ebus;


import java.io.InputStream;
import java.util.Map;

import org.craftercms.social.domain.UGC;

/**
 * Created by Carlos Ortiz on 7/29/14.
 */
public class SocialEvent<T extends UGC> {

    private T source;
    private String ugcId;
    private Map<String,Object> attributes;
    private InputStream[] attachments;
    private String attachmentId;

    private UGCEvent type;

    public SocialEvent(final T source) {
        this.source = source;
        this.attributes=source.getAttributes();
        this.ugcId=source.getId().toString();
    }


    public SocialEvent(final String ugcId) {
        this.ugcId = ugcId;
    }

    public SocialEvent(final String ugcId, final Map<String, Object> attributes) {
        this.ugcId = ugcId;
        this.attributes = attributes;
    }

    public SocialEvent(final String ugcId, final String attachmentId) {
        this.ugcId = ugcId;
        this.attachmentId = attachmentId;
    }

    public T getSource() {
        return source;
    }

    public String getUgcId() {
        return ugcId;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public SocialEvent(final String ugcId, final InputStream[] attachments) {
        this.ugcId = ugcId;
        this.attachments = attachments;
    }

    public InputStream[] getAttachments() {
        return attachments;
    }

    public UGCEvent getType() {
        return type;
    }


}
