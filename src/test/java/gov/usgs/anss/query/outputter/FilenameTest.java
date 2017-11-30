/*
 * Copyright 2010, Institute of Geological & Nuclear Sciences Ltd or
 * third-party contributors as indicated by the @author tags.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package gov.usgs.anss.query.outputter;

import gov.usgs.anss.query.NSCL;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

        assertEquals("SDS mask", "2009/NZ/WEL/HHZ.D/NZ.WEL.10.HHZ.D.2009.001",
                Filename.makeFilename("%SDS", new NSCL("NZ", "WEL  ", "HHZ", "10"), new DateTime(2009, 1, 1, 11, 11, 11, 0, tz)));

    }
}