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
import gov.usgs.anss.seed.MiniSeed;
import gov.usgs.anss.util.SeedUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author geoffc
 */
public class Filename {

    private static DateTimeZone tz = DateTimeZone.forID("UTC");
    private static DateTimeFormatter yearFormat = DateTimeFormat.forPattern("YYYY").withZone(tz);
    private static DateTimeFormatter shortYearFormat = DateTimeFormat.forPattern("YY").withZone(tz);
    private static DateTimeFormatter dayOfYearFormat = DateTimeFormat.forPattern("DDD").withZone(tz);
    private static DateTimeFormatter monthFormat = DateTimeFormat.forPattern("MM").withZone(tz);
    private static DateTimeFormatter dayOfMonthFormat = DateTimeFormat.forPattern("dd").withZone(tz);
    private static DateTimeFormatter hourFormat = DateTimeFormat.forPattern("HH").withZone(tz);
    private static DateTimeFormatter minuteFormat = DateTimeFormat.forPattern("mm").withZone(tz);
    private static DateTimeFormatter secondFormat = DateTimeFormat.forPattern("ss").withZone(tz);


    public static String makeFilename(String mask, NSCL nscl, DateTime begin) {

        String name = mask;

		// GeoNet types: .D .T (01-ACE) .L (01-LOG)
		// from http://www.seiscomp3.org/wiki/doc/applications/slarchive
		//type	description
		//'D'   Waveform data
		//'E'   Detection data
		//'L'   Log data
		//'T'   Timing data
		//'C'   Calibration data
		//'R'   Response data
		//'O'   Opaque data
		// e.g. 2011/NZ/WVZ/HHE.D/NZ.WVZ.10.HHE.D.2011.017
		// Note the %z to remove underscores.
		name = name.replaceAll("%SDS", "%z%y/%n/%s/%c.D/%n.%s.%l.%c.D.%y.%j");
        
        name = name.replaceAll("%N", nscl.toString().replaceAll(" ", "_"));
        name = name.replaceAll("%n", nscl.getNetwork().replaceAll(" ", "_"));
        name = name.replaceAll("%s", nscl.getStation().replaceAll(" ", "_"));
        name = name.replaceAll("%c", nscl.getChannel().replaceAll(" ", "_"));
        name = name.replaceAll("%l", nscl.getLocation().replaceAll(" ", "_"));

        name = name.replaceAll("%y", yearFormat.print(begin));
        name = name.replaceAll("%Y", shortYearFormat.print(begin));

        name = name.replaceAll("%j", dayOfYearFormat.print(begin));
        name = name.replaceAll("%J", Integer.toString(SeedUtil.toJulian(begin.toGregorianCalendar())));

        name = name.replaceAll("%M", monthFormat.print(begin));
        name = name.replaceAll("%d", dayOfMonthFormat.print(begin));
        name = name.replaceAll("%D", dayOfMonthFormat.print(begin));
        name = name.replaceAll("%h", hourFormat.print(begin));
        name = name.replaceAll("%m", minuteFormat.print(begin));
        name = name.replaceAll("%S", secondFormat.print(begin));
        if (name.indexOf("%z") >= 0) {
            name = name.replaceAll("%z", "");
            name = name.replaceAll("_", "");
        }
        return name;
    }

       public static String makeFilename(String mask, NSCL nscl, MiniSeed ms) {

        return makeFilename(mask, nscl, new DateTime(ms.getGregorianCalendar().getTimeInMillis(), tz));
    }
}
