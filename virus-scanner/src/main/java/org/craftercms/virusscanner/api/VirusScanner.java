package org.craftercms.virusscanner.api;

import java.io.InputStream;

public interface VirusScanner {

    void scan(String filename);
    void scan(InputStream inputStream);

}
