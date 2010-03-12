/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.filefactory;

import edu.sc.seis.TauP.Arrival;
import edu.sc.seis.TauP.SacTimeSeries;
import edu.sc.seis.TauP.SphericalCoords;
import edu.sc.seis.TauP.TauModelException;
import edu.sc.seis.TauP.TauP_Time;
import gov.usgs.anss.query.metadata.ChannelMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.org.geonet.quakeml.v1_0_1.client.QuakemlUtils;
import nz.org.geonet.quakeml.v1_0_1.domain.Event;
import nz.org.geonet.quakeml.v1_0_1.domain.Magnitude;
import nz.org.geonet.quakeml.v1_0_1.domain.Origin;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;
import nz.org.geonet.quakeml.v1_0_1.report.ArrivalPick;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author geoffc
 */
public class SacHeaders {

    private static final Logger logger = Logger.getLogger(SacHeaders.class.getName());

    static {
        logger.fine("$Id: SacHeaders.java 1995 2010-02-15 01:10:51Z richardg $");
    }

    /**
     * Triplicated phases may have mulitple arrivals for the same phase type.  This
     * method removes the later arrivals for phases of the same type in the list.
     * <p>
     * See books like http://books.google.co.nz/books?id=CSCuMPt5CTcC&lpg=PT217&ots=CWmdxYPEJz&dq=seismology%20triplicated%20phases&pg=PA1#v=onepage&q=&f=false
     * for more information.
     *
     * @param phases
     * @return list with later arrivals for phases of the same type in the list removed.
     */
    public static List<SacPhasePick> reduceTriplicatedPhases(List<SacPhasePick> phases) {

        // Go through the phases backwards and use a
        // map to make the phases unique.  This will
        // preserve the first arrival for a phase type
        // which we are assuming is the most important.
        Map<String, Double> map = new HashMap<String, Double>();

        Collections.reverse(phases);

        for (SacPhasePick phase : phases) {
            map.put(phase.getPhaseName(), phase.getTimeAfterOriginInSeconds());
        }

        List<SacPhasePick> reducedPhases = new LinkedList<SacPhasePick>();

        for (String phaseName : map.keySet()) {
            reducedPhases.add(new SacPhasePick(phaseName, map.get(phaseName)));
        }

        Collections.sort(reducedPhases);

        return reducedPhases;
    }

    // QuakeML doens't restrict mag type - these are the
    // values in the GeoNet DB.
    public enum SacMagType {

        MB(52, "IMB (Bodywave Magnitude)"),
        MS(53, "IMS (Surfacewave Magnitude)"),
        ML(54, "IML (Local Magnitude)"),
        MW(55, "IMW (Moment Magnitude)"),
        MD(56, "IMD (Duration Magnitude)"),
        MX(57, "IMX (User Defined Magnitude)");
        private int magNum;
        private String description;

        SacMagType(int magNum, String description) {
            this.magNum = magNum;
            this.description = description;
        }

        public int magNum() {
            return magNum;
        }
    }

    public enum SacEventType {

        EARTHQUAKE(40, "IQUAKE (Earthquake)"),
        EXPLOSION(79, "IEX (Other explosion)"),
        QUARRYBLAST(70, "IQB (Quarry or mine blast confirmed by quarry)"),
        CHEMICALEXPLOSION(43, "ICHEM (Chemical explosion)"),
        NUCLEAREXPLOSION(37, "INUCL (Nuclear event)"),
        LANDSLIDE(82, "IO_ (Other source of known origin)"),
        DEBRISAVALANCHE(82, "IO_ (Other source of known origin)"),
        ROCKSLIDE(82, "IO_ (Other source of known origin)"),
        MINECOLLAPSE(82, "IO_ (Other source of known origin)"),
        VOLCANICERUPTION(82, "IO_ (Other source of known origin)"),
        METEORIMPACT(82, "IO_ (Other source of known origin)"),
        PLANECRASH(82, "IO_ (Other source of known origin)"),
        BUILDINGCOLLAPSE(82, "IO_ (Other source of known origin)"),
        SONICBOOM(82, "IO_ (Other source of known origin)"),
        NOTEXISTING(86, "IU (Undetermined or conflicting information)"),
        NULL(86, "IU (Undetermined or conflicting information)"),
        OTHER(44, "IOTHER (Other)");
        private int eventTypeNum;
        private String description;

        SacEventType(int eventTypeNum, String description) {
            this.eventTypeNum = eventTypeNum;
            this.description = description;
        }

