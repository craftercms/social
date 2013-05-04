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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.util.FileCopyUtils;

/**
 *  Non dependent representation of a Attachment(File)
 * @author cortiz
 */
public class Attachment {

	private OutputStream output;
	private String contentType;
	private long length;
	private String filename;

	public Attachment(String contentType, long length,
			String filename, OutputStream output) {
		super();
		this.contentType = contentType;
		this.length = length;
		this.filename = filename;
		this.output = output;
	}
	
	public Attachment(String contentType, long length,
			String filename) {
		this(contentType,length,filename, new ByteArrayOutputStream());
	}

	public Attachment() {
	}

	/**
	 * Reads the file an store it in the internal Stream
	 * @param in File to be Read
	 * @throws IOException
	 */
	public void read(InputStream in) throws IOException{
		FileCopyUtils.copy(in, output);
	}
	
	public OutputStream getOutputStream() {
		return output;
	}
	
	public String getContentType() {
		return contentType;
	}

	public long getLength() {
		return length;
	}

	public String getFilename() {
		return filename;
	}

}
