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

import gov.usgs.anss.seed.MiniSeed;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author richardg
 */
@RunWith(Parameterized.class)
public class CWBDataServerMSEEDTest {

    private static FDSNDataServerMSEED cwbServer;
    private static DateTimeZone tz = DateTimeZone.forID("UTC");

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
                    {"-b \"2009/01/01 00:00:00\" -s \"NZWLGT.BTZ40\" -d 600",
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, tz),
                        600d,
                        "NZWLGT.BTZ40",
                        new String[]{"target/test-classes/NZWLGT_BTZ40-2009-01-01-00-00-00.ms"}
                    },
                    {"-b \"2009/06/01 12:00:00\" -s \"NZ.....BTZ..\" -d 600",
                        new DateTime(2009, 6, 1, 12, 0, 0, 0, tz),
                        600d,
                        "NZ.....BTZ..",
                        new String[]{"target/test-classes/NZAUCT_BTZ40-2009-06-01-11-59-51.ms", "target/test-classes/NZAUCT_BTZ41-2009-06-01-11-59-17.ms", "target/test-classes/NZCHIT_BTZ40-2009-06-01-11-59-27.ms", "target/test-classes/NZCHIT_BTZ41-2009-06-01-11-59-33.ms", "target/test-classes/NZGIST_BTZ40-2009-06-01-11-59-50.ms", "target/test-classes/NZGIST_BTZ41-2009-06-01-11-59-38.ms", "target/test-classes/NZLOTT_BTZ40-2009-06-01-11-59-58.ms", "target/test-classes/NZLOTT_BTZ41-2009-06-01-11-59-37.ms", "target/test-classes/NZNAPT_BTZ40-2009-06-01-11-59-40.ms", "target/test-classes/NZNAPT_BTZ41-2009-06-01-11-59-32.ms", "target/test-classes/NZNCPT_BTZ40-2009-06-01-11-59-58.ms", "target/test-classes/NZNCPT_BTZ41-2009-06-01-11-59-50.ms", "target/test-classes/NZRBCT_BTZ40-2009-06-01-11-59-40.ms", "target/test-classes/NZRBCT_BTZ41-2009-06-01-11-59-43.ms", "target/test-classes/NZRFRT_BTZ40-2009-06-01-11-59-50.ms", "target/test-classes/NZRFRT_BTZ41-2009-06-01-11-59-58.ms", "target/test-classes/NZTAUT_BTZ40-2009-06-01-11-59-47.ms", "target/test-classes/NZTAUT_BTZ41-2009-06-01-11-59-21.ms", "target/test-classes/NZWLGT_BTZ40-2009-06-01-11-59-56.ms", "target/test-classes/NZWLGT_BTZ41-2009-06-01-11-59-35.ms"}
                    },
                    {"-b \"2009/01/01 00:00:00\" -s \"NZMRZ..HHZ10\" -d 1800",
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, tz),
                        1800d,
                        "NZMRZ..HHZ10",
                        new String[]{"target/test-classes/NZMRZ__HHZ10-2009-01-01-00-00-00.ms"}
                    },
                    {"-b \"2009/01/01 00:00:00\" -s \"XXARSE\"",
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, tz),
                        300d,
                        "XXARSE",
                        new String[]{}
                    },
					{"-b \"2009/01/01 00:00:00\" -s \"NZWEL..HH[ENZ]..|NZBFZ..HHZ..|.*WAZ.*20\" -d 300",
						new DateTime(2009, 1, 1, 0, 0, 0, 0, tz),
						300d,
						"NZWEL..HH[ENZ]..|NZBFZ..HHZ..|.*WAZ.*20",
						new String[]{"target/test-classes/NZBFZ__HHZ10-2009-01-01-00-00-00.ms", "target/test-classes/NZWAZ__LNE20-2009-01-01-00-00-00.ms", "target/test-classes/NZWAZ__LNZ20-2009-01-01-00-00-00.ms", "target/test-classes/NZWEL__HHE10-2009-01-01-00-00-00.ms", "target/test-classes/NZWEL__HHN10-2009-01-01-00-00-00.ms", "target/test-classes/NZWEL__HHZ10-2009-01-01-00-00-00.ms"}
					},
					{"-s \"NZAPZ..HHZ\" -b \"2010/02/25 04:38:00\" -d 300",
						new DateTime(2010, 2, 25, 4, 38, 0, 0, tz),
						300d,
						"NZAPZ..HHZ",
						new String[]{"target/test-classes/NZAPZ__HHZ10-2010-02-25-04-37-56.ms"}
					},
