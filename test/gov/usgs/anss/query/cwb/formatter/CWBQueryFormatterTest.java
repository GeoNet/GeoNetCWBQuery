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
package gov.usgs.anss.query.cwb.formatter;

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
public class CWBQueryFormatterTest {

    public CWBQueryFormatterTest() {
    }
    private static DateTimeZone tz = DateTimeZone.forID("UTC");

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testListQuery() {
        DateTime begin = new DateTime(2009, 1, 1, 11, 11, 11, 0, tz);
        Double duration = 1800d;
        String result = CWBQueryFormatter.listChannels(begin, duration);
        assertEquals("list query 1", "'-b' '2009/01/01 11:11:11.000' '-d' '1800.0' '-lsc'\n", result);

    }

    @Test
    public void testMiniSEEDQuery() {
        NSCL nscl = NSCL.stringToNSCL("NZMRZ..HHZ10");
        DateTime begin = new DateTime(2009, 1, 1, 0, 0, 0, 0, tz);
        Double duration = 1800d;
        String result = CWBQueryFormatter.miniSEED(begin, duration, nscl);
        assertEquals("data query 1", "'-b' '2009/01/01 00:00:00.000' '-s' 'NZMRZ..HHZ10' '-d' '1800.0'\t", result);
        result = CWBQueryFormatter.miniSEED(begin, duration, "NZMRZ..HHZ10");
        assertEquals("data query 1", "'-b' '2009/01/01 00:00:00.000' '-s' 'NZMRZ..HHZ10' '-d' '1800.0'\t", result);
        result = CWBQueryFormatter.miniSEED(begin.withMillisOfSecond(799), 300d, "NZMRZ..HHZ10");
        assertEquals("data query 1", "'-b' '2009/01/01 00:00:00.799' '-s' 'NZMRZ..HHZ10' '-d' '300.0'\t", result);
    }
}