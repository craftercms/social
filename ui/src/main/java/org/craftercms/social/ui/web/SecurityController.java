package org.craftercms.social.ui.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SecurityController {
	
	@RequestMapping("/login")
	public String login(Model model, @RequestParam(required=false) String message) {
		return "login";
	}
	
	@RequestMapping("/logout")
	public String logout() {
		return "redirect:/logout";
	}

}
