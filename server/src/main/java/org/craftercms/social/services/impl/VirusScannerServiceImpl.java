package org.craftercms.social.services.impl;


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
     * @param files the files to be scanned
     * @return null on success or error message
     */
	@Override
	public String scan(File[] files) {

		String userErrorMessage = null;

		if (files != null) {
			for (File file : files) {

				try {
					InputStream inputStream = new FileInputStream(file);
					userErrorMessage = this.virusScanner.scan(inputStream);
				} catch (IOException e) {
					userErrorMessage = ClamavVirusScannerImpl.SCAN_FAILED_MESSAGE;
					log.error(e + " - USER MESSAGE: " + userErrorMessage);
				}

				if (userErrorMessage != null) {
                    userErrorMessage += " when scanning " + file.getName();
					break;
				}

			}
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
