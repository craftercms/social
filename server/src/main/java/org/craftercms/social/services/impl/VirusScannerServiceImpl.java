package org.craftercms.social.services.impl;


import org.apache.commons.io.FilenameUtils;
import org.craftercms.social.services.VirusScannerService;
import org.craftercms.virusscanner.api.VirusScanner;
import org.craftercms.virusscanner.impl.ClamavVirusScannerImpl;
import org.craftercms.virusscanner.impl.NullVirusScannerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Virus Scanner Service that scans files using a crafter virus scanner implementation
 */
public class VirusScannerServiceImpl implements VirusScannerService {

	private final transient Logger log = LoggerFactory.getLogger(VirusScannerServiceImpl.class);

	private VirusScanner virusScanner;

    /**
     *
     * @param tmpFile the file to be scanned
     * @param originalFilename the simple file name of the original file
     * @return null on success or error message
     */
	@Override
	public String scan(File tmpFile, String originalFilename) {

		String userErrorMessage = null;

        if(originalFilename == null){
            originalFilename = tmpFile.getName();
        }

        try {
            InputStream inputStream = new FileInputStream(tmpFile);
            userErrorMessage = this.virusScanner.scan(inputStream);
        } catch (IOException e) {
            userErrorMessage = ClamavVirusScannerImpl.SCAN_FAILED_MESSAGE;
            log.error(e + " - USER MESSAGE: " + userErrorMessage);
        }

        if (userErrorMessage != null) {
            userErrorMessage += " when scanning " + FilenameUtils.getName(originalFilename);
        }

		return userErrorMessage;
	}

	@Override
	public boolean isNullScanner() {
		return virusScanner instanceof NullVirusScannerImpl;
	}

    /**
     *
     * @param virusScanner the virus scanner that will be use to scan (scan() method)
     */
	@Required
	public void setVirusScanner(VirusScanner virusScanner) {
		this.virusScanner = virusScanner;
	}
}
