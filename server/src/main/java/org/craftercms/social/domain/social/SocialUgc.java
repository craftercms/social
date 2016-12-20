package org.craftercms.social.domain.social;

import java.util.HashSet;
import java.util.Set;

import org.craftercms.social.domain.UGC;

/**
 *
 */
public class SocialUgc extends UGC{

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
    private Set<Flag> flags;


    public SocialUgc() {
       init();
    }

    public <T extends UGC> SocialUgc(final T base) {
        super(base);
        init();
    }

    private void init(){
        votesUp = new HashSet<>();
        votesDown = new HashSet<>();
        flags = new HashSet<>();
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

    public Set<Flag> getFlags() {
        return flags;
    }

    public void setFlags(final Set<Flag> flags) {
        this.flags = flags;
    }
}
