package org.craftercms.social.helpers;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MultipartFileClone implements MultipartFile {

	File tempFile = null;
	MultipartFile multipartFile;

	public MultipartFileClone(MultipartFile multipartFile) throws IOException {
		this.multipartFile = multipartFile;
		makeTempFile();
	}

	protected void makeTempFile() throws IOException {
		tempFile = File.createTempFile("tmp",null);
		multipartFile.transferTo(tempFile);
	}

	@Override
	public String getName() {
		return multipartFile.getName();
	}

	@Override
	public String getOriginalFilename() {
		return multipartFile.getOriginalFilename();
	}

	@Override
	public String getContentType() {
		return multipartFile.getContentType();
	}

	@Override
	public boolean isEmpty() {
		return tempFile.length() == 0;
	}

	@Override
	public long getSize() {
		return tempFile.length();
	}

	@Override
	public byte[] getBytes() throws IOException {
		return FileCopyUtils.copyToByteArray(getInputStream());
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(tempFile);
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		FileCopyUtils.copy(tempFile, dest);
	}
}
