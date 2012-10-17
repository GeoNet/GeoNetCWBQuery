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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import nz.org.geonet.quakeml.v1_0_1.client.QuakemlUtils;
import nz.org.geonet.quakeml.v1_0_1.domain.Origin;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;
import nz.org.geonet.quakeml.v1_0_1.domain.Event;
import nz.org.geonet.quakeml.v1_0_1.report.ArrivalPick;

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
     * @param quakeml
     * @return
     */
    static List<NSCL> getPhases(Quakeml quakeml) {
        Origin origin = null;
        Event event = QuakemlUtils.getFirstEvent(quakeml);
        origin = QuakemlUtils.getPreferredOrigin(event);

        List<ArrivalPick> picks = null;
        if (origin != null) {
            try {
                picks = QuakemlUtils.getArrivalPicks(event, origin);
            } catch (Exception ex) {
                logger.warning("unable to read phase picks.");
            }
        } else {
            logger.warning("found no origin information in the QuakeML, will not be able to retrieve data for picks");
        }

        List<NSCL> nscls = new ArrayList<NSCL>();

        if (picks != null && !picks.isEmpty()) {
            logger.fine("Found picks in the quakeml.");

            for (ArrivalPick pick : picks) {

                String phaseName = (pick.getArrival().getPhase().getValue() + "      ").substring(0, 6);

                try {
                    phaseName += (pick.getPick().getEvaluationMode().value().substring(0, 1) + pick.getPick().getEvaluationStatus().value().substring(0, 1));
                } catch (Exception ex) {
                    logger.warning("Found no pick evaluation mode in the quakeml.  Still able to set picks.");
                }

                double arrivalTime = pick.getMillisAfterOrigin() / 1000.0d;


                if (pick.getPick().getWaveformID().getNetworkCode() != null &&
                        pick.getPick().getWaveformID().getStationCode() != null &&
                        pick.getPick().getWaveformID().getChannelCode() != null) {

                    String network = (pick.getPick().getWaveformID().getNetworkCode().trim() + "  ").substring(0, 2);
                    String station = (pick.getPick().getWaveformID().getStationCode().trim() + "     ").substring(0, 5);
                    String channel = (pick.getPick().getWaveformID().getChannelCode().trim() + "   ").substring(0, 3);

                    String location = "..";
                    if (pick.getPick().getWaveformID().getLocationCode() != null) {
                        location = (pick.getPick().getWaveformID().getLocationCode().trim() + "  ").substring(0, 2);
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
