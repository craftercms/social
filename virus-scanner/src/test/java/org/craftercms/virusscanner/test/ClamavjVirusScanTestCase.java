package org.craftercms.virusscanner.test;

import org.craftercms.virusscanner.impl.ClamavjVirusScannerImpl;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ClamavjVirusScanTestCase {

    @Test
    public void testNoVirusFromFile() throws Exception {
        ClamavjVirusScannerImpl virusScanner = new ClamavjVirusScannerImpl("localhost", 3310, 60000);
        String path = getClass().getResource("/clean.txt").getPath();
        String message = virusScanner.scan(path);
        assertEquals(null, message);
    }

    @Test
    public void testVirusFromFile() throws Exception {
        ClamavjVirusScannerImpl virusScanner = new ClamavjVirusScannerImpl("localhost", 3310, 60000);
        String path = getClass().getResource("/eicar.txt").getPath();
        String message = virusScanner.scan(path);
        assertNotNull(message);
    }

    @Test
    public void testNoVirusFromInputStream() throws Exception {
        ClamavjVirusScannerImpl virusScanner = new ClamavjVirusScannerImpl("localhost", 3310, 60000);
        String path = getClass().getResource("/clean.txt").getPath();
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        String message = virusScanner.scan(fileInputStream);
        assertEquals(null, message);
    }

    @Test
    public void testVirusFromInputStream() throws Exception {
        ClamavjVirusScannerImpl virusScanner = new ClamavjVirusScannerImpl("localhost", 3310, 60000);
        String path = getClass().getResource("/eicar.txt").getPath();
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        String message = virusScanner.scan(fileInputStream);
        assertNotNull(message);
    }

    @Test
    public void testNoFile() throws Exception {
        ClamavjVirusScannerImpl virusScanner = new ClamavjVirusScannerImpl("localhost", 3310, 60000);
        String message = virusScanner.scan("nofile.txt");
        assertNotNull(message);
    }

    @Test
    public void testFailedConnection() throws Exception {
        ClamavjVirusScannerImpl virusScanner = new ClamavjVirusScannerImpl("localhost",8080,60000);
        String path = getClass().getResource("/eicar.txt").getPath();
        String message = virusScanner.scan(path);
        assertNotNull(message);
    }

    @Test
    public void testTimeoutFailedConnection() throws Exception {
        ClamavjVirusScannerImpl virusScanner = new ClamavjVirusScannerImpl("localhost",3310,0);
        String path = getClass().getResource("/eicar.txt").getPath();
        String message = virusScanner.scan(path);
        assertNotNull(message);
    }

}
