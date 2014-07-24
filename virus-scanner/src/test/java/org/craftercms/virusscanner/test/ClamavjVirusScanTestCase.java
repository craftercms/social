package org.craftercms.virusscanner.test;

import org.craftercms.virusscanner.impl.ClamavVirusScannerImpl;
import org.craftercms.virusscanner.impl.VirusScannerException;
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
        virusScanner.scan(path);
    }

    @Test
    public void testVirusFromFile() throws Exception {
        ClamavVirusScannerImpl virusScanner = new ClamavVirusScannerImpl("localhost", 3310, 60000);
        String path = getClass().getResource("/eicar.txt").getPath();
        virusScanner.scan(path);
    }

    @Test
    public void testNoVirusFromInputStream() throws Exception {
        ClamavVirusScannerImpl virusScanner = new ClamavVirusScannerImpl("localhost", 3310, 60000);
        String path = getClass().getResource("/clean.txt").getPath();
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        virusScanner.scan(fileInputStream);
    }

    @Test(expected = VirusScannerException.class)
    public void testVirusFromInputStream() throws Exception {
        ClamavVirusScannerImpl virusScanner = new ClamavVirusScannerImpl("localhost", 3310, 60000);
        String path = getClass().getResource("/eicar.txt").getPath();
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        virusScanner.scan(fileInputStream);
    }

    @Test(expected = VirusScannerException.class)
    public void testNoFile() throws Exception {
        ClamavVirusScannerImpl virusScanner = new ClamavVirusScannerImpl("localhost", 3310, 60000);
        virusScanner.scan("nofile.txt");
    }

    @Test(expected = VirusScannerException.class)
    public void testFailedConnection() throws Exception {
        ClamavVirusScannerImpl virusScanner = new ClamavVirusScannerImpl("localhost",8383,60000);
        String path = getClass().getResource("/clean.txt").getPath();
        virusScanner.scan(path);
    }

}
