/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.filefactory;

import gov.usgs.anss.query.cwb.data.CWBDataServerMSEEDMock;
import edu.sc.seis.TauP.SacTimeSeries;
import edu.sc.seis.TauP.SacTimeSeriesTestUtil;
import gov.usgs.anss.query.metadata.MetaDataServerMock;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

/**
 *
 * @author geoffc
 */
@RunWith(Parameterized.class)
public class SacFileFactoryTest {

    @Parameters
    public static Collection data() throws Exception {
        return Arrays.asList(new Object[][]{
                    {
                        new CWBDataServerMSEEDMock("dummy", 666),
                        new MetaDataServerMock("dummy", 666),
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.ms", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.ms", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.ms"},
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.sac.pz", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.sac.pz", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.sac.pz"},
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.sac"},
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        null //quakml
                    },
                    { // No meta-data
                        new CWBDataServerMSEEDMock("dummy", 666),
                        null,
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-meta/NZMRZ__HHZ10.ms", "/test-data/gov/usgs/anss/query/filefactory/no-meta/NZMRZ__HHN10.ms", "/test-data/gov/usgs/anss/query/filefactory/no-meta/NZMRZ__HHE10.ms"},
                        new String[]{"null"},
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-meta/NZMRZ__HHZ10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-meta/NZMRZ__HHN10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-meta/NZMRZ__HHE10.sac"},
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        null //quakml
                    },
                    { // No sac if gaps - shouldn't be any

                        new CWBDataServerMSEEDMock("dummy", 666),
                        new MetaDataServerMock("dummy", 666),
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.ms",
                            "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.ms", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.ms"},
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.sac.pz",
                            "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.sac.pz", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.sac.pz"},
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.sac",
                            "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.sac"},
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        false, //gaps
                        true, //trim
                        null //quakml
                    },
                    { // MS has gaps should produce null sac.
                        new CWBDataServerMSEEDMock("dummy", 666),
                        new MetaDataServerMock("dummy", 666),
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/gaps/NZBFZ__HHE10.ms",},
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/gaps/NZBFZ__HHE10.sac.pz",},
                        new String[]{
                            "null",},
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        false, //gaps
                        true, //trim
                        null //quakml
                    },
                    { // MS has gaps but we allow them in the sac.
                        new CWBDataServerMSEEDMock("dummy", 666),
                        new MetaDataServerMock("dummy", 666),
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/gaps/NZBFZ__HHE10.ms",},
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/gaps/NZBFZ__HHE10.sac.pz",},
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/gaps/NZBFZ__HHE10.sac",},
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        null //quakml
                    }, { // Event data.
                        new CWBDataServerMSEEDMock("dummy", 666),
                        new MetaDataServerMock("dummy", 666),
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.ms",},
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.sac.pz",},
                        new String[]{
                            "/gov/usgs/anss/query/filefactory/event/200705120730.TSZ.HHN.10.NZ.sac",},
                        new DateTime(2007, 5, 12, 7, 30, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        new QuakemlFactory().getQuakeml(SacFileFactoryTest.class.getResourceAsStream("/gov/usgs/anss/query/filefactory/quakeml_2732452.xml"), null) //quakml
                    },});

    }
    private CWBDataServerMSEEDMock cwbServer;
    private MetaDataServerMock mdServer;
    private String[] mseedFiles;
    private String[] pazFiles;
    private String[] expectedSacFiles;
    private DateTime begin;
    private double duration;
    private Integer fill;
    private boolean trim;
    private Quakeml quakeml;
    private boolean gaps;
    private ArrayList<SacTimeSeries> expectedSac;
    private Iterator<SacTimeSeries> expectedSacIter;

