package org.craftercms.social.services;


import java.io.File;

public interface VirusScannerService {

	/**
	 *
	 * @param files
	 * @return  null on success or error string
	 */
    public String scan(File[] files);

	/**
	 * Used to avoid data preparation for scan() method
	 * @return
	 */
	public boolean isNullScanner();
}
