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
package gov.usgs.anss.query.cwb.holdings;

import gov.usgs.anss.query.cwb.formatter.CWBQueryFormatter;
import gov.usgs.anss.query.cwb.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 *
 * @author geoffc
 */
public class CWBHoldingsServerImpl implements CWBHoldingsServer {

    private static final Logger logger = Logger.getLogger(CWBHoldingsServerImpl.class.getName());
    private static DateTimeFormatter hmsFormat = ISODateTimeFormat.time().withZone(DateTimeZone.forID("UTC"));


    static {
        logger.fine("$Id: CWBServerImpl.java 1831 2010-02-04 23:55:40Z geoffc $");
    }
    private String host;
    private int port;

    public CWBHoldingsServerImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String listChannels(DateTime begin, Double duration) {
        try {

            byte[] b = new byte[4096];
            Socket ds = new Socket(this.host, this.port);
            ds.setReceiveBufferSize(512000);

            InputStream in = ds.getInputStream();
            OutputStream outtcp = ds.getOutputStream();

// This option is not documented in the help so won't implement it for now.
//            if (options.exclude != null) {
//                line = "'-el' '" + options.exclude + "' ";
//            } else {
//                line = "";
//            }
// This option is ont documented in the help so won't implement it for now.
//                if (options.showIllegals) {
//                    line += "'-si' ";
//                }
// This option is stated as not useful for users so not going to implement it.
//            } else {
//                line += "'-ls'\n";
//            }

            String line = CWBQueryFormatter.listChannels(begin, duration);

            logger.config("line=" + line + ":");
            outtcp.write(line.getBytes());
            StringBuffer sb = new StringBuffer(100000);
            int len = 0;
            while ((len = in.read(b, 0, 512)) > 0) {
                sb.append(new String(b, 0, len));
            }
            // TODO - multiple returns, bad form.
            return sb.toString();
        } catch (IOException e) {
            logger.severe(e + " list channels");
            return null;
        }
    }

}
