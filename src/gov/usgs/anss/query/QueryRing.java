/*
 * QueryRing.java
 *
 * Created on February 23, 2007, 10:27 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import gov.usgs.anss.edge.*;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.text.DecimalFormat;
import java.util.ArrayList;
import gov.usgs.anss.seed.MiniSeed;
import gov.usgs.anss.util.*;

/** This class keeps a little buffer of in the clear data and satisfies requests from it.
 * When data is needed which is not in the buffer, a query is made and more data read in from
 * a preDuration for a duration in length.  
 *
 * Its a good batch class for getting time series from the CWB.
 *
 * @author davidketchum
 */
public class QueryRing extends ZeroFilledSpan {

    public static int FILL_VALUE = 2147000000;
    EdgeQueryClient query;
    String seedname;
    DecimalFormat df2;
    String host;
    int port;
    double duration;
    double preDuration;
    String[] args;

    /**
     * Creates a new instance of QueryRing set the duration to make the reads not happen
     * too often and the preDuration to be how far in the past you might ask for data from
     * the normal near-real time processing time.
     * @param h The host where the query server is running, if null or "", its gcwb.cr.usgs.gov
     * @param p The port where the cwb is running, if zero, defaults to 2061
     * @param st The starting time for the initialization
     * @param dur The length of the ring buffer in seconds.
     * @param preDur the length of time before the "real time" to make sure is available
     * @param name The seed name of the data channel.
     */
    public QueryRing(String h, int p, String name, GregorianCalendar st,
            double dur, double preDur) {
        super(new ArrayList<MiniSeed>(1), st, dur, FILL_VALUE);
        seedname = name;
        host = h;
        port = p;
        start = st;
        duration = dur;
        preDuration = preDur;
        if (h == null) {
            host = "gcwb.cr.usgs.gov";
        }
        if (h.equals("")) {
            host = "gcwb.cr.usgs.gov";
        }
        if (port <= 0) {
            port = 2061;
        }
        df2 = new DecimalFormat("00");
        args = new String[9];
        args[0] = "-s";
        args[1] = seedname;
        args[2] = "-t";
        args[3] = "null";
        args[4] = "-d";
        args[5] = "" + duration;
        args[6] = "-q";
        args[7] = "-b";
        args[8] = "" + start.get(Calendar.YEAR) + "," + start.get(Calendar.DAY_OF_YEAR) + "-" +
                df2.format(start.get(Calendar.HOUR_OF_DAY)) + ":" + df2.format(start.get(Calendar.MINUTE)) +
                ":" + df2.format(start.get(Calendar.SECOND));
        ArrayList<ArrayList<MiniSeed>> mslist = EdgeQueryClient.query(args);
        if (mslist.size() == 1) {
            refill(mslist.get(0), start, duration, FILL_VALUE);
        }
    }

    /**
     * return a chunk of time series starting at a given time for a number of samples
     * @param starting The starting time as a GregorianCalendar
     * @param nsamp The number of samples to return
     * @param d A data array which must be at least nsamp long where the data are returned.
     * @return The number of samples actually returned, will be < nsamp if the data are not yet available.
     */
    public int getDataAt(GregorianCalendar starting, int nsamp, int[] d) {
        if (dbg) {
            Util.prt("Ask for data at " + Util.ascdate(starting) + " " + Util.asctime2(starting) + " ns=" + nsamp + " start=" + Util.ascdate(start) + Util.asctime2(start));
        }
        if (starting.compareTo(start) < 0 ||
                ((long) (start.getTimeInMillis() + duration * 1000)) < ((long) (starting.getTimeInMillis() + nsamp / rate * 1000.))) {
            if (dbg) {
                Util.prt("Data not in range buf start=" + Util.ascdate(start) + " " + Util.asctime2(start) + " dur=" + duration);
            }
            GregorianCalendar now = new GregorianCalendar();
            now.setTimeInMillis(starting.getTimeInMillis());
            now.add(Calendar.MILLISECOND, (int) (-preDuration * 1000.));
            if (dbg) {
                Util.prt("Query for data start=" + Util.ascdate(now) + " " + Util.asctime2(now) + " predur=" + preDuration);
            }
            args[8] = "" + now.get(Calendar.YEAR) + "," + now.get(Calendar.DAY_OF_YEAR) + "-" +
                    df2.format(now.get(Calendar.HOUR_OF_DAY)) + ":" + df2.format(now.get(Calendar.MINUTE)) +
                    ":" + df2.format(now.get(Calendar.SECOND));
            ArrayList<ArrayList<MiniSeed>> mslist = EdgeQueryClient.query(args);
            if (mslist.size() == 1) {
                refill(mslist.get(0), now, duration, FILL_VALUE);
            } else {
                return 0;
            }
        }
        return getData(starting, nsamp, d);
    }

    /** test routine
     *@param args The args */
    public static void main(String[] args) {
        Util.setModeGMT();
        QueryRing ring = new QueryRing("136.177.24.70", 2061, "USDUG  BHZ  ", new GregorianCalendar(2007, 0, 1, 0, 0, 0), 600., 30.);
        String[] args2 = new String[9];
        args2[0] = "-s";
        args2[1] = "USDUG  BHZ  ";
        args2[2] = "-t";
        args2[3] = "null";
        args2[4] = "-d";
        args2[5] = "1d";
        args2[6] = "-q";
        args2[7] = "-b";
        args2[8] = "2007,1-00:00:00";

        ArrayList<ArrayList<MiniSeed>> mslist = EdgeQueryClient.query(args2);
        if (mslist.size() == 1) {
            ZeroFilledSpan span = new ZeroFilledSpan(EdgeQueryClient.query(args2).get(0), FILL_VALUE);
            GregorianCalendar now = new GregorianCalendar(2007, 0, 1, 0, 0, 0);
            int[] d = new int[4000];
            int[] d2 = new int[4000];
            for (int i = 0; i < 86400; i = i + 10) {
                if (i == 3510) {
                    Util.prt("3510");
                }
                Util.prt("i=" + i + " " + Util.ascdate(now) + " " + Util.asctime2(now));
                int ns = ring.getDataAt(now, 4000, d);
                int ns2 = span.getData(now, 4000, d2);
                for (int j = 0; j < Math.min(ns2, ns); j++) {
                    if (d[j] != d2[j]) {
                        Util.prt(j + " " + d[j] + "!=" + d2[j]);
                    }
                }
                now.add(Calendar.MILLISECOND, 10000);
            }

        }
    }
}
