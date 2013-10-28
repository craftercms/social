package org.craftercms.social.services;


import org.springframework.web.multipart.MultipartFile;

public interface VirusScannerService {

    public String scan(MultipartFile[] files);

}
