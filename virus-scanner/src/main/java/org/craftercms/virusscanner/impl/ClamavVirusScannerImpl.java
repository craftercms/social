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

/**
 * A virus scanner that uses ClamScan to scan input-streams/files
 */
public class ClamavVirusScannerImpl implements VirusScanner {

	public static final String THREAT_FOUND_MESSAGE = "Threat found";
	public static final String FILE_NOT_FOUND_MESSAGE = "File not found";
	public static final String SCAN_FAILED_MESSAGE = "Scan failed";

	private static Log log = LogFactory.getLog(ClamavVirusScannerImpl.class);

	private String host;
	private int port;
	private int timeout;

    public ClamavVirusScannerImpl() {
        this("localhost",3310,60000);
    }

    /**
     *
     * @param host the clamd server host
     * @param port the clamd server port
     * @param timeout milliseconds to wait for the connection
     */
	public ClamavVirusScannerImpl(String host, int port, int timeout) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	}

    /**
     *
     * @param filename full path
     * @return  null for a successful scan (the file is clean) or an error message if the scan fails or a threat is found
     */
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
					log.error("STATUS: " + statusName + " - RESULT: " + result + " - SIGNATURE " + signature + " - USER MESSAGE: " + userMessage);
				} else {
					log.debug("STATUS: " + statusName + " - RESULT: " + result);
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

    /**
     *
     * @param inputStream
     * @return  null for a successful scan (the input stream is clean) or an error message if the scan fails or a threat is found
     */
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
				log.error("STATUS: " + statusName + " - RESULT: " + result + " - SIGNATURE " + signature + " - USER MESSAGE: " + userMessage);
			} else {
				log.debug("STATUS: " + statusName + " - RESULT: " + result);
			}

		} else {
			userMessage = SCAN_FAILED_MESSAGE;
			log.error(new IllegalArgumentException("null inputStream") + userMessage);
		}

		return userMessage;

	}

    /**
     *
     * @param host the clamd server host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     *
     * @param port the clamd server port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     *
     * @param timeout milliseconds to wait for the connection
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     *
     * @return the clamd server host
     */
    public String getHost() {
        return host;
    }

    /**
     *
     * @return the clamd server port
     */
    public int getPort() {
        return port;
    }

    /**
     *
     * @return the milliseconds to wait for the connection
     */
    public int getTimeout() {
        return timeout;
    }


}
