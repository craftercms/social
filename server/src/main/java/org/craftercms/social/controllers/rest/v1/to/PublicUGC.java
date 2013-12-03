package org.craftercms.social.controllers.rest.v1.to;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.craftercms.social.domain.UGC;

public class PublicUGC {


    private String id;
    private String tenant;
    private String textContent;
    private String targetId;
    private String subject;
    private Date creationDate;
    private int likes;
    private int dislikes;
    private int flags;
    private List<String> attachments;
    private Map<String, Object> attributes;
    private Map<String, Object> profile;
    private UserInfo userInfo;


    public PublicUGC(final UGC templateUGC, final String profileId, final List<String> actions) {
        profile=new HashMap<String, Object>();
        this.attachments = new ArrayList<String>();
        this.id = templateUGC.getId().toString();
        this.tenant = templateUGC.getTenant();
        this.textContent = templateUGC.getTextContent();
        this.targetId = templateUGC.getTargetId();
        this.subject = templateUGC.getSubject();
        this.creationDate = templateUGC.getCreatedDate();
        if (templateUGC.getProfile() == null || templateUGC.getAttributes() == null) {
            this.profile.put("displayName", "User");
        } else {
            this.profile.put("displayName", templateUGC.getProfile().getAttributes().get("DISPLAY_NAME").toString());
        }
        this.likes = templateUGC.getLikes().size();
        this.dislikes = templateUGC.getDislikes().size();
        this.flags = templateUGC.getFlags().size();
        this.attributes = templateUGC.getAttributes();
        userInfo = new UserInfo( templateUGC.getLikes().contains(profileId),templateUGC.getDislikes().contains
            (profileId),templateUGC.getFlags().contains(profileId),actions);
        if (templateUGC.getAttachmentId() != null) {
            for (ObjectId objectId : templateUGC.getAttachmentId()) {
                this.attachments.add(objectId.toString());
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getTenant() {
        return tenant;
    }

    public String getTextContent() {
        return textContent;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getSubject() {
        return subject;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Map<String, Object> getProfile() {
        return profile;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public int getFlags() {
        return flags;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public List<String> getAttachments() {
        return attachments;
    }



    class UserInfo {
        private boolean liked;
        private boolean disliked;
        private boolean flagged;
        private List<String> actions;

        UserInfo(final boolean userLiked, final boolean userDisliked, final boolean userFlaged,
                 final List<String> actions) {
            this.liked = userLiked;
            this.disliked = userDisliked;
            this.flagged = userFlaged;
            this.actions = actions;
        }

        public boolean isLiked() {
            return liked;
        }

        public boolean isDisliked() {
            return disliked;
        }

        public boolean isFlagged() {
            return flagged;
        }

        public List<String> getActions() {
            return actions;
        }
    }
}
