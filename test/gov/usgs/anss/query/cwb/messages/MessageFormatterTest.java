/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.cwb.messages;

import gov.usgs.anss.seed.MiniSeed;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author geoffc
 */
public class MessageFormatterTest {

    public MessageFormatterTest() {
    }
    private static DateTimeZone tz = DateTimeZone.forID("UTC");

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testMiniSeedSummary() throws Exception {
        // java -jar GeoNetCWBQuery.jar -t msz -b "2009/01/01 00:00:00" -s "NZ.....HHZ.." -d 1800
        // 02:07:45.992Z Query on NZAPZ  HHZ10 000423 mini-seed blks 2009 001:00:00:00.0083 2009 001:00:30:00.438  ns=180044

        String filename = "build/test/classes/gov/usgs/anss/query/cwb/messages/NZAPZ__HHZ10.ms";

        File ms = new File(filename);
        long fileSize = ms.length();

        TreeSet<MiniSeed> miniSeed = new TreeSet<MiniSeed>();

        byte[] buf = new byte[512];
        FileInputStream in = new FileInputStream(ms);
        for (long pos = 0; pos < fileSize; pos += 512) {
            if (in.read(buf) == -1) {
                break;
            }
            miniSeed.add(new MiniSeed(buf));
        }

       DateTime now = new DateTime(2010, 2, 5, 2, 7, 45, 992, tz);

        String expResult = "02:07:45.992Z Query on NZAPZ  HHZ10 000423 mini-seed blks 2009 001:00:00:00.008 2009 001:00:29:59.998 ns=180000";
        String result = MessageFormatter.miniSeedSummary(now, miniSeed);
         assertEquals(expResult, result);

		expResult = "02:07:45.992Z No mini-seed blocks returned.";
		result = MessageFormatter.miniSeedSummary(now, new TreeSet<MiniSeed>());
        assertEquals(expResult, result);
    }
}