        public int eventTypeNum() {
            return eventTypeNum;
        }
    }

    public static SacTimeSeries setEventHeader(
            SacTimeSeries sac,
            DateTime eventOrigin,
            Double eventLat,
            Double eventLon,
            Double eventDepth,
            Double eventMag,
            int sacMagType,
            int sacEventType) {

        if (eventLat == null) {
            eventLat = -12345.0;
        }

        if (eventLon == null) {
            eventLon = -12345.0;
        }

        if (eventDepth == null) {
            eventDepth = -12345.0;
        }

        if (eventMag == null) {
            eventMag = -12345.0;
        }

        // SAC stores year day (nzjday) but not month and day.  
        DateTime start = new DateTime(sac.nzyear, 1, 1, sac.nzhour, sac.nzmin, sac.nzsec, sac.nzmsec, DateTimeZone.UTC);
        start = start.withDayOfYear(sac.nzjday);

        double timeDiff = (start.getMillis() - eventOrigin.getMillis()) / 1000.0d;

        sac.nzyear = eventOrigin.getYear();
        sac.nzjday = eventOrigin.getDayOfYear();
        sac.nzhour = eventOrigin.getHourOfDay();
        sac.nzmin = eventOrigin.getMinuteOfHour();
        sac.nzsec = eventOrigin.getSecondOfMinute();
        sac.nzmsec = eventOrigin.getMillisOfSecond();

        sac.b = sac.b + timeDiff;
        sac.e = sac.e + timeDiff;

        sac.iztype = SacTimeSeries.IO;

        sac.evla = eventLat;
        sac.evlo = eventLon;
        sac.evdp = eventDepth;
        sac.mag = eventMag;
        sac.imagtyp = sacMagType;
        sac.ievtyp = sacEventType;

        sac.lcalda = 1;

        return sac;
    }

    public static SacTimeSeries setEventHeader(SacTimeSeries sac, Quakeml quakeml) {

        Origin origin = null;
        Double magnitude = null;
        Double latitude = null;
        Double longitude = null;
        Double depth = null;
        int magType = sacMagType("MX");
        int eventType = sacMagType("NULL");
        Magnitude mag = null;

        Event event = QuakemlUtils.getFirstEvent(quakeml);
        origin = QuakemlUtils.getPreferredOrigin(event);

        try {
            mag = QuakemlUtils.getPreferredMagnitude(event);
        } catch (Exception ex) {
            logger.warning("Found no magnitude definition setting to unknown.");
        }
        if (mag != null) {
            magnitude = mag.getMag().getValue();
            magType = sacMagType(mag.getType());
        } else {
            logger.warning("Found no magnitude definition setting to unknown.");
        }

        try {
            latitude = origin.getLatitude().getValue();
        } catch (Exception ex) {
            logger.warning("Found no latitude definition setting to unknown.");
        }

        try {
            longitude = origin.getLongitude().getValue();
        } catch (Exception ex) {
            logger.warning("Found no longitude definition setting to unknown.");
        }

        try {
            depth = origin.getDepth().getValue();
        } catch (Exception ex) {
            logger.warning("Found no depth definition setting to unknown.");
        }

        if (depth != null) {
            depth = depth * 1000.0d;
        }

        try {
            eventType = sacEventType(event.getType().value());
        } catch (Exception ex) {
            logger.warning("Found no event type definition setting to unknown.");
        }

        DateTime eventTime = null;

        if (origin != null) {
            eventTime = QuakemlUtils.getOriginTime(origin);
        }

        if (eventTime != null) {
            sac = SacHeaders.setEventHeader(sac, eventTime, // eventLat, eventLon, eventDepth, eventMag, sacMagType)
                    latitude,
                    longitude,
                    depth, // assume meters.
                    magnitude,
                    magType,
                    eventType);
        } else {
            logger.warning("found no event time definition in the QUakeML.  Not updating header.");
        }

        return sac;
    }

    /**
     * Extracts phase picks from the QuakeML object for the given SAC object and writes them into the SAC header.
     * The following SAC headers must be set: sac.knetwk, sac.kstnm, sac.kcmpnm
     *
     * The evaluation mode and evaluation status is added to the end of the phase
     * name:
     * <p>
     * 'ac' - automatic confirmed.
     * <p>
     * 'ar' - automatic rejected.
     * <p>
     *  'mc' - manual confirmed.
     *
     * @param sac
     * @param quakeml
     * @return
     */
    public static SacTimeSeries setPhasePicks(SacTimeSeries sac, Quakeml quakeml) {
        return SacHeaders.setHeaderPhasePicks(sac, getQuakeMLPhasePicks(sac, quakeml));
    }

