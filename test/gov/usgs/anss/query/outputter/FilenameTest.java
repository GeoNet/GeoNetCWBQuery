/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.outputter;

import gov.usgs.anss.query.NSCL;
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
public class FilenameTest {

    private static DateTimeZone tz = DateTimeZone.forID("UTC");

    public FilenameTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testMakeFilename() {

        assertEquals("%y", "2009",
                Filename.makeFilename("%y", new NSCL("NZ", "WEL  ", "HHZ", "10"), new DateTime(2009, 1, 2, 1, 12, 13, 0, tz)));

        assertEquals("%Y", "09",
                Filename.makeFilename("%Y", new NSCL("NZ", "WEL  ", "HHZ", "10"), new DateTime(2009, 1, 2, 1, 12, 13, 0, tz)));

        assertEquals("%j", "002",
                Filename.makeFilename("%j", new NSCL("NZ", "WEL  ", "HHZ", "10"), new DateTime(2009, 1, 2, 1, 12, 13, 0, tz)));

        assertEquals("%J", "2454834",
                Filename.makeFilename("%J", new NSCL("NZ", "WEL  ", "HHZ", "10"), new DateTime(2009, 1, 2, 1, 12, 13, 0, tz)));

        assertEquals("%M", "01",
                Filename.makeFilename("%M", new NSCL("NZ", "WEL  ", "HHZ", "10"), new DateTime(2009, 1, 2, 1, 12, 13, 0, tz)));

        assertEquals("%d", "02",
                Filename.makeFilename("%d", new NSCL("NZ", "WEL  ", "HHZ", "10"), new DateTime(2009, 1, 2, 1, 12, 13, 0, tz)));

        assertEquals("%D", "02",
                Filename.makeFilename("%D", new NSCL("NZ", "WEL  ", "HHZ", "10"), new DateTime(2009, 1, 2, 1, 12, 13, 0, tz)));

        assertEquals("%h", "01",
                Filename.makeFilename("%h", new NSCL("NZ", "WEL  ", "HHZ", "10"), new DateTime(2009, 1, 2, 1, 12, 13, 0, tz)));

        assertEquals("%m", "12",
                Filename.makeFilename("%m", new NSCL("NZ", "WEL  ", "HHZ", "10"), new DateTime(2009, 1, 2, 1, 12, 13, 0, tz)));

        assertEquals("%S", "13",
                Filename.makeFilename("%S", new NSCL("NZ", "WEL  ", "HHZ", "10"), new DateTime(2009, 1, 2, 1, 12, 13, 0, tz)));

        assertEquals("sac mask", "200901011111.WEL.HHZ.10.NZ.sac",
                Filename.makeFilename("%z%y%M%D%h%m.%s.%c.%l.%n.sac", new NSCL("NZ", "WEL  ", "HHZ", "10"), new DateTime(2009, 1, 1, 11, 11, 11, 0, tz)));

    }
}