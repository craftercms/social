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

package clamavj.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import clamavj.ClamScan;
import clamavj.ScanResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ClamScanTestCase {

    private ClamScan scanner;

    @Before
    public void setUp() {
        scanner = new ClamScan("localhost", 3310, 60000);
    }

    @Test
    public void testSuccess() throws Exception {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append("abcde12345");
        }

        InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
        assertNotNull(is);
        ScanResult result = scanner.scan(is);
        Assert.assertEquals(ScanResult.Status.PASSED, result.getStatus());
        Assert.assertEquals(ScanResult.RESPONSE_OK, result.getResult());
    }

    @Test
    public void testVirus() throws Exception {
        InputStream is = new ByteArrayInputStream(
                "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*".getBytes());
        assertNotNull(is);
        ScanResult result = scanner.scan(is);
        Assert.assertEquals(ScanResult.Status.FAILED, result.getStatus());
        assertEquals("stream: Eicar-Test-Signature FOUND", result.getResult());
        assertEquals("Eicar-Test-Signature", result.getSignature());
    }

    @Test
    public void testVirusAsByteArray() throws Exception {
        byte[] bytes = "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*".getBytes();
        ScanResult result = scanner.scan(bytes);
        Assert.assertEquals(ScanResult.Status.FAILED, result.getStatus());
        assertEquals("stream: Eicar-Test-Signature FOUND", result.getResult());
        assertEquals("Eicar-Test-Signature", result.getSignature());
    }

    @Test
    public void test_virus_from_file() throws Exception {

        int byteCount;
        byte bytes[];
        given: {
            String path = getClass().getResource("/eicar.txt").getPath();
            File f = new File(path);
            //File f = new File("src/test/resources/eicar.txt");
            FileInputStream fis = new FileInputStream(f);
            bytes = new byte[(int) f.length()];
            byteCount = fis.read(bytes);
        }

        ScanResult result;
        when: {
            result = scanner.scan(bytes);
        }

        then: {
            assertTrue(byteCount > 0);
            Assert.assertEquals(ScanResult.Status.FAILED, result.getStatus());
            assertEquals("stream: Eicar-Test-Signature FOUND", result.getResult());
            assertEquals("Eicar-Test-Signature", result.getSignature());

        }
    }

    @Test
    public void testNoArgConstructor() throws Exception {
        scanner = new ClamScan();
        scanner.setHost("localhost");
        scanner.setPort(3310);
        scanner.setTimeout(60000);

        byte[] bytes = "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*".getBytes();
        ScanResult result = scanner.scan(bytes);
        Assert.assertEquals(ScanResult.Status.FAILED, result.getStatus());
        assertEquals("stream: Eicar-Test-Signature FOUND", result.getResult());
        assertEquals("Eicar-Test-Signature", result.getSignature());
    }

    @Test
    public void testMultipleOfChunkSize() throws Exception {
        InputStream is = new ArbitraryInputStream(ClamScan.CHUNK_SIZE);
        assertNotNull(is);
        ScanResult result = scanner.scan(is);
        Assert.assertEquals(ScanResult.Status.PASSED, result.getStatus());
        Assert.assertEquals(ScanResult.RESPONSE_OK, result.getResult());
    }

    @Test
    public void testTooLarge() throws Exception {
        InputStream is = new ArbitraryInputStream();
        assertNotNull(is);
        ScanResult result = scanner.scan(is);
        Assert.assertEquals(result.getResult(), ScanResult.Status.ERROR, result.getStatus());
        assertEquals(ScanResult.RESPONSE_SIZE_EXCEEDED, result.getResult());
    }

    @Test
    public void testStats() throws Exception {
        String result = scanner.stats();
        assertTrue("didn't contain POOLS: \n" + result, result.contains("POOLS:") || result.contains("STATE:"));
    }

    @Test
    public void testPing() throws Exception {
        assertTrue(scanner.ping());
    }

}
