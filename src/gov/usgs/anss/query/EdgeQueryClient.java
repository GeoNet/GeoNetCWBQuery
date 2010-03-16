/*
 * EdgeQueryClient.java
 *
 * Created on February 9, 2006, 1:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import gov.usgs.anss.edge.*;
import gov.usgs.anss.query.EdgeQueryOptions.OutputType;
import gov.usgs.anss.query.cwb.holdings.CWBHoldingsServerImpl;
import gov.usgs.anss.query.outputter.Filename;
import gov.usgs.anss.seed.MiniSeed;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Collections;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/** This class is the main class for CWBQuery which allows the user to make queries
 * against all files on a CWB or Edge computer.  The program has two modes :
 * command line, and command file.
 *
 *In command line mode the user can specify all of the options and get data from
 *a single seedname mask.
 *
 *In command file mode some of the command line args come from the command line
 * while the seedname mask, start time and duration come from the command file
 *
 *The command line arguments are :
 * -s seedmask  Set the seedmask as a regular expression (.=anychar, [] set of matches).
 *          The 12 character seedname is NNSSSSSCCCLL N=network, S=station code, 
 *          C= channel code L=location code.
 *          The regular expression can be useful in that IMPD03.[BS]H... would return
 *          all components with network IM, the first 4 chars of station code are PD03,
 *          for all BH or SH components and any location code.
 * -b yyyy/mm/dd hh:mm:ss The time to start the query.
 * -d secs  The number of seconds of data (the duration).
 *
 * -t type Legal types are "sac", "ms", "msz", dcc512, and "dcc" where 
 *         sac = sac binary format. Data will be zero filled and start at next sample
 *              at or after the -b time with exactly the duration
 *         ms = mini-seed raw format.  The mini-seed blocks are returned in sorted order
 *              but they might overlap or be duplicated.
 *         msz = mini-seed but zero-filled and recompressed.  No blk 1000, 1001 are 
 *              preserved.  Data will start at sample follow -b time and there will be
 *              a full duration of the data.  If -msb is specified, use that block length.
 *        dcc = blocks processed to 4096 to best effort Mini-seed eliminating overlaps, etc.
 *        dcc512 = blocks processed to 512 best effort Mini-seed eliminating overlaps, etc.
 * -f filename Use the file command mode.  The list of -s -b -d are in the file one per line
 * -h host    The host of the server computer as a name or dotted IP address
 * -p port    The port on which the service is running
 * -msb  blocksize Set the blocksize for msz output
 *-dbg Turn on the debug flag
 *
 * @author davidketchum
 * TODO: consider prepending (e.g.) - if (logger.getLevel().intValue() <= Level.FINEST.intValue()) - to low level logger statements with concatenated toString parameter(s).
 */
public class EdgeQueryClient {

    static DecimalFormat df2;
    static DecimalFormat df4;
    static DecimalFormat df6;
    private static final Logger logger = Logger.getLogger(EdgeQueryClient.class.getName());


    static {
        logger.fine("$Id$");
    }
    private static DateTimeFormatter hmsFormat = ISODateTimeFormat.time().withZone(DateTimeZone.forID("UTC"));

    /** Creates a new instance of EdgeQueryClient */
    public EdgeQueryClient() {
    }

