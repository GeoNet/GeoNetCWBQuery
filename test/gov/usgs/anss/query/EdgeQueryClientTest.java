/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.anss.query;

import gov.usgs.anss.seed.MiniSeed;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

/**
 *
 * @author richardg
 */
@RunWith(Parameterized.class)
public class EdgeQueryClientTest {

    @Parameters
    public static Collection data() {
		return Arrays.asList(new Object[][]{
					// args line,	filenames...
					{"-b \"2009/01/01 00:00:00\" -s \"NZWLGT.BTZ40\" -d 600",
						new String[]{"build/NZWLGT_BTZ40-2009-01-01-00-00-00.ms"}
					},
					{"-b \"2009/06/01 12:00:00\" -s \"NZ.....BTZ..\" -d 600",
						new String[]{"build/NZAUCT_BTZ40-2009-06-01-11-59-51.ms", "build/NZAUCT_BTZ41-2009-06-01-11-59-17.ms", "build/NZCHIT_BTZ40-2009-06-01-11-59-27.ms", "build/NZCHIT_BTZ41-2009-06-01-11-59-33.ms", "build/NZGIST_BTZ40-2009-06-01-11-59-50.ms", "build/NZGIST_BTZ41-2009-06-01-11-59-38.ms", "build/NZLOTT_BTZ40-2009-06-01-11-59-58.ms", "build/NZLOTT_BTZ41-2009-06-01-11-59-37.ms", "build/NZNAPT_BTZ40-2009-06-01-11-59-40.ms", "build/NZNAPT_BTZ41-2009-06-01-11-59-32.ms", "build/NZNCPT_BTZ40-2009-06-01-11-59-58.ms", "build/NZNCPT_BTZ41-2009-06-01-11-59-50.ms", "build/NZRBCT_BTZ40-2009-06-01-11-59-40.ms", "build/NZRBCT_BTZ41-2009-06-01-11-59-43.ms", "build/NZRFRT_BTZ40-2009-06-01-11-59-50.ms", "build/NZRFRT_BTZ41-2009-06-01-11-59-58.ms", "build/NZTAUT_BTZ40-2009-06-01-11-59-47.ms", "build/NZTAUT_BTZ41-2009-06-01-11-59-21.ms", "build/NZWLGT_BTZ40-2009-06-01-11-59-56.ms", "build/NZWLGT_BTZ41-2009-06-01-11-59-35.ms"}
					},
                    {"-b \"2009/01/01 00:00:00\" -s \"NZMRZ..HHZ10\" -d 1800",
                           new String[]{"build/NZMRZ__HHZ10-2009-01-01-00-00-00.ms"}
                    }
		});
    }

	private String queryLine;
	private String[] filenames;

    public EdgeQueryClientTest(String queryLine, String[] filenames) {
		// Note the -t NULL
        this.queryLine = queryLine;
		this.filenames = filenames;
	}

	@Before
	public void getMSUsingOldClient() throws Exception {
		System.out.println("get ms using old client");
		try {
			Process getMS =
			Runtime.getRuntime().exec(
					"java -jar lib-ivy/external/GeoNetCWBQuery-2.0.0.jar "
					+ queryLine + " -t ms -o build/%N-%y-%M-%D-%h-%m-%S.ms");
			getMS.waitFor();
		} catch (IOException ex) {
			Logger.getLogger(EdgeQueryClientTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Test of query method, of class EdgeQueryClient.
	 * -b "2009/01/01 00:00:00" -s "NZWLGT.BTZ40" -d 600 -t ms -o "%N-%Y-%M-%D-%h-%m-%S"
	 * generates NZWLGT_BTZ40-09-01-01-00-00-00
	 * 
	 * TODO: Change to parameterized test of query lines and list of file names.
	 */
	@Test
	public void testQuery() throws Exception {
		System.out.println("query");
		
		// Load the blocks
		ArrayList<ArrayList<MiniSeed>> expResult = new ArrayList<ArrayList<MiniSeed>>();
		for(String filename : filenames) {
			File ms = new File(filename);
			long fileSize = ms.length();
			ArrayList<MiniSeed> blks = new ArrayList<MiniSeed>((int) (fileSize / 512));

			byte[] buf = new byte[512];
			FileInputStream in = new FileInputStream(ms);
			for (long pos = 0; pos < fileSize; pos += 512) {
				if (in.read(buf) == -1) break;
				blks.add(new MiniSeed(buf));
			}
			expResult.add(blks);
		}
		
		// Run the query. Note the -t NULL
		ArrayList<ArrayList<MiniSeed>> result = EdgeQueryClient.query(
				new EdgeQueryOptions(queryLine + " -t NULL"));
		assertCollectionEquals("entire collection", expResult.get(0), result.get(0));
	}

	private static void assertCollectionEquals(
			Collection<? extends Comparable> c1,
			Collection<? extends Comparable> c2) {
		assertCollectionEquals(null, c1, c2);
	}

	private static void assertCollectionEquals(String message,
			Collection<? extends Comparable> c1,
			Collection<? extends Comparable> c2) {

		assertEquals(message + ": size mismatch", c1.size(), c2.size());

		Iterator<? extends Comparable> i1 = c1.iterator();
		Iterator<? extends Comparable> i2 = c2.iterator();

		while (i1.hasNext() && i2.hasNext()) {
			assertEquals(message + ": comparison inequality", 0, i1.next().compareTo(i2.next()));
		}
		
	}
}