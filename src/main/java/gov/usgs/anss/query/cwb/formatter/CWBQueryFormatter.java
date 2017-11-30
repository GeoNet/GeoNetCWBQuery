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

import gov.usgs.anss.query.EdgeQueryOptions;
import gov.usgs.anss.query.NSCL;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides methods that return strings that are suitably formatted for querying
 * a CWB server.
 *
 * @author geoffc
 * @updated Howard Wu 29/09/2017
 */
public class CWBQueryFormatter {

    private static String fdsnBeginFormat = "YYYY-MM-dd'T'HH:mm:ss.SSSSSS";
    private static DateTimeFormatter parseFDSNFormat = DateTimeFormat.forPattern(fdsnBeginFormat).withZone(DateTimeZone.forID("UTC"));

    public static String fdsnQueryBody(DateTime begin, Double duration, NSCL nsclSelect) {
        return fdsnQueryBody(begin, duration, nsclSelect.toString());
    }

    public static String fdsnQueryBody(DateTime begin, Double duration, String nsclSelectString) {
        StringBuilder streamBody= new StringBuilder("");

        String startTime = parseFDSNFormat.withZone(DateTimeZone.UTC).print(begin);
        String endTime;
        if (duration >= 0.0f) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(begin.toDate());
            calendar.add(Calendar.SECOND, duration.intValue());
            endTime = parseFDSNFormat.withZone(DateTimeZone.UTC).print(new DateTime(calendar.getTime()));
        } else {
            endTime = "*";
        }

        // Here we convert CWB format seednames into FDSN post content

        // Split stream names by '|' : example: NZKARZ EHZ10|NZOMRZ EHZ10|NZLIRZ EHZ10
        String []seednames = nsclSelectString.split("\\|");

        // For each stream name we have the format:
        // 1. Basic format NNSSSSSCCCLL
        // More complicated stream names example:
        // 2. NZAPZ..LHN00  produces "NZ APZ* 00 LHN"
        // 3. NZA produces "NZ A* * *"
        // 4. NZAPZ..L produces "NZ APZ* * L*"
        // 5. NZAPZ..LH[ZN].. produces "NZ APZ* * LHZ" and "NZ APZ* * LHN" (2 lines) ( see NOTE below)
        // 6. NZ…..L[HN]Z.* produces "NZ * * LHZ" and "NZ *, LNZ, *  ( 2 lines)

        // NOTE: For FDSN, the proper result for "NZAPZ..LH[ZN].." should be
        // "NZ APZ* * LHZ,LHN" but it would be quite complicate to produce .
        // Pre-scan stream name to make [xyz] into 3 different line, so each line will follow NNSSSSSCCCLL
        List<String> fdsnStreams = new ArrayList<String>();
        Pattern reg = Pattern.compile("(.*)(\\[.+\\])(.*)");

        for(String stream:seednames) {
            Matcher matcher = reg.matcher(stream);
            if(!matcher.matches()) {
                // No bracket in stream name, happy path
                fdsnStreams.add(stream);
            } else {
                // [xyz] (contains bracket)
                String s = matcher.group(2);
                for(int i=1; i<s.length()-1; i++) {
                    fdsnStreams.add(String.format("%s%s%s", matcher.group(1), s.charAt(i), matcher.group(3)));
                }
            }
        }

        // Now every stream name in fdsnStreams have the length of 12
        for(String stream:fdsnStreams) {
            if (stream.length()==0)
                continue;

            String network = "*";
            String station = "*";
            String channel = "*";
            String location = "*";

            while(true) {
                network = genWildcard(stream, 2);
                if (stream.length() < 2) {
                    break;
                }
                stream = stream.substring(2);
                station = genWildcard(stream, 5);

                if (stream.length() < 5) {
                    break;
                }
                stream = stream.substring(5);
                channel = genWildcard(stream, 3);

                if(stream.length()<3) {
                    break;
                }
                stream = stream.substring(3);
                location = genWildcard(stream, 2);
                break;
            }
            String line = String.format("%s %s %s %s %s %s\n",
                    network, station, location, channel, startTime, endTime);
            streamBody.append(line);
        }

        return streamBody.toString();
    }

    private static String genWildcard(String input, int maxLen) {
        if(input.length()>maxLen) {
            input = input.substring(0, maxLen);
        }
        input = input
                .replaceAll("\\.|\\*", "");

        if(input.length() < maxLen) {
            return input + "*";
        }
        return input;
    }
}
