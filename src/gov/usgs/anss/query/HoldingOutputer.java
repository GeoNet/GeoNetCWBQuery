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
    public HoldingOutputer() {
    }

    public void makeFile(String comp, String filename, String filemask, ArrayList<MiniSeed> blks,
            java.util.Date beg, double duration, String[] args) throws IOException {

        MiniSeed ms2 = null;
        String holdingIP = QueryProperties.getGeoNetCwbIP();
        String holdingType = "CW";
        int holdingPort = QueryProperties.getGeoNetCwbPort();
        for (int i = 0; i < args.length; i++) {
            if (args[i].indexOf("-hold") == 0) {
                String[] a = args[i].split(":");
                if (a.length == 4) {
                    holdingIP = a[1];
                    holdingPort = Integer.parseInt(a[2]);
                    holdingType = a[3];
                }
            }
        }
        if (hs == null) {
            try {
                hs = new HoldingSender("-h " + holdingIP + " -p " + holdingPort + " -t " + holdingType + " -q 10000 -tcp", "");
            } catch (UnknownHostException e) {
                logger.severe("Unknown host exception host=" + holdingIP);
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

