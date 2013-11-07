package org.craftercms.social.services;


import java.io.File;

public interface VirusScannerService {

    /**
     *
     * @param tmpFile
     * @param originalFilename
     * @return null on success or error message
     */
    public String scan(File tmpFile, String originalFilename);

	/**
	 * Used to avoid data preparation for scan() method
	 * @return
	 */
	public boolean isNullScanner();
}
