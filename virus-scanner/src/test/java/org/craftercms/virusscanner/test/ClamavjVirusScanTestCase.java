package org.craftercms.virusscanner.test;

import org.craftercms.virusscanner.impl.ClamavVirusScannerImpl;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ClamavjVirusScanTestCase {

    @Test
    public void testNoVirusFromFile() throws Exception {
        ClamavVirusScannerImpl virusScanner = new ClamavVirusScannerImpl("localhost", 3310, 60000);
        String path = getClass().getResource("/clean.txt").getPath();
        String message = virusScanner.scan(path);
        assertEquals(null, message);
    }

    @Test
    public void testVirusFromFile() throws Exception {
        ClamavVirusScannerImpl virusScanner = new ClamavVirusScannerImpl("localhost", 3310, 60000);
        String path = getClass().getResource("/eicar.txt").getPath();
        String message = virusScanner.scan(path);
        assertTrue(message.contains(ClamavVirusScannerImpl.THREAT_FOUND_MESSAGE));
    }

    @Test
    public void testNoVirusFromInputStream() throws Exception {
        ClamavVirusScannerImpl virusScanner = new ClamavVirusScannerImpl("localhost", 3310, 60000);
        String path = getClass().getResource("/clean.txt").getPath();
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        String message = virusScanner.scan(fileInputStream);
        assertEquals(null, message);
    }

    @Test
    public void testVirusFromInputStream() throws Exception {
        ClamavVirusScannerImpl virusScanner = new ClamavVirusScannerImpl("localhost", 3310, 60000);
        String path = getClass().getResource("/eicar.txt").getPath();
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        String message = virusScanner.scan(fileInputStream);
        assertTrue(message.contains(ClamavVirusScannerImpl.THREAT_FOUND_MESSAGE));
    }

    @Test
    public void testNoFile() throws Exception {
        ClamavVirusScannerImpl virusScanner = new ClamavVirusScannerImpl("localhost", 3310, 60000);
        String message = virusScanner.scan("nofile.txt");
        assertTrue(message.contains(ClamavVirusScannerImpl.FILE_NOT_FOUND_MESSAGE));
    }

    @Test
    public void testFailedConnection() throws Exception {
        ClamavVirusScannerImpl virusScanner = new ClamavVirusScannerImpl("localhost",8383,60000);
        String path = getClass().getResource("/clean.txt").getPath();
        String message = virusScanner.scan(path);
        assertNotNull(message);
    }


}
