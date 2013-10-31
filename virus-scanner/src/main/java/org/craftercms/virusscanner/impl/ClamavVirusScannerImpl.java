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

public class ClamavVirusScannerImpl implements VirusScanner {

	public static final String THREAT_FOUND_MESSAGE = "Threat found";
	public static final String FILE_NOT_FOUND_MESSAGE = "File not found";
	public static final String SCAN_FAILED_MESSAGE = "Scan failed";

	private static Log log = LogFactory.getLog(ClamavVirusScannerImpl.class);

	private String host;
	private int port;
	private int timeout;

    public ClamavVirusScannerImpl() {

    }

	public ClamavVirusScannerImpl(String host, int port, int timeout) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
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

				clamScan = new ClamScan(this.host, this.port, this.timeout);

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

			clamScan = new ClamScan(this.host, this.port, this.timeout);

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

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getTimeout() {
        return timeout;
    }


}
