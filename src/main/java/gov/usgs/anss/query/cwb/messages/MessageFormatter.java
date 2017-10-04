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
package gov.usgs.anss.query.cwb.messages;

import gov.usgs.anss.seed.MiniSeed;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Collection;
import java.util.Iterator;

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
