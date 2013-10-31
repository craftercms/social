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


public class VirusScannerServiceImpl implements VirusScannerService {

	private final transient Logger log = LoggerFactory.getLogger(VirusScannerServiceImpl.class);

	private VirusScanner virusScanner;

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

	@Required
	public void setVirusScanner(VirusScanner virusScanner) {
		this.virusScanner = virusScanner;
	}
}
