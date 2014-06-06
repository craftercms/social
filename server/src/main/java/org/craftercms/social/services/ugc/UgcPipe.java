package org.craftercms.social.services.ugc;

import org.craftercms.social.domain.UGC;
import org.craftercms.social.exceptions.SocialException;

/**
 * UGC Pipeline Definition.
 */
public interface UgcPipe {

    /**
     * Process the given ugc in to the pipeline
     * @param ugc UGC of going thru the pipeline
     * @param <T> Any UGC object
     * @throws SocialException If the UGC can't be process. <b>Stops the pipeline execution</b>
     */
    <T extends UGC> void process(final T ugc) throws SocialException;
}
