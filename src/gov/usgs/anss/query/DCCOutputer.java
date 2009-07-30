/*
 * DCCOutputer.java
 *
 * Created on October 27, 2006, 12:06 PM
 *
 * 
 */
package gov.usgs.anss.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.nio.ByteBuffer;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.io.IOException;
import edu.iris.Fissures.codec.Steim1;
import edu.iris.Fissures.codec.Steim2;
import edu.iris.Fissures.codec.SteimException;
import gov.usgs.anss.edge.*;
import gov.usgs.anss.seed.*;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/** This class generates 4096 byte mini-seed from the returned blocks from a 
 * query.  It uses the following general algorithm :
 *
 *1)  Create "spans" from the list of blocks (list of contiguouse blocks)  This
 *insures blocks that are "overlapping" are in separate spans so the best spans
 * can be chosen.
 *
 *2)  The spans are processed to proved a single block covering each section of time.
 *Overlaps are guaranteed to be only one block long.
 *
 *3)  The list of blocks are then processed into 4096 byte blocks by adding the data
 *sections of full data frames together.  The status sections (ClockIO bits, data quality
 *bits, and activities bits are "OR"ed together from the assembly of blocks.  The 
 *clock quality in Blockette 1001 is the minimum clock quality from the assembled blocks.
 *
 *4)  The blocks are output when a)  There is not enough room in the 4096 to add the next one,
 *b) The block to be added would cause the reverse integration error for the assembled block
 *c) The last block added is a "partial" that is not a full set of data frames.
 *d)  The next block to add is not contiguous in time
 * 
 * (b) above occurs when the RawToMiniSeed class compresses data in more than one section but
 *in separate runs.  Whenthis happens the first difference in the block is to zero rather than
 *the last data value of the prior data frame.  The choice is to fix the difference or to break
 *the blocks at these boundaries.
 *
 * @author davidketchum
 */
public class DCCOutputer extends Outputer implements MiniSeedOutputHandler {

    static String[] Q330S = {"AAM", "ACSO", "AGMN", "BBGH", "BCIP", "BLA", "BRAL", "CNNC", "COWI",
        "DGMT", "DUG", "ECSD", "EGAK", "EGMT", "EYMN", "GLMI", "GOGA", "GRGR", "GTMO", "HAWA",
        "HDIL", "HLID", "ISCO", "JCT", "JFWS", "KSU1", "KVTX", "LONY", "MIAR", "MVCO", "NEW", "NLWA",
        "PKME", "SCIA", "SDDR", "TGUH", "WMOK", "WRAK", "WUAZ", "WVOR"};
    private boolean dbg;
    private boolean check;
    private ArrayList<MiniSeed> outblks;    // Used by check mode
    private ArrayList<Run> runs;
    private DecimalFormat df6;
    private FileOutputStream out, outin;
    private boolean lastPartial;      // Last block added was not a full set of frames, cut off next one
    private long expected;          // Time in Millis of next expected from
    private byte[] rawTime;          // The raw time bytes for the ms being worked on.
    private int frameCount;           // number of frames put in revised buffer
    private int gapThreshold;         // Threshold in millis for a gap declaration
    private int minClockQual;         // accumulate the minimum for the timing quality
    private int activityFlags;        // accumulate to OR of activity flags
    private int clockFlags;           // accumulate the OR of clock flags
    private int dataQual;             // accumulate the OR of data quality flags
    private int nsamp;                // number of samples in revised records.
    private short rateFactor;           // THe rate factor for the next header
    private short rateMult;             // The rate multiplier for the next sample
    private Blockette1000 b1000;      // a blockette 1000 for the current record
    private Blockette1001 b1001;      // a blockette 1001 for the current record
    private byte[] dummy;            // buffer space to build up
    private byte[] outputBytes;      // buffer for RawToMiniSeed to putbuf()
    private ByteBuffer bb;            // a wrap of dummy
    private int encoding;             // expected encoding for this run
    private MiniSeed msout;           // A miniseed record for doing the output
    private int usecs;                // first value of usecs
    private int sequence;
    private int lastSequence;         // sequence of last block processed
    private byte[] frames;
    private byte[] scratch;
    private ByteBuffer bbscratch;
    private int lastReverse;
    private byte[] empty1000 = {3, (byte) 232, 0, 56, 11, 1, 12, 0};
    private byte[] empty1001 = {3, (byte) 233, 0, 0, 0, 0, 0, 7};
    int maxnsamp;                 // storage for the most samples that could be in a block before midnight
    long dropDeadEnd;
	static {logger.fine("$Id$");}

    private static DateTimeFormatter hmsFormat = ISODateTimeFormat.time().withZone(DateTimeZone.forID("UTC"));
    private static DateTimeFormatter dtFormat = ISODateTimeFormat.dateTime().withZone(DateTimeZone.forID("UTC"));

    /** Creates a new instance of DCCOutputer */
    public DCCOutputer() {
    }

