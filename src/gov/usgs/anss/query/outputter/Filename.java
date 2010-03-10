/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
