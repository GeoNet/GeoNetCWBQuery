/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import gov.usgs.anss.query.cwb.formatter.CWBQueryFormatter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nz.org.geonet.quakeml.v1_0_1.client.QuakemlFactory;
import nz.org.geonet.quakeml.v1_0_1.client.QuakemlUtils;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * An attempt to encapsulate (read isolate) EdgeQueryClient command line args.
 * TODO: move line space handling, double quoting and splitting of arguments to a separate method.
 * 
 * @author richardg
 */
public class EdgeQueryOptions {

    private static final Logger logger = Logger.getLogger(EdgeQueryOptions.class.getName());


    static {
        logger.fine("$Id$");
    }

    public enum OutputType {

        ms,
        msz,
        sac,
        dcc,
        dcc512,
        HOLD,
        text,
        NULL;
    }
    private static String beginFormat = "YYYY/MM/dd HH:mm:ss";
    private static String beginFormatDoy = "YYYY,DDD-HH:mm:ss";
    private static DateTimeFormatter parseBeginFormat = DateTimeFormat.forPattern(beginFormat).withZone(DateTimeZone.forID("UTC"));
    private static DateTimeFormatter parseBeginFormatDoy = DateTimeFormat.forPattern(beginFormatDoy).withZone(DateTimeZone.forID("UTC"));
    public String host = QueryProperties.getGeoNetCwbIP();

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }
    public int port = QueryProperties.getGeoNetCwbPort();
    public String[] args;
    public List<String> extraArgs;
    private Double duration = 300.0;
    private String seedname = null;
    private DateTime begin = null;
    private OutputType type = OutputType.sac;
    public boolean dbg = false;
    public boolean lsoption = false;
    public boolean lschannels = false;
    public int julian = 0;
    public String filenamein = null;
    public int blocksize = 512;        // only used for msz type
    public String filemask = "%N";
    public boolean quiet = false;
    public boolean gapsonly = false;
    // Make a pass for the command line args for either mode!
    public String exclude = null;
    public boolean nosort = false;
    public boolean holdingMode = false;
    public String holdingIP = QueryProperties.getGeoNetCwbIP();
    public int holdingPort = QueryProperties.getGeoNetCwbPort();
    public String holdingType = "CWB";
    public boolean showIllegals = false;
    public boolean perfMonitor = false;
    public boolean chkDups = false;
    public boolean sacpz = false;
    public String pzunit = "nm";
    private Quakeml event = null;
    private ReadableDuration offset = null;

    /**
     * Parses known args into object fields. Does some argument validation and
     * potentially System.exit(0).
     * TODO: move any/all validation to a validateArgs method.
     * TODO: return a String array of unused/unparsed args to be used for
     * outputter customisation.
     * @param args the arguments to parse
     * @return unused args (unmodified order)
     */
    public List parse(String[] args) {

//		List<String> argList = Arrays.asList(args);
        ArrayList<String> argList = new ArrayList(Arrays.asList(args));
        int pos = argList.indexOf("-f");
        if (pos != -1) {
            argList.remove(pos);
            filenamein = argList.remove(pos);
            quiet = argList.remove("-q");
            dbg = argList.remove("-dbg");

            return argList;
        }

        ArrayList<String> extraArgsList = new ArrayList(args.length);

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-f")) {  // Documented functionality.
                filenamein = args[i + 1];
                i++;
            } else if (args[i].equals("-t")) {  // Documented functionality.
                setType(args[i + 1]);
                i++;
            } else if (args[i].equals("-msb")) {   // Documented functionality.
                blocksize = Integer.parseInt(args[i + 1]);
                i++;
            } else if (args[i].equals("-o")) { // Documented functionality.
                filemask = args[i + 1];
                i++;
            } else if (args[i].equals("-e")) {
                exclude = "exclude.txt";
            } else if (args[i].equals("-el")) {
                exclude = args[i + 1];
                i++;
            } else if (args[i].equals("-ls")) { // Documented functionality.
                lsoption = true;
            } else if (args[i].equals("-lsc")) { // Documented functionality.
                lschannels = true;
            } else if (args[i].equals("-b")) { // Documented functionality.
                setBegin(args[i + 1]);
                i++;
            } else if (args[i].equals("-s")) { // Documented functionality.
                setSeedname(args[i + 1]);
                i++;
            } else if (args[i].equals("-d")) { // Documented functionality.
                setDuration(args[i + 1]);
                i++;
            } else if (args[i].equals("-q")) { // Documented functionality.
                quiet = true;
            } else if (args[i].equals("-nosort")) { // Documented functionality.
                nosort = true;
            } else if (args[i].equals("-nogaps")); // legal for sac and zero MS
            else if (args[i].equals("-nodups")) {
                chkDups = true;
            } else if (args[i].equals("-sactrim")); // legal for sac and zero MS
            else if (args[i].equals("-gaps")) {
                gapsonly = true;     // legal for zero MS
            } else if (args[i].equals("-msgaps")); // legal for zero ms
            else if (args[i].equals("-udphold")) {
                gapsonly = true;  // legal for zero MS
            } else if (args[i].equals("-chk")); // valid only for -t dcc
            else if (args[i].equals("-dccdbg")); // valid only for -t dcc & -t dcc512
            else if (args[i].equals("-perf")) {
                perfMonitor = true;
            } else if (args[i].equals("-nometa")); else if (args[i].equals("-fill")) {
                i++;
            } else if (args[i].equals("-sacpz")) {
                sacpz = true;
                pzunit = args[i + 1];
                i++;
            } else if (args[i].equals("-si")) {
                showIllegals = true;
            } else if (args[i].indexOf("-hold") == 0) {
                holdingMode = true;
                gapsonly = true;
                setType(OutputType.HOLD);
                String[] a = args[i].split(":");
                if (a.length == 4) {
                    holdingIP = a[1];
                    holdingPort = Integer.parseInt(a[2]);
                    holdingType = a[3];
                }
                logger.config("Holdings server=" + holdingIP + "/" + holdingPort + " type=" + holdingType);
            } else if (args[i].equals("-event")) {
                setEvent(args[i + 1]);
                i++;
            } else if (args[i].equals("-offset")) {
                setOffset(Double.parseDouble(args[i + 1]));
                i++;
            } else {
                logger.info("Unknown CWB Query argument=" + args[i]);
                extraArgsList.add(args[i]);
            }

        }
        return extraArgsList;
    }

    /**
     * Return true if the arguments specified a batch file.
     * @return
     */
    public boolean isFileMode() {
        return filenamein != null;
    }

    /**
     * Return true if a list query -ls or -lsc was defined.
     * @return
     */
    public boolean isListQuery() {
        return (lsoption || lschannels);
    }

    /**
     * Validate parsed args. This should (initially at least) mimic the dodgy
     * args validation of the current client.
     * TODO: clean up later for context driven help???
     * @return boolean representing whether the args are valid
     */
    public boolean isValid() {
        if (isFileMode()) {
            return (extraArgs.isEmpty());
        }

        if (isListQuery()) {
            // No args checking done here.
            return true;
        }

        if (blocksize != 512 && blocksize != 4096) {
            logger.severe("-msb must be 512 or 4096 and is only meaningful for msz type");
            return false;
        }

        if (getBegin() == null) {
            logger.severe("You must enter a beginning time");
            return false;
        }

        if (getSeedname() == null) {
            logger.severe("-s SCNL is not optional, you must specify a seedname.");
            return false;
        }

        if (sacpz) {
            if (!pzunit.equalsIgnoreCase("nm") && !pzunit.equalsIgnoreCase("um")) {
                logger.warning("   ****** -sacpz units must be either um or nm switch values is " + pzunit);
                return false;
            }
        }

        if (getNsclComparator() == NSCL.NetworkComparator || getNsclComparator() == NSCL.StationComparator) {
            if (getType() == OutputType.sac) {
                logger.severe("\n    ***** Sac files must have names including the channel! *****");
                return false;
            }
            if (getType() == OutputType.msz) {
                logger.severe("\n    ***** msz files must have names including the channel! *****");
                return false;
            }
        }

        // TODO more checking to come.
        return true;
    }

    public Outputer getOutputter() {
        switch (getType()) {
            case ms:
                return new MSOutputer(this);
            case sac:
                return new SacOutputer(this);
            case msz:
                return new MSZOutputer(this);
            case dcc:
                return new DCCOutputer(this);
            case dcc512:
                return new DCC512Outputer(this);
            case HOLD:
                return new HoldingOutputer(this);
            case text:
                return new TextOutputer(this);
        }
        return null;
    }

    public Comparator getNsclComparator() {
        // default to LocationComparator
        Comparator comparator = NSCL.LocationComparator;
        if (filemask.indexOf("%n") >= 0) {
            comparator = NSCL.NetworkComparator;
        }
        if (filemask.indexOf("%s") >= 0) {
            comparator = NSCL.StationComparator;
        }
        if (filemask.indexOf("%c") >= 0) {
            comparator = NSCL.ChannelComparator;
        }
        if (filemask.indexOf("%N") >= 0) {
            comparator = NSCL.LocationComparator;
        }
        if (filemask.indexOf("%N") >= 0) {
            comparator = NSCL.LocationComparator;
        }

        return comparator;
    }

    /**
     * Generate some (context?) appropriate help text.
     * @return the help string.
     */
    public String getHelp() {
        return QueryProperties.getUsage();
    }

    /**
     * Generate some (context?) appropriate usage text.
     * @return the usage string.
     */
    public String getUsage() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * A default constructor.
     */
    public EdgeQueryOptions() {
    }

    /**
     * Creates an EdgeQueryOptions object from a set of command line args.
     * @param args
     */
    public EdgeQueryOptions(String[] args) {
        this.args = args;
        this.extraArgs = parse(this.args);
    }

    /**
     * Creates an EdgeQueryOptions object from a single command line string.
     * TODO: Attempt to understand and sanitise this method.
     * @param line
     */
    public EdgeQueryOptions(String line) {
        boolean on = false;

        // Spaces and quoting...?
        char[] linechar = line.toCharArray();
        for (int i = 0; i < line.length(); i++) {
            if (linechar[i] == '"') {
                on = !on;
            } else if (linechar[i] == ' ') {
                if (on) {
                    linechar[i] = '@';
                }
            }
        }

        line = new String(linechar);
        line = line.replaceAll("\"", " ");
        line = line.replaceAll("  ", " ");
        line = line.replaceAll("  ", " ");
        line = line.replaceAll("  ", " ");

        this.args = line.split(" ");
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].replaceAll("@", " ");
        }
        this.extraArgs = parse(this.args);
    }

    /**
     * Returns a FileReader if filename has been specified, or a StringReader if
     * it just contains command line args.
     * TODO: replace this with a command line iterator...?
     * @return
     * @throws FileNotFoundException
     */
    public Reader getAsReader() throws FileNotFoundException {

        // if not -f mode, read in more command line parameters for the run
        if (this.filenamein != null) {
            return new FileReader(this.filenamein);
        }

        String line = "";
        for (int i = 0; i < this.args.length; i++) {
            line += this.args[i].replaceAll(" ", "@") + " ";
        }
        return new StringReader(line);

    }

    /**
     * Puts the command line args in single quotes, to be sent to the server.
     * TODO: This should be constructed from the fields.
     * @return the command line args, single quoted.
     */
    public String getSingleQuotedCommand() {
        // put command line in single quotes.
        return CWBQueryFormatter.miniSEED(getBeginWithOffset(), duration, seedname);
    }

    /**
     * Parses the begin time.  This tries to match
     * the documentation for CWBClient but does not
     * match the Util.stringToDate2 method which attempted
     * to allow for milliseconds.
     *
     * @param beginTime
     * @return java.util.Date parsed from the being time.
     * @throws java.lang.IllegalArgumentException
     */
    public void setBegin(String beginTime) throws IllegalArgumentException {
        begin = null;

        try {
            begin = parseBeginFormat.parseDateTime(beginTime);
        } catch (Exception e) {
        }

        if (begin == null) {
            try {
                begin = parseBeginFormatDoy.parseDateTime(beginTime);
            } catch (Exception e) {
            }
        }

        // TODO Would be ideal if this error contained any range errors from
        // parseDateTime but this is hard with the two attempts at parsing.
        if (begin == null) {
            throw new IllegalArgumentException("Error parsing begin time.  Allowable formats " +
                    "are: " + beginFormat + " or " + beginFormatDoy);
        }
    }

    /**
     * @return the duration
     */
    public Double getDuration() {
        return duration;
    }

    /**
     * Sets the duration from a String. By default this is interpreted as seconds
     * but the user can append 'd' or 'D' to the number to represent days.
     * @param duration the duration string to set
     */
    private void setDuration(String durationString) {
        if (durationString != null) {
            if (durationString.endsWith("d") || durationString.endsWith("D")) {
                setDuration(Double.parseDouble(durationString.substring(0, durationString.length() - 1)) * 86400.);
            } else {
                setDuration(Double.parseDouble(durationString));
            }
        }
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(Double duration) {
        this.duration = duration;
    }

    /**
     * @return the seedname
     */
    public String getSeedname() {
        if (seedname == null) {
			if (getEvent() != null) {
				List<NSCL> nscls = QuakeMLQuery.getPhases(getEvent());
				if (!nscls.isEmpty()) {
					StringBuffer sb = new StringBuffer(nscls.size() * 13);
					for (NSCL nscl : nscls) {
						sb.append(nscl.toString()).append('|');
					}
					sb.setLength(sb.length() - 1);
					return sb.toString();
				}
			}
		}
		return seedname;
    }

    /**
     * @param seedname the seedname to set
     */
    public void setSeedname(String seedname) {
        // Append wildcards to end of seedname
        if (seedname != null && seedname.length() < 12) {
            this.seedname = (seedname + ".............").substring(0, 12);
        } else {
            this.seedname = seedname;
        }
    }

    /**
     * @return the type
     */
    public OutputType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(OutputType type) {
        this.type = type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = OutputType.valueOf(type);
    }

    /**
     * @return the offset
     */
    public ReadableDuration getOffset() {
        return offset;
    }

    /**
     * @param seconds the offset to set
     */
    public void setOffset(double seconds) {
        this.offset = new Duration((long) (seconds * 1000));
    }

    /**
     * @return the begin
     */
    public DateTime getBegin() {

        DateTime quakeMLBegin = null;

        if (begin != null) {
            return begin;
		} else if (getEvent() != null) {
			quakeMLBegin = QuakemlUtils.getOriginTime(QuakemlUtils.getPreferredOrigin(QuakemlUtils.getFirstEvent(event)));
        }

        return quakeMLBegin;
    }

    /**
     * @return the begin
     */
    public Date getBeginAsDate() {
        return getBegin().toDate();
    }

    /**
     * @return the begin
     */
    public String getBeginAsString() {
        return parseBeginFormat.withZone(DateTimeZone.UTC).print(getBegin());
    }

    /**
     * @return the begin
     */
    public DateTime getBeginWithOffset() {
        return getBegin().plus(getOffset());
    }

    /**
     * @return the begin
     */
    public Date getBeginWithOffsetAsDate() {
        return getBeginWithOffset().toDate();
    }

    /**
     * @return the begin
     */
    public String getBeginWithOffsetAsString() {
        return parseBeginFormat.withZone(DateTimeZone.UTC).print(getBeginWithOffset());
    }

    /**
     * @param begin the begin to set
     */
    public void setBegin(DateTime begin) {
        this.begin = begin;
    }

    /**
     * @return the event
     */
    public Quakeml getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(Quakeml event) {
        this.event = event;
    }

    /**
     * TODO: throw an exception if the event can't be found...?
     * @param event the GeoNet public ID, or fully qualified http or file URI of
     * an event to use.
     */
    public void setEvent(String event) {
        String quakeMlSchemaUrl = null;
		URI uri = null;
        try {
            uri = new URI(event);
        } catch (URISyntaxException ex) {
            Logger.getLogger(EdgeQueryOptions.class.getName()).log(
                    Level.INFO,
                    "Event parameter was not opaque URI, assuming it's GeoNet ID.",
                    ex);
        }

        if (uri != null) {
            if (uri.isAbsolute()) {
                if (uri.getScheme().startsWith("http")) {
                    String username = uri.getUserInfo();
                    String password = null;
                    if (username != null) {
                        int split = username.indexOf(":");
                        if (split != -1) {
                            password = username.substring(split);
                            username = username.substring(0, split);
                        }
                    }
                    this.event = new QuakemlFactory().getQuakeml(
                            event,
							quakeMlSchemaUrl,
                            username,
                            password);
                } else if (uri.getScheme().equals("file")) {
                    try {
                        this.event = new QuakemlFactory().getQuakeml(
								new FileInputStream(new File(uri)),
								quakeMlSchemaUrl);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(EdgeQueryOptions.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(EdgeQueryOptions.class.getName()).log(Level.SEVERE, null, ex);
					}
                } else {
					String quakeMlUri = null;
					try{
						quakeMlUri = QueryProperties.getQuakeMlUri(uri.getScheme());
					} catch (MissingResourceException ex) {
						logger.warning("Couldn't find quakeML URI pattern for " + uri.getScheme());
						// TODO: list available patterns?
						logger.info("Known QuakeML authorities are: " +
								QueryProperties.getQuakeMlAuthorities().toString());
					}

					if (quakeMlUri != null) {
						// Assume it's a flagged authority's public ID.
						Matcher quakeMlUriMatcher = Pattern.compile("%ref%").matcher(quakeMlUri);
						this.event = new QuakemlFactory().getQuakeml(
								quakeMlUriMatcher.replaceAll(uri.getSchemeSpecificPart()),
								quakeMlSchemaUrl, null, null);
					}
				}
            }
        }

        if (this.event == null) {
            logger.severe("failed to retrieve details for event string " + event);
        }
    }
}
