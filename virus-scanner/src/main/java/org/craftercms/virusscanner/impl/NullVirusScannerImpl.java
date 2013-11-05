package org.craftercms.virusscanner.impl;


import org.craftercms.virusscanner.api.VirusScanner;

import java.io.InputStream;

/**
 *  Dummy virus scanner that does nothing
 */
public class NullVirusScannerImpl implements VirusScanner {

    /**
     *
     * @param filename
     * @return null
     */
	public String scan(String filename) {
		return null;
	}

    /**
     *
     * @param inputStream
     * @return null
     */
	public String scan(InputStream inputStream) {
		return null;
	}
}
