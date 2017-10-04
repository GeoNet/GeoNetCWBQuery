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
package gov.usgs.anss.query.cwb.data;

import gov.usgs.anss.edge.IllegalSeednameException;
import gov.usgs.anss.seed.MiniSeed;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author geoffc
 */
public class CWBDataServerMSEEDMock implements CWBDataServer {

    private ArrayList<TreeSet<MiniSeed>> expResult;
    private Iterator<TreeSet<MiniSeed>> iter;

    public CWBDataServerMSEEDMock() {

    }

    public void loadMSEEDFiles(String[] filenames) {
        expResult = new ArrayList<TreeSet<MiniSeed>>();
        for (String filename : filenames) {

            long fileSize = CWBDataServerMSEEDMockTest.class.getResource(filename).getFile().length() * 4098;

            TreeSet<MiniSeed> blks = new TreeSet<MiniSeed>();
//            ArrayList<MiniSeed> blks = new ArrayList<MiniSeed>((int) (fileSize / 512));

            byte[] buf = new byte[512];
            InputStream in = CWBDataServerMSEEDMock.class.getResourceAsStream(filename);
            for (long pos = 0; pos < fileSize; pos += 512) {
                try {
                    if (in.read(buf) == -1) {
                        break;
                    }
                    try {
                        blks.add(new MiniSeed(buf));
                    } catch (IllegalSeednameException ex) {
                        Logger.getLogger(CWBDataServerMSEEDMock.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(CWBDataServerMSEEDMock.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            expResult.add(blks);
        }
        iter = expResult.listIterator();
    }

    public void query(DateTime begin, Double duration, String nsclSelectString) {
    }

    public TreeSet<MiniSeed> getNext() {
        return iter.next();
    }

    public boolean hasNext() {
        return iter.hasNext();
    }

    public void quiet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	public String getHost() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getPort() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
