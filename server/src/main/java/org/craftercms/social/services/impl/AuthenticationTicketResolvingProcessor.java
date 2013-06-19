package org.craftercms.social.services.impl;

import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.RequestSecurityProcessorChain;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.authentication.AuthenticationToken;
import org.craftercms.security.impl.processors.AuthenticationTokenResolvingProcessor;
import org.craftercms.security.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationTicketResolvingProcessor extends AuthenticationTokenResolvingProcessor {
	
	public static final Logger logger = LoggerFactory.getLogger(AuthenticationTicketResolvingProcessor.class);
	
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        // Make sure not to run the logic if there's already a token in the context
       if (context.getAuthenticationToken() == null) {
            AuthenticationToken token;

            if (logger.isDebugEnabled()) {
                logger.debug("Retrieving authentication token for request '" + context.getRequestUri() + "' from cache");
            }
            String ticket = context.getRequest().getParameter("ticket");
            if (ticket != null && !ticket.equals("")) {
            	token = new AuthenticationToken();
            	UserProfile profile = authenticationService.getProfile(ticket);
              if (profile != null) {
                  token.setProfile(profile);

                  if (logger.isDebugEnabled()) {
                      logger.debug("Caching authentication token " + token);
                  }

                  // If profile is not null, set it in token and cache the token.
                  authenticationTokenCache.saveToken(context, token);
              } else {
                  if (logger.isDebugEnabled()) {
                      logger.debug("No profile found for ticket '" + token.getTicket() + "'");
                  }

                  // Authentication token was cached, profile was outdated and authentication service couldn't retrieve a profile
                  // for the ticket, which means the authentication as a whole expired, so remove token from cache.
                  if (token.getProfile() != null) {
                      if (logger.isDebugEnabled()) {
                          logger.debug("Authentication expired: removing authentication token " + token + " from cache");
                      }

                      authenticationTokenCache.removeToken(context, token);
                  }

                  token.setTicket(null);
                  token.setProfile(SecurityUtils.getAnonymousProfile());
              }
              context.setAuthenticationToken(token);
              processorChain.processRequest(context);
            } else {
            	super.processRequest(context, processorChain);
            }

           } else {
                processorChain.processRequest(context);
           }
    }

}
