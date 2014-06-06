package org.craftercms.social.services.ugc.pipeline;

import java.util.List;

import org.craftercms.social.domain.UGC;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.services.ugc.UgcPipe;

/**
 * Created by cortiz on 5/29/14.
 */
public class UgcPipeline {

    private List<UgcPipe> pipeList;


    public <T extends UGC> void processUgc(T ugc) throws SocialException {
        for (UgcPipe ugcPipe : pipeList) {
            ugcPipe.process(ugc);
        }
    }

    public void setPipeList(final List<UgcPipe> pipeList) {
        this.pipeList = pipeList;
    }
}
