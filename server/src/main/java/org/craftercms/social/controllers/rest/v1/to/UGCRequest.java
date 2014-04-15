package org.craftercms.social.controllers.rest.v1.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.craftercms.social.domain.Action;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Sandra O'Keeffe
 * Date: 11/4/13
 * Time: 9:34 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UGCRequest {

    private String tenant;

    private String ugcId;
    private String parentId;

    private String targetId;
    private String targetUrl;
    private String targetDescription;

    private String textContent;
    private String subject;

    //private String content;

    private MultipartFile[] attachments;
    private Boolean anonymousFlag = false;

    private Map<String, Object> attributes;

    private List<Action> actions;

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getUgcId() {
        return ugcId;
    }

    public void setUgcId(String ugcId) {
        this.ugcId = ugcId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String target) {
        this.targetId = target;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getTargetDescription() {
        return targetDescription;
    }

    public void setTargetDescription(String targetDescription) {
        this.targetDescription = targetDescription;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public MultipartFile[] getAttachments() {
        return attachments;
    }

    public void setAttachments(MultipartFile[] attachments) {
        this.attachments = attachments;
    }

    public Boolean getAnonymousFlag() {
        return anonymousFlag;
    }

    public void setAnonymousFlag(Boolean anonymousFlag) {
        this.anonymousFlag = anonymousFlag;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }
}
