/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
