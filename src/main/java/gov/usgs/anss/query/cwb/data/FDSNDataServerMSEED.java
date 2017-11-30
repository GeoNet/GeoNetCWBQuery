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
import gov.usgs.anss.query.NSCL;
import gov.usgs.anss.query.QueryProperties;
import gov.usgs.anss.query.cwb.formatter.CWBQueryFormatter;
import gov.usgs.anss.query.cwb.messages.MessageFormatter;
import gov.usgs.anss.seed.MiniSeed;
import nz.org.geonet.HashCodeUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Howard Wu
 */
public class FDSNDataServerMSEED implements CWBDataServer {

    private static final Logger logger = Logger.getLogger(FDSNDataServerMSEED.class.getName());
    private static DateTimeFormatter hmsFormat = ISODateTimeFormat.time().withZone(DateTimeZone.forID("UTC"));


    static {
        logger.fine("$Id: CWBServerImpl.java 1806 2010-02-03 02:59:12Z geoffc $");
    }
    private HttpURLConnection conn;
    private InputStream inStream;
    private LinkedBlockingQueue<MiniSeed> incomingMiniSEED;
    private NSCL newNSCL = null;
    private NSCL lastNSCL = null;
    private boolean quiet = false;
	private boolean inStreamOk = false;

    /**
     * Runs a query against the server.
     *
     * @param begin the start time for the data query.
     * @param duration the duration in seconds to extract data for.
     * @param nscl the network, station, channel, and location data to query for.  These are all possible wild carded.
     */
    public void query(DateTime begin, Double duration, String nsclSelectString) {
		String serviceUrl = QueryProperties.getFDSNDataselectQueryUrl();

		String postBody = CWBQueryFormatter.fdsnQueryBody(begin, duration, nsclSelectString);

		try {
			URL url = new URL(serviceUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/vnd.fdsn.mseed");
			conn.setDoOutput(true);
			conn.getOutputStream().write(postBody.getBytes());
			inStream = conn.getInputStream();
		} catch (IOException ex) {
			logger.warning("FDSN service " + serviceUrl + " error:" + ex);
			System.exit(1);
		}


        incomingMiniSEED = new LinkedBlockingQueue<MiniSeed>();

        // Get the first block
        try {
            MiniSeed ms = null;
            if ((ms = read(inStream)) != null) {
                // The logical inversion of the test to continue read in getNext.
                if (ms.getIndicator().compareTo("D ") >= 0) {
                    newNSCL = NSCL.stringToNSCL(ms.getSeedName());
                    lastNSCL = newNSCL;
                    incomingMiniSEED.add(ms);
                } else {
                    logger.info("First block in query not data: " + ms);
                }
            } else {
                logger.warning("Failed to read first block in query.");
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
	
	/**
	 * Attempts to read a MiniSeed object from a given input stream setting the
	 * subsequent StreamStatus.
	 * @param inStream
	 * @return the MiniSeed object to read in to.
	 * @throws IOException
	 */
	public MiniSeed read(InputStream inStream) throws IOException {
		byte[] b = new byte[4096];
		MiniSeed ms = null;

		if (!read(inStream, b, 0, 512)) {
			logger.fine("Failed to read block from input stream - connection lost?");
			inStreamOk = false;
			return null;
		}
		
		if (b[0] == '<' && b[1] == 'E' && b[2] == 'O' && b[3] == 'R' && b[4] == '>') {
			logger.fine("EOR found");
			inStreamOk = false;
			return null;
		} else {

			try {
				// If we don't specify the offset and length it allocates the same
				// size as b for the MiniSeed buf, which is particularly wasteful
				// given most blocks are only 512 (not 4096).
				ms = new MiniSeed(b, 0, 512);
			} catch (IllegalSeednameException ex) {
				logger.log(Level.SEVERE, null, ex);
			}

			// This happens when the incoming blocks are bigger than 512, so we
			// have to "re-read" them at the correct size.
			if (ms.getBlockSize() != 512) {
				read(inStream, b, 512, ms.getBlockSize() - 512);

				try {
					ms = new MiniSeed(b, 0, ms.getBlockSize());
				} catch (IllegalSeednameException ex) {
					logger.log(Level.SEVERE, null, ex);
				}

			}
		}
		

		inStreamOk = true;
		return ms;
	}

    /**
     * Returns the next data record.  This is equivalent to the data for a fully qualified NSCL.
     *
     * @return
     */
    public TreeSet<MiniSeed> getNext() {

        TreeSet<MiniSeed> blks = new TreeSet<MiniSeed>();

		try {
			MiniSeed ms;
			read:
			while (inStreamOk) {
				if ((ms = read(inStream)) != null) {

					if (ms.getIndicator().compareTo("D ") < 0) {
						continue read;
					}

					// This sets up the NSCL on the very first miniSEED block
					if (lastNSCL == null) {
						lastNSCL = NSCL.stringToNSCL(ms.getSeedName());
					}

					newNSCL = NSCL.stringToNSCL(ms.getSeedName());

					if (newNSCL.equals(lastNSCL)) {
						incomingMiniSEED.add(ms);
						lastNSCL = newNSCL;
					} else {
						incomingMiniSEED.drainTo(blks);
						incomingMiniSEED.add(ms);
						lastNSCL = newNSCL;
						break read;
					}
				} else {
					logger.fine("Failed to read MiniSeed.");
				}
			}
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
		}

        // This is triggered for the last channel off the stream.
        if (blks.isEmpty()) {
            incomingMiniSEED.drainTo(blks);
        }

        if (!quiet) {
            logger.info(MessageFormatter.miniSeedSummary(new DateTime(), blks));
        }

        return blks;
    }

    /**
     * Returns true if there are more data records.
     *
     * @return
     */
    public boolean hasNext() {
		if (inStreamOk)
			return true;

		return !incomingMiniSEED.isEmpty();
	}

    public static boolean read(InputStream in, byte[] b, int off, int l)
            throws IOException {
        int len;
        while ((len = in.read(b, off, l)) > 0) {
            off += len;
            l -=
                    len;
            if (l == 0) {
                return true;
            }

        }
        return false;
    }

    /**
     * By default some progress information is provided to the user.
     * This turns that output off.
     **/
    public void quiet() {
        this.quiet = true;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        conn.disconnect();
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj.getClass() == this.getClass()) {
			final FDSNDataServerMSEED other = (FDSNDataServerMSEED) obj;
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = HashCodeUtil.SEED;
		return result;
	}
}