    public void makeFile(String comp, String filename, String filemask, ArrayList<MiniSeed> blks,
            java.util.Date beg, double duration, String[] args) throws IOException {
        MiniSeed ms2 = null;
        frames = new byte[4096 - 64];   // scratch space for decompression
        runs = new ArrayList<Run>(100);
        dummy = new byte[4096];
        outputBytes = new byte[4096];
        scratch = new byte[4096];
        bbscratch = ByteBuffer.wrap(scratch);
        bb = ByteBuffer.wrap(dummy);
        ZeroFilledSpan checkInput = null;
        ZeroFilledSpan checkOutput = null;
        dropDeadEnd = (long) (beg.getTime() + duration * 1000. + 0.5);

        encoding = 11;
        check = false;                  // This controls whether we perform checks
        if (blks.size() == 0) {
            return;
        }
        Steim2.setStrictRIC(true);
        if (blks.get(0).getSeedName().substring(7, 10).equals("ACE")) {
            return;
        }
        // Before Jan 20, 2007 some HR data had BHN and BHE at 1 hz.  Drop any such packets!
        // Also cull and packets which cannot be decompressed
        for (int i = blks.size() - 1; i >= 0; i--) {
            if (blks.get(i).getRate() < 1.001) {
                if (blks.get(i).getSeedName().substring(7, 9).equals("BH")) {
                    logger.info("DCC rate=1 and BH" + blks.get(i).toString());
                    blks.remove(i);
                    continue;
                }
            }

            ms2 = blks.get(i);
            if (ms2.getNsamp() == 0 || ms2.getRate() < 0.0001) {
                blks.remove(i);
                continue;
            }
            // If a block cannot be decompressed, cull it out
            boolean steimError = false;
            try {
                int reverse = 0;
                int[] samples = null;
                System.arraycopy(ms2.getBuf(), ms2.getDataOffset(), frames, 0, 4096 - ms2.getDataOffset());

                if (ms2.getEncoding() == 10) {
                    samples = Steim1.decode(frames, ms2.getNsamp(), ms2.isSwapBytes(), reverse);
                }
                if (ms2.getEncoding() == 11) {
                    samples = Steim2.decode(frames, ms2.getNsamp(), ms2.isSwapBytes(), reverse);
                }
            } catch (SteimException e) {
                logger.severe("Got Steim error e=" + e);
                steimError = true;
            }
            if (ms2.getEncoding() == 11) {
                if (Steim2.hadSampleCountError() || steimError) {
                    logger.warning("Culling bad Steim II decomp blk ms=" + ms2);
                    blks.remove(i);
                    continue;
                }
                if (Steim2.hadReverseError()) {
                    logger.warning("Fix Steim II reverse integration error ms=" + ms2);
                    ms2.fixReverseIntegration();
                }
            }
        }
        if (blks.size() == 0) {
            return;      // discarded all the data, do not create a file
        }
        // Some of the early Q330 data did not put Husec in Mini-seed but truncated MS.  Reverse this process
        boolean q330 = false;
        for (int i = 0; i < Q330S.length; i++) {
            if (comp.substring(2, 7).trim().equals(Q330S[i])) {
                q330 = true;
                break;
            }
        }
        if (q330) {
            for (int i = 0; i < blks.size(); i++) {
                blks.get(i).fixHusecondsQ330();
            }
        }

        // process args for specail usage here
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-chk")) {
                check = true;
            }
            if (args[i].equals("-dccdbg")) {
                dbg = true;
            }
        }
        if (filemask.equals("%N")) {
            filename += ".msd";
        }

        filename = filename.replaceAll("[__]", "_");
        out = new FileOutputStream(filename);
        Collections.sort(blks);

        double rate = -1.;
        MiniSeed model = null;


        // create some number of runs to put together into a  long one.
        for (int i = 0; i < blks.size(); i++) {
            ms2 = (MiniSeed) blks.get(i);
            boolean found = false;
            if (ms2.getRate() > rate) {
                rate = ms2.getRate();
                rateFactor = ms2.getRateFactor();
                rateMult = ms2.getRateMultiplier();
                if (rate < 0.0000000001) {
                    gapThreshold = 25;
                } else {
                    gapThreshold = (int) (500. / rate + 0.5);
                }
            }
            for (int j = 0; j < runs.size(); j++) {
                if (runs.get(j).add(ms2)) {
                    found = true;
                    break;
                }
            }
            if (ms2.getNBlockettes() == 2) {
                if (ms2.getBlocketteType(0) == 1000 && ms2.getBlocketteType(1) == 1001) {
                    model = ms2;
                    rate = ms2.getRate();
                    rateFactor = ms2.getRateFactor();
                    rateMult = ms2.getRateMultiplier();
                    gapThreshold = (int) (500. / rate + 0.5);
                }
            }
            if (!found) {
                runs.add(new Run(ms2));
            }
        }
        Collections.sort(runs);

		logger.finer("\n");
		for (int j = 0; j < runs.size(); j++) {
			logger.finer(j + " " + runs.get(j).toString());
		}

        // find begining time and ending time of all runs
        GregorianCalendar curr = runs.get(0).getStart();
        GregorianCalendar end = runs.get(0).getEnd();
        for (int i = 0; i < runs.size(); i++) {
            if (end.compareTo(runs.get(i).getEnd()) < 0) {
                end = runs.get(i).getEnd();
            }
        }

        boolean done = false;
        int iblk = 0;
        Run r = runs.get(0);
        int currRun = 0;
        ArrayList<MiniSeed> list = new ArrayList<MiniSeed>(1000); // this will contain the purged list of blocks
        int insize = blks.size();
        while (!done) {
            // put all of the blocks from iblk on into the list of blocks
            for (int i = iblk; i < r.getNBlocks(); i++) {
                list.add(r.getMS(i));
            }

			logger.fine("This run ends with " + r.getMS(r.getNBlocks() - 1) + " end tim=" + r.getMS(r.getNBlocks() - 1).getEndTimeString());


            // was that the last run?
            if (currRun >= runs.size() - 1) {
                done = true;
                break;
            }

            // we are at the end of a run, pick the longest overlapping run, if none pick next in time
            long earliest = Long.MAX_VALUE;
            long latest = Long.MIN_VALUE;
            int iearliest = -1;
            int ilatest = -1;
            for (int i = currRun + 1; i < runs.size(); i++) {
                // check this run for having the earliest start time which also spans the end of th last run
                if (runs.get(i).getStart().getTimeInMillis() < earliest &&
                        runs.get(i).getEnd().compareTo(r.getEnd()) > 0) {
                    earliest = runs.get(i).getStart().getTimeInMillis();
                    iearliest = i;
                }

                // does this run include the ending time of the last one? If so, save it if it end later than best so far
                if (runs.get(i).getStart().compareTo(r.getEnd()) <= 0 && runs.get(i).getEnd().compareTo(r.getEnd()) >= 0) {
                    if (runs.get(i).getEnd().getTimeInMillis() > latest) {
                        latest = runs.get(i).getEnd().getTimeInMillis();
                        ilatest = i;
                    }
                }
            }
            blks.clear();             // release memory in blks!

            // Either the latest has a value and we will use it, or the earliest must be used
            if (ilatest >= 0) {
                iblk = 0;
                for (iblk = 0; iblk <= runs.get(ilatest).getNBlocks(); iblk++) {
                    if (iblk == runs.get(ilatest).getNBlocks()) {
                        break;   // avoid one off the end problems
                    }
                    if (runs.get(ilatest).getMS(iblk).getGregorianCalendar().compareTo(r.getEnd()) > 0) {
                        break;
                    }
                }
                if (iblk != runs.get(ilatest).getNBlocks() ||
                        (iblk == runs.get(ilatest).getNBlocks() &&
                        runs.get(ilatest).getMS(Math.max(iblk - 1, 0)).getGregorianCalendar().compareTo(r.getEnd()) <= 0) &&
                        runs.get(ilatest).getMS(Math.max(iblk - 1, 0)).getEndTime().compareTo(r.getEnd()) > 0) {
                    iblk = iblk - 1;

					logger.fine("ms = " + hmsFormat.print(runs.get(ilatest).getMS(iblk).getGregorianCalendar().getTimeInMillis()) + " r.end=" + hmsFormat.print(r.getEnd().getTimeInMillis()) +
							" compareTO=" + runs.get(ilatest).getMS(iblk).getGregorianCalendar().compareTo(r.getEnd()) + " ms=" + runs.get(ilatest).getMS(iblk).toString());


					logger.fine(" latest start with =" + runs.get(ilatest).getMS(iblk).toString());

                } else // In this case leave iblk past end so no blocks are processed.

				logger.fine("Last one is still before end ms=" + hmsFormat.print(runs.get(ilatest).getMS(iblk - 1).getGregorianCalendar().getTimeInMillis()) + " r.end=" + hmsFormat.print(r.getEnd().getTimeInMillis()) +
						" compareTO=" + runs.get(ilatest).getMS(iblk - 1).getGregorianCalendar().compareTo(r.getEnd()) + " ms=" + runs.get(ilatest).getMS(iblk - 1).toString());

                if (iblk < 0) {
                    iblk = 0;
                }
                r = runs.get(ilatest);
                currRun = ilatest;
            } else if (iearliest < 0) {

				logger.fine("No more data beyond run =" + r.toString());

                break;
            } else {
                iblk = 0;

                r = runs.get(iearliest);        // This is the next segment after the gap

				logger.fine("Earliest (with gap) =" + r.getMS(0));

                currRun = iearliest;
            }
        }
        for (int i = 0; i < runs.size(); i++) {
            runs.get(i).clear();
        }


		logger.fine("#input blocks=" + insize + " of #blks=" + list.size() + " " +
				((double) list.size()) / insize * 100 + " %");


        // At this point list contains a list of all the blocks needed to make the whole time,
        // process this into 512 point miniseed
        // If checks are turned on, we need zero filled objects for input blocks and output blocks
        if (check) {
            checkInput = new ZeroFilledSpan(list, 2147000000);
            outin = new FileOutputStream(filename + "i");
            outblks = new ArrayList<MiniSeed>(blks.size());

            for (int i = 0; i < list.size(); i++) {
                ms2 = list.get(i);
                outin.write(ms2.getBuf(), 0, ms2.getBlockSize());
            }
            outin.close();
        }

        // Now we need to go through the frames agreegating them into 4096 byte frames as needed
        if (model != null) {
            System.arraycopy(model.getBuf(), 0, dummy, 0, 64);
            bb.position(6);
            bb.put("D ".getBytes());
            bb.position(44);
            bb.putShort((short) 64);
            encoding = model.getEncoding();
        } else {      // If we do not have a good model
            System.arraycopy(list.get(0).getBuf(), 0, dummy, 0, 64);
            bb.position(6);
            bb.put("D ".getBytes());
            bb.position(36);
            bb.putInt(0);      // SET FLAGS TO ZERO
            bb.position(39);
            bb.put((byte) 2);  // two blockettes follow
            bb.putLong((long) 0);               // zero 40-47 time correction an beginning of daa
            // point to first of data
            bb.position(44);                    // point to beginning of blocketss
            bb.putShort((short) 64);            //
            // point to blockette 1001
            bb.putShort((short) 48);            // Where is next blockette (1000)
            bb.putShort((short) 1000);           // Its a blockette 1000
            bb.putShort((short) 56);            // Were is the blockette 1001
            bb.put((byte) 11);                  // Encoding format
            bb.put((byte) 1);                   // Word order
            bb.put((byte) 12);                  // record length 4096
            bb.put((byte) 0);                   // reserved
            bb.putShort((short) 1001);          // Blockette 1001
            bb.putShort((short) 0);             // no next blockette
            bb.putInt(0);                       // Timing quality zero, usec=0, reserv=0 framecount=0

        }
        bb.position(0);     // do sequence
        bb.put("200001".getBytes());
        bb.position(54);        // data rec length
        bb.put((byte) 12);     // 4096 in the output records
        bb.position(36);        // activity, clock and qual flags
        for (int i = 0; i < 3; i++) {
            bb.put((byte) 0);    // set activity, IO and clock flags, data quality flags
        }
        bb.put((byte) 2);        // a blockette 1000 and 1002 follow
        msout = null;
        try {
            msout = new MiniSeed(dummy);       // This is a place to build miniseed records
        } catch (IllegalSeednameException e) {
            logger.severe("*** Dummy is not valid e=" + e.getMessage());
            System.exit(0);
        }

        // Set up the

        frameCount = 0;                     // This must be zero for writeBlock() to initizlize and not write
        sequence = 200001;                  // starting sequence #
        writeBlock(list.get(0));            // Initialize starting parameters

        // add each successive block to output Mini-seed.
        for (int i = 0; i < list.size(); i++) {
            long start = list.get(i).getGregorianCalendar().getTimeInMillis();

            // if it continues the current block, just add it on
            if (Math.abs(start - expected) < gapThreshold) {
                addBlock(list.get(i));
            } // does this block overlap previous?  Must decompress and recompress at right point.
            else if (start - expected < 0) {
                int[] data = null;
                MiniSeed ms = list.get(i);
                System.arraycopy(ms.getBuf(), ms.getDataOffset(), frames, 0, ms.getBlockSize() - ms.getDataOffset());
                try {
                    if (list.get(i).getEncoding() == 11) {
                        data = Steim2.decode(frames, ms.getNsamp(), ms.isSwapBytes());
                    }
                    if (list.get(i).getEncoding() == 10) {
                        data = Steim1.decode(frames, ms.getNsamp(), ms.isSwapBytes());
                    }

                    // Figure the offset and do the recompression using RawToMiniSeed
                    int offset = (int) ((expected - start) * rate / 1000. + 0.5);
                    int newnsamp = ms.getNsamp() - offset;
                    GregorianCalendar sss = ms.getGregorianCalendar();

					logger.fine(" Overlapping offset=" + offset + " ns=" + ms.getNsamp() + " new ns=" + newnsamp +
							" ms=" + ms);

                    for (int j = 0; j < 64; j++) {
                        outputBytes[j] = 0;   // Insure its a cleared
                    }
                    System.arraycopy(data, offset, data, 0, newnsamp);
                    sss.setTimeInMillis(ms.getTimeInMillis());    // start time of ms buffer
                    sss.add(Calendar.MILLISECOND, (int) (offset / rate * 1000. + 0.5));// add offset to first sample

                    RawToMiniSeed rtms = new RawToMiniSeed(ms.getSeedName(), rate, 63,
                            sss.get(Calendar.YEAR), sss.get(Calendar.DAY_OF_YEAR),
                            sss.get(Calendar.HOUR) * 3600 + sss.get(Calendar.MINUTE) * 60 + sss.get(Calendar.SECOND),
                            //ms.getHour()*3600+ms.getMinute()*60+ms.getSeconds(),
                            sss.get(Calendar.MILLISECOND) * 1000,
                            //ms.getHuseconds()*100,
                            1, null);
                    rtms.setOutputHandler(this);        // This registers our putbuf
                    if (encoding == 11 || encoding == 10) {
                        bb.position(72);
                    } else if (encoding == 19) {
                        bb.position(76);
                    }
                    int lastRev = bb.getInt();                          // put value in last

					logger.fine("new buffer computed output time=" + hmsFormat.print(sss.getTimeInMillis()) + " lastvalue=" + lastRev);


                    rtms.process(data, newnsamp,
                            sss.get(Calendar.YEAR), sss.get(Calendar.DAY_OF_YEAR),
                            //ms.getYear(), ms.getDoy(),
                            sss.get(Calendar.HOUR_OF_DAY) * 3600 + sss.get(Calendar.MINUTE) * 60 + sss.get(Calendar.SECOND),
                            //ms.getHour()*3600+ms.getMinute()*60+ms.getSeconds(),  // seconds
                            sss.get(Calendar.MILLISECOND) * 1000,
                            //ms.getHuseconds()*100,
                            ms.getActivityFlags(), ms.getIOClockFlags(), ms.getDataQualityFlags(),
                            ms.getTimingQuality(), lastRev);  // usecs
                    for (int j = 0; j < 64; j++) {
                        if (outputBytes[j] != 0) {
                            try {
                                ms = new MiniSeed(outputBytes);
                                logger.warning("*** Putbuf has been called before forceout!!!!!!" + ms);
                                addBlock(ms);
                                for (int jj = 0; jj < 64; jj++) {
                                    outputBytes[jj] = 0;   // Insure its a cleared
                                }
                            } catch (IllegalSeednameException e) {
                                logger.severe("*** IllegalSeedname =" + e.getMessage());
                                System.exit(1);
                            }
                            break;
                            //System.exit(1);               // This had better never happen
                        }
                    }
                    rtms.forceOut();                  // This forces call to our putbuf
                    try {
                        boolean ok = false;
                        for (int j = 0; j < 64; j++) {
                            if (outputBytes[j] != 0) {
                                ok = true;
                            }
                        }
                        if (ok) {
                            ms = new MiniSeed(outputBytes);     // create one from just compressed!

							logger.fine("overlap created little block=" + ms);

                            addBlock(ms);                 // Add recompressed block
                            for (int j = 0; j < 64; j++) {
                                outputBytes[j] = 0;   // Insure its a cleared
                            }
                            lastPartial = true;             // Force output before next block
                        }
                    } catch (IllegalSeednameException e) {
                        logger.severe("*** IllegalSeedname =" + e.getMessage());
                        System.exit(1);
                    }
                } catch (SteimException e) {
                    logger.severe("   *** steim1/2 err=" + e.getMessage() + " i=" + iblk + " ms=" + list.get(i).toString());
                }
            } // There is a gap here - flush out block and start new compression sequence
            else {
                writeBlock(list.get(i));
                addBlock(list.get(i));
            }
        }
        writeBlock(list.get(0));        // Write out the last "partial block

        //
        if (check) {
            checkOutput = new ZeroFilledSpan(outblks, 2147000000);
            logger.info(checkInput.differences(checkOutput));
        }
        out.close();
        list.clear();

		logger.fine("Channel done");

    }

    /** add this miniseed blockette to one being built in dummy.  The activity, IOClock, and
     * data quality flags are ORed together and the blockette 1001 clock quality is set to
     * minimum.
     *@param ms The miniseed block to add data frames to dummay
     */
    private void addBlock(MiniSeed ms) {
        MiniSeed ms2 = null;
        if (ms.getNsamp() == 0) {
            return;      // nothing to do!
        }
        if (ms.getGregorianCalendar().getTimeInMillis() >= dropDeadEnd) {
            return;    // This packet is past the drop dead time
        }
        do {
            int maxns = (int) ((86400000L - (ms.getGregorianCalendar().getTimeInMillis() % 86400000L) + ((long) 1000 / ms.getRate() - 1)) * ms.getRate() / 1000);
            if (maxns < ms.getNsamp()) {    // if we need to shorten this block to make it stop at the day boundardy, do so

				logger.fine("Attempted to make final block too long.  Trimed from " + ms.getNsamp() + " to " + maxns);

                try {
                    int reverse = 0;
                    byte[] frames2 = new byte[4096 - 64];
                    System.arraycopy(ms.getBuf(), ms.getDataOffset(), frames2, 0, 4096 - ms.getDataOffset());
                    int[] samples = null;
                    if (ms.getEncoding() == 10) {
                        samples = Steim1.decode(frames2, ms.getNsamp(), ms.isSwapBytes(), reverse);
                    }
                    if (ms.getEncoding() == 11) {
                        samples = Steim2.decode(frames2, ms.getNsamp(), ms.isSwapBytes(), reverse);
                    }
                    // the shortened length reverse integration constant is in the maxnsamp-1 sample
                    //lastReverse = samples[maxnsamp-1];
                    //nsamp=maxnsamp;
                    ByteBuffer nbb = ByteBuffer.wrap(ms.getBuf());
                    // Change the reverse integration constant and number of samples
                    nbb.position(72);
                    nbb.putInt(samples[maxns - 1]);
                    nbb.position(30);
                    nbb.putShort((short) maxns);
                    ms.load(ms.getBuf());

					logger.fine("REvisedms=" + ms);


                    // now create an output buffer for the next day with the rest of the data!

                    GregorianCalendar e = ms.getGregorianCalendar();
                    e.add(Calendar.MILLISECOND, (int) (maxns / ms.getRate() * 1000. + 0.05));
                    if (e.getTimeInMillis() < dropDeadEnd) {

                        RawToMiniSeed rtms = new RawToMiniSeed(ms.getSeedName(), ms.getRate(), 63,
                                e.get(Calendar.YEAR), e.get(Calendar.DAY_OF_YEAR),
                                (int) ((e.getTimeInMillis() % 86400000l) / 1000), (int) ((e.getTimeInMillis() % 1000L) * 1000L),
                                sequence, null);
                        rtms.setOutputHandler(this);
                        System.arraycopy(samples, maxns, samples, 0, samples.length - maxns);
                        rtms.process(samples, samples.length - maxns, e.get(Calendar.YEAR), e.get(Calendar.DAY_OF_YEAR),
                                (int) ((e.getTimeInMillis() % 86400000l) / 1000), (int) ((e.getTimeInMillis() % 1000L) * 1000L),
                                0, 0, 0, 0, lastReverse);
                        rtms.forceOut();
                        // putbuf was just called putting new data int outputBytes
                        ms2 = new MiniSeed(outputBytes);
                    }
                } catch (SteimException e) {
                    logger.severe("**** block gave steim decode error. " + e.getMessage());
                } catch (IllegalSeednameException e) {
                    logger.severe("WOW! I got an illegal seedname compressing out and end of day.");
                }
            }
            int nblockettes = ms.getNBlockettes();
            b1000 = null;
            b1001 = null;

            // build up blockette 1000 and 1001 from this data
            for (int j = 0; j < nblockettes; j++) {
                if (ms.getBlocketteType(j) == 1000) {
                    b1000 = new Blockette1000(ms.getBlockette1000());
                    if (b1000.getEncoding() != encoding) {
                        logger.severe("*** The encoding of data in this channel is inconsistent!");
                        System.exit(0);
                    }
                }
                if (ms.getBlocketteType(j) == 1001) {
                    b1001 = new Blockette1001(ms.getBlockette1001());
                    if (minClockQual > b1001.getTimingQuality()) {
                        minClockQual = b1001.getTimingQuality();
                    }
                    if (usecs > 100000) {
                        usecs = b1001.getUSecs();    // save first usecs
                    }
                }

            }
            // If the 1000 or 1001 is empty, create default ones.
            int nfexpect = 0;
            int nf = 0;
            if (b1000 == null) {
                b1000 = new Blockette1000(empty1000);
            }
            if (b1001 == null) {
                b1001 = new Blockette1001(empty1001);
                b1001.setFrameCount((ms.getBlockSize() - ms.getDataOffset()) / 64);
            }


            // Process the flags and number of data frames.
            nf = b1001.getFrameCount();
            nfexpect = b1001.getFrameCount();
            activityFlags |= ms.getActivityFlags();
            clockFlags |= ms.getIOClockFlags();
            dataQual |= ms.getDataQualityFlags();
            if (rateFactor != ms.getRateFactor()) {
                logger.warning("Changing rate factor at ms=" + ms);
                rateFactor = ms.getRateFactor();
            }
            if (rateMult != ms.getRateMultiplier()) {
                logger.warning("Changing rate multiplier at ms=" + ms);
                rateMult = ms.getRateMultiplier();

            }
            if (ms.getUsedFrameCount() < nf) {
                nf = ms.getUsedFrameCount();
            }

            // is there enough room to add it?  Last partial if true, forces write because last block added was not fully packed
            if (frameCount + nf > 63 || lastPartial) {
                writeBlock(ms);
                lastPartial = false;
            }
            // If this block is not fully packed set lastPartial flag for next time
            //if( ms.getUsedFrameCount() != nfexpect)
            //  lastPartial=true;


            // Now move the reverse integration constant from just added frame to first frame of output record
            if (encoding == 10 || encoding == 11) {
                System.arraycopy(dummy, 72, scratch, 0, 4);        // save old reverse integration
                System.arraycopy(ms.getBuf(), ms.getDataOffset() + 8, dummy, 72, 4);  // its in byte 8
            } else if (encoding == 19) {   // Steim III
                System.arraycopy(dummy, 76, scratch, 0, 4);        // save old reverse integration
                System.arraycopy(ms.getBuf(), ms.getDataOffset() + 12, dummy, 76, 4);  // its in byte 12 of frame
            }

            // insure last data point matches the first difference here or fix it
            bbscratch.position(0);
            int rev = bbscratch.getInt();
            if (frameCount != 0 && rev != lastReverse) {
                logger.warning(" * revs do not agree! rev=" + rev + " lastRev=" + lastReverse);
            }

			logger.fine("ms=" + ms);


            if (frameCount != 0 && ms.getEncoding() == 11) // fixit only works on Steim2
            {
                if (!fixit(rev, ms.getBuf())) {
                    logger.info(" * INFO: Could not fix a reverse integration.  Send as short packet.");
                    System.arraycopy(scratch, 0, dummy, 72, 4);
                    writeBlock(ms);
                }
            }
            // Add the frames to end
            System.arraycopy(ms.getBuf(), ms.getDataOffset(), dummy, 64 + frameCount * 64, nf * 64);


            // The blocks from the Edge for 512 might be contiguous, but also might not be from one long
            // compression run.  When this happens, the first difference in the block is from zero rather
            // than from the last sample of the last buffer,  when this happens it cannot just be concatenated
            // to prior blocks and must stand on its own.  Force the block out!  The sequence will likely be
            // zero or 800000 or 900000
            boolean steimError = false;
            int[] samples = null;
            try {
                int reverse = 0;
                System.arraycopy(dummy, 64, frames, 0, 4096 - 64);
                if (ms.getEncoding() == 10) {
                    samples = Steim1.decode(frames, nsamp + ms.getNsamp(), ms.isSwapBytes(), reverse);
                }
                if (ms.getEncoding() == 11) {
                    samples = Steim2.decode(frames, nsamp + ms.getNsamp(), ms.isSwapBytes(), reverse);
                }
                lastReverse = samples[samples.length - 1];
            } catch (SteimException e) {
                steimError = true;
                logger.severe("**** block gave steim decode error. " + e.getMessage());
            }

            // Would adding this block "as is" cause a reverse constant error (or steim error)?  If so, restore block
            // to state before adding this one, write it out, and make this block the beginning of next output block
            if (Steim2.hadReverseError() || steimError || Steim2.hadSampleCountError()) {
                if (Steim2.hadReverseError()) {
                    logger.warning("addBLock " + Steim2.getReverseError());
                }
                if (Steim2.hadSampleCountError()) {
                    logger.warning("addBlock " + Steim2.getSampleCountError());
                }
                try {
                    samples = Steim2.decode(frames, nsamp + ms.getNsamp(), ms.isSwapBytes(), 0);
                } catch (Exception e) {
                }
                System.arraycopy(ms.getBuf(), 64, frames, 0, nf * 64);
                int[] aft = null;
                int lastDiff = 0;
                int ndiff = 0;
                try {
                    if (ms.getEncoding() == 11) {
                        aft = Steim2.decode(frames, ms.getNsamp(), ms.isSwapBytes(), 0);
                    }
                    for (int i = 0; i < ms.getNsamp(); i++) {
                        if (aft[i] - samples[nsamp + i] != lastDiff) {
                            logger.info(i + " " + aft[i] + " != " + samples[nsamp + i] + " df=" + (aft[i] - samples[nsamp + i]) + " ndiff=" + ndiff + " nsamp=" + nsamp);
                            ndiff = 0;
                            lastDiff = aft[i] - samples[nsamp + i];
                        }
                        if (lastDiff != 0) {
                            ndiff++;
                        }
                    }
                    if (ndiff > 0) {
                        logger.info("last ndiff=" + ndiff);
                    }
                } catch (SteimException e) {
                }
                bbscratch.position(0);
                if (ms.getSequence() == lastSequence + 1) {
                    logger.warning("   *** found case of bad decomp at seq+1=" + ms.getSequence() +
                            "last=" + lastSequence + " ms=" + ms);
                } else if (ms.getSequence() % 100000 == 0) {
                    logger.warning("*** Expected bad decomp occurred(seq=0). sq=" + ms.getSequence() + " " + lastSequence + " ms=" + ms);
                } else {
                    logger.warning("   *** found case of bad decomp and not right sequence=" + ms.getSequence() +
                            "last=" + lastSequence + " ms=" + ms);
                }
                // Zero Bytes just added
                for (int i = 64 + frameCount * 64; i < 64 + frameCount * 64 + nf * 64; i++) {
                    dummy[i] = 0;
                }
                if (encoding == 10 || encoding == 11) {
                    System.arraycopy(scratch, 0, dummy, 72, 4);  // restore saved reverse integration constant
                } else if (encoding == 19) // Steim III
                {
                    System.arraycopy(scratch, 0, dummy, 76, 4);  // restore saved reverse integration constant
                }
                writeBlock(ms);               // Write the block out.
                System.arraycopy(ms.getBuf(), 64, dummy, 64 + frameCount * 64, nf * 64);// put data frames at beginning
            }

            // Update nsamp, number of frames and expected time for next record to process.
            lastSequence = ms.getSequence();
            nsamp += ms.getNsamp();
            frameCount += nf;
            expected = ms.getGregorianCalendar().getTimeInMillis() + (long) (ms.getNsamp() * 1000. / ms.getRate() + 0.5);
            if (ms2 != null) {
                writeBlock(ms2);        // forces out using the current block
                ms = ms2;
            }
        } while (ms2 != null);
    }

    /** examine the first difference, the last reverse constant (point prior to first one
     *in this miniseed record and correct the difference if it does not match up.  This
     *allows us to fix differences that are due to separate compression runs that are
     *contiguous (the 1st difference in any compression one is to zero not to the correct value)
     */
    private boolean fixit(int rev, byte[] buf) {
        //boolean dbg=false;
        ByteBuffer bb2 = ByteBuffer.wrap(buf);
        bb2.position(64);
        int keys = bb2.getInt();         // Get "nibbles" or key word
        int forward = bb2.getInt();    // forwrad interation is here by convention
        int reverse = bb2.getInt();      // reverse integration is here by convention
        //if(reverse == 0) return true;
        int diffwork = bb2.getInt();   // This is the first differencing word in block
        int type = keys >> 24 & 3;     // put the 2 bit nibble for 1st working word at bottom
        int nbits = -1;                 // set the # of bits in the difference to "none"
        int diff = 0;                 // this will contain the actual first difference
        int dnib = 0;                   // This is the upper two bits of working word for certain "type" values
        int ishift = 2;                 // number of bits to shift iwork to put 1st data bit at top of word
        // The number of bits in the difference is a determinted by the "nibble" in the key
        // for this work (the type here) and possible the "dnib" from top two bits of differences
        // word.  Figure out how many bits from these two data and how much to left shift the working
        // word to put the first difference sign bit in the 32 bit word sign bit.
        switch (type) {
            case 0:

				logger.fine("non data!");    // This should never happen

                break;
            case 1:     // 4 one byte differences
                nbits = 8;
                ishift = 0;
                break;
            case 2:
                dnib = diffwork >> 30 & 3;
                ishift = 2;
                switch (dnib) {
                    case 1:
                        nbits = 30;     // 1 30 bit diff
                        break;
                    case 2:
                        nbits = 15;     // 2 15 bit diff
                        break;
                    case 3:
                        nbits = 10;     // 3 10 bit diff
                        break;
                }
                break;
            case 3:
                dnib = diffwork >> 30 & 3;
                switch (dnib) {
                    case 0:
                        ishift = 2;
                        nbits = 6;    // 5 samps at 6 bits
                        break;
                    case 1:
                        ishift = 2;
                        nbits = 5;   // 6 samps at 5 bits
                        break;
                    case 2:
                        ishift = 4;
                        nbits = 4;  // 7 samps at 4  bits - note need toshift by 4!
                        break;

                }
                break;
        }
        int diffs = diffwork;                     // put working word in diff
        if (ishift != 0) {
            diffs = diffs << ishift;  // put differences at top of word (for sign extend)
        }
        diff = diffs >> (32 - nbits);                 // shift to bottom of word (sign extended)

        // check to see if everything agrees.
        if (forward - diff == rev) // if all is good fwd-diff=reverse
        {

			logger.fine("Its ok as is");

            return true;
        }
        boolean dbg2 = !(forward - diff == 0);
        //if( forward - diff == 0) {               // If this is the compression run case they should be equal
        int newdiff = forward - rev;
        int max = 1 << (nbits - 1);
        String msg = null;
        if (dbg || dbg2) {
            msg = "fwd=" + forward + " diff=" + diff + "=" + (forward - diff) + " rev=" + rev + " newdiff=" + newdiff + " max=" + max;
        }
        if (newdiff < -max || newdiff > max - 1) {

			logger.finer("     key=" + toHex(keys) + " wk=" + toHex(diffwork) + " diffs=" + toHex(diffs) +
					" diff=" + toHex(diff) + " nb=" + nbits + " ty=" + type);


			logger.finer("***** its out of range! " + msg + " not zero=" + dbg2);

            return false;
        }
        int mask = 0;     // remaining difference mask
        int mask2 = 0;      // new difference mask
        int newdiffwork = 0;
        if (type >= 2) {   // is it a dnib case yes.
            for (int i = 0; i < 30 - nbits; i++) {
                mask = (mask << 1) | 1;
            }
            for (int i = 0; i < nbits; i++) {
                mask2 = (mask2 << 1) | 1;
            }
            mask2 = mask2 << 30 - nbits;
            // recreatate the diffwork as the dbnib (31-30), new diff 29-29-nbits, remainder bits
            newdiffwork = (dnib << 30) | (((forward - rev) << 30 - nbits) & mask2) | (mask & diffwork);
        } else {// No dnib
            for (int i = 0; i < 32 - nbits; i++) {
                mask = (mask << 1) | 1;
            }
            newdiffwork = (forward - rev) << 32 - nbits | (mask & diffwork);

        }
        bb2.position(76);
        bb2.putInt(newdiffwork);

		logger.finer("     key=" + toHex(keys) + " wk=" + toHex(diffwork) +
				" newwk=" + toHex(newdiffwork) + " diffs=" + toHex(diffs) +
				" diff=" + toHex(diff) + " nb=" + nbits + " ty=" + type);


		logger.fine("* Its fixed! " + msg);

        return true;
        /*}
        else {
        if(dbg) System.out.println("****cannot fix it!");
        return false;
        }*/
    }

    /** write out the current block (add it to the list in outblks) and fill in needed
     * header data for next one, call with
     * frameCount=0 to cause just the setup to be taken.  I All of the setup variables are reset
     * after a call.
     *@param ms The miniseed block which will start off the next block (the one too long to put
     * in current block)
     */
    public void writeBlock(MiniSeed ms) {
        // Set the current version of blockette 1000, 1001
        if (frameCount > 0) {
            if (b1000 == null) {
                logger.warning("IMpossible b1000 is null");
            }
            b1000.setRecordLength(4096);
            if (b1000.getNextOffset() != 56) {
                b1000.setNextOffset(56);    // some miniseed has 1000 but no 1001, add its offset
            }
            System.arraycopy(b1000.getBytes(), 0, dummy, 48, 8);
            b1001.setTimingQuality((byte) ((minClockQual > 100) ? 0 : minClockQual));
            b1001.setUSecs((Math.abs(usecs) > 50 ? 0 : usecs));
            b1001.setFrameCount(frameCount);
            System.arraycopy(b1001.getBytes(), 0, dummy, 56, 8);
            // Need to set start time based on first start time
            GregorianCalendar g = new GregorianCalendar();

            // put sequence number in record
            if (df6 == null) {
                df6 = new DecimalFormat("######");
            }
            String seq = df6.format(sequence++);
            seq = seq.replaceAll(" ", "0");
            bb.position(0);
            bb.put(seq.getBytes());

            // Put the time in the record
            bb.position(20);
            bb.put(rawTime);

            // Put the number of samples
            bb.position(30);

            bb.putShort((short) nsamp);
            bb.putShort(rateFactor);
            bb.putShort(rateMult);
            // put out the activity flags, IO/Clock flags, data quality
            bb.position(36);
            bb.put((byte) activityFlags);
            bb.put((byte) clockFlags);
            bb.put((byte) dataQual);
            bb.put((byte) 2);           // # of following blockettes (1000, 1001)
            // Write out the completed block
            boolean steimError = false;
            boolean badSeedname = false;
            MiniSeed ms2 = null;
            try {
                ms2 = new MiniSeed(dummy);
                int reverse = 0;
                int[] samples = null;
                System.arraycopy(dummy, 64, frames, 0, 4096 - 64);

                if (ms2.getEncoding() == 10) {
                    samples = Steim1.decode(frames, ms2.getNsamp(), ms2.isSwapBytes(), reverse);
                }
                if (ms2.getEncoding() == 11) {
                    samples = Steim2.decode(frames, ms2.getNsamp(), ms2.isSwapBytes(), reverse);
                }
            } catch (IllegalSeednameException e) {
                logger.severe("**** final check block gave badd seedname exception e=" + e.getMessage());
                badSeedname = true;
            } catch (SteimException e) {
                steimError = true;
                logger.severe("**** final check block gave steim decode error. " + e.getMessage());
            }
            if (!Steim2.hadReverseError() && !Steim2.hadSampleCountError() && !steimError && !badSeedname) {
                try {
                    out.write(dummy, 0, 4096);
                    if (check) {
                        try {
                            MiniSeed ms3 = new MiniSeed(dummy);
                            outblks.add(ms3);
                            logger.info("ms=" + ms3);
                        } catch (IllegalSeednameException e) {
                            logger.severe("*** Illegal seedname in output block durring check=" + e.getMessage());
                        }
                    }
                } catch (IOException e) {
                    logger.severe("*** IOException : " + e.getMessage());
                    System.exit(1);
                }
            } else {
                logger.warning("  *** Skip block Steim=" + steimError + " rev=" + Steim2.getReverseError() +
                        " nsamperr=" + Steim2.getSampleCountError() + " badSeedname=" + badSeedname + "\n   *** Skip " + ms2);
            }
        }
        for (int i = 80; i < dummy.length; i++) {
            dummy[i] = 0;
        }
        // Initialize the parameters for a new block
        // The maxnsamp is the max # of samples to the mandatory midnight cut off
        maxnsamp = (int) ((86400000L - (ms.getGregorianCalendar().getTimeInMillis() % 86400000L) + (int) (1000 / ms.getRate() - 1)) / 1000. * ms.getRate());
        frameCount = 0;
        minClockQual = 10000;
        activityFlags = 0;
        clockFlags = 0;
        dataQual = 0;
        nsamp = 0;
        usecs = 2000000;
        expected = ms.getGregorianCalendar().getTimeInMillis();
        rawTime = ms.getRawTimeBuf();

        //b1000 = null;
        //b1001 = null;
    }

    /** implement compression handler
     * @param b Buffer with bytes to put out
     * @param size Length of b with valid compressed data
     */
    public void putbuf(byte[] b, int size) {
        System.arraycopy(b, 0, outputBytes, 0, size);
    }

    public void close() {
    }

    /** This class creates a list of contiguous blocks.  A block can be added to it
     *and will be rejected if it is not contiguouse at the end.  The user just attempts
     *to add the next data block in time to each of the known runs, and creates a new run
     *with the block when none of the existing ones accepts it
     */
    class Run implements Comparable<Run> {

        ArrayList<MiniSeed> blks;     // List of sequenctial contiuous Mini-seed blocks
        GregorianCalendar start;      // start time of this run
        GregorianCalendar end;        // current ending time of this run (expected time of next block)

        /** return the start time of the run
         *@return the start time as GregorianCalendar*/
        public GregorianCalendar getStart() {
            return start;
        }

        /** return the end time of the run (Actually the time of the next expected sample)
         *@return the end time as GregorianCalendar*/
        public GregorianCalendar getEnd() {
            return end;
        }

        /** return duration of run in seconds
         *@return The duration of run in seconds*/
        public double getLength() {
            return (end.getTimeInMillis() - start.getTimeInMillis()) / 1000.;
        }


        /** string representation
         *@return a String representation of this run */
        @Override
        public String toString() {
            return "Run from " + dtFormat.print(start.getTimeInMillis()) + " to " +
                    dtFormat.print(end.getTimeInMillis()) + " " + getLength() + " s #blks=" + blks.size();
        }


        /** return the ith miniseed block
         *@param Index of desired Mini-seed block
         *@return the Miniseed block */
        public MiniSeed getMS(int i) {
            return blks.get(i);
        }

        /** retun length of miniseed list for this run
         *@return The length of the miniseed list for this run */
        public int getNBlocks() {
            return blks.size();
        }

        /** clear the list (used mainly to free up associated memory)*/
        public void clear() {
            blks.clear();
            start = null;
            end = null;
        }

        /** implement Comparable
         *@param the Run to compare this to
         *@return -1 if <, 0 if =, 1 if >than */
        public int compareTo(Run r) {
            return start.compareTo(((Run) r).getStart());
        }

        /** create a new run with the given miniseed as initial block
         *@param ms The miniseed block to first include */
        public Run(MiniSeed ms) {
            start = ms.getGregorianCalendar();
            blks = new ArrayList<MiniSeed>(1000);
            blks.add(ms);
            end = ms.getGregorianCalendar();
            end.setTimeInMillis(end.getTimeInMillis() + ((long) (ms.getNsamp() / ms.getRate() * 1000 + 0.49)));

        }

        /** see if this miniseed block will add contiguously to the end of this run
         *@param the miniseed block to consider for contiguousnexx, add it if is is
         *@return true, if block was contiguous and was added to this run, false otherwise*/
        public boolean add(MiniSeed ms) {

            // Is the beginning of this one near the end of the last one!
            if (Math.abs(ms.getGregorianCalendar().getTimeInMillis() - end.getTimeInMillis()) <
                    500. / ms.getRate()) {
                // add this block to the list
                blks.add(ms);
                end = ms.getGregorianCalendar();
                end.setTimeInMillis(end.getTimeInMillis() +
                        ((long) (ms.getNsamp() / ms.getRate() * 1000 + 0.49)));
                return true;
            } else {
                return false;
            }
        }
    }
}
