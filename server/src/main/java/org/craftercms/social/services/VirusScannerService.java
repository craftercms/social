package org.craftercms.social.services;


import org.springframework.web.multipart.MultipartFile;

public interface VirusScannerService {

	/**
	 *
	 * @param files
	 * @return  null on success or error string
	 */
    public String scan(MultipartFile[] files);

}
