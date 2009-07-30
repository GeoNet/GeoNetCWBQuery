/*
 * ZeroFilledSpan.java
 *
 * Created on January 30, 2006, 3:32 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package gov.usgs.anss.query;

import edu.iris.Fissures.codec.*;
import gov.usgs.anss.seed.MiniSeed;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Collections;
import java.util.logging.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/** This class represent a time series chunk which is zero filled if data is
 * missing.  The idea is to allow creation, population by a series of blocks
 * which may not be contiguous or even in order.  Constructors needs to deal
 * with different data types and construction methods.
 *
 *Initially, this calss assumed it would be created from a list of mini-seed
 * blocks.  However, it is quite likely it will need to be extended to allow
 * a pre-allocation followed by many calls adding data to the timeseries.
 *
 * @author davidketchum
 */
public class ZeroFilledSpan {

    int nsamp;
    int fillValue;
    GregorianCalendar start;
    int[] data;                // The data array of nsamp samples
    double rate = 0.;
    boolean dbg = false;
    String missingSummary;
    protected static final Logger logger = Logger.getLogger(ZeroFilledSpan.class.getName());


    static {logger.fine("$Id$");}
   
    private static DateTimeFormatter dtFormat = ISODateTimeFormat.dateTime().withZone(DateTimeZone.forID("UTC"));
    private static DateTimeFormatter hmsFormat = DateTimeFormat.forPattern("hh:mm:ss.SSS").withZone(DateTimeZone.forID("UTC"));

    /** string represting this time series
     *@return a String with nsamp, rate and start date/time*/
    @Override
    public String toString() {
        getNMissingData();
        return "ZeroFilledSpan:  ns=" + nsamp + " rt=" + rate + " " +
                dtFormat.print(start.getTimeInMillis()) + " missing: " + missingSummary;
    }

    public String timeAt(int i) {
        GregorianCalendar e = new GregorianCalendar();
        e.setTimeInMillis(start.getTimeInMillis() + ((long) (i / rate * 1000. + 0.5)));
        return hmsFormat.print(e.getTimeInMillis());
    }

    public GregorianCalendar getGregorianCalendarAt(int i) {
        GregorianCalendar e = new GregorianCalendar();
        e.setTimeInMillis(start.getTimeInMillis() + ((long) (i / rate * 1000. + 0.5)));
        return e;
    }

    /** get the digitizing rate in Hz
     *@return The digitizing rate in Hz.*/
    public double getRate() {
        return rate;
    }

    /**
     * This setter is only to allow for unit testing timeAt(int i)
     * properly.
     *
     * @param r digitizing rate in HZ
     */
    protected void setRate(double r) {
        this.rate = r;
    }

    public int getData(GregorianCalendar starting, int nsamp, int[] d) {
        long msoff = starting.getTimeInMillis() - start.getTimeInMillis();
        int offset = (int) ((msoff + 1. / rate * 1000 / 2. - 1.) / 1000. * rate);
        if (offset < 0) {
            return -1;
        }

        logger.fine("getData starting =" + hmsFormat.print(starting.getTimeInMillis()) + " buf start=" + hmsFormat.print(start.getTimeInMillis()) + " offset=" + offset);

        if (nsamp + offset > data.length) {
            nsamp = data.length - offset;
        }
        System.arraycopy(data, offset, d, 0, nsamp);
        return nsamp;

    }

    /** get the time series as an arry of ints
     *@return The timeseries as an array of ints*/
    public int[] getData() {
        return data;
    }

    /** get the ith time series value
     *@param i THe index (starting with zero) of the data point to return.
     *@return The ith timeseries value*/
    public int getData(int i) {
        return data[i];
    }

    /** get a chunk of the data into an array,
     *@param d The array to put the data in
     *@param off The offset in the internal data buffer to start
     *@param len The maximum length of data to return (d must be dimensioned >len)
     *@return The number of samples actually returned <=len
     */
    public int getData(int[] d, int off, int len) {
        int n = len;
        if ((nsamp - off) < len) {
            n = nsamp - off;
        }
        System.arraycopy(data, off, d, 0, n);
        return n;
    }

    /** get number of data samples in timeseries (many might be zeros)
     *@return Number of samples in series */
    public int getNsamp() {
        return nsamp;
    }

    /** return start time as a GregorianCalendar
     *@return The start time*/
    public GregorianCalendar getStart() {
        return start;
    }

