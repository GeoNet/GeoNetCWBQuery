/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.cwb.messages;

import gov.usgs.anss.seed.MiniSeed;
import java.util.Collection;
import java.util.Iterator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author geoffc
 */
public class MessageFormatter {

    private static DateTimeFormatter nowFormat = DateTimeFormat.forPattern("HH:mm:ss.SSS").withZone(DateTimeZone.forID("UTC"));
    private static DateTimeFormatter msFormat = DateTimeFormat.forPattern("YYYY DDD:HH:mm:ss.SSS").withZone(DateTimeZone.forID("UTC"));

    public static String miniSeedSummary(DateTime now, Collection<MiniSeed> miniSeed) {

        // 02:07:45.992Z Query on NZAPZ  HHZ10 000431 mini-seed blks 2009 001:00:00:00.0083 2009 001:00:30:00.438  ns=180044

        Iterator<MiniSeed> iter = miniSeed.iterator();

		if (!iter.hasNext()) {
			return String.format("%sZ No mini-seed blocks returned.",
					now.toString(nowFormat));
		}


        MiniSeed ms = iter.next();
        int numSamples = ms.getNsamp();

        DateTime begin = new DateTime(ms.getGregorianCalendar().getTimeInMillis(), DateTimeZone.forID("UTC"));

        while (iter.hasNext()) {
            ms = iter.next();
            numSamples += ms.getNsamp();
        }

        DateTime end = new DateTime(ms.getEndTime().getTimeInMillis(), DateTimeZone.forID("UTC"));

        return String.format("%sZ Query on %s %06d mini-seed blks %s %s ns=%d",
                now.toString(nowFormat),
                ms.getSeedName(),
                miniSeed.size(),
                begin.toString(msFormat),
                end.toString(msFormat),
                numSamples);
    }
}
