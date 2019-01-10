/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.virusscanner.impl;

import clamavj.ClamScan;
import clamavj.ScanResult;
import org.craftercms.virusscanner.api.VirusScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static Logger log = LoggerFactory.getLogger(ClamavVirusScannerImpl.class);

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
     * Scans the file for virus.
     * @param filename full path
     */
	public void scan(String filename) {
        try {
            scan(new FileInputStream(filename));
        } catch (FileNotFoundException e) {
            throw new VirusScannerException("Unable to read given file",e);
        }

    }

    /**
     * Scans the {@link InputStream} for viruses.
     * @param inputStream
     */
	public void scan(InputStream inputStream) {
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
                final String msg = "STATUS: " + statusName + " - RESULT: " + result + " - EXCEPTION: " + scanResult
                    .getException() ;
				log.error(msg);
                throw new VirusScannerException(msg,scanResult.getException());
			} else if (scanResult.getStatus().compareTo(ScanResult.Status.FAILED) == 0) {
                final String msg = "STATUS: " + statusName + " - RESULT: " + result + " - SIGNATURE " + signature +
                    "" + " - USER ";
                throw new VirusScannerException(msg,scanResult.getException());
			} else {
				log.debug("STATUS: " + statusName + " - RESULT: " + result);
			}
		} else {
			throw new IllegalArgumentException("null inputStream");
		}
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
