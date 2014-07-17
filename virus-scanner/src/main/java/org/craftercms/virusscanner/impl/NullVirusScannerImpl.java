package org.craftercms.virusscanner.impl;


import org.craftercms.virusscanner.api.VirusScanner;

import java.io.InputStream;

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
