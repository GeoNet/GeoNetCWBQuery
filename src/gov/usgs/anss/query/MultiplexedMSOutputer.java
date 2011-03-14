/*
 *  Copyright (C) 2011 richardg
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gov.usgs.anss.query;

import gov.usgs.anss.edge.IllegalSeednameException;
import gov.usgs.anss.seed.MiniSeed;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author richardg
 */
public class MultiplexedMSOutputer extends Outputer {

	boolean dbg;

	static {
		logger.fine("$Id$");
	}
	private String temp = FileUtils.getTempDirectoryPath();
//			+ "/cwb.multiplex." + ManagementFactory.getRuntimeMXBean().getName();
	private ArrayList<File> tempFiles;
	private ArrayList<MiniSeed> mxBlks;
	private final MSOutputer slave;
	private final String origFileMask;
	private boolean cleanup = true;

	public MultiplexedMSOutputer(EdgeQueryOptions options) {
		this.options = options;
		this.origFileMask = options.filemask;

		parseExtras(options);
		if (temp == null) {
			this.mxBlks = new ArrayList<MiniSeed>();
			options.nosort = true;
		} else {
			this.tempFiles = new ArrayList<File>();
			options.filemask = temp + "/%N.tmp.ms";
		}
		this.slave = new MSOutputer(options);

		// This shutdown hook is the key to this hack.
		Runtime.getRuntime().addShutdownHook(
				new Thread("makeMultiplexedFile") {

					@Override
					public void run() {
						try {
							makeMultiplexedFile();
						} catch (IOException ex) {
							Logger.getLogger(MultiplexedMSOutputer.class.getName()).log(
									Level.SEVERE,
									"IOException while attempting to create multiplexed miniseed file.",
									ex);
						}
					}
				});
	}

	public void parseExtras(EdgeQueryOptions options) {
		for (int i = 0; i < options.extraArgs.size(); i++) {
			if (options.extraArgs.get(i).equals("-temp")) {
				this.temp = options.extraArgs.get(i + 1);
			}
			if (options.extraArgs.get(i).equals("-notemp")) {
				this.temp = null;
			}
			if (options.extraArgs.get(i).equals("-nocleanup")) {
				this.cleanup = false;
			}
		}
	}

	/**
	 * This satisfies the interface, but doesn't really do much.
	 * When using temp files, this will simply pass the work on to MSOutputer to
	 * sort each individual file.
	 * And when temp files are disabled, we just keep appending the miniseed
	 * blocks to mxBlks, for later sorting and output.
	 * @see makeMultiplexedFile
	 * @param nscl
	 * @param filename
	 * @param blks
	 * @throws IOException
	 */
	public void makeFile(NSCL nscl, String filename,
			ArrayList<MiniSeed> blks) throws IOException {
		if (temp == null) {
			mxBlks.addAll(blks);
		} else {
			// Temp files
			slave.makeFile(nscl, filename, blks);
			tempFiles.add(new File(filename));
		}
	}

	/**
	 * This does the hard work of sorting - called as a shutdown hook.
	 * @throws IOException
	 */
	public void makeMultiplexedFile() throws IOException {
		if (temp == null) {
			Collections.sort(mxBlks, new MiniSeedTimeOnlyComparator());
			logger.info("Block list");
			for (MiniSeed ms : mxBlks) {
				System.err.println(ms.getSeedName() + " " + ms.getTimeString() + " " + ms.getEndTimeString() + " " + ms.getTimeInMillis() + " " + ms.getHuseconds());
			}
			slave.makeFile(null, origFileMask, mxBlks);
		} else {
			FileOutputStream out = FileUtils.openOutputStream(new File(origFileMask));
			// The hard part, sorting the temp files...
			TreeMap<MiniSeed, FileInputStream> blks =
					new TreeMap<MiniSeed, FileInputStream>(new MiniSeedTimeOnlyComparator());
			// Prime the TreeMap
			for (File file : tempFiles) {
				logger.info(file.toString());
				FileInputStream fs = FileUtils.openInputStream(file);
				MiniSeed ms = readMiniSeed(fs);
				if (ms != null)
					blks.put(ms, fs);
			}
			
			while (!blks.isEmpty()) {
				MiniSeed next = blks.firstKey();
				out.write(next.getBuf(), 0, next.getBlockSize());

				FileInputStream fs = blks.remove(next);
				next = readMiniSeed(fs);
				if (next != null)
					blks.put(next, fs);
			}

			out.close();
			if (cleanup) {
				for (File file : tempFiles) {
					FileUtils.deleteQuietly(file);
				}
			}
		}
	}

	/**
	 * Attempts to read a MiniSeed object from a given input stream.
	 * @param inStream
	 * @return the MiniSeed object or null.
	 * @throws IOException
	 */
	public static MiniSeed readMiniSeed(InputStream inStream) throws IOException {
		byte[] b = new byte[4096];
		MiniSeed ms = null;

		if (read(inStream, b, 0, 512)) {
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
		return ms;
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
	 * A Comparator for MiniSeed objects.
	 */
	private static class MiniSeedTimeOnlyComparator implements Comparator<MiniSeed> {

		// TODO: Sort by time then name...? dynamically changable?
		public int compare(MiniSeed o1, MiniSeed o2) {
			if (o1.isClear()) {
				return -1;    // Cleared MiniSeeds are always at end
			}
			if (o2.isClear()) {
				return 1;
			}

			if (o1.getGregorianCalendar().before(o2.getGregorianCalendar())) {
				return -1;
			} else if (o1.getGregorianCalendar().after(o2.getGregorianCalendar())) {
				return 1;
			} else {
				// reverse sort on network, then station, then channel.
				NSCL nscl1 = NSCL.stringToNSCL(o1.getSeedName());
				NSCL nscl2 = NSCL.stringToNSCL(o2.getSeedName());
				return NSCL.LocationComparator.compare(nscl1, nscl2);
			}
		}
	}
}
