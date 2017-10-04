/*
 * Copyright 2010, Institute of Geological & Nuclear Sciences Ltd or
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
//import gov.usgs.anss.util.*;

/**
 * TextOutputer, largely based on MSOutputer.java and SacOutputer.java.
 *
 * @author	richardg
 * @version	$Id$
 */
public class TextOutputer extends Outputer {

	public static final int WINSTON_NO_DATA = Integer.MIN_VALUE;	// chosen to be the same as Winston Waves.
//	public static final int SAC_UNDEFINED = -12345;		// this is the undefined value for the Sac data format.
	static {logger.fine("$Id$");}

    /** Creates a new instance of SacOutputer */
    public TextOutputer(EdgeQueryOptions options) {
		this.options = options;
    }


    public void makeFile(NSCL nscl, String filename,
			ArrayList<MiniSeed> blks) throws IOException {

        // Process the args for things that affect us
        if (blks.isEmpty()) {
            return;    // no data to save
        }
		int fill = WINSTON_NO_DATA;
        boolean nogaps = false;		// if true, do not generate a file if it has any gaps!

        for (int i = 0; i < options.extraArgs.size(); i++) {
            if (options.extraArgs.get(i).equals("-fill")) {
                fill = Integer.parseInt(options.extraArgs.get(i + 1));
            }
            if (options.extraArgs.get(i).equals("-nogaps")) {
                nogaps = true;
            }
        }
        if (options.filemask.equals("%N")) {
            filename += ".txt";
        }
		logger.info("filename=" + filename);
        filename = filename.replaceAll("[__]", "_");
        final PrintWriter out = new PrintWriter(new FileOutputStream(filename), false);

        // Use the span to populate a sac file
        GregorianCalendar start = new GregorianCalendar();
        start.setTimeInMillis(options.getBeginWithOffset().getMillis());

        // build the zero filled area (either with exact limits or with all blocks)
        final ZeroFilledSpan span = new ZeroFilledSpan(blks, start, options.getDuration(), fill);
        if (span.getRate() <= 0.00) {
            return;         // There is no real data to put in SAC
        }
		logger.fine("ZeroSpan=" + span.toString());

		GregorianCalendar spanStart = span.getStart();

		double currentTime = spanStart.getTimeInMillis();
		final double period = 1000.0 / span.getRate();
		int data;

		for (int i = 0; i < span.getNsamp(); i++) {
			data = span.getData(i);
			if (nogaps || data != fill) {
				out.println((long)Math.round(currentTime) + " " + span.getData(i));
			}
			currentTime += period;
		}


        out.close();
    }
}
