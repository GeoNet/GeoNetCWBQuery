/*
 * HoldingOutputer.java
 *
 * Created on December 19, 2006, 9:00 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import gov.usgs.anss.seed.MiniSeed;
import java.util.ArrayList;
import java.util.Collections;
import java.net.*;
import java.io.IOException;
import gov.usgs.anss.edge.*;

/**
 * This outputer just sends the blocks to the TCP/IP based holdings server.  The user
 * can override the gacqdb/7996:CW if necessary.
 *
 * @author davidketchum
 */
public class HoldingOutputer extends Outputer {

    boolean dbg;
    HoldingSender hs;
	static {logger.fine("$Id$");}

    /** Creates a new instance of HoldingsOutput */
    public HoldingOutputer(EdgeQueryOptions options) {
		this.options = options;
    }

    public void makeFile(NSCL nscl, String filename,
			ArrayList<MiniSeed> blks) throws IOException {

        MiniSeed ms2 = null;
        
        if (hs == null) {
            try {
                hs = new HoldingSender("-h " + options.holdingIP + " -p " + options.holdingPort + " -t " + options.holdingType + " -q 10000 -tcp", "");
            } catch (UnknownHostException e) {
                logger.severe("Unknown host exception host=" + options.holdingIP);
                System.exit(1);
            }
        }

        Collections.sort(blks);
        for (int i = 0; i < blks.size(); i++) {
            ms2 = (MiniSeed) blks.get(i);
            while (hs.getNleft() < 100) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
            hs.send(ms2);

        }
    }
}

