package org.craftercms.social.util.ebus;


import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
    private String userId;


    private UGCEvent type;

    public SocialEvent(final T source,final String userId,UGCEvent type) {
        this.source = source;
        this.attributes=source.getAttributes();
        this.ugcId=source.getId().toString();
        this.userId=userId;
        this.type=type;
    }


    public SocialEvent(final String ugcId,final String userId,UGCEvent type) {
        this.ugcId = ugcId;
        this.userId= userId;
        this.type=type;
    }

    public SocialEvent(final String ugcId, final Map<String, Object> attributes,final String userId,UGCEvent type) {
        this.ugcId = ugcId;
        this.attributes = attributes;
        this.userId=userId;
        this.type=type;
    }

    public SocialEvent(final String ugcId, final String attachmentId,final String userId,UGCEvent type) {
        this.ugcId = ugcId;
        this.attachmentId = attachmentId;
        this.userId=userId;
        this.type=type;
    }

    public T getSource() {
        return source;
    }

    public String getUgcId() {
        return ugcId;
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

    public String getUserId() {
        return userId;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttribute(final String key, final Object value) {
        attributes.put(key,value);
    }

    public <T> T getAttribute(final String key){
        return (T)attributes.get(key);
    }
}
