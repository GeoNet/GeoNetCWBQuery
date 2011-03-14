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
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author davidketchum
 */
public class MSOutputer extends Outputer {

    boolean dbg;
	static {logger.fine("$Id$");}


    public MSOutputer(EdgeQueryOptions options) {
		this.options = options;
    }


    public void makeFile(NSCL nscl, String filename,
			ArrayList<MiniSeed> blks) throws IOException {
        MiniSeed ms2 = null;
        if (options.filemask.equals("%N")) {
            filename += ".ms";
        }
        filename = filename.replaceAll("[__]", "_");
        FileOutputStream out = FileUtils.openOutputStream(new File(filename));
        if (!options.nosort) {
            Collections.sort(blks);
        }
        if (options.chkDups) {
            for (int i = blks.size() - 1; i > 0; i--) {
                if (blks.get(i).isDuplicate(blks.get(i - 1))) {
                    blks.remove(i);
                }
            }
        }

        for (int i = 0; i < blks.size(); i++) {
            ms2 = (MiniSeed) blks.get(i);

			logger.fine("Out:" + ms2.getSeedName() + " " + ms2.getTimeString() +
					" ns=" + ms2.getNsamp() + " rt=" + ms2.getRate());

            out.write(ms2.getBuf(), 0, ms2.getBlockSize());
        }
        out.close();
    }
}