    /** do a query.  The command line arguments are passed in as they are for the query tool
     * a files is created unless -t null is specified.  In that case the return is an ArrayList
     * containing ArrayLists<MiniSeed> for each channel returned
     *@param args The String array with args per the documentation
     *@return The ArrayList with ArrayLists of miniseed one for each channel returned.
     */
    public static ArrayList<ArrayList<MiniSeed>> query(EdgeQueryOptions options) {


        String line = "";



        long msSetup = 0;
        long msConnect = 0;
        long msTransfer = 0;
        long msOutput = 0;
        long msCommand = 0;
        long startTime = System.currentTimeMillis();
        long startPhase = startTime;

        byte[] b = new byte[4096];
        Outputer out = null;
        if (df6 == null) {
            df6 = new DecimalFormat("000000");
        }
        GregorianCalendar jan_01_2007 = new GregorianCalendar(2007, 0, 1);

        ArrayList<ArrayList<MiniSeed>> blksAll = null;
        String filename = "";
        BufferedReader infile = null;

        // TODO: Push this into EdgeQueryOptions in favour of a command line iterator.
        try {
            infile = new BufferedReader(options.getAsReader());
        } catch (FileNotFoundException ex) {
            logger.severe("did not find the input file=" + options.filenamein);
        }

        // the "in" BufferedReader will give us the command lines we need for the other end
        try {
            // for each line of input, read it, reformat it with single quotes, send to server
            int nline = 0;
            int totblks = 0;
            // particularly for the DCC we want this program to not error out if we cannot connect to the server
            // So make sure we can connect and print messages
            Socket ds = null;
            while (ds == null) {
                try {
                    ds = new Socket(options.host, options.port);
                } catch (IOException e) {
                    ds = null;
                    if (e != null) {
                        if (e.getMessage() != null) {
                            if (e.getMessage().indexOf("Connection refused") >= 0) {
                                logger.warning("Got a connection refused. " + options.host + "/" + options.port + "  Is the server up?  Wait 20 and try again");
                            }
                        } else {
                            logger.warning("Got IOError opening socket to server e=" + e);
                        }
                    } else {
                        logger.warning("Got IOError opening socket to server e=" + e);
                    }
                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException ex) {
                        // This isn't necessarily a major issue, and for the purposes
                        // of sleep, we really don't care.
                        logger.log(Level.FINE, "sleep interrupted.", ex);
                    }
                }
            }
            InputStream in = ds.getInputStream();        // Get input and output streams
            OutputStream outtcp = ds.getOutputStream();
            msConnect += (System.currentTimeMillis() - startPhase);
            startPhase = System.currentTimeMillis();
            while ((line = infile.readLine()) != null) {
                if (line.length() < 2) {
                    continue;
                }
                nline++;

                options = new EdgeQueryOptions(line);
                if (!options.isValid()) {
                    logger.severe("Error @line " + nline);
                    return null;
                }

                out = options.getOutputter();
                if (out == null) {
                    blksAll = new ArrayList<ArrayList<MiniSeed>>(20);
                } else if (out instanceof SacOutputer) {
					((SacOutputer) out).doQuery();
					continue;
				}

                // The length at which our compare for changes depends on the output file mask
                Comparator nsclComparator = options.getNsclComparator();

                long maxTime = 0;
                int ndups = 0;
                line = options.getSingleQuotedCommand();
                try {
                    msSetup += (System.currentTimeMillis() - startPhase);
                    startPhase = System.currentTimeMillis();
                    boolean perfStart = true;
                    outtcp.write(line.getBytes());
                    int iblk = 0;
                    NSCL nscl = null;
                    boolean eof = false;
                    MiniSeed ms = null;
                    int npur = 0;
                    ArrayList<MiniSeed> blks = new ArrayList<MiniSeed>(100);

                    while (!eof) {
                        try {
                            // Try to read a mini-seed, if it failes mark eof
                            if (read(in, b, 0, (options.gapsonly ? 64 : 512))) {
                                
                                if (b[0] == '<' && b[1] == 'E' && b[2] == 'O' && b[3] == 'R' && b[4] == '>') {
                                    eof = true;
                                    ms = null;

                                    logger.fine("EOR found");

                                } else {
                                    ms = new MiniSeed(b);
                                    logger.finest("" + ms);
                                    if (!options.gapsonly && ms.getBlockSize() != 512) {
                                        read(in, b, 512, ms.getBlockSize() - 512);
                                        ms = new MiniSeed(b);
                                    }
                                    iblk++;
                                    totblks++;
                                }
                            } else {
                                eof = true;         // still need to process this last channel THIS SHOULD NEVER  HAPPEN unless socket is lost
                                ms = null;
                                logger.warning("   *** Unexpected EOF Found");
                                if (out != null) {
                                    System.exit(1);      // error out with no file
                                }
                            }
                            if (perfStart) {
                                msCommand += (System.currentTimeMillis() - startPhase);
                                startPhase = System.currentTimeMillis();
                                perfStart = false;

                            }
                            logger.finest(iblk + " " + ms);
                            if (!options.quiet && iblk % 1000 == 0 && iblk > 0) {
                                // This is a user-feedback counter.
                                System.out.print("\r            \r" + iblk + "...");
                            }

                            if (eof || (nscl != null &&
                                    (ms == null ? true : nsclComparator.compare(nscl, NSCL.stringToNSCL(ms.getSeedName())) != 0))) {
                                msTransfer += (System.currentTimeMillis() - startPhase);
                                startPhase = System.currentTimeMillis();
                                if (!options.quiet) {
                                    // TODO could go into a helper method
                                    int nsgot = 0;
                                    if (blks.size() > 0) {
                                        Collections.sort(blks);
                                        logger.finer(blks.size() + " " + iblk);
                                        for (int i = 0; i < blks.size(); i++) {
                                            nsgot += (blks.get(i)).getNsamp();
                                        }
                                        logger.finest("" + (MiniSeed) blks.get(blks.size() - 1));
                                        System.out.print('\r');
                                        DateTime dt = new DateTime().withZone(DateTimeZone.forID("UTC"));


                                        logger.info(hmsFormat.print(dt.getMillis()) + " Query on " + nscl + " " +
                                                df6.format(blks.size()) + " mini-seed blks " +
                                                (blks.get(0) == null ? "Null" : ((MiniSeed) blks.get(0)).getTimeString()) + " " +
                                                (blks.get((blks.size() - 1)) == null ? "Null" : (blks.get(blks.size() - 1)).getEndTimeString()) + " " +
                                                " ns=" + nsgot);
                                    } else {
                                        System.out.print('\r');
                                        logger.info("Query on " + options.getSeedname() + " returned 0 blocks!");
                                    }


                                }

                                if (blks.size() > 0) {
                                    MiniSeed ms2 = blks.get(0);
                                    if (out == null) {     // Get the array list output
                                        ArrayList<MiniSeed> newBlks = new ArrayList<MiniSeed>(blks.size());
                                        for (int i = 0; i < blks.size(); i++) {
                                            newBlks.add(i, blks.get(i));
                                        }
                                        blksAll.add(newBlks);
                                    } else {      // create the output file
                                        if (options.getType() == OutputType.ms ||
                                                options.getType() == OutputType.dcc ||
                                                options.getType() == OutputType.dcc512 ||
                                                options.getType() == OutputType.msz) {
                                            filename = Filename.makeFilename(options.filemask, nscl, ms2);
                                        } else {
                                            filename = Filename.makeFilename(options.filemask, nscl, options.getBegin());
                                        }

                                        //filename = lastComp;
                                        // TODO - should happen in the makeFilename methods.
                                        filename = filename.replaceAll(" ", "_");

                                        logger.finest(((MiniSeed) blks.get(0)).getTimeString() + " to " +
                                                ((MiniSeed) blks.get(blks.size() - 1)).getTimeString() +
                                                " " + (((MiniSeed) blks.get(0)).getGregorianCalendar().getTimeInMillis() -
                                                ((MiniSeed) blks.get(blks.size() - 1)).getGregorianCalendar().getTimeInMillis()) / 1000L);

                                        // Due to a foul up in data in Nov, Dec 2006 it is possible the Q330s got the
                                        // same baler block twice, but the last 7 512's of the block zeroed and the other
                                        // correct.  Find these and purge the bad ones.

                                        if (!options.gapsonly) {
                                            for (int i = blks.size() - 1; i >= 0; i--) {
                                                if (blks.get(i).getBlockSize() == 4096 && // Has to be a big block or it does not happen
                                                        blks.get(i).getGregorianCalendar().compareTo(jan_01_2007) < 0 &&
                                                        blks.get(i).getUsedFrameCount() < blks.get(i).getB1001FrameCount() &&
                                                        blks.get(i).getUsedFrameCount() <= 7 && blks.get(i).getB1001FrameCount() > 7) {
                                                    blks.remove(i);
                                                    npur++;
                                                }
                                            }
                                        }
                                        logger.finer("Found " + npur + " recs with on first block of 4096 valid");
                                        blks.trimToSize();
                                        //for(int i=0; i<blks.size(); i++) logger.finest(((MiniSeed) blks.get(i)).toString());
                                        // TODO: Change the signature to pass options only once.

                                        out.makeFile(nscl, filename, blks);
                                    }
                                }
                                maxTime = 0;
                                if (blks.size() > 0) {
                                    blks.clear();
                                    System.gc();        // Lots of memory just abandoned.  Try garbage collector
                                }
                                msOutput += (System.currentTimeMillis() - startPhase);
                                startPhase = System.currentTimeMillis();
                            }

                            // If this block is the first in a new component, clear the blks array
                            //if(!lastComp.substring(0,compareLength).equals(
                            //    ms.getSeedName().substring(0,compareLength))) blks.clear();
                            /* in late 2007 there was some files which were massively duplicated by block.
                             * to prevent this from blowing memory when there are so may we eliminate and duplicate
                             * blocks here.  If it is massively out of order , all of these block checks will slow things
                             * down.
                             **/


                            boolean isDuplicate = false;
                            if (ms != null) {
                                if (ms.getTimeInMillis() <= maxTime) {    // No need to check duplicates if this is newest seen
                                    if (!options.gapsonly) {
                                        if (blks.size() >= 1) {
                                            for (int i = blks.size() - 1; i >= 0; i--) {
                                                if (ms.isDuplicate(blks.get(i))) {
                                                    isDuplicate = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (!isDuplicate && ms.getIndicator().compareTo("D ") >= 0) {
                                        blks.add(ms);
                                    } else {
                                        ndups++;
                                    }
                                } else {
                                    if (ms.getIndicator().compareTo("D ") >= 0) {
                                        blks.add(ms); // If its not D or better, its been zapped!
                                    }
                                    maxTime = ms.getTimeInMillis();
                                }
                                nscl = NSCL.stringToNSCL(ms.getSeedName());
                            }
                        } catch (IllegalSeednameException e) {
                            logger.severe("Seedname exception making a seed record e=" + e.getMessage());
                        }
                    }   // while(!eof)
                    if (!options.quiet && iblk > 0) {
                        logger.info(iblk + " Total blocks transferred in " +
                                (System.currentTimeMillis() - startTime) + " ms " +
                                (iblk * 1000L / Math.max(System.currentTimeMillis() - startTime, 1)) + " b/s " + npur + " #dups=" + ndups);
                    }
                    if (out == null) {
                        return blksAll;      // If called in no file output mode, return the blocks
                    }
                    blks.clear();
                } catch (UnknownHostException e) {
                    logger.severe("EQC main: Host is unknown=" + options.host + "/" + options.port);
                    if (out != null) {
                        System.exit(1);
                    }
                    return null;
                } catch (IOException e) {
                    if (e.getMessage().equalsIgnoreCase("Connection refused")) {
                        logger.severe("The connection was refused.  Server is likely down or is blocked. This should never happen.");
                        return null;
                    } else {
                        logger.severe(e + " EQC main: IO error opening/reading socket=" + options.host + "/" + options.port);
                        if (out != null) {
                            System.exit(1);
                        }
                    }
                }
            }       // End of readline
            outtcp.write("\n".getBytes());      // Send end of request marker to query
            if (ds.isClosed()) {
                try {
                    ds.close();
                } catch (IOException e) {
                }
            }
            if (options.perfMonitor) {
                long msEnd = System.currentTimeMillis() - startPhase;
                logger.info("Perf setup=" + msSetup + " connect=" + msConnect + " Cmd=" + msCommand + " xfr=" + msTransfer + " out=" + msOutput +
                        " last=" + msEnd + " tot=" + (msSetup + msConnect + msTransfer + msOutput + msEnd) + " #blks=" + totblks + " #lines=" + nline);
            }
            return null;
        } catch (IOException e) {
            logger.severe(e + " IOError reading input lines.");
        }
        return null;
    }

    public static boolean read(InputStream in, byte[] b, int off, int l)
            throws IOException {
        int len;
        while ((len = in.read(b, off, l)) > 0) {
            off += len;
            l -= len;
            if (l == 0) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {


        if (args.length == 0) {
            System.out.println(QueryProperties.getUsage());
            System.exit(1);
        }

        // Load a default logging properties file if none already set.
        String customLogConfigFile = System.getProperty("java.util.logging.config.file");
        if (customLogConfigFile == null) {
            // Use default logging
            try {
                InputStream configFile = ClassLoader.getSystemResourceAsStream("resources/logging.properties");
                LogManager.getLogManager().readConfiguration(configFile);
            } catch (IOException ex) {
                logger.severe("Failed to open configuration file, logging not configured.");
            }
            logger.config("Using default logging configuration.");
        } else {
            logger.config("Using custom logging config file: " + customLogConfigFile);
        }

        //TODO this any any others should get explicitly set on calendars. "UTC"?
        TimeZone tz = TimeZone.getTimeZone("GMT+0");
        TimeZone.setDefault(tz);

        logger.finest("Running Edge Query");

        EdgeQueryOptions options = new EdgeQueryOptions(args);


        CWBHoldingsServerImpl cwbServer = new CWBHoldingsServerImpl(options.host, options.port);

        // The ls option does not require any args checking
        if (options.isListQuery()) {
            logger.info(cwbServer.listChannels(options.getBegin(), options.getDuration()));
		} else {
            query(options);
        }

    }
}