    /** return the max value of the time serieis
     *@return Max value of the timeseries*/
    public int getMin() {
        int min = 2147000000;
        for (int i = 0; i < nsamp; i++) {
            if (data[i] < min) {
                min = data[i];
            }
        }
        return min;
    }

    /** return the max value of the time serieis
     *@return Max value of the timeseries*/
    public int getMax() {
        int max = -2147000000;
        for (int i = 0; i < nsamp; i++) {
            if (data[i] > max) {
                max = data[i];
            }
        }
        return max;
    }

    /** return true if any portion of the allocated space has a "no data" or fill value.
     * Trim number of samples to reflect actual sample if end contains some no data points.
     * @return true if there is at least on missing data value*/
    public boolean hasGapsBeforeEnd() {
        int ns = nsamp;
        if (data[0] == fillValue) {
            return true;     // opening with fill
        }
        int i = 0;
        for (i = nsamp - 1; i >= 0; i--) {
            if (data[i] != fillValue) {
                ns = i + 1;
                break;
            }
        }
        if (i <= 0 && ns == nsamp) {
            return false;      // no fill Values found looking for last one!
        }
        for (i = 0; i < ns; i++) {
            if (data[i] == fillValue) {
                return true;
            }
        }
        nsamp = ns;
        return false;
    }

    /** return true if any portion of the allocated space has a "no data" or fill value
     * @return true if there is at least on missing data value*/
    public boolean hasGaps() {
        for (int i = 0; i < nsamp; i++) {
            if (data[i] == fillValue) {
                return true;
            }
        }
        return false;
    }

    /** return number of missing data points *
     * @return Number of missing data points
     */
    public int getNMissingData() {
        int noval = 0;
        int first = -1;
        int last = nsamp + 1;
        for (int i = 0; i < nsamp; i++) {
            if (data[i] == fillValue) {
                noval++;
                last = i;
                if (noval == 1) {
                    first = i;
                }
            }
        }
        if (first >= 0) {
            missingSummary = "First at " + first + " last at " + last + " # missing=" + noval;
        }
        return noval;
    }

    /**
     * compare to ZeroFilledSpans for "equivalence"
     * @return True if equivaleng
     * @param z Another ZeroFilledSpan to compare against.
     */
    public String differences(ZeroFilledSpan z) {
        StringBuffer sb = new StringBuffer(1000);
        StringBuffer details = new StringBuffer(1000);
        sb.append("Summary " + toString() + "\n");
        sb.append("Summary " + z.toString() + "\n");

        if (getNMissingData() != z.getNMissingData()) {
            sb.append("*** # missing different " + getNMissingData() + "!=" + z.getNMissingData() + "\n");
        }
        if (getNsamp() != z.getNsamp()) {
            sb.append("*** Nsamp different " + nsamp + " != " + z.getNsamp() + " diff = " + (nsamp - z.getNsamp()) + "\n");
        }
        int gapStart = -1;
        int gapSize = 0;
        for (int i = 0; i < Math.min(nsamp, z.getNsamp()); i++) {
            if (data[i] != z.getData(i)) {
                if (gapStart == -1) {
                    sb.append(" difference start at " + i + " " + timeAt(i));
                    gapStart = i;
                    gapSize++;
                } else {
                    gapSize++;
                }
                details.append("*** " + (i + "        ").substring(0, 8) +
                        leftPad((data[i] == fillValue ? "  nodata  " : "" + data[i]), 8) +
                        leftPad((z.getData(i) == fillValue ? "  nodata  " : "" + z.getData(i)), 8));
                if (data[i] == fillValue || z.getData(i) == fillValue) {
                    details.append("\n");
                } else {
                    details.append(leftPad("df=" + (data[i] - z.getData(i)), 14) + "\n");
                }
            } else {
                if (gapStart != -1) {
                    sb.append(" ends at " + i + " " + timeAt(i) + " # diff=" + gapSize + "\n");
                    gapStart = -1;
                    gapSize = 0;
                }
            }
        }
        if (gapStart != -1) {
            sb.append(" ends at " + nsamp + " " + timeAt(nsamp) + " # diff=" + gapSize + "\n");
        }
        return sb.toString() + "\nDetails:\n" + details.toString();
    }

