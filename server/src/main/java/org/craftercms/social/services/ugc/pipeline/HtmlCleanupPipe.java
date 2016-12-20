package org.craftercms.social.services.ugc.pipeline;

import java.util.Map;

import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.social.Flag;
import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.services.ugc.UgcPipe;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Cleans up the body of the UGC to prevent XSS
 */
public class HtmlCleanupPipe implements UgcPipe{
    private final Whitelist whitelist = Whitelist.relaxed().addTags("div","em");
    @Override
    public <T extends UGC> void process(final T ugc,Map<String,Object> params) throws SocialException {
        ugc.setBody(cleanup(ugc.getBody()));
        if(ugc instanceof SocialUgc) {
            for (Flag flag : ((SocialUgc)ugc).getFlags()){
                flag.setReason(cleanup(flag.getReason()));
            }
        }
    }

    /**
     * Does the actual cleanup.
     * @param toCleanup Text to cleanup.
     * @return cleanup text.
     */
    private String cleanup(final String toCleanup){
        return Jsoup.clean(toCleanup,whitelist);
    }
}
