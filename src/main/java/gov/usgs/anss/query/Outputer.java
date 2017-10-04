/*
 * Copyright 2006, United States Geological Survey or
 * third-party contributors as indicated by the @author tags.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package gov.usgs.anss.query;

import gov.usgs.anss.seed.MiniSeed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 * @author davidketchum
 */
abstract public class Outputer {

	protected static final Logger logger = Logger.getLogger(Outputer.class.getName());
	static {logger.fine("$Id$");}

	protected EdgeQueryOptions options;

    /**
	 * the main routine gives an UNSORTED list in blks.  If it needs to be sorted call
     * Collections.sort(blks);
     */
    abstract public void makeFile(NSCL nscl, String filename,
			ArrayList<MiniSeed> blks) throws IOException;

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
