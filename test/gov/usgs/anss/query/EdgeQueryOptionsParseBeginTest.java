/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

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