package org.craftercms.social.util;

import org.craftercms.social.security.SecurityActionNames;
import org.craftercms.social.util.ebus.SocialEvent;
import org.craftercms.social.util.ebus.UGCEvent;
import org.craftrercms.commons.ebus.annotations.EListener;
import org.craftrercms.commons.ebus.annotations.EventHandler;
import org.craftrercms.commons.ebus.annotations.EventSelectorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.event.Event;

/**
 *
 */
@EListener
public class EventLogger {
    private Logger logger = LoggerFactory.getLogger(EventLogger.class);

    @EventHandler(ebus = "@socialReactor",event = SecurityActionNames.UGC_CREATE,type = EventSelectorType.REGEX)
    public void logEvent(Event<SocialEvent> source){
        final SocialEvent event = source.getData();
        logger.debug("Event {} type {} object {} "+source.getId(),event.getType().getName(),event.toString());
    }
}
