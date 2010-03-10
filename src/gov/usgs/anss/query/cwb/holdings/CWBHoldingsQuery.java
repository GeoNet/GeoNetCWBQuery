/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.cwb.holdings;

import gov.usgs.anss.query.cwb.*;
import gov.usgs.anss.query.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author geoffc
 */
public class CWBHoldingsQuery {

    private CWBHoldingsServer cwb;
    protected static final Logger logger = Logger.getLogger(CWBHoldingsQuery.class.getName());

    /**
     * Allows queries to made against a CWB server.
     *
     * @param cwb
     */
    public CWBHoldingsQuery(CWBHoldingsServer cwb) {
        this.cwb = cwb;
    }

    /**
     * List data channels that are available at the give date time and duration.
     *
     * @param begin
     * @param duration
     * @return
     */
    public ArrayList<NSCL> listChannels(DateTime begin, Double duration) {

            ArrayList<NSCL> nscl = new ArrayList();

            String channels = cwb.listChannels(begin, duration);

        BufferedReader sr = new BufferedReader(new StringReader(channels));

        String nextLine = null;
        try {
            while ((nextLine = sr.readLine()) != null) {

                if (lscToNscl(nextLine) != null) {
                    nscl.add((lscToNscl(nextLine)));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CWBHoldingsQuery.class.getName()).log(Level.SEVERE, null, ex);
        }

        return nscl;
    }


    /**
     * Extracts a NSCL from a channel listing string.
     * 
     * @param lsc
     * @return
     */
    protected static NSCL lscToNscl(String lsc) {
       NSCL r;

        if (lsc.startsWith("There are") || lsc.isEmpty()) {
            r = null;
        } else {
            r = NSCL.stringToNSCL(lsc.substring(0, 12));
        }

        return r;
    }
}
