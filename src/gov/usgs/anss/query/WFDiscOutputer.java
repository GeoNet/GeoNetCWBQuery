/*
 * WFDiscOutputer.java
 *
 * Created on April 20, 2006, 4:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.text.DecimalFormat;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import gov.usgs.anss.util.*;
import gov.usgs.anss.edge.RawDisk;
import gov.usgs.anss.seed.*;
import gov.usgs.anss.util.Complex;

/**
 *
 * @author davidketchum
 */
public class WFDiscOutputer extends Outputer {

    boolean dbg;
    private static SacPZ stasrv;
    private boolean firstCall = true;

    /** Creates a new instance of WFDiscOutputer */
    public WFDiscOutputer() {
    }

    public void makeFile(String lastComp, String filename, String filemask, ArrayList<MiniSeed> blks,
            java.util.Date beg, double duration, String[] args) throws IOException {

        // Process the args for things that affect us
        boolean nogaps = false;       // if true, do not generate a file if it has any gaps!
        int fill = 2147000000;
        boolean sacpz = false;
        boolean quiet = false;
        boolean sactrim = false;      // return full length padded with no data value
        String pzunit = "nm";
        DecimalFormat df5 = new DecimalFormat("0.000000");
        DecimalFormat df3 = new DecimalFormat("000");
        DecimalFormat df2 = new DecimalFormat("00");
        DecimalFormat ef6 = new DecimalFormat("0.00000E00");
        String begin = "";
        String stahost = Util.getProperty("metadataserver");
        int staport = 2052;
        boolean doFap = false;
        boolean oneFile = false;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-fill")) {
                fill = Integer.parseInt(args[i + 1]);
            }
            if (args[i].equals("-nogaps")) {
                fill = 2147000000;
                nogaps = true;
            }
            if (args[i].equals("-q")) {
                quiet = true;
            }
            if (args[i].equals("-b")) {
                begin = args[i + 1];
            }
            if (args[i].equals("-fap")) {
                doFap = true;
            }
            if (args[i].equals("-t")) {
                if (args[i + 1].equalsIgnoreCase("wf1")) {
                    oneFile = true;
                }
            }
            if (args[i].equals("-sacpz")) {
                pzunit = args[i + 1];
                if (!args[i + 1].equalsIgnoreCase("nm") && !args[i + 1].equalsIgnoreCase("um")) {
                    Util.prt("   ****** -sacpz units must be either um or nm switch values is " + args[i + 1]);
                    System.exit(0);
                }
            }
        }
        if (stahost == null || stahost.equals("")) {
            stahost = "137.227.230.1";
        }
        if (stasrv == null) {
            stasrv = new SacPZ(stahost, pzunit);
        }
        // Use the span to populate a sac file
        GregorianCalendar start = new GregorianCalendar();
        start.setTimeInMillis(beg.getTime());

        // build the zero filled area (either with exact limits or with all blocks)
        ZeroFilledSpan span = new ZeroFilledSpan(blks, start, duration, fill);
        if (dbg) {
            Util.prt("ZeroSpan=" + span.toString());
        }
        if (oneFile) {
            filename = EdgeQueryClient.makeFilename(filemask, lastComp, Util.stringToDate2(begin));
            Util.prt("one file name=" + filename);
        } else {
            filename = filename.substring(2);
            filename = filename.replaceAll("[__]", "_");
            filename = filename.toLowerCase();
            filename = filename.replaceAll("_", ".");
            while (filename.endsWith(".")) {
                filename = filename.substring(0, filename.length() - 1);
            }
        }

        PNZ pnz = null;
        String s = stasrv.getSACResponse(lastComp, begin);
        /*String s = "* A0-SEED      4.8539E07\n"+"CONSTANT              3.0538E+07\n"+
        "ZEROS   3\n"+
        "         0.0000E+00   0.0000E+00\n"+
        "         0.0000E+00   0.0000E+00\n"+
        "         0.0000E+00   0.0000E+00\n"+
        "POLES   5\n"+
        "        -3.7024E-02   3.7024E-02\n"+
        "        -3.7024E-02  -3.7024E-02\n"+
        "        -2.5133E+02   0.0000E+00\n"+
        "        -1.1863E+02   4.2306E+02\n"+
        "        -1.1863E+02  -4.2306E+02\n"+
        "* <EOE>\n"+
        "* <EOR>\n";*/
        double period = 1.;
        double mag = 0.00001;
        if (s.indexOf("no channels") > 0) {
            period = -1.;
            mag = .000001;
        } else {
            pnz = new PNZ(s);
            Complex resp = pnz.getResponse(period);
            mag = resp.getReal() * resp.getReal() + resp.getImag() * resp.getImag();
            mag = Math.sqrt(mag);
            if (doFap) {
                StringBuilder fap = new StringBuilder(10000);
                for (int i = 0; i < 41; i++) {
                    double f = Math.pow(10., -1. + i * 0.05);
                    resp = pnz.getResponse(f);
                    fap.append(Util.leftPad(ef6.format(f), 13) + "  " +
                            Util.leftPad(ef6.format(Math.sqrt(resp.getReal() * resp.getReal() + resp.getImag() * resp.getImag()) / mag), 13) +
                            " " + Util.leftPad(ef6.format(Math.atan2(resp.getImag(), resp.getReal()) * 180. / Math.PI), 13) + "\n");

                }
                String pafile = lastComp.substring(2, 7).toLowerCase().replaceAll("_", " ").trim() + "." + lastComp.substring(7, 10).toLowerCase().trim() + ".fap";
                try {
                    RawDisk pf = new RawDisk(pafile, "rw");
                    pf.seek(0L);
                    pf.write(fap.toString().getBytes());
                    pf.setLength(fap.length());
                    pf.close();
                } catch (IOException e) {
                    Util.prt("IOExcept writing paf e=" + e);
                }
            }
        }


        double rate = span.getRate();
        int nmax = (int) (rate * duration + 1.5);
        byte[] buf = new byte[nmax * 4];
        ByteBuffer bb = ByteBuffer.wrap(buf);
        StringBuilder wfdisc = new StringBuilder(10000);    // Put the wfdisc stuff in here
        bb.position(0);
        GregorianCalendar startTime = null;
        String dir = System.getProperty("user.dir");
        boolean inGap = false;
        long offset = 0;
        long startOffset = 0;
        if (oneFile) {
            try {
                RawDisk rw = new RawDisk(filename.trim() + ".w", "rw");
                if (firstCall) {
                    rw.setLength(0L);
                }
                offset = rw.length();
                startOffset = offset;
                rw.close();
                rw = new RawDisk(filename.trim() + ".wfdisc", "rw");
                if (firstCall) {
                    rw.setLength(0L);
                } else {
                    rw.read(buf, 0, (int) rw.length());
                    wfdisc.append(new String(buf, 0, (int) rw.length()));
                }
                firstCall = false;

            } catch (FileNotFoundException e) {
                Util.prt("First file");
            }
        }
        for (int i = 0; i < span.getNsamp(); i++) {
            if (bb.position() == 0) {        // first sample in file, save time
                startTime = span.getGregorianCalendarAt(i);
            }
            int samp = span.getData(i);
            if (samp != fill) {
                bb.putInt(samp);
                inGap = false;
            }
            if (samp == fill || i == span.getNsamp() - 1) {              // this is either a gap or a start of a gap
                if (inGap && i < span.getNsamp() - 1) {
                    continue;
                }
                inGap = true;
                if (bb.position() != 0) {      // There is data
                    int ns = bb.position() / 4;   // number of samples
                    GregorianCalendar now = new GregorianCalendar();
                    wfdisc.append(Util.rightPad(lastComp.substring(2, 7).trim(), 7) +
                            Util.rightPad(lastComp.substring(7, 12).trim(), 8) + " " +
                            Util.leftPad(df5.format(startTime.getTimeInMillis() / 1000.), 17) +
                            "       -1 " +
                            "      -1  " + // chanid
                            startTime.get(Calendar.YEAR) + df3.format(startTime.get(Calendar.DAY_OF_YEAR)) + " " +
                            Util.leftPad(df5.format(startTime.getTimeInMillis() / 1000. + (bb.position() / 4 - 1) / span.getRate()), 17) + " " +// end time
                            Util.leftPad("" + bb.position() / 4, 8) + " " + // nsamp
                            Util.leftPad(df5.format(span.getRate()), 11) + " " + // sample rate
                            Util.leftPad(df5.format(mag), 16) + " " + // callib

                            Util.leftPad(df5.format(period), 16) + " " + // Calper
                            "-      " + //inst type
                            "- " + // segtype
                            "s4 " + // data type
                            "- " + //clip
                            //Util.rightPad(dir, 65)+         // directory
                            Util.rightPad(".", 65) +
                            Util.rightPad(filename.trim() + ".w", 33) + // Filename
                            Util.leftPad("" + offset, 10) +
                            "       -1 " +
                            now.get(Calendar.YEAR) + df2.format(now.get(Calendar.MONTH) + 1) + df2.format(now.get(Calendar.DAY_OF_MONTH)) + " " +
                            df2.format(now.get(Calendar.HOUR_OF_DAY)) + ":" + df2.format(now.get(Calendar.MINUTE)) + ":" +
                            df2.format(now.get(Calendar.SECOND)) + "\n");
                    offset = bb.position();       // save offset in binary file
                }
            }
        }
        try {
            RawDisk wf = new RawDisk(filename.trim() + ".w", "rw");
            Util.prt("add to waveform file=" + filename + " offset=" + startOffset + " len=" + bb.position());
            wf.seek(startOffset);
            wf.write(buf, 0, bb.position());
            wf.close();
            wf = new RawDisk(filename.trim() + ".wfdisc", "rw");
            wf.seek(0L);
            wf.write(wfdisc.toString().getBytes());
            wf.setLength(wfdisc.length());
            wf.close();
        } catch (FileNotFoundException e) {
            Util.IOErrorPrint(e, "File not found opening " + filename);
        } catch (IOException e) {
            Util.IOErrorPrint(e, "Writing file=" + filename);
        }
        // Now march through the data creating .w and .wfdisc records


    }
}
