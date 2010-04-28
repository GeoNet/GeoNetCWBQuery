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
