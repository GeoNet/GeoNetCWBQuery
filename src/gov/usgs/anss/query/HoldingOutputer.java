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
import java.io.FileOutputStream;
import java.io.IOException;
import gov.usgs.anss.edge.*;
import gov.usgs.anss.util.*;

/**
 * This outputer just sends the blocks to the TCP/IP based holdings server.  The user
 * can override the gacqdb/7996:CW if necessary.
 *
 * @author davidketchum
 */
public class HoldingOutputer extends Outputer {

    boolean dbg;
    HoldingSender hs;

    /** Creates a new instance of HoldingsOutput */
    public HoldingOutputer() {
    }

    public void makeFile(String comp, String filename, String filemask, ArrayList<MiniSeed> blks,
            java.util.Date beg, double duration, String[] args) throws IOException {
        MiniSeed ms2 = null;
        String holdingIP = "136.177.24.92";
        String holdingType = "CW";
        int holdingPort = 7996;
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
                Util.prt("Unknown host exception host=" + holdingIP);
                System.exit(1);
            }
        }

        Collections.sort(blks);
        for (int i = 0; i < blks.size(); i++) {
            ms2 = (MiniSeed) blks.get(i);
            while (hs.getNleft() < 100) {
                //Util.prta("Slept 1 left="+hs.getNleft()+" "+hs.getStatusString());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
            hs.send(ms2);

        }
    }
}

