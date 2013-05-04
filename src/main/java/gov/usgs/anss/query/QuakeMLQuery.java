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

import nz.org.geonet.simplequakeml.domain.Event;
import nz.org.geonet.simplequakeml.domain.Pick;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author geoffc
 */
public class QuakeMLQuery {

    private static final Logger logger = Logger.getLogger(QuakeMLQuery.class.getName());

    static {
        logger.fine("$Id: QuakeMLQuery.java 1995 2010-02-15 01:10:51Z richardg $");
    }

    /**
     * Extracts the phase picks from a QuakeML object and returns a list of NSCL.
     *
     * @param event
     * @return
     */
    static List<NSCL> getPhases(Event event) {

        List<Pick> picks = event.getPicks();

        List<NSCL> nscls = new ArrayList<NSCL>();

        if (!picks.isEmpty()) {
            logger.fine("Found picks for the event.");

            DateTime originTime = event.getTime();

            for (Pick pick : picks) {

                String phaseName = (pick.getPhase() + "      ").substring(0, 6);

                if (pick.getMode() != null && pick.getStatus() != null) {
                    phaseName += (pick.getMode().substring(0, 1) + pick.getStatus().substring(0, 1));
                } else {
                    logger.warning("Found no pick evaluation mode in the event.  Still able to set picks.");
                }

//                double arrivalTime = pick.getMillisAfterOrigin() / 1000.0d;
                double arrivalTime = (pick.getTime().getMillis() - originTime.getMillis()) / 1000.0d;


                if (pick.getNetwork() != null &&
                        pick.getStation() != null &&
                        pick.getChannel() != null) {

                    String network = (pick.getNetwork().trim() + "  ").substring(0, 2);
                    String station = (pick.getStation().trim() + "     ").substring(0, 5);
                    String channel = (pick.getChannel().trim() + "   ").substring(0, 3);

                    String location = "..";
                    if (pick.getLocation() != null) {
                        location = (pick.getLocation().trim() + "  ").substring(0, 2);
                    }

                    logger.fine(String.format("Pick:%s,%s,%s,%s,%s,%s.", phaseName, arrivalTime, network, station, channel, location));

                    nscls.add(new NSCL(network, station, channel, location));

                } else {
                    logger.warning("Did not find enough information to extract data for pick.");
                }
            }

        }

        return nscls;
    }
}