//					{"-b \"2009/07/15 09:22:00\" -s \"NZ..Z..HH\" -d 1800",
//						new DateTime(2009, 7, 15, 9, 22, 0, 0, tz),
//						1800d,
//						"NZ..Z..HH",
//						new String[]{"target/test-classes/NZAPZ__HHE10-2009-07-15-09-21-59.ms", "target/test-classes/NZAPZ__HHN10-2009-07-15-09-21-55.ms", "target/test-classes/NZAPZ__HHZ10-2009-07-15-09-21-59.ms", "target/test-classes/NZBFZ__HHE10-2009-07-15-09-21-57.ms", "target/test-classes/NZBFZ__HHN10-2009-07-15-09-21-59.ms", "target/test-classes/NZBFZ__HHZ10-2009-07-15-09-21-55.ms", "target/test-classes/NZBKZ__HHE10-2009-07-15-09-21-57.ms", "target/test-classes/NZBKZ__HHN10-2009-07-15-09-21-56.ms", "target/test-classes/NZBKZ__HHZ10-2009-07-15-09-21-54.ms", "target/test-classes/NZCTZ__HHE10-2009-07-15-09-21-57.ms", "target/test-classes/NZCTZ__HHN10-2009-07-15-09-21-57.ms", "target/test-classes/NZCTZ__HHZ10-2009-07-15-09-21-59.ms", "target/test-classes/NZDCZ__HHE10-2009-07-15-09-21-59.ms", "target/test-classes/NZDCZ__HHN10-2009-07-15-09-21-55.ms", "target/test-classes/NZDCZ__HHZ10-2009-07-15-09-21-57.ms", "target/test-classes/NZDSZ__HHE10-2009-07-15-09-21-56.ms", "target/test-classes/NZDSZ__HHN10-2009-07-15-09-21-59.ms", "target/test-classes/NZDSZ__HHZ10-2009-07-15-09-21-57.ms", "target/test-classes/NZEAZ__HHE10-2009-07-15-09-21-57.ms", "target/test-classes/NZEAZ__HHN10-2009-07-15-09-21-59.ms", "target/test-classes/NZEAZ__HHZ10-2009-07-15-09-21-56.ms", "target/test-classes/NZFOZ__HHE10-2009-07-15-09-21-58.ms", "target/test-classes/NZFOZ__HHN10-2009-07-15-09-21-59.ms", "target/test-classes/NZFOZ__HHZ10-2009-07-15-09-21-55.ms", "target/test-classes/NZHIZ__HHE10-2009-07-15-09-21-54.ms", "target/test-classes/NZHIZ__HHN10-2009-07-15-09-21-55.ms", "target/test-classes/NZHIZ__HHZ10-2009-07-15-09-21-57.ms", "target/test-classes/NZJCZ__HHE10-2009-07-15-09-21-56.ms", "target/test-classes/NZJCZ__HHN10-2009-07-15-09-21-57.ms", "target/test-classes/NZJCZ__HHZ10-2009-07-15-09-21-57.ms", "target/test-classes/NZKHZ__HHE10-2009-07-15-09-21-56.ms", "target/test-classes/NZKHZ__HHN10-2009-07-15-09-21-56.ms", "target/test-classes/NZKHZ__HHZ10-2009-07-15-09-21-58.ms", "target/test-classes/NZKNZ__HHE10-2009-07-15-09-21-57.ms", "target/test-classes/NZKNZ__HHN10-2009-07-15-09-21-57.ms", "target/test-classes/NZKNZ__HHZ10-2009-07-15-09-21-56.ms", "target/test-classes/NZKUZ__HHE10-2009-07-15-09-21-55.ms", "target/test-classes/NZKUZ__HHN10-2009-07-15-09-21-54.ms", "target/test-classes/NZKUZ__HHZ10-2009-07-15-09-21-57.ms", "target/test-classes/NZLBZ__HHE10-2009-07-15-09-21-55.ms", "target/test-classes/NZLBZ__HHN10-2009-07-15-09-21-59.ms", "target/test-classes/NZLBZ__HHZ10-2009-07-15-09-21-57.ms", "target/test-classes/NZLTZ__HHE10-2009-07-15-09-21-56.ms", "target/test-classes/NZLTZ__HHN10-2009-07-15-09-21-58.ms", "target/test-classes/NZLTZ__HHZ10-2009-07-15-09-21-59.ms", "target/test-classes/NZMLZ__HHE10-2009-07-15-09-21-59.ms", "target/test-classes/NZMLZ__HHN10-2009-07-15-09-21-55.ms", "target/test-classes/NZMLZ__HHZ10-2009-07-15-09-21-56.ms", "target/test-classes/NZMQZ__HHE10-2009-07-15-09-21-55.ms", "target/test-classes/NZMQZ__HHN10-2009-07-15-09-21-55.ms", "target/test-classes/NZMQZ__HHZ10-2009-07-15-09-21-59.ms", "target/test-classes/NZMRZ__HHE10-2009-07-15-09-21-58.ms", "target/test-classes/NZMRZ__HHN10-2009-07-15-09-21-58.ms", "target/test-classes/NZMRZ__HHZ10-2009-07-15-09-21-55.ms", "target/test-classes/NZMSZ__HHE10-2009-07-15-09-21-57.ms", "target/test-classes/NZMSZ__HHN10-2009-07-15-09-21-59.ms", "target/test-classes/NZMSZ__HHZ10-2009-07-15-09-21-56.ms", "target/test-classes/NZMWZ__HHE10-2009-07-15-09-21-54.ms", "target/test-classes/NZMWZ__HHN10-2009-07-15-09-21-53.ms", "target/test-classes/NZMWZ__HHZ10-2009-07-15-09-21-54.ms", "target/test-classes/NZMXZ__HHE10-2009-07-15-09-21-54.ms", "target/test-classes/NZMXZ__HHN10-2009-07-15-09-21-59.ms", "target/test-classes/NZMXZ__HHZ10-2009-07-15-09-21-55.ms", "target/test-classes/NZNNZ__HHE10-2009-07-15-09-21-57.ms", "target/test-classes/NZNNZ__HHN10-2009-07-15-09-21-56.ms", "target/test-classes/NZNNZ__HHZ10-2009-07-15-09-21-56.ms", "target/test-classes/NZODZ__HHE10-2009-07-15-09-21-57.ms", "target/test-classes/NZODZ__HHN10-2009-07-15-09-21-56.ms", "target/test-classes/NZODZ__HHZ10-2009-07-15-09-21-56.ms", "target/test-classes/NZOPZ__HHE10-2009-07-15-09-21-59.ms", "target/test-classes/NZOPZ__HHN10-2009-07-15-09-21-57.ms", "target/test-classes/NZOPZ__HHZ10-2009-07-15-09-21-58.ms", "target/test-classes/NZOUZ__HHE10-2009-07-15-09-21-57.ms", "target/test-classes/NZOUZ__HHN10-2009-07-15-09-21-56.ms", "target/test-classes/NZOUZ__HHZ10-2009-07-15-09-21-56.ms", "target/test-classes/NZOXZ__HHE10-2009-07-15-09-21-59.ms", "target/test-classes/NZOXZ__HHN10-2009-07-15-09-21-58.ms", "target/test-classes/NZOXZ__HHZ10-2009-07-15-09-21-57.ms", "target/test-classes/NZPUZ__HHE10-2009-07-15-09-21-56.ms", "target/test-classes/NZPUZ__HHN10-2009-07-15-09-21-57.ms", "target/test-classes/NZPUZ__HHZ10-2009-07-15-09-21-58.ms", "target/test-classes/NZPXZ__HHE10-2009-07-15-09-21-55.ms", "target/test-classes/NZPXZ__HHN10-2009-07-15-09-21-58.ms", "target/test-classes/NZPXZ__HHZ10-2009-07-15-09-21-59.ms", "target/test-classes/NZPYZ__HHE10-2009-07-15-09-21-55.ms", "target/test-classes/NZPYZ__HHN10-2009-07-15-09-21-59.ms", "target/test-classes/NZPYZ__HHZ10-2009-07-15-09-21-55.ms", "target/test-classes/NZQRZ__HHE10-2009-07-15-09-21-59.ms", "target/test-classes/NZQRZ__HHN10-2009-07-15-09-21-54.ms", "target/test-classes/NZQRZ__HHZ10-2009-07-15-09-21-56.ms", "target/test-classes/NZRIZ__HHE10-2009-07-15-09-21-58.ms", "target/test-classes/NZRIZ__HHN10-2009-07-15-09-21-59.ms", "target/test-classes/NZRIZ__HHZ10-2009-07-15-09-21-59.ms", "target/test-classes/NZRPZ__HH110-2009-07-15-09-21-57.ms", "target/test-classes/NZRPZ__HH210-2009-07-15-09-21-58.ms", "target/test-classes/NZRPZ__HHZ10-2009-07-15-09-21-57.ms", "target/test-classes/NZSYZ__HHE10-2009-07-15-09-21-55.ms", "target/test-classes/NZSYZ__HHN10-2009-07-15-09-21-59.ms", "target/test-classes/NZSYZ__HHZ10-2009-07-15-09-21-56.ms", "target/test-classes/NZTHZ__HHE10-2009-07-15-09-21-53.ms", "target/test-classes/NZTHZ__HHN10-2009-07-15-09-21-59.ms", "target/test-classes/NZTHZ__HHZ10-2009-07-15-09-21-58.ms", "target/test-classes/NZTLZ__HHE10-2009-07-15-09-21-55.ms", "target/test-classes/NZTLZ__HHN10-2009-07-15-09-21-54.ms", "target/test-classes/NZTLZ__HHZ10-2009-07-15-09-21-56.ms", "target/test-classes/NZTOZ__HHE10-2009-07-15-09-21-59.ms", "target/test-classes/NZTOZ__HHN10-2009-07-15-09-21-59.ms", "target/test-classes/NZTOZ__HHZ10-2009-07-15-09-21-58.ms", "target/test-classes/NZTSZ__HHE10-2009-07-15-09-21-57.ms", "target/test-classes/NZTSZ__HHN10-2009-07-15-09-21-59.ms", "target/test-classes/NZTSZ__HHZ10-2009-07-15-09-21-57.ms", "target/test-classes/NZTUZ__HHE10-2009-07-15-09-21-59.ms", "target/test-classes/NZTUZ__HHN10-2009-07-15-09-21-54.ms", "target/test-classes/NZTUZ__HHZ10-2009-07-15-09-21-59.ms", "target/test-classes/NZURZ__HH110-2009-07-15-09-21-59.ms", "target/test-classes/NZURZ__HH210-2009-07-15-09-21-58.ms", "target/test-classes/NZURZ__HHZ10-2009-07-15-09-21-58.ms", "target/test-classes/NZVRZ__HHE10-2009-07-15-09-21-58.ms", "target/test-classes/NZVRZ__HHN10-2009-07-15-09-21-55.ms", "target/test-classes/NZVRZ__HHZ10-2009-07-15-09-21-56.ms", "target/test-classes/NZWAZ__HHE10-2009-07-15-09-21-57.ms", "target/test-classes/NZWAZ__HHN10-2009-07-15-09-21-56.ms", "target/test-classes/NZWAZ__HHZ10-2009-07-15-09-21-58.ms", "target/test-classes/NZWCZ__HHE10-2009-07-15-09-21-57.ms", "target/test-classes/NZWCZ__HHN10-2009-07-15-09-21-55.ms", "target/test-classes/NZWCZ__HHZ10-2009-07-15-09-21-56.ms", "target/test-classes/NZWHZ__HHE10-2009-07-15-09-21-54.ms", "target/test-classes/NZWHZ__HHN10-2009-07-15-09-21-58.ms", "target/test-classes/NZWHZ__HHZ10-2009-07-15-09-21-57.ms", "target/test-classes/NZWIZ__HHE10-2009-07-15-09-21-59.ms", "target/test-classes/NZWIZ__HHN10-2009-07-15-09-21-58.ms", "target/test-classes/NZWIZ__HHZ10-2009-07-15-09-21-57.ms", "target/test-classes/NZWKZ__HHE10-2009-07-15-09-21-59.ms", "target/test-classes/NZWKZ__HHN10-2009-07-15-09-21-57.ms", "target/test-classes/NZWKZ__HHZ10-2009-07-15-09-21-57.ms", "target/test-classes/NZWVZ__HHE10-2009-07-15-09-21-57.ms", "target/test-classes/NZWVZ__HHN10-2009-07-15-09-21-59.ms", "target/test-classes/NZWVZ__HHZ10-2009-07-15-09-21-58.ms"}
//					},
                });
    }
    private String queryLine;
    private DateTime begin;
    private Double duration;
    private String nsclSelectString;
    private String[] filenames;

    public CWBDataServerMSEEDTest(String queryLine, DateTime begin, Double duration, String nscl, String[] filenames) {
        // Note the -t NULL
        this.queryLine = queryLine;
        this.filenames = filenames;
        this.begin = begin;
        this.duration = duration;
        this.nsclSelectString = nscl;
    }

    @Before
    public void getMSUsingOldClient() throws Exception {
        System.out.println("get ms using old client");
        try {
            Process getMS =
                    Runtime.getRuntime().exec(
                    "java -jar target/dependency/GeoNetCWBQuery-2.0.0.jar " + queryLine + " -t ms -o target/test-classes/%N-%y-%M-%D-%h-%m-%S.ms");
            getMS.waitFor();
        } catch (IOException ex) {
            Logger.getLogger(CWBDataServerMSEEDTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * To make netbeans work with junit tests incorporating System.out.print calls.
     */
    @After
    public void tearDown() {
        System.out.println();
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
        for (String filename : filenames) {
            File ms = new File(filename);
            long fileSize = ms.length();
            ArrayList<MiniSeed> blks = new ArrayList<MiniSeed>((int) (fileSize / 512));

            byte[] buf = new byte[512];
            FileInputStream in = new FileInputStream(ms);
            for (long pos = 0; pos < fileSize; pos += 512) {
                if (in.read(buf) == -1) {
                    break;
                }
                blks.add(new MiniSeed(buf));
            }
            expResult.add(blks);
        }


        ArrayList<TreeSet<MiniSeed>> result = new ArrayList<TreeSet<MiniSeed>>();
        cwbServer = new FDSNDataServerMSEED();
        cwbServer.query(begin, duration, nsclSelectString);

        while (cwbServer.hasNext()) {
            result.add(cwbServer.getNext());
        }

        assertEquals("collection lengths", expResult.size(), result.size());

        for (int i = 0; i < result.size(); i++) {
            assertMiniSeedCollectionEquals(
					"\n" + expResult.get(i).get(0) +
					"\n" + result.get(i).first(),
					expResult.get(i), result.get(i));
        }
    }

    private static void assertMiniSeedCollectionEquals(String message,
            Collection<MiniSeed> c1,
            Collection<MiniSeed> c2) {

        assertEquals(message + ": size mismatch", c1.size(), c2.size());

        Iterator<MiniSeed> i1 = c1.iterator();
        Iterator<MiniSeed> i2 = c2.iterator();

        while (i1.hasNext() && i2.hasNext()) {
			MiniSeed o1 = i1.next();
			MiniSeed o2 = i2.next();
            assertEquals(message + ": comparison inequality (" + o1.toString() +
					"," + o2.toString() + ")", 0, o1.compareTo(o2));

			assertArrayEquals("MiniSeed not byte matched", o1.getBuf(), o2.getBuf());
        }
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
			Comparable o1 = i1.next();
			Comparable o2 = i2.next();
            assertEquals(message + ": comparison inequality (" + o1.toString() +
					"," + o2.toString() + ")", 0, o1.compareTo(o2));
        }

    }
}