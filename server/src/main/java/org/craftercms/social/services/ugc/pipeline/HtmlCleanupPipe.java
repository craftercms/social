package org.craftercms.social.services.ugc.pipeline;

import org.craftercms.social.domain.UGC;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.services.ugc.UgcPipe;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Cleans up the body of the UGC to prevent XSS
 */
public class HtmlCleanupPipe implements UgcPipe{
    @Override
    public <T extends UGC> void process(final T ugc) throws SocialException {
        ugc.setBody(Jsoup.clean(ugc.getBody(), Whitelist.relaxed().addTags("div","em")));
    }
}
