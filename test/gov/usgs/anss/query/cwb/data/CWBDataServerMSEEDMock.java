/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.cwb.data;

import gov.usgs.anss.edge.IllegalSeednameException;
import gov.usgs.anss.seed.MiniSeed;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author geoffc
 */
public class CWBDataServerMSEEDMock implements CWBDataServer {

    private ArrayList<TreeSet<MiniSeed>> expResult;
    private Iterator<TreeSet<MiniSeed>> iter;

    public CWBDataServerMSEEDMock(String host, int port) {

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