    public SacFileFactoryTest(CWBDataServerMSEEDMock cwbServer, MetaDataServerMock mdServer, String[] mseedFiles, String[] pazFiles, String[] expectedSacFiles, DateTime begin, double duration, Integer fill, boolean gaps, boolean trim, Quakeml quakeml) throws Exception {
        this.cwbServer = cwbServer;
        this.mdServer = mdServer;
        this.mseedFiles = mseedFiles;
        this.pazFiles = pazFiles;
        this.expectedSacFiles = expectedSacFiles;
        this.begin = begin;
        this.duration = duration;
        this.fill = fill;
        this.trim = trim;
        this.quakeml = quakeml;
        this.gaps = gaps;

        expectedSac =
                new ArrayList<SacTimeSeries>();
        for (String filename : expectedSacFiles) {
            if (filename.matches("null")) {
                expectedSac.add(null);
            } else {
                expectedSac.add(SacTimeSeriesTestUtil.loadSacTimeSeriesFromClasspath(filename));
            }

        }

        expectedSacIter = expectedSac.iterator();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of makeTimeSeries method, of class SacFileFactory.
     * Load the MiniSeed treeset from a miniseed file in the test data jar.
     * Load the SacTimeSeries object from the corrosponding sac file in the test data jar.
     * Compare data.
     */
    @Test
    public void testMakeTimeSeries() throws Exception {

        cwbServer.loadMSEEDFiles(mseedFiles);

        if (mdServer != null) {
            mdServer.loadPAZFile(pazFiles);
        }

        SacFileFactory sacFileFactory = new SacFileFactory();
        sacFileFactory.setCWBDataServer(cwbServer);
        sacFileFactory.setMetaDataServer(mdServer);

        // The nsclSelectString does nothing on the mocked server 
        cwbServer.query(begin, duration, "DUMMY       ");

        while (expectedSacIter.hasNext()) {

            SacTimeSeries expResult = expectedSacIter.next();
            SacTimeSeries result = sacFileFactory.makeTimeSeries(cwbServer.getNext(), begin, duration, fill, gaps, trim, quakeml);

            if (expResult == null) {
                assertEquals("expected null", result, null);
            }

            if (expResult != null && result != null) {

                assertEquals("length ", result.y.length, expResult.y.length);

                for (int i = 0; i <
                        result.y.length; i++) {
                    assertEquals("data " + i, result.y[i], expResult.y[i], 0.0);
                }

                assertEquals("nvhdr", result.nvhdr, expResult.nvhdr);
                assertEquals("b ± " + Math.ulp((float) expResult.b),
                        (float) result.b, expResult.b, Math.ulp((float) expResult.b));
                assertEquals("e ± " + Math.ulp((float) expResult.e),
                        result.e, expResult.e, Math.ulp((float) expResult.e));
                assertEquals("iftype", result.iftype, expResult.iftype);
                assertEquals("leven", result.leven, expResult.leven);
                assertEquals("delta ± " + Math.ulp((float) expResult.delta),
                        result.delta, expResult.delta, Math.ulp((float) expResult.delta));
                assertEquals("depmin", result.depmin, expResult.depmin, 0.0);
                assertEquals("depmax", result.depmax, expResult.depmax, 0.0);

                assertEquals("nzyear", result.nzyear, expResult.nzyear);
                assertEquals("nzjday", result.nzjday, expResult.nzjday);
                assertEquals("nzhour", result.nzhour, expResult.nzhour);
                assertEquals("nzmin", result.nzmin, expResult.nzmin);
                assertEquals("nzsec", result.nzsec, expResult.nzsec);
                assertEquals("nzmsec", result.nzmsec, expResult.nzmsec);

                assertEquals("iztype", result.iztype, expResult.iztype);

                // Some white space padding gets added when writing header.
                assertEquals("knetwk", result.knetwk, expResult.knetwk.trim());
                assertEquals("kstnm", result.kstnm, expResult.kstnm.trim());
                assertEquals("kcmpn", result.kcmpnm, expResult.kcmpnm.trim());
                assertEquals("khole", result.khole, expResult.khole.trim());

                assertEquals("Lat", result.stla, expResult.stla, Math.ulp((float) expResult.stla));
                assertEquals("Lon", result.stlo, expResult.stlo, Math.ulp((float) expResult.stlo));
                assertEquals("Elev", result.stel, expResult.stel, 0.0);
                assertEquals("Depth", result.stdp, expResult.stdp, 0.0);
                assertEquals("Azimuth", result.cmpaz, expResult.cmpaz, 0.0);
                assertEquals("Inc", result.cmpinc, expResult.cmpinc, 0.0);

                if (quakeml != null) {
                    assertEquals("event lat", result.evla, expResult.evla, Math.ulp((float) expResult.evla));
                    assertEquals("event lon", result.evlo, expResult.evlo, Math.ulp((float) expResult.evlo));
                    assertEquals("event dep", result.evdp, expResult.evdp, Math.ulp((float) expResult.evdp));
                    assertEquals("event mag", result.mag, expResult.mag, Math.ulp((float) expResult.mag));
                    assertEquals("imagtyp", result.imagtyp, expResult.imagtyp);
                    assertEquals("ievtyp", result.ievtyp, expResult.ievtyp);
                    assertEquals("lcalda", result.lcalda, expResult.lcalda);

                    assertEquals("Phase ", result.kt0, expResult.kt0.trim());
                    assertEquals("Phase t", result.t0, expResult.t0, Math.ulp((float) expResult.t0));
                }
            }
        }
    }
}
