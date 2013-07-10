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
package org.craftercms.social.domain;

import org.bson.types.ObjectId;

public class AttachmentModel {
	
	private static final String BASE_URL = "/crafter-social/api/2/get_attachment/";
	
	private String filename;
	private String attachmentId;
	private String contentType;
	private String url;
	
	public AttachmentModel() {}
	
	public AttachmentModel(String fileName, ObjectId attachmentId, String contentType, String tenant) {
		this.filename = fileName;
		this.attachmentId = attachmentId.toString();
		this.contentType = contentType;
		url = BASE_URL + attachmentId + "?tenant=" + tenant;
	}

	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getAttachmentId() {
		return attachmentId;
	}
	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public String getUrl() {
		return url;
	}
	
}
