/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.virusscanner.test;

import java.io.File;
import java.io.FileInputStream;

import org.craftercms.virusscanner.impl.ClamavVirusScannerImpl;
import org.craftercms.virusscanner.impl.VirusScannerException;
import org.junit.Test;

public class ClamavjVirusScanTestCase {

    @Test
    public void testNoVirusFromFile() throws Exception {
        ClamavVirusScannerImpl virusScanner = new ClamavVirusScannerImpl("localhost", 3310, 60000);
        String path = getClass().getResource("/clean.txt").getPath();
        virusScanner.scan(path);
    }

//    @Test
//    public void testVirusFromFile() throws Exception {
//        ClamavVirusScannerImpl virusScanner = new ClamavVirusScannerImpl("localhost", 3310, 60000);
//        String path = getClass().getResource("/eicar.txt").getPath();
//        virusScanner.scan(path);
//    }

    @Test
    public void testNoVirusFromInputStream() throws Exception {
        ClamavVirusScannerImpl virusScanner = new ClamavVirusScannerImpl("localhost", 3310, 60000);
        String path = getClass().getResource("/clean.txt").getPath();
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        virusScanner.scan(fileInputStream);
    }

//    @Test(expected = VirusScannerException.class)
//    public void testVirusFromInputStream() throws Exception {
//        ClamavVirusScannerImpl virusScanner = new ClamavVirusScannerImpl("localhost", 3310, 60000);
//        String path = getClass().getResource("/eicar.txt").getPath();
//        File file = new File(path);
//        FileInputStream fileInputStream = new FileInputStream(file);
//        virusScanner.scan(fileInputStream);
//    }

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
