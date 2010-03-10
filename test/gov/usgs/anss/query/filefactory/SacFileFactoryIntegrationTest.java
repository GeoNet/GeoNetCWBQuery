/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.filefactory;

import edu.sc.seis.TauP.SacTimeSeries;
import edu.sc.seis.TauP.SacTimeSeriesTestUtil;
import gov.usgs.anss.query.cwb.data.CWBDataServerMSEED;
import gov.usgs.anss.query.metadata.MetaDataServer;
import gov.usgs.anss.query.metadata.MetaDataServerImpl;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import nz.org.geonet.quakeml.v1_0_1.client.QuakemlFactory;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

/**
 *
 * @author geoffc
 */
@RunWith(Parameterized.class)
public class SacFileFactoryIntegrationTest {

    @Parameters
    public static Collection data() throws Exception {
        return Arrays.asList(new Object[][]{
                    {
                        new CWBDataServerMSEED("cwb.geonet.org.nz", 80),
                        new MetaDataServerImpl("cwb-pub.cr.usgs.gov", 2052),
                        "NZMRZ..HH.10",
                        "%N.sac",
                        true, // output expected
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.sac"},
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        "nm",
                        true, // paz files expected
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.sac.pz", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.sac.pz", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.sac.pz"},
                        null //quakml
                    },
                    { // Will have metadata but no paz files due to null unit.
                        new CWBDataServerMSEED("cwb.geonet.org.nz", 80),
                        new MetaDataServerImpl("cwb-pub.cr.usgs.gov", 2052),
                        "NZMRZ..HH.10",
                        "%N.sac",
                        true, // output expected
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.sac"},
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        null,
                        false, // paz files expected
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.sac.pz", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.sac.pz", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.sac.pz"},
                        null //quakml
                    },
                    { // No meta-data
                        new CWBDataServerMSEED("cwb.geonet.org.nz", 80),
                        null,
                        "NZMRZ..HH.10",
                        "%N.sac",
                        true, // output expected
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-meta/NZMRZ__HHZ10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-meta/NZMRZ__HHN10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-meta/NZMRZ__HHE10.sac"},
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        "nm",
                        false, // paz files expected
                        new String[]{"null"},
                        null //quakml
                    },
                    { // No sac if gaps - shouldn't be any
                        new CWBDataServerMSEED("cwb.geonet.org.nz", 80),
                        new MetaDataServerImpl("cwb-pub.cr.usgs.gov", 2052),
                        "NZMRZ..HH.10",
                        "%N.sac",
                        true, // output expected
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.sac"},
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        false, //gaps
                        true, //trim
                        "nm",
                        true, // paz files expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.sac.pz",
                            "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.sac.pz", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.sac.pz"},
                        null //quakml
                    },
                    { // MS has gaps should produce null sac.
                        new CWBDataServerMSEED("cwb.geonet.org.nz", 80),
                        new MetaDataServerImpl("cwb-pub.cr.usgs.gov", 2052),
                        "NZBFZ..HHE10",
                        "%N.sac",
                        false, // output expected
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/gaps/NZBFZ__HHE10.sac",},
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        false, //gaps
                        true, //trim
                        "nm",
                        false, // paz files expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/gaps/NZBFZ__HHE10.sac.pz",},
                        null //quakml
                    },
                    { // MS has gaps but we allow them in the sac.
                        new CWBDataServerMSEED("cwb.geonet.org.nz", 80),
                        new MetaDataServerImpl("cwb-pub.cr.usgs.gov", 2052),
                        "NZBFZ..HHE10",
                        "%N.sac",
                        true, // output expected
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/gaps/NZBFZ__HHE10.sac",},
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        "nm",
                        true, // paz files expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/gaps/NZBFZ__HHE10.sac.pz",},
                        null //quakml
                    },
                    { // Event data.
                        new CWBDataServerMSEED("cwb.geonet.org.nz", 80),
                        new MetaDataServerImpl("cwb-pub.cr.usgs.gov", 2052),
                        "NZTSZ..HHN10",
                        "%z%y%M%D%h%m.%s.%c.%l.%n.sac",
                        true, // output expected
                        new String[]{"/gov/usgs/anss/query/filefactory/event/200705120730.TSZ.HHN.10.NZ.sac",},
                        new DateTime(2007, 5, 12, 7, 30, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        "nm",
                        true, // paz files expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.sac.pz",},
                        new QuakemlFactory().getQuakeml(SacFileFactoryIntegrationTest.class.getResourceAsStream("/gov/usgs/anss/query/filefactory/quakeml_2732452.xml"), null) //quakml
                    },});
    }
    private CWBDataServerMSEED cwbServer;
    private MetaDataServer mdServer;
    private String nsclSelectString;
    private String fileMask;
    private boolean sacFilesExpected;
    private String[] expectedSacFiles;
    private DateTime begin;
    private double duration;
    private Integer fill;
    private boolean trim;
    private String pazUnits;
    private boolean pazFilesExpected;
    String[] expectedPazFiles;
    private Quakeml quakeml;
    private boolean gaps;
    private ArrayList<SacTimeSeries> expectedSac;
    private Iterator<SacTimeSeries> expectedSacIter;

    public SacFileFactoryIntegrationTest(CWBDataServerMSEED cwbServer, MetaDataServer mdServer, String nsclSelectString, String fileMask, boolean outputExpected, String[] expectedSacFiles, DateTime begin, double duration, Integer fill, boolean gaps, boolean trim, String pazUnits, boolean pazFilesExpected, String[] expectedPazFiles, Quakeml quakeml) throws Exception {
        this.cwbServer = cwbServer;
        this.mdServer = mdServer;
        this.nsclSelectString = nsclSelectString;
        this.fileMask = fileMask;
        this.sacFilesExpected = outputExpected;
        this.expectedSacFiles = expectedSacFiles;
        this.begin = begin;
        this.duration = duration;
        this.fill = fill;
        this.trim = trim;
        this.pazUnits = pazUnits;
        this.pazFilesExpected = pazFilesExpected;
        this.expectedPazFiles = expectedPazFiles;
        this.quakeml = quakeml;
        this.gaps = gaps;
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * Integration test for SacFileFactory - runs against real server.
     * Does not replace black box tests.
     *
     * See also SacFileFactoryTest which runs against mocks.
     */
    @Test
    public void testMakeTimeSeries() throws Exception {


        SacFileFactory sacFileFactory = new SacFileFactory();
        sacFileFactory.setCWBDataServer(cwbServer);
        sacFileFactory.setMetaDataServer(mdServer);

        sacFileFactory.makeFiles(begin, duration, nsclSelectString, folder.getRoot().getAbsolutePath() + File.separator + fileMask, fill, gaps, trim, pazUnits, quakeml);

        for (String filename : expectedSacFiles) {
            SacTimeSeries expResult = null;
            SacTimeSeries result = null;

            String[] tmp = filename.split("\\/");
            File expectedSacFileName = new File(folder.getRoot().getAbsolutePath() + File.separator + tmp[tmp.length - 1]);
            File expectedPazFileName = new File(folder.getRoot().getAbsolutePath() + File.separator + tmp[tmp.length - 1] + ".pz");

            if (sacFilesExpected) {
            }

            if (!sacFilesExpected) {
                assertFalse("Found unexpected SAC file.", expectedSacFileName.exists());
            }

            if (!pazFilesExpected) {
                assertFalse("Found unexpected PAZ file.", expectedPazFileName.exists());
            }

            if (pazFilesExpected) {
                assertTrue("Didn't find expected PAZ file.", expectedPazFileName.exists());
                assertTrue("Didn't find a file at expected PAZ file.", expectedPazFileName.isFile());
                assertTrue("Found empty PAZ file.", expectedPazFileName.length() > 0l);
            // Will not compare the contents of the response file.
            // This could change over time and break the test (which would break
            // other tests here as well).
            }


            if (sacFilesExpected) {

                expResult = SacTimeSeriesTestUtil.loadSacTimeSeriesFromClasspath(filename);
                result = new SacTimeSeries();
                result.read(expectedSacFileName);

                assertEquals("length ", result.y.length, expResult.y.length);

                for (int i = 0; i < result.y.length; i++) {
                    assertEquals("data " + i, result.y[i], expResult.y[i], 0.0);
                }

                assertEquals("nvhdr", result.nvhdr, expResult.nvhdr);
                assertEquals("b", result.b, expResult.b, 0.01);
                assertEquals("e", result.e, expResult.e, 0.01);
                assertEquals("iftype", result.iftype, expResult.iftype);
                assertEquals("leven", result.leven, expResult.leven);
                assertEquals("delta", result.delta, expResult.delta, 0.001);  // Slight discrepancy
                assertEquals("depmin", result.depmin, expResult.depmin, 0.0);
                assertEquals("depmax", result.depmax, expResult.depmax, 0.0);

                assertEquals("nzyear", result.nzyear, expResult.nzyear);
                assertEquals("nzjday", result.nzjday, expResult.nzjday);
                assertEquals("nzhour", result.nzhour, expResult.nzhour);
                assertEquals("nzmin", result.nzmin, expResult.nzmin);
                assertEquals("nzsec", result.nzsec, expResult.nzsec);
                assertEquals("nzmsec", result.nzmsec, expResult.nzmsec, 2);

                assertEquals("iztype", result.iztype, expResult.iztype);

                assertEquals("knetwk", result.knetwk, expResult.knetwk);
                assertEquals("kstnm", result.kstnm, expResult.kstnm);
                assertEquals("kcmpn", result.kcmpnm, expResult.kcmpnm);
                assertEquals("khole", result.khole, expResult.khole);

                assertEquals("Lat", result.stla, expResult.stla, 0.00001);
                assertEquals("Lon", result.stlo, expResult.stlo, 0.00001);
                assertEquals("Elev", result.stel, expResult.stel, 0.0);
                assertEquals("Depth", result.stdp, expResult.stdp, 0.0);
                assertEquals("Azimuth", result.cmpaz, expResult.cmpaz, 0.0);
                assertEquals("Inc", result.cmpinc, expResult.cmpinc, 0.0);

                if (quakeml != null) {
                    assertEquals("lat", result.evla, expResult.evla, 0.00001);
                    assertEquals("lon", result.evlo, expResult.evlo, 0.00001);
                    assertEquals("dep", result.evdp, expResult.evdp, 0.1);
                    assertEquals("mag", result.mag, expResult.mag, 0.001);
                    assertEquals("imagtyp", result.imagtyp, expResult.imagtyp);
                    assertEquals("ievtyp", result.ievtyp, expResult.ievtyp);
                    assertEquals("lcalda", result.lcalda, expResult.lcalda);

                    assertEquals("Phase ", result.kt0, expResult.kt0.trim());
                    assertEquals("Phase t", result.t0, expResult.t0, 0.001);
                }

            }
        }
    }
}