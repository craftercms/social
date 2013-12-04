package org.craftercms.social.controllers.rest.v1;

import java.util.Map;

import org.craftercms.social.services.ClientConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles all the configuration Service
 */
@Controller
@RequestMapping("/api/2/config")
public class ConfigurationController {

    @Autowired
    private ClientConfigurationService clientConfigurationService;

   @RequestMapping(value = "config",method = RequestMethod.GET)
   public Map<String,Object> getUIConfiguration(){
     return clientConfigurationService.getClientConfiguration();
   }
}