    /** create a new instance of ZeroFilledSpan from a list of mini-seed blockettes
     *@param list A list containing Mini-seed objects to put in this series
     */
    public ZeroFilledSpan(ArrayList<MiniSeed> list) {
        Collections.sort(list);

        // Need to find first and last data block to calculate time span
        int j = 0;
        MiniSeed ms = (MiniSeed) list.get(j);
        while (ms.getRate() <= 0.) {
            ms = (MiniSeed) list.get(j++);
        }
        j = list.size() - 1;
        MiniSeed msend = (MiniSeed) list.get(j);
        if (ms.getRate() <= 0.) {
            msend = (MiniSeed) list.get(j--);
        }

        // calculate span and do it
        double duration = (msend.getGregorianCalendar().getTimeInMillis() -
                ms.getGregorianCalendar().getTimeInMillis()) / 1000. + msend.getNsamp() / msend.getRate();
        doZeroFilledSpan(list, ms.getGregorianCalendar(), duration, 0);
    }

    /** Creates a new instance of ZeroFilledSpan - this represents zero filled
     * time series record
     *@param list  A list containing Mini-seed objects to put in this series
     *@param trim  The start time - data before this time are discarded
     *@param duration Time in seconds that this series is to represent
     */
    public ZeroFilledSpan(ArrayList<MiniSeed> list, GregorianCalendar trim, double duration) {
        doZeroFilledSpan(list, trim, duration, 0);
    }

    /** create a new instance of ZeroFilledSpan from a list of mini-seed blockettes
     *@param list A list containing Mini-seed objects to put in this series
     *@param fill a integer to use to pre-fill the array, (the not a data value)
     */
    public ZeroFilledSpan(ArrayList<MiniSeed> list, int fill) {
        fillValue = fill;
        // Need to find first and last data block to calculate time span
        int j = 0;
        MiniSeed ms = (MiniSeed) list.get(j);
        while (ms.getRate() <= 0.) {
            ms = (MiniSeed) list.get(j++);
        }
        j = list.size() - 1;
        MiniSeed msend = (MiniSeed) list.get(j);
        if (ms.getRate() <= 0.) {
            msend = (MiniSeed) list.get(j--);
        }

        // calculate span and do it
        double duration = (msend.getGregorianCalendar().getTimeInMillis() -
                ms.getGregorianCalendar().getTimeInMillis()) / 1000. + msend.getNsamp() / msend.getRate();
        doZeroFilledSpan(list, ms.getGregorianCalendar(), duration, fill);
    }

    /** Creates a new instance of ZeroFilledSpan - this represents zero filled
     * time series record
     *@param list  A list containing Mini-seed objects to put in this series
     *@param trim  The start time - data before this time are discarded
     *@param duration Time in seconds that this series is to represent
     *@param fill a integer to use to pre-fill the array, (the not a data value)
     */
    public ZeroFilledSpan(ArrayList<MiniSeed> list, GregorianCalendar trim, double duration, int fill) {
        doZeroFilledSpan(list, trim, duration, fill);
    }

    public void refill(ArrayList<MiniSeed> list, GregorianCalendar trim, double dur, int fill) {
        int j = 0;
        MiniSeed ms = list.get(j);
        while (ms.getRate() <= 0.) {
            ms = (MiniSeed) list.get(j++);
        }
        if (ms == null) {
            return;
        }
        double duration = dur;
        if (data.length < dur * ms.getRate() + 0.01) {
            data = new int[(int) (dur * ms.getRate() + 0.01)];
        }
        doZeroFilledSpan(list, trim, duration, fill);
    }

    /** populate a zero filled span.  Called by the constructors
     *
     * @param list ArrayList of mini-seed data
     * @param trim Start time - data before this time is discarded.
     * @param duration Time in seconds to do it
     * @param fill an integer to use to pre-fill the array (the not-a-data value)
     */
    private void doZeroFilledSpan(ArrayList<MiniSeed> list, GregorianCalendar trim, double duration, int fill) {

        fillValue = fill;
        // Look through blocks until we find one that has a rate (i.e. probably data!)
        rate = 0.;
        int j = 0;
        MiniSeed ms = null;
        while (rate == 0. && j < list.size()) {
            ms = (MiniSeed) list.get(j);
            rate = ms.getRate();
            j++;
        }
        if (rate == 0.) {
            logger.info("There is no data in this span");
            nsamp = 0;
            data = new int[1];
            start = new GregorianCalendar();
            if (ms == null) {
                start.setTimeInMillis(trim.getTimeInMillis());
            } else {
                start.setTimeInMillis(ms.getGregorianCalendar().getTimeInMillis());
            }
            return;
        }
        int begoffset = (int) ((trim.getTimeInMillis() - ms.getGregorianCalendar().getTimeInMillis()) *
                rate / 1000. + 0.01);

        logger.fine(dtFormat.print(trim.getTimeInMillis()) + " start = " + dtFormat.print(ms.getGregorianCalendar().getTimeInMillis()));

        // The start time of this span is the time of first sample from first ms after
        // the trim start time
        start = new GregorianCalendar();
        start.setTimeInMillis(ms.getGregorianCalendar().getTimeInMillis());
        start.add(Calendar.MILLISECOND, (int) (begoffset / rate * 1000.));// first in trimmed interval

        logger.fine(dtFormat.print(start.getTimeInMillis()) + " begoff= " + begoffset);

        byte[] frames;
        MiniSeed msend = (MiniSeed) list.get(list.size() - 1);
        nsamp = (int) (duration * ms.getRate() + 0.5);
        //logger.finer("duration="+duration+" nsf="+(duration*ms.getRate())+"nsamp="+nsamp);
        data = new int[nsamp];
        if (fill != 0) {
            for (int i = 0; i < nsamp; i++) {
                data[i] = fill;
            }
        }
        int msover2 = (int) (1. / rate * 1000. / 2.);         // 1/2 of a bin width in  millis
        for (int i = list.size() - 1; i >= 0; i--) {
            ms = (MiniSeed) list.get(i);
            int offset = (int) ((ms.getGregorianCalendar().getTimeInMillis() -
                    start.getTimeInMillis() + msover2) * rate / 1000.);
            long mod = (long) ((ms.getGregorianCalendar().getTimeInMillis() -
                    start.getTimeInMillis() + msover2) * rate) % 1000L;

            logger.fine(dtFormat.print(start.getTimeInMillis()) + " ms[0] =" +
                    dtFormat.print(ms.getGregorianCalendar().getTimeInMillis()) + " offset=" + offset + " ns=" + ms.getNsamp());


            // get the compression frames
            frames = new byte[ms.getBlockSize() - ms.getDataOffset()];
            System.arraycopy(ms.getBuf(), ms.getDataOffset(), frames, 0, ms.getBlockSize() - ms.getDataOffset());
            if (ms.getEncoding() != 11 && ms.getEncoding() != 10) {
                boolean skip = false;
                for (int ith = 0; ith < ms.getNBlockettes(); ith++) {
                    if (ms.getBlocketteType(ith) == 201) {
                        skip = true;     // its a Murdock Hutt, skip it
                    }
                }
                if (!skip) {
                    logger.warning("ZeroFilledSpan: Cannot decode - not Steim I or II type=" + ms.getEncoding() + " blk=" + i);
                    logger.warning(ms.toString());
                }
                continue;
            //System.exit(0);
            }
            try {
                int reverse = 0;
                int[] samples = null;
                if (ms.getEncoding() == 10) {
                    samples = Steim1.decode(frames, ms.getNsamp(), ms.isSwapBytes(), reverse);
                }
                if (ms.getEncoding() == 11) {
                    samples = Steim2.decode(frames, ms.getNsamp(), ms.isSwapBytes(), reverse);
                }
                // if the offset calculated is negative, shorten the transfer to beginning
                //logger.finer("offset="+offset+" ms.nsamp="+ms.getNsamp()+" bufsiz="+nsamp);
                if (offset < 0) {
                    if (ms.getNsamp() + offset - 1 > 0) {
                        System.arraycopy(samples, -offset + 1, data, 0,
                                Math.min(ms.getNsamp() + offset - 1, nsamp));
                    }
                } else if (nsamp - offset > 0) {
                    System.arraycopy(samples, 0, data, offset,
                            Math.min(ms.getNsamp(), nsamp - offset));
                }
            } catch (SteimException e) {
                logger.severe("block " + i + " gave steim decode error. " + e.getMessage());
            }
        }           // end for each block in list
    }

	/** Left pad a string s to Width.
	 *@param s The string to pad
	 *@param width The desired width
	 *@return The padded string to width
	 */
	protected static String leftPad(String s, int width) {
		String tmp = "";
		int npad = width - s.length();
		if (npad < 0) {
			tmp = s.substring(0, width);
		} else if (npad == 0) {
			tmp = s;
		} else {
			for (int i = 0; i < npad; i++) {
				tmp += " ";
			}
			tmp += s;
		}
		return tmp;
	}
}
