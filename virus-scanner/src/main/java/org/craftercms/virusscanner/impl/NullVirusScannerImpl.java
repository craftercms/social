package org.craftercms.virusscanner.impl;


import java.io.InputStream;

import org.craftercms.virusscanner.api.VirusScanner;

/**
 *  Dummy virus scanner that does nothing
 */
public class NullVirusScannerImpl implements VirusScanner {

    @Override
    public void scan(final String filename) {

    }

    @Override
    public void scan(final InputStream inputStream) {
    }
}
