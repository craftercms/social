package org.craftercms.social.controllers.rest.v1;

import org.craftercms.security.api.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/api/2/auth")
public class AuthenticateRestController {
	
	private final Logger log = LoggerFactory
			.getLogger(AuthenticateRestController.class);
	
	@RequestMapping(value = "/is_valid_ticket", method = RequestMethod.GET)
	@ModelAttribute
	public boolean isValid(@RequestParam String tenant) {
		log.debug(String.format("Is allowed create ugc ", tenant));
		try {
			log.debug(String.format("Is allowed create ugc "));
			try {
				String id = RequestContext.getCurrent().getAuthenticationToken().getProfile().getId();
				if (id == null || id.equalsIgnoreCase("anonymous")) {
					return false;
				}
				return true;
			} catch (Exception e) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

}