    /**
     * Uses TauP (http://www.seis.sc.edu/taup/) to calculate synthetic arrival times
     * for goups of phases on standard velocity models and writes the picks into the sac headers.
     * The triplicated phases are removed before writing to the SAC headers.
     * The following SAC headers must
     * be set for any phases to be calculated: sac.evdp, sac.evla, sac.evlo, sac.stla,
     * sac.stlo
     *<p>
     * If sac.cmpinc is set for a vertical component then P phases will be returned;<p>
     * for extendedPhaseGroups == false: p, P, Pn, Pdiff, PKP, PKiKP, PKIKP<p>
     * for extendedPhaseGroups == true: p, P, Pn, Pdiff, PKP, PKiKP, PKIKP, PcP, pP, pPdiff, pPKP, pPKIKP, pPKiKP, sP, sPdiff, sPKP, sPKIKP, sPKiKP<p>
     *<p>
     * If sac.cmpinc is set for a horizontal component then S phases will be returned.<p>
     * for extendedPhaseGroups == false: s, S, Sn, Sdiff, SKS, SKIKS<p>
     * for extendedPhaseGroups == true: s, S, Sn, Sdiff, SKS, SKIKS, sS, sSdiff, sSKS, sSKIKS, ScS, pS, pSdiff, pSKS, pSKIKS
     *<p>
     * Otherwise, if it is not possible to determine if a component is horizontal or vertical
     * a basic set of P and S phases is returned.
     *<p>
     * For details about the standard velocity models see:
     * http://rses.anu.edu.au/seismology/ak135/ak135f.html http://www.iaspei.org/projects/0903srl_iasp91_Arthur_Snoke.pdf http://books.google.co.nz/books?id=J-TObT4IEiUC&lpg=PA228&ots=PmOxLjcZqi&dq=prem%20seismic%20velocity%20model&pg=PA228#v=onepage&q=prem%20seismic%20velocity%20model&f=false
     *
     * @param sac
     * @param extendedPhaseGroups set true for extended phase groups (additional phases).
     * @param velocityModel either "iasp91" (default), "ak135", or "prem".
     * @return
     */
    public static SacTimeSeries setPhasePicks(SacTimeSeries sac, boolean extendedPhaseGroups, String velocityModel) {
        List<SacPhasePick> picks = getSyntheticPhases(sac, extendedPhaseGroups, velocityModel);

        return (setHeaderPhasePicks(sac, reduceTriplicatedPhases(picks)));
    }

    /**
     * Sets phase picks from QuakeML and calculates and sets synthetic picks.
     * <p>
     * Extracts phase picks from the QuakeML object for the given SAC object and writes them into the SAC header.
     * The following SAC headers must be set: sac.knetwk, sac.kstnm, sac.kcmpnm
     *
     * The evaluation mode and evaluation status is added to the end of the phase
     * name:
     * <p>
     * 'ac' - automatic confirmed.
     * <p>
     * 'ar' - automatic rejected.
     * <p>
     *  'mc' - manual confirmed.
     *
     * <p>
     * Uses TauP (http://www.seis.sc.edu/taup/) to calculate synthetic arrival times
     * for goups of phases on standard velocity models and writes the picks into the sac headers.
     * The triplicated phases are removed before writing to the SAC headers.
     * The following SAC headers must
     * be set for any phases to be calculated: sac.evdp, sac.evla, sac.evlo, sac.stla,
     * sac.stlo
     *<p>
     * If sac.cmpinc is set for a vertical component then P phases will be returned;<p>
     * for extendedPhaseGroups == false: p, P, Pn, Pdiff, PKP, PKiKP, PKIKP<p>
     * for extendedPhaseGroups == true: p, P, Pn, Pdiff, PKP, PKiKP, PKIKP, PcP, pP, pPdiff, pPKP, pPKIKP, pPKiKP, sP, sPdiff, sPKP, sPKIKP, sPKiKP<p>
     *<p>
     * If sac.cmpinc is set for a horizontal component then S phases will be returned.<p>
     * for extendedPhaseGroups == false: s, S, Sn, Sdiff, SKS, SKIKS<p>
     * for extendedPhaseGroups == true: s, S, Sn, Sdiff, SKS, SKIKS, sS, sSdiff, sSKS, sSKIKS, ScS, pS, pSdiff, pSKS, pSKIKS
     *<p>
     * Otherwise, if it is not possible to determine if a component is horizontal or vertical
     * a basic set of P and S phases is returned.
     *<p>
     * For details about the standard velocity models see:
     * http://rses.anu.edu.au/seismology/ak135/ak135f.html http://www.iaspei.org/projects/0903srl_iasp91_Arthur_Snoke.pdf http://books.google.co.nz/books?id=J-TObT4IEiUC&lpg=PA228&ots=PmOxLjcZqi&dq=prem%20seismic%20velocity%20model&pg=PA228#v=onepage&q=prem%20seismic%20velocity%20model&f=false
     *
     */
    public static SacTimeSeries setPhasePicks(SacTimeSeries sac, Quakeml quakeml, boolean extendedPhaseGroups, String velocityModel) {
        List<SacPhasePick> picks = reduceTriplicatedPhases(getSyntheticPhases(sac, extendedPhaseGroups, velocityModel));

        picks.addAll(getQuakeMLPhasePicks(sac, quakeml));

        return (setHeaderPhasePicks(sac, picks));
    }

    /**
     * Extracts phase picks from the QuakeML object for the given SAC object.
     * The following SAC headers must be set: sac.knetwk, sac.kstnm, sac.kcmpnm
     *
     * The evaluation mode and evaluation status is added to the end of the phase
     * name:
     * <p>
     * 'ac' - automatic confirmed.
     * <p>
     * 'ar' - automatic rejected.
     * <p>
     *  'mc' - manual confirmed.
     * <p>
     *  'mr' - manual rejected.
     *
     * @param sac
     * @param quakeml
     * @return
     */
    public static List<SacPhasePick> getQuakeMLPhasePicks(SacTimeSeries sac, Quakeml quakeml) {
        Origin origin = null;
        Event event = QuakemlUtils.getFirstEvent(quakeml);
        origin = QuakemlUtils.getPreferredOrigin(event);

        List<ArrivalPick> picks = null;
        if (origin != null) {
            try {
                picks = QuakemlUtils.getArrivalPicksByStationChannel(event, origin, sac.knetwk.trim(), sac.kstnm.trim(), sac.kcmpnm);
            } catch (Exception ex) {
                logger.warning("unable to read phase picks.");
            }
        } else {
            logger.warning("found no origin information in the QuakeML, will not be able to set picks");
        }

        List<SacPhasePick> phasePicks = new ArrayList<SacPhasePick>();

        for (ArrivalPick pick : picks) {

            String phaseName = pick.getArrival().getPhase().getValue();
            String status = "u";
            String mode = "u";

            try {
                status = (pick.getPick().getEvaluationStatus().value().substring(0, 1));
            } catch (Exception ex) {
                logger.warning("Found no pick evaluation status in the quakeml.  Setting to 'u'.");
            }

            try {
                mode = pick.getPick().getEvaluationMode().value().substring(0, 1);
            } catch (Exception ex) {
                logger.warning("Found no pick evaluation mode in the quakeml.  Setting to 'u'.");
            }

            // If the pick has no weight then mark it rejected.
            // In the GeoNet CUSP case this means the pick
            // hasn't been 'X'ed but it's residual is so high
            // that it's not included in the solution.
            try {
                Double weight = pick.getArrival().getWeight();
                if (weight == 0.0) {
                    status = "r";
                }
            } catch (Exception ex) {
                logger.warning("Found no pick weight in the quakeml.  Status may be incorrect.");

            }

            String phaseString = String.format("%s %s%s", phaseName, mode, status);

            double arrivalTime = pick.getMillisAfterOrigin() / 1000.0d;

            phasePicks.add(new SacPhasePick(phaseString, arrivalTime));
        }
        return phasePicks;
    }

