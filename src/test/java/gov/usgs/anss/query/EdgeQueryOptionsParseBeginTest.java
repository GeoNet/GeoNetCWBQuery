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
package gov.usgs.anss.query;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author geoffc
 */
@RunWith(Parameterized.class)
public class EdgeQueryOptionsParseBeginTest {

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
                    // beginTime         ms        Date
                    {"1970/12/31 23:59:12", 0, new DateTime(1970, 12, 31, 23, 59, 12, 0, tz)},
                    {"2009/12/31 23:59:12", 0, new DateTime(2009, 12, 31, 23, 59, 12, 0, tz)},
                    {"1970/12/31 03:59:12", 0, new DateTime(1970, 12, 31, 03, 59, 12, 0, tz)},
                    {"2009/12/31 03:59:12", 0, new DateTime(2009, 12, 31, 03, 59, 12, 0, tz)},
                    {"2009,022-23:59:12", 0, new DateTime(2009, 01, 22, 23, 59, 12, 0, tz)},
                    {"2009,022-03:59:12", 0, new DateTime(2009, 01, 22, 03, 59, 12, 0, tz)},
                    {"2009,095-23:59:12", 0, new DateTime(2009, 04, 05, 23, 59, 12, 0, tz)},
                    {"2008,095-23:59:12", 0, new DateTime(2008, 04, 04, 23, 59, 12, 0, tz)},

                    {"2009/12/31 23:59:12", 48.0, new DateTime(2010, 01, 01, 0, 0, 0, 0, tz)},
                    {"1970/12/31 03:59:12", 1.0, new DateTime(1970, 12, 31, 03, 59, 13, 0, tz)},
                    {"2009/12/31 03:59:12", -4.0, new DateTime(2009, 12, 31, 03, 59, 8, 0, tz)},

                    {"2009/12/31 23:59:12", 48.555, new DateTime(2010, 01, 01, 0, 0, 0, 555, tz)},
		});
    }
    private String beginTime;
	private double offset;
	private DateTime begin;

    private static DateTimeZone tz = DateTimeZone.forID("UTC");

	private EdgeQueryOptions options = new EdgeQueryOptions();

    public EdgeQueryOptionsParseBeginTest(String beginTime, double offset, DateTime begin) {
        this.beginTime = beginTime;
		this.offset = offset;
        this.begin = begin;
    }

    @Test
    public void testParseBegin() {
		options.setBegin(beginTime);
		options.setOffset(offset);

        assertEquals(begin, options.getBeginWithOffset());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseBeginError() {
        options.setBegin(beginTime + "junk");
    }
}