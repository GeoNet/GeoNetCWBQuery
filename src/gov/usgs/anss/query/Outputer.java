/*
 * Outputer.java
 *
 * Created on April 20, 2006, 4:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import java.util.ArrayList;
import java.io.IOException;
import gov.usgs.anss.seed.MiniSeed;
import java.util.logging.Logger;

/**
 *
 * @author davidketchum
 */
abstract public class Outputer {

	protected static final Logger logger = Logger.getLogger(Outputer.class.getName());
	static {logger.fine("$Id$");}

    /** Creates a new instance of Outputer */
    public Outputer() {
    }

    /** the main routine gives an UNSORTED list in blks.  If it needs to be sorted call
     * Collections.sort(blks);
     */
    abstract public void makeFile(String comp, String filename, String mask, ArrayList<MiniSeed> blks,
            java.util.Date beg, double duration, String[] args) throws IOException;

      /** convert to hex string
     *@param b The item to convert to hex
     *@return The hex string */
    protected static String toHex(byte b) {
        return toHex(((long) b) & 0xFFL);
    }

    /** convert to hex string
     *@param b The item to convert to hex
     *@return The hex string */
    protected static String toHex(short b) {
        return toHex(((long) b) & 0xFFFFL);
    }

    /** convert to hex string
     *@param b The item to convert to hex
     *@return The hex string */
    protected static String toHex(int b) {
        return toHex(((long) b) & 0xFFFFFFFFL);
    }

    /** convert to hex string
     *@param i The item to convert to hex
     *@return The hex string */
    protected static String toHex(long i) {
        StringBuilder s = new StringBuilder(16);
        int j = 60;
        int k;
        long val;
        char c;
        boolean flag = false;
        s.append("0x");

        for (k = 0; k < 16; k++) {
            val = (i >> j) & 0xf;
            //prt(i+" i >> j="+j+" 0xF="+val);
            if (val < 10) {
                c = (char) (val + '0');
            } else {
                c = (char) (val - 10 + 'a');
            }
            if (c != '0') {
                flag = true;
            }
            if (flag) {
                s.append(c);
            }
            j = j - 4;
        }
        if (!flag) {
            s.append("0");
        }
        return s.toString();
    }

}
