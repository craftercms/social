package org.craftercms.social.services.impl;


import org.craftercms.social.services.VirusScannerService;
import org.craftercms.virusscanner.api.VirusScanner;
import org.craftercms.virusscanner.impl.ClamavjVirusScannerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class VirusScannerServiceImpl implements VirusScannerService {

    private final transient Logger log = LoggerFactory.getLogger(VirusScannerServiceImpl.class);

    private VirusScanner virusScanner;

    @Override
    public String scan(MultipartFile[] files) {

        String userErrorMessage = null;

        if(files != null){
            for(MultipartFile multipartFile : files) {

                File tempFile = null;
                InputStream inputStream = null;

                try {
                    tempFile = File.createTempFile("tmp",null);
                    multipartFile.transferTo(tempFile);
                    inputStream = new FileInputStream(tempFile);
                    userErrorMessage = this.virusScanner.scan(inputStream);
                } catch (IOException e) {
                    userErrorMessage = ClamavjVirusScannerImpl.SCAN_FAILED_MESSAGE;
                    log.error(e + " - USER MESSAGE: " + userErrorMessage);
                }
                finally {
                    if(inputStream != null){
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            userErrorMessage = ClamavjVirusScannerImpl.SCAN_FAILED_MESSAGE;
                            log.error(e + " - USER MESSAGE: " + userErrorMessage);
                        }
                    }
                    if(tempFile != null){
                        if(!tempFile.delete()){
                            log.error("The temporary file could not be deleted");
                        }
                    }
                }

                if(userErrorMessage != null){
                    break;
                }

            }
        }

        return userErrorMessage;
    }

	@Required
    public void setVirusScanner(VirusScanner virusScanner) {
        this.virusScanner = virusScanner;
    }
}
