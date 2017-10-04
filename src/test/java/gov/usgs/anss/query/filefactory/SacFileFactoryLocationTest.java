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
package gov.usgs.anss.query.filefactory;

import edu.sc.seis.TauP.SacTimeSeries;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author Geoff Clitheroe
 */
public class SacFileFactoryLocationTest {

    public SacFileFactoryLocationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testMakeSacWithLocation() {

        SacTimeSeries sac = new SacTimeSeries();

        sac.knetwk = "AU";
        sac.kstnm = "ARMA";
        sac.kcmpnm = "BHZ";
        sac.khole = "10";

        sac.npts = 10;

        sac.y = new double[10];

        for (int i = 0; i < 10; i++) {
            sac.y[i] = 99.9;
        }

        SacFileFactory ff = new SacFileFactory();

        ff.outputFile(sac, new DateTime(2007, 5, 12, 7, 30, 0, 0, DateTimeZone.UTC), folder.getRoot().getAbsolutePath() + File.separator + "%N.sac", "nm");

        File expectedSacFileName = new File(folder.getRoot().getAbsolutePath() + File.separator + "AUARMA_BHZ10.sac");

        assertTrue("Didn't find expected SAC file.", expectedSacFileName.exists());
    }

    @Test
    public void testMakeSacWithNoLocation() {

        SacTimeSeries sac = new SacTimeSeries();

        sac.knetwk = "AU";
        sac.kstnm = "ARMA";
        sac.kcmpnm = "BHZ";
        sac.khole = "";

        sac.npts = 10;

        sac.y = new double[10];

        for (int i = 0; i < 10; i++) {
            sac.y[i] = 99.9;
        }

        SacFileFactory ff = new SacFileFactory();

        ff.outputFile(sac, new DateTime(2007, 5, 12, 7, 30, 0, 0, DateTimeZone.UTC), folder.getRoot().getAbsolutePath() + File.separator + "%N.sac", "nm");

        File expectedSacFileName = new File(folder.getRoot().getAbsolutePath() + File.separator + "AUARMA_BHZ__.sac");

        assertTrue("Didn't find expected SAC file.", expectedSacFileName.exists());
    }
}
