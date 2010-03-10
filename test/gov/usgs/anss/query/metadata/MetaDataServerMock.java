/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.metadata;

import gov.usgs.anss.query.NSCL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author geoffc
 */
public class MetaDataServerMock implements MetaDataServer {

    private ArrayList<String> responses;
    private Iterator<String> iter;

    /**
     * Mock for meta data server testing.  No server is actually contacted.
     *
     * @param ip - dummy value, not used.
     * @param port - dummy value, not used.
     */
    public MetaDataServerMock(String ip, int port) {
    }

    /**
     * Loads SAC PAZ files (the same as the response from the meta data server.
     * These will be returned by getResponseData in the order that they are loaded
     * so be sure to get this correct for your needs.
     *
     * @param filenames
     */
    public void loadPAZFile(String[] filenames) {
        responses = new ArrayList<String>();

        for (String filename : filenames) {
            StringBuilder contents = new StringBuilder();

            InputStream in = MetaDataServerMock.class.getResourceAsStream(filename);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            } catch (IOException x) {
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        Logger.getLogger(MetaDataServerMock.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        
            responses.add(contents.toString());
        }
        iter = responses.listIterator();
    }

    /**
     * Returns the contents of the next response file in the order in which they where loaded.
     * The method params have no effect internally for the mock.
     *
     * @param nscl
     * @param date
     * @param units
     * @return
     */
    public String getResponseData(NSCL nscl, DateTime date, String units) {
        return iter.next();
    }
}
