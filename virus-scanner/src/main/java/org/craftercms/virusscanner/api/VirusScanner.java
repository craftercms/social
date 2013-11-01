package org.craftercms.virusscanner.api;

import java.io.InputStream;

public interface VirusScanner {

    String scan(String filename);
    String scan(InputStream inputStream);

}
