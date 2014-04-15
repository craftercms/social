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
package org.craftercms.social.exceptions;

import java.io.IOException;

import org.craftercms.social.util.action.ActionEnum;


public class AttachmentErrorException extends SocialException {

    private static final long serialVersionUID = -6487913701852114662L;

    public AttachmentErrorException(String msg) {
        super(msg);
    }

	public AttachmentErrorException(String msg, ActionEnum action, Throwable thr) {
		super(msg + ": Action " + action, thr);
	}

	public AttachmentErrorException(String msg, ActionEnum action) {
		super(msg + ": Action " + action);
	}

    public AttachmentErrorException(final Throwable e) {
        super("Unable to create Attachment",e);
    }
}
