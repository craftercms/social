package org.craftercms.virusscanner.impl;

import clamavj.ClamScan;
import clamavj.ScanResult;
import org.craftercms.virusscanner.api.VirusScanner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ClamavjVirusScannerImpl implements VirusScanner {

	public static final String THREAT_FOUND_MESSAGE = "Threat found";
	public static final String FILE_NOT_FOUND_MESSAGE = "File not found";
	public static final String SCAN_FAILED_MESSAGE = "Scan failed";

	private static Log log = LogFactory.getLog(ClamavjVirusScannerImpl.class);

	private String clamdHost;
	private int clamdPort;
	private int clamdTimeout;


	public ClamavjVirusScannerImpl(String clamdHost, int clamdPort, int clamdTimeout) {
		this.clamdHost = clamdHost;
		this.clamdPort = clamdPort;
		this.clamdTimeout = clamdTimeout;
	}

	public String scan(String filename) {

		String userMessage = null;

		File file;
		FileInputStream fileInputStream;

		ClamScan clamScan;
		ScanResult scanResult;

		if (filename != null && !filename.isEmpty()) {

			String result;
			String statusName;
			String signature;

			try {

				file = new File(filename);
				fileInputStream = new FileInputStream(file);

				clamScan = new ClamScan(this.clamdHost, this.clamdPort, this.clamdTimeout);

				scanResult = clamScan.scan(fileInputStream);

				result = scanResult.getResult();
				signature = scanResult.getSignature();
				statusName = scanResult.getStatus().name();

				if (scanResult.getStatus().compareTo(ScanResult.Status.ERROR) == 0) {
					userMessage = SCAN_FAILED_MESSAGE;
					log.error("STATUS: " + statusName + " - RESULT: " + result + " - EXCEPTION: " + scanResult.getException() + " - USER MESSAGE: " + userMessage);
				} else if (scanResult.getStatus().compareTo(ScanResult.Status.FAILED) == 0) {
					userMessage = THREAT_FOUND_MESSAGE;
					log.info("STATUS: " + statusName + " - RESULT: " + result + " - SIGNATURE " + signature + " - USER MESSAGE: " + userMessage);
				} else {
					log.info("STATUS: " + statusName + " - RESULT: " + result);
				}

			} catch (FileNotFoundException e) {
				userMessage = FILE_NOT_FOUND_MESSAGE;
				log.error(e + " - USER MESSAGE: " + userMessage);
			}

		} else {
			userMessage = FILE_NOT_FOUND_MESSAGE;
			log.error(new IllegalArgumentException() + userMessage);
		}

		return userMessage;

	}

	public String scan(InputStream inputStream) {

		String userMessage = null;

		ClamScan clamScan;
		ScanResult scanResult;

		if (inputStream != null) {

			String result;
			String statusName;
			String signature;

			clamScan = new ClamScan(this.clamdHost, this.clamdPort, this.clamdTimeout);

			scanResult = clamScan.scan(inputStream);

			result = scanResult.getResult();
			signature = scanResult.getSignature();
			statusName = scanResult.getStatus().name();

			if (scanResult.getStatus().compareTo(ScanResult.Status.ERROR) == 0) {
				userMessage = SCAN_FAILED_MESSAGE;
				log.error("STATUS: " + statusName + " - RESULT: " + result + " - EXCEPTION: " + scanResult.getException() + " - USER MESSAGE: " + userMessage);
			} else if (scanResult.getStatus().compareTo(ScanResult.Status.FAILED) == 0) {
				userMessage = THREAT_FOUND_MESSAGE;
				log.info("STATUS: " + statusName + " - RESULT: " + result + " - SIGNATURE " + signature + " - USER MESSAGE: " + userMessage);
			} else {
				log.info("STATUS: " + statusName + " - RESULT: " + result);
			}

		} else {
			userMessage = SCAN_FAILED_MESSAGE;
			log.error(new IllegalArgumentException("null inputStream") + userMessage);
		}

		return userMessage;

	}

    public void setClamdHost(String clamdHost) {
        this.clamdHost = clamdHost;
    }

    public void setClamdPort(int clamdPort) {
        this.clamdPort = clamdPort;
    }

    public void setClamdTimeout(int clamdTimeout) {
        this.clamdTimeout = clamdTimeout;
    }

    public String getClamdHost() {
        return clamdHost;
    }

    public int getClamdPort() {
        return clamdPort;
    }

    public int getClamdTimeout() {
        return clamdTimeout;
    }


}
