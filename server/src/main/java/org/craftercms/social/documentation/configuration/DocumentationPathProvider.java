/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
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

package org.craftercms.social.documentation.configuration;

import com.mangofactory.swagger.paths.SwaggerPathProvider;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Dejan Brkic
 */
public class DocumentationPathProvider extends SwaggerPathProvider {

    private String hostUrl;

    private SwaggerPathProvider defaultSwaggerPathProvider;
    @Autowired
    private ServletContext servletContext;



    public void setDefaultSwaggerPathProvider(final SwaggerPathProvider defaultSwaggerPathProvider) {
        this.defaultSwaggerPathProvider = defaultSwaggerPathProvider;
    }

    public void setHostUrl(final String hostUrl) {
        this.hostUrl = hostUrl;
    }

    @Override
    protected String applicationPath() {
        return hostUrl + "/";
    }

    @Override
    protected String getDocumentationPath() {
        return hostUrl + "/";
    }
}
