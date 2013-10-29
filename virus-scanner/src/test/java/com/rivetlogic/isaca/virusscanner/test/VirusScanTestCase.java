package com.rivetlogic.isaca.virusscanner.test;

import com.philvarner.clamavj.ClamScan;
import com.philvarner.clamavj.ScanResult;
import com.philvarner.clamavj.ScanResult.Status;
import com.rivetlogic.isaca.virusscanner.impl.VirusScannerImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

import static com.philvarner.clamavj.ScanResult.RESPONSE_OK;
import static org.junit.Assert.assertNotNull;

public class VirusScanTestCase {

    @Test
    public void testNoVirusFromFile() throws Exception {
        VirusScannerImpl virusScanner = new VirusScannerImpl();
        String message = virusScanner.scan("src/test/resources/clean.txt");
        assertEquals(null, message);
    }

    @Test
    public void testVirusFromFile() throws Exception {
        VirusScannerImpl virusScanner = new VirusScannerImpl();
        String message = virusScanner.scan("src/test/resources/eicar.txt");
        assertNotNull(message);
    }

    @Test
    public void testNoVirusFromInputStream() throws Exception {
        VirusScannerImpl virusScanner = new VirusScannerImpl();
        File file = new File("src/test/resources/clean.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        String message = virusScanner.scan(fileInputStream);
        assertEquals(null, message);
    }

    @Test
    public void testVirusFromInputStream() throws Exception {
        VirusScannerImpl virusScanner = new VirusScannerImpl();
        File file = new File("src/test/resources/eicar.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        String message = virusScanner.scan(fileInputStream);
        assertNotNull(message);
    }

    @Test
    public void testNoFile() throws Exception {
        VirusScannerImpl virusScanner = new VirusScannerImpl();
        String message = virusScanner.scan("src/test/resources/nofile.txt");
        assertNotNull(message);
    }

    @Test
    public void testFailedConnection() throws Exception {
        VirusScannerImpl virusScanner = new VirusScannerImpl("localhost",8080,60000);
        String message = virusScanner.scan("src/test/resources/eicar.txt");
        assertNotNull(message);
    }

    @Test
    public void testTimeoutFailedConnection() throws Exception {
        VirusScannerImpl virusScanner = new VirusScannerImpl("localhost",3310,0);
        String message = virusScanner.scan("src/test/resources/eicar.txt");
        assertNotNull(message);
    }

}
