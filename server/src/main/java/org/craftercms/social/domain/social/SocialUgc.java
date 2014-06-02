package org.craftercms.social.domain.social;

import java.util.HashSet;
import java.util.Set;

import org.craftercms.social.domain.UGC;

/**
 *
 */
public class SocialUgc extends UGC{

    public enum ModerationStatus {
        UNMODERATED, PENDING, APPROVED, SPAM, TRASH
    }

    private ModerationStatus moderationStatus;
    /**
     * List of profiles that like this.
     */
    private Set<String> votesUp;

    /**
     * List of profiles that like dislike.
     */
    private Set<String> votesDown;

    /**
     * List of profiles that flag this.
     */
    private Set<String> flags;


    public SocialUgc() {
        votesUp =new HashSet<>();
        votesDown =new HashSet<>();
        flags=new HashSet<>();
    }

    public <T extends UGC> SocialUgc(final T base) {
        super(base);
    }

    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(final ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public Set<String> getVotesUp() {
        return votesUp;
    }

    public void setVotesUp(final Set<String> votesUp) {
        this.votesUp = votesUp;
    }

    public Set<String> getVotesDown() {
        return votesDown;
    }

    public void setVotesDown(final Set<String> votesDown) {
        this.votesDown = votesDown;
    }

    public Set<String> getFlags() {
        return flags;
    }

    public void setFlags(final Set<String> flags) {
        this.flags = flags;
    }
}
