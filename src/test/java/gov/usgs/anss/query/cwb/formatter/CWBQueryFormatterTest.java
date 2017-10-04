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

import static org.junit.Assert.assertEquals;

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
    public void testMiniSEEDQuery() {
        NSCL nscl = NSCL.stringToNSCL("NZMRZ..HHZ10");
        DateTime begin = new DateTime(2009, 1, 1, 0, 0, 0, 0, tz);
        Double duration = 1800d;
        String result = CWBQueryFormatter.fdsnQueryBody(begin, duration, nscl);
        assertEquals("data query 1", "NZ MRZ* 10 HHZ 2009-01-01T00:00:00.000000 2009-01-01T00:30:00.000000\n", result);
        result = CWBQueryFormatter.fdsnQueryBody(begin, duration, "NZMRZ..HHZ10");
        assertEquals("data query 1", "NZ MRZ* 10 HHZ 2009-01-01T00:00:00.000000 2009-01-01T00:30:00.000000\n", result);
        result = CWBQueryFormatter.fdsnQueryBody(begin.withMillisOfSecond(799), 300d, "NZMRZ..HHZ10");
        assertEquals("data query 1", "NZ MRZ* 10 HHZ 2009-01-01T00:00:79.000000 2009-01-01T00:05:79.000000\n", result);
        result = CWBQueryFormatter.fdsnQueryBody(begin, duration, "NZA");
        assertEquals("data query 1", "NZ A* * * 2009-01-01T00:00:00.000000 2009-01-01T00:30:00.000000\n", result);
        result = CWBQueryFormatter.fdsnQueryBody(begin, duration, "NZAPZ..LH[ZN]..");
        assertEquals("data query 1", "NZ APZ* * LHZ 2009-01-01T00:00:00.000000 2009-01-01T00:30:00.000000\n" +
                "NZ APZ* * LHN 2009-01-01T00:00:00.000000 2009-01-01T00:30:00.000000\n", result);
        result = CWBQueryFormatter.fdsnQueryBody(begin, duration, "NZAPZ..L");
        assertEquals("data query 1", "NZ APZ* * L* 2009-01-01T00:00:00.000000 2009-01-01T00:30:00.000000\n", result);
        result = CWBQueryFormatter.fdsnQueryBody(begin, duration, "NZ.....L[HN]Z.*");
        assertEquals("data query 1", "NZ * * LHZ 2009-01-01T00:00:00.000000 2009-01-01T00:30:00.000000\n" +
                "NZ * * LNZ 2009-01-01T00:00:00.000000 2009-01-01T00:30:00.000000\n", result);
        result = CWBQueryFormatter.fdsnQueryBody(begin, duration, "NZA.*|NZB.*");
        assertEquals("data query 1", "NZ A* * * 2009-01-01T00:00:00.000000 2009-01-01T00:30:00.000000\n" +
                "NZ B* * * 2009-01-01T00:00:00.000000 2009-01-01T00:30:00.000000\n", result);
    }
}