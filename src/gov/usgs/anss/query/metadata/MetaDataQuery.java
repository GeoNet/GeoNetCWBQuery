/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.anss.query.metadata;

import gov.usgs.anss.query.NSCL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.logging.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author geoffc
 */
public class MetaDataQuery {

    private MetaDataServer mds;
    private static final String pzunit = "nm";
    protected static final Logger logger = Logger.getLogger(MetaDataQuery.class.getName());

    public MetaDataQuery(MetaDataServer mds) {
        this.mds = mds;
    }

    /**
     *
     * @param network
     * @param code
     * @param component
     * @param location
     * @param date
     * @return
     */
    public ChannelMetaData getChannelMetaData(NSCL nscl,  DateTime date) {
 
        String s = mds.getResponseData(nscl, date, pzunit);

        ChannelMetaData md = new ChannelMetaData(nscl);

        try {
            BufferedReader in = new BufferedReader(new StringReader(s));
            String line = "";
            while ((line = in.readLine()) != null) {
                if (line.indexOf("LAT-SEED") > 0) {
                    md.setLatitude(Double.parseDouble(line.substring(15)));
                } else if (line.indexOf("LONG-SEED") > 0) {
                    md.setLongitude(Double.parseDouble(line.substring(15)));
                } else if (line.indexOf("ELEV-SEED") > 0) {
                    md.setElevation(Double.parseDouble(line.substring(15)));
                } else if (line.indexOf("AZIMUTH") > 0) {
                    md.setAzimuth(Double.parseDouble(line.substring(15)));
                } else if (line.indexOf("DIP") > 0) {
                    md.setDip(Double.parseDouble(line.substring(15)));
                } else if (line.indexOf("DEPTH") > 0) {
                    md.setDepth(Double.parseDouble(line.substring(15)));
                }
            }

        } catch (IOException e) {
            logger.severe("Error parsing metadata " + e.getMessage());
        }

        if (md.getLatitude() == Double.MIN_VALUE && md.getLongitude() == Double.MIN_VALUE) {
            logger.warning("      ***** " + nscl.toString() +
                    " Did not get station location.  Server is down or missing meta data?");
        }

        if (md.getAzimuth() == Double.MIN_VALUE && md.getDip() == Double.MIN_VALUE) {
            logger.warning("      ***** " + nscl.toString() +
                    " Did not get component orientation.  Server is down or missing meta data?");
        }

        return md;
    }

       /**
     *
     * @param network
     * @param code
     * @param component
     * @param location
     * @param date
     * @param units
     * @param filename
     */
    public void getSACResponse(
            NSCL nscl,
            DateTime date,
            String units,
            String filename) {
        String s = mds.getResponseData(nscl, date, pzunit);

        try {
            PrintWriter fout = new PrintWriter(filename);
            fout.write(s);
            fout.close();
        } catch (IOException e) {
            logger.severe("OUtput error writing sac response file " + filename + ".resp e=" + e.getMessage());
        }
    }

}
