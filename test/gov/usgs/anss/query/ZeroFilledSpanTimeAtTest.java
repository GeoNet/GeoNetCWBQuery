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

import gov.usgs.anss.seed.MiniSeed;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
public class ZeroFilledSpanTimeAtTest {

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
                    //Calendar,                    MILLISECOND, rate, iTime, expected
                    {new GregorianCalendar(2007, 1, 1, 1, 1, 1), 0, 100d, 0, "01:01:01.000"},
                    {new GregorianCalendar(2007, 1, 1, 1, 1, 1), 0, 0d, 0, "01:01:01.000"},
                    {new GregorianCalendar(2007, 1, 1, 1, 1, 1), 1, 100d, 0, "01:01:01.001"},
                    {new GregorianCalendar(2007, 1, 1, 1, 1, 1), 50, 50d, 0, "01:01:01.050"},
                    {new GregorianCalendar(2007, 1, 1, 1, 1, 1), 100, 50d, 0, "01:01:01.100"},
                    {new GregorianCalendar(2007, 1, 1, 1, 1, 1), 101, 50d, 0, "01:01:01.101"},
                    {new GregorianCalendar(2007, 1, 1, 1, 1, 1), 1000, 100d, 0, "01:01:02.000"},
                    {new GregorianCalendar(2007, 1, 1, 1, 1, 1), 500, 100d, 0, "01:01:01.500"},
                    {new GregorianCalendar(2007, 1, 1, 1, 1, 1), 501, 100d, 0, "01:01:01.501"},
                    {new GregorianCalendar(2007, 1, 1, 1, 1, 1), 499, 100d, 0, "01:01:01.499"},});
    }
    private GregorianCalendar cal;
    private int ms;
    private double rate;
    private int iTime;
    private String expected;

    private TimeZone tz = TimeZone.getTimeZone("GMT+0");
     
    public ZeroFilledSpanTimeAtTest(GregorianCalendar cal, int ms, double rate, int iTime, String expected) {
        this.cal = cal;
        this.ms = ms;
        this.rate = rate;
        this.iTime = iTime;
        this.expected = expected;
    }

    @Test
    public void testTimeAt() {
        cal.set(GregorianCalendar.MILLISECOND, ms);

        cal.setTimeZone(tz);
        ZeroFilledSpan instance = new ZeroFilledSpan(new ArrayList<MiniSeed>(), cal, 100.0d, 10);
        instance.setRate(rate);
        String result = instance.timeAt(iTime);
        System.out.println(result);
        assertEquals(expected, result);
    }
}