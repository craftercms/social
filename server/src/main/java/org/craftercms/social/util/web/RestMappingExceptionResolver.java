/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.social.util.web;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;


/**
 * Overrides the {@link SimpleMappingExceptionResolver} <br/><br/>
 *  {@link #doResolveException(HttpServletRequest, HttpServletResponse, Object, Exception)} removes the 
 *  StackTrace of the exception <br/><br/>
 *  {@link #determineViewName(Exception, HttpServletRequest)} Retunrs the Canotical name of the Excetion
 *  <br/>
 *  <br/>
 *  now {@link #determineStatusCode(HttpServletRequest, String)} Will use the exction canotical name to 
 *  resolve the status
 * @author cortiz
 */
public class RestMappingExceptionResolver extends SimpleMappingExceptionResolver {

    private final transient Logger log = LoggerFactory.getLogger(RestMappingExceptionResolver.class);

	@Override
	protected String determineViewName(Exception ex, HttpServletRequest request) {
		return ex.getClass().getCanonicalName();
	}

	@Override
	protected ModelAndView getModelAndView(String viewName, Exception ex) {
        log.error(ex.getMessage(), ex);

		ModelAndView mv = new ModelAndView(viewName);
		HashMap<String,Object> map=new HashMap<String,Object>(); 
		map.put("message", ex.getMessage());
		map.put("localizedMessage", ex.getLocalizedMessage());
		mv.addAllObjects(map);

		return mv;
	}
	
	
	
}
