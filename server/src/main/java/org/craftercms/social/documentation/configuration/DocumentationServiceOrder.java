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

/**
 * Utility class for Documentation Service order
 *
 * @author Dejan Brkic
 */
public class DocumentationServiceOrder {

    /**
     * Order value for Asset Service.
     */
    public static final int ASSET_SERVICE = 0;

    /**
     * Order value for ContentType Service.
     */
    public static final int CONTENT_TYPE_SERVICE = 1;

    /**
     * Order value for Descriptor Service.
     */
    public static final int DESCRIPTOR_SERVICE = 2;

    /**
     * Order value for Template Service.
     */
    public static final int TEMPLATE_SERVICE = 3;

    private DocumentationServiceOrder() { }
}
