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
package gov.usgs.anss.query.metadata;

import gov.usgs.anss.query.NSCL;
import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author geoffc
 */
public class MetaDataServerMock implements MetaDataServer {

    Map<String, String> responses;

    /**
     * Loads SAC PAZ files (the same as the response from the meta data server.
     * These will be returned by getResponseData in the order that they are loaded
     * so be sure to get this correct for your needs.
     *
     * @param filenames
     */
    public void loadPAZFile(String[] nscls, String[] filenames) {

        responses = new HashMap<String, String>();

        for (int i = 0; i < nscls.length; i++) {
            String filename = filenames[i];
            String nscl = nscls[i];


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

            responses.put(nscl, contents.toString());
        }
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
        return responses.containsKey(nscl.toString()) ?  responses.get(nscl.toString()) : "";
    }
}