    /**
     * Sets the phase pick fields in the SAC header.  There are ten fields
     * available for picks in the SAC header so the list of SacPhasePick is
     * sorted and the first ten written into the header.
     *<p>
     * For more details on the SAC header see:
     * http://www.iris.edu/manuals/sac/SAC_Manuals/FileFormatPt2.html
     *
     * @param sac
     * @param phasePicks
     * @return
     */
    public static SacTimeSeries setHeaderPhasePicks(SacTimeSeries sac, List<SacPhasePick> phasePicks) {

        Collections.sort(phasePicks);

        Iterator<SacPhasePick> iter = phasePicks.iterator();

        // The SAC header has fields kt[0-9] and t[0-9]
        // There is no way to iterate them - they are all
        //  explictly named so see if we have enough data
        // to set each one.
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt0 = pick.getPhaseName();
            sac.t0 = pick.getTimeAfterOriginInSeconds();
        }
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt1 = pick.getPhaseName();
            sac.t1 = pick.getTimeAfterOriginInSeconds();
        }
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt2 = pick.getPhaseName();
            sac.t2 = pick.getTimeAfterOriginInSeconds();
        }
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt3 = pick.getPhaseName();
            sac.t3 = pick.getTimeAfterOriginInSeconds();
        }
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt4 = pick.getPhaseName();
            sac.t4 = pick.getTimeAfterOriginInSeconds();
        }
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt5 = pick.getPhaseName();
            sac.t5 = pick.getTimeAfterOriginInSeconds();
        }
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt6 = pick.getPhaseName();
            sac.t6 = pick.getTimeAfterOriginInSeconds();
        }
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt7 = pick.getPhaseName();
            sac.t7 = pick.getTimeAfterOriginInSeconds();
        }
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt8 = pick.getPhaseName();
            sac.t8 = pick.getTimeAfterOriginInSeconds();
        }
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt9 = pick.getPhaseName();
            sac.t9 = pick.getTimeAfterOriginInSeconds();
        }

        return sac;
    }

    public static SacTimeSeries setChannelHeader(SacTimeSeries sac, ChannelMetaData md) {
        if (md.getLatitude() != Double.MIN_VALUE) {
            sac.stla = md.getLatitude();
        }
        if (md.getLongitude() != Double.MIN_VALUE) {
            sac.stlo = md.getLongitude();
        }
        if (md.getElevation() != Double.MIN_VALUE) {
            sac.stel = md.getElevation();
        }
        if (md.getDepth() != Double.MIN_VALUE) {
            sac.stdp = md.getDepth();
        }
        if (md.getAzimuth() != Double.MIN_VALUE) {
            sac.cmpaz = md.getAzimuth();
        }
        if (md.getDip() != Double.MIN_VALUE) {
            sac.cmpinc = (md.getDip() + 90.);   // seed is down from horiz, sac is down from vertical
        }

        return sac;
    }

    public static int sacMagType(String magType) {
        // Provide a default - this is the closest to unknown for magType
        int num = SacMagType.valueOf("MX").magNum();

        try {
            num = SacMagType.valueOf(magType).magNum();
        } catch (Exception e) {
        }

        return num;
    }

    public static int sacEventType(String eventType) {
        int num = SacEventType.valueOf("NULL").eventTypeNum();

        try {
            num = SacEventType.valueOf(eventType.replaceAll("\\s+", "").toUpperCase()).eventTypeNum();
        } catch (Exception e) {
        }

        return num;
    }

    /**
     * Returns TauP phase classes based on the sac.cmpinc - P phase
     * groups for verticals and S phase groups for horizontals.
     * If the component isn't vertical or horizontal a basic group of
     * P and S picks is returned.
     *
     * @param sac
     * @param extendedPhaseGroups set true for extended phase groups (additional phases).
     * @return string suitable for passing to TauP_Time.getPhaseNames().
     */
    public static String componentOrientationToPhaseGroup(SacTimeSeries sac, boolean extendedPhaseGroups) {
        String phaseGroup = "ttbasic";

        // Vertical
        if (sac.cmpinc == 0.0d) {
            if (extendedPhaseGroups) {
                phaseGroup = "ttp+";
            } else {
                phaseGroup = "ttp";
            }
        }

        // Horizontal
        if (sac.cmpinc == 90.0d) {
            if (extendedPhaseGroups) {
                phaseGroup = "tts+";
            } else {
                phaseGroup = "tts";
            }
        }

        return phaseGroup;
    }

    /**
     * Uses TauP (http://www.seis.sc.edu/taup/) to calculate synthetic arrival times
     * for goups of phases on standard velocity models.  The following SAC headers must
     * be set for any phases to be calculated: sac.evdp, sac.evla, sac.evlo, sac.stla,
     * sac.stlo
     *<p>
     * If sac.cmpinc is set for a vertical component then P phases will be returned;<p>
     * for extendedPhaseGroups == false: p, P, Pn, Pdiff, PKP, PKiKP, PKIKP<p>
     * for extendedPhaseGroups == true: p, P, Pn, Pdiff, PKP, PKiKP, PKIKP, PcP, pP, pPdiff, pPKP, pPKIKP, pPKiKP, sP, sPdiff, sPKP, sPKIKP, sPKiKP<p>
     *<p>
     * If sac.cmpinc is set for a horizontal component then S phases will be returned.<p>
     * for extendedPhaseGroups == false: s, S, Sn, Sdiff, SKS, SKIKS<p>
     * for extendedPhaseGroups == true: s, S, Sn, Sdiff, SKS, SKIKS, sS, sSdiff, sSKS, sSKIKS, ScS, pS, pSdiff, pSKS, pSKIKS
     *<p>
     * Otherwise, if it is not possible to determine if a component is horizontal or vertical
     * a basic set of P and S phases is returned.
     *<p>
     * For details about the standard velocity models see:
     * http://rses.anu.edu.au/seismology/ak135/ak135f.html http://www.iaspei.org/projects/0903srl_iasp91_Arthur_Snoke.pdf http://books.google.co.nz/books?id=J-TObT4IEiUC&lpg=PA228&ots=PmOxLjcZqi&dq=prem%20seismic%20velocity%20model&pg=PA228#v=onepage&q=prem%20seismic%20velocity%20model&f=false
     *
     * @param sac
     * @param extendedPhaseGroups set true for extended phase groups (additional phases).
     * @param velocityModel either "iasp91" (default), "ak135", or "prem".
     * @return
     */
    public static List<SacPhasePick> getSyntheticPhases(SacTimeSeries sac, boolean extendedPhaseGroups, String velocityModel) {

        List<SacPhasePick> sacPhasePicks = new ArrayList<SacPhasePick>();

        String model = velocityModel == null ? "iasp91" : velocityModel;

        // Checks that we have all the values we need set in the header.
        boolean requiredValues = true;

        if (sac.evdp == -12345.0d) {
            logger.warning("Event depth not set, will not be able to calculate phases.");
            requiredValues = false;
        }
        if (sac.stla == -12345.0d) {
            logger.warning("Station latitude not set, will not be able to calculate phases.");
            requiredValues = false;
        }
        if (sac.stlo == -12345.0d) {
            logger.warning("Station longitude not set, will not be able to calculate phases.");
            requiredValues = false;
        }
        if (sac.evla == -12345.0d) {
            logger.warning("Event latitude not set, will not be able to calculate phases.");
            requiredValues = false;
        }
        if (sac.evlo == -12345.0d) {
            logger.warning("Event longitude not set, will not be able to calculate phases.");
            requiredValues = false;
        }

        if (requiredValues) {
            double deg = SphericalCoords.distance(sac.stla, sac.stlo, sac.evla, sac.evlo);

            TauP_Time taup = null;

            try {
                taup = new TauP_Time(model);
            } catch (Exception ex) {
                logger.warning("Problem loading velocity model, will not be able to calculate phases.");
            }

            if (taup != null) {

                String phaseGroup = SacHeaders.componentOrientationToPhaseGroup(sac, extendedPhaseGroups);

                if (phaseGroup.equals("ttbasic")) {
                    logger.warning("Problem determining if component is horizontal or vertical will use a basic phase group with P and S phases.");
                }

                // TODO
                // Get some standard lists of phases from Taup.  There
                // doesn't seem to be a clean way in Taup to add them as
                // the phases of interest without iterating the list.
                // Did I miss something?
                List phaseNames = TauP_Time.getPhaseNames(phaseGroup);
                Iterator phaseIter = phaseNames.iterator();
                while (phaseIter.hasNext()) {
                    taup.appendPhaseName((String) phaseIter.next());
                }

                try {
                    taup.depthCorrect(sac.evdp / 1000.0d);  // SAC header is in m and _looks_ like this requires km.
                } catch (TauModelException ex) {
                    Logger.getLogger(SacHeaders.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    taup.calculate(deg);
                } catch (TauModelException ex) {
                    Logger.getLogger(SacHeaders.class.getName()).log(Level.SEVERE, null, ex);
                }

                Arrival[] arrivals = taup.getArrivals();

                for (int i = 0; i < arrivals.length; i++) {
                    sacPhasePicks.add(new SacPhasePick(arrivals[i].getName() + " " + model, arrivals[i].getTime()));
                }
            }
        }

        return sacPhasePicks;
    }
}
