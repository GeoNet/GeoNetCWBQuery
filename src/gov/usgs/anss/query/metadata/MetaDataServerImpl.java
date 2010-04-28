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
import gov.usgs.anss.util.StaSrv;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author geoffc
 */
public class MetaDataServerImpl implements MetaDataServer {

    private StaSrv stasrv;
    protected static final Logger logger = Logger.getLogger(MetaDataServerImpl.class.getName());

    // This is used to format the query to the meta data server,
    // not the CWB server.  It looks like the format is different
    // so we will format it ourselves here instead of creating
    //  a new instance of options.
    private static String beginFormat = "YYYY/MM/dd-HH:mm:ss";
    private static DateTimeFormatter parseBeginFormat = DateTimeFormat.forPattern(beginFormat).withZone(DateTimeZone.forID("UTC"));

    static {
        logger.fine("$Id$");
    }

    /**
     *
     * @param metaDataServerHost
     * @param metaDataServerPort
     */
    public MetaDataServerImpl(String metaDataServerHost, int metaDataServerPort) {
        stasrv = new StaSrv(metaDataServerHost, metaDataServerPort);
    }
    
    public String getResponseData(NSCL nscl, DateTime date, String pzunit) {
        String s = stasrv.getSACResponse(nscl.toString(),
                parseBeginFormat.withZone(DateTimeZone.UTC).print(date),
                pzunit);

        // Its not at all clear how this will get triggered
        // there has to be message come back from the server for
        // this to work...
        int loop = 0;
        while (s.indexOf("MetaDataServer not up") >= 0) {
            logger.warning("MetaDataServer is not up - waiting for connection");
            if (loop++ % 15 == 1) {
                logger.warning("MetaDataServer is not up - waiting for connection");
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            s = stasrv.getSACResponse(nscl.toString(), parseBeginFormat.withZone(DateTimeZone.UTC).print(date), pzunit);
        }
        return s;
    }

 
}
