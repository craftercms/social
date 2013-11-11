package org.craftercms.testing.unit;

import org.craftercms.security.api.RequestContext;
import org.craftercms.social.services.VirusScannerService;
import org.craftercms.social.services.impl.VirusScannerServiceImpl;
import org.craftercms.virusscanner.api.VirusScanner;
import org.craftercms.virusscanner.impl.ClamavVirusScannerImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Clamav clamd daemon configuration to run this test:
 * TCPAddr localhost
 * TCPSocket 3310
 */
@RunWith(PowerMockRunner.class)
public class VirusScannerServiceTest {

    static final String CLEAN_TXT_PATH = VirusScannerServiceTest.class.getResource("/virusscanner/clean.txt").getPath();
    static final String CLEAN_PDF_PATH = VirusScannerServiceTest.class.getResource("/virusscanner/warranty.pdf").getPath();
    static final String VIRUS_TXT_PATH = VirusScannerServiceTest.class.getResource("/virusscanner/eicar.txt").getPath();

    private File cleanTxtFile;
    private File cleanPdfFile;
    private File virusTxtFile;

    private VirusScannerServiceImpl virusScannerService;

    public VirusScannerServiceTest(){
        this.virusScannerService = new VirusScannerServiceImpl();
        this.virusScannerService.setVirusScanner(new ClamavVirusScannerImpl("localhost",3310,60000));
    }

    @Before
    public void startup() {
        cleanTxtFile = new File(CLEAN_TXT_PATH);
        cleanPdfFile = new File(CLEAN_PDF_PATH);
        virusTxtFile = new File(VIRUS_TXT_PATH);
    }

    @Test
    public void testScanCleanTxtFile(){
        this.virusScannerService.scan(this.cleanTxtFile, this.cleanTxtFile.getName());
    }

    @Test
    public void testScanCleanPdfFile(){
        this.virusScannerService.scan(this.cleanPdfFile, this.cleanPdfFile.getName());
    }

    @Test
    public void testScanVirusFile(){
        this.virusScannerService.scan(this.virusTxtFile, this.virusTxtFile.getName());
    }


}
