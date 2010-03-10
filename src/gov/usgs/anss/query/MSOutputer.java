/*
 * MSOutput.java
 *
 * Created on April 20, 2006, 4:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import gov.usgs.anss.seed.MiniSeed;
import java.util.ArrayList;
import java.util.Collections;
import java.io.FileOutputStream;
import java.io.IOException;

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
        FileOutputStream out = new FileOutputStream(filename);
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
