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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author richardg
 */
public class MultiplexedMSOutputer extends Outputer {

	static {
		logger.fine("$Id$");
	}
	private String temp = FileUtils.getTempDirectoryPath();
//			+ "/cwb.multiplex." + ManagementFactory.getRuntimeMXBean().getName();
	private ArrayList<File> tempFiles;
	private ArrayList<MiniSeed> mxBlks;
	private final MSOutputer slave;
	private final String origFileMask;
	private boolean cleanup;
	private boolean allowEmpty = true;

	public MultiplexedMSOutputer(EdgeQueryOptions options) {
		this(options, true);
	}

	public MultiplexedMSOutputer(EdgeQueryOptions options, boolean cleanupDefault) {
		this.cleanup = cleanupDefault;
		this.origFileMask = options.filemask;
		this.tempFiles = new ArrayList<File>();

		parseExtras(options);
		if (temp == null) {
			this.mxBlks = new ArrayList<MiniSeed>();
			options.nosort = true;
		} else {
			options.filemask = temp + "/%N.tmp.ms";
		}
		this.slave = new MSOutputer(options);

		// This shutdown hook is the key to this hack.
		Runtime.getRuntime().addShutdownHook(
				new Thread("makeMultiplexedFile") {

					@Override
					public void run() {
						try {
							System.err.println("Multiplexing fetched blocks...");
							if (temp == null) {
								if (!allowEmpty) {
									logger.fine("Removing any empty MiniSeed blocks");
									ArrayList<MiniSeed> empty = new ArrayList<MiniSeed>();
									for (MiniSeed ms : mxBlks) {
										if (ms.getNsamp() > 0 && ms.getRate() > 0)
											empty.add(ms);
									}
									mxBlks.removeAll(empty);
								}
								logger.fine("Directly sorting block list.");
								Collections.sort(mxBlks, new MiniSeedTimeOnlyComparator());
								slave.makeFile(null, origFileMask, mxBlks);
							} else {
								multiplexFiles(origFileMask, tempFiles, cleanup, allowEmpty);
							}
							System.err.println("Done!");
						} catch (Exception ex) {
							System.err.println("Exception while attempting to create multiplexed miniseed file.");
							System.err.println(ex);
							System.exit(1);
						}
					}
				});
	}

	private void parseExtras(EdgeQueryOptions options) {
		for (int i = 0; i < options.extraArgs.size(); i++) {
			if (options.extraArgs.get(i).equals("-temp")) {
				this.temp = options.extraArgs.get(++i);
			}
			else if(options.extraArgs.get(i).equals("-notemp")) {
				this.temp = null;
			}
			else if(options.extraArgs.get(i).equals("-nocleanup")) {
				this.cleanup = false;
			}
			else if(options.extraArgs.get(i).equals("-noempty")) {
				this.allowEmpty = false;
			}
			else {
				File f = new File(options.extraArgs.get(i));
				tempFiles.add(f);
			}
		}
	}

	/**
	 * This satisfies the interface, but doesn't really do much.
	 * When using temp files, this will simply pass the work on to MSOutputer to
	 * sort each individual file.
	 * And when temp files are disabled, we just keep appending the MiniSEED
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
	 * TODO: consider recursion.
	 * @param outputName name for the output file.
	 * @param files list of MiniSEED files to multiplex.
	 * @param cleanup flag indicating whether to cleanup after ourselves or not.
	 * @throws IOException
	 */
	public static void multiplexFiles(String outputName, List<File> files, boolean cleanup, boolean allowEmpty) throws IOException {
		ArrayList<File> cleanupFiles = new ArrayList<File>(files);
		ArrayList<File> moreFiles = new ArrayList<File>();

		File outputFile = new File(outputName);
		File tempOutputFile = new File(outputName + ".tmp");

		do {
			// This checks if we're in a subsequent (i.e. not the first) iteration and if there are any more files to process...?
			if (!moreFiles.isEmpty()) {
				logger.info("more files left to multiplex...");
				FileUtils.deleteQuietly(tempOutputFile);
				FileUtils.moveFile(outputFile, tempOutputFile);

				cleanupFiles.add(tempOutputFile);
				moreFiles.add(tempOutputFile);
				files = moreFiles;
				moreFiles = new ArrayList<File>();
			}

			logger.log(Level.FINE, "Multiplexing blocks from {0} temp files to {1}", new Object[]{files.size(), outputName});
			BufferedOutputStream out = new BufferedOutputStream(FileUtils.openOutputStream(outputFile));

			// The hard part, sorting the temp files...
			TreeMap<MiniSeed, FileInputStream> blks =
					new TreeMap<MiniSeed, FileInputStream>(new MiniSeedTimeOnlyComparator());
			// Prime the TreeMap
			logger.log(Level.FINEST, "Priming the TreeMap with files: {0}", files);
			for (File file : files) {
				logger.log(Level.INFO, "Reading first block from {0}", file.toString());
				try {
					FileInputStream fs = FileUtils.openInputStream(file);
					MiniSeed ms = getNextValidMiniSeed(fs, allowEmpty);
					if (ms != null) {
						blks.put(ms, fs);
					} else {
						logger.log(Level.WARNING, "Failed to read valid MiniSEED block from {0}", file.toString());
					}
				} catch (IOException ex) {
					// Catch "Too many open files" i.e. hitting ulimit, throw anything else.
					if(ex.getMessage().contains("Too many open files")) {
						logger.log(Level.INFO, "Too many open files - {0} deferred.", file.toString());
						moreFiles.add(file);
					}
					else
						throw ex;
				}
			}

			while (!blks.isEmpty()) {
				MiniSeed next = blks.firstKey();
				out.write(next.getBuf(), 0, next.getBlockSize());

				FileInputStream fs = blks.remove(next);
				next = getNextValidMiniSeed(fs, allowEmpty);
				if (next != null) {
					blks.put(next, fs);
				} else {
					fs.close();
				}
			}

			out.close();
		} while (!moreFiles.isEmpty());

		if (cleanup) {
			logger.log(Level.INFO, "Cleaning up...");
			for (File file : cleanupFiles) {
				FileUtils.deleteQuietly(file);
			}
		}
	}

	public static MiniSeed getNextValidMiniSeed(InputStream inStream, boolean allowEmpty) throws IOException {
		MiniSeed ms = null;
		while ((ms = readMiniSeed(inStream)) != null) {
			// Ensure we have samples and a rate. TODO: Further validation?
			if (allowEmpty || (ms.getNsamp() > 0 && ms.getRate() > 0.0)) {
				break;
			} else {
				logger.log(Level.WARNING, "Empty MiniSEED block found {0}", ms.toString());
			}
		}
		return ms;
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
            l -= len;
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

			if (o1.getEndTime().before(o2.getEndTime())) {
				return -1;
			} else if (o1.getEndTime().after(o2.getEndTime())) {
				return 1;
			} else {
				// reverse sort on network, then station, then channel.
				NSCL nscl1 = NSCL.stringToNSCL(o1.getSeedName());
				NSCL nscl2 = NSCL.stringToNSCL(o2.getSeedName());
				return NSCL.LocationComparator.compare(nscl1, nscl2);
			}
		}
	}

	/**
	 * Possibly useful for arbitrary pre-existing sorted MiniSEED volumes.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// We don't want to default to cleanup temp files in this mode.
		MultiplexedMSOutputer mx = new MultiplexedMSOutputer(new EdgeQueryOptions(args), false);
	}
}
