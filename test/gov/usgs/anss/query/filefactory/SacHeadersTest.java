/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.filefactory;

import edu.sc.seis.TauP.SacTimeSeries;
import gov.usgs.anss.query.metadata.ChannelMetaData;
import java.util.ArrayList;
import java.util.List;
import nz.org.geonet.quakeml.v1_0_1.client.QuakemlFactory;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author geoffc
 */
public class SacHeadersTest {

    public SacHeadersTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    // For SAC header refer to http://www.iris.edu/manuals/sac/SAC_Manuals/FileFormatPt2.html
    @Test
    public void testSetEventHeaderEventAfterBegin() throws Exception {


// java -jar GeoNetCWBQuery.jar -t sac -b "2007/05/12 07:30:00" -s "NZTHZ..HHZ10" -d 1800 -o %z%y%M%D%h%m.%s.%c.%l.%n.sac

//SAC> chnhdr O gmt 2007 132 7 41 4 874
//SAC> lh o
//
//  FILE: 200705120730.THZ.HHZ.10.NZ.sac - 1
// ------------------------------------
//
//     o = 6.648770e+02
//SAC> chnhdr allt -6.648770e+02 iztype io
//

        DateTime eventTime = new DateTime(2007, 5, 12, 7, 41, 4, 874, DateTimeZone.UTC);
        double eventLat = -40.60804d;
        double eventLon = 176.13933d;
        double eventDepth = 17946.3d;  // meters
        double eventMag = 4.389d;
        int magType = 54;
        int evType = 40;

        SacTimeSeries sac = new SacTimeSeries();

        sac.nzyear = 2007;
        sac.nzjday = 132;
        sac.nzhour = 7;
        sac.nzmin = 29;
        sac.nzsec = 59;
        sac.nzmsec = 997;

        sac.b = 0.000000e+00d;
        sac.e = 1.799990e+03;

        sac.iztype = SacTimeSeries.IB;

        sac = SacHeaders.setEventHeader(sac, eventTime, eventLat, eventLon, eventDepth, eventMag, magType, evType);

        assertEquals("year", sac.nzyear, 2007);
        assertEquals("month", sac.nzjday, 132);
        assertEquals("hour", sac.nzhour, 7);
        assertEquals("minute", sac.nzmin, 41);
        assertEquals("sec", sac.nzsec, 4);
        assertEquals("msec", sac.nzmsec, 874);
        assertEquals("b", sac.b, -6.648770e+02d, 0.0000001);
        assertEquals("e", sac.e, 1.135113e+03, 0.0000001);
        assertEquals("iztype", sac.iztype, SacTimeSeries.IO);
        assertEquals("lat", sac.evla, eventLat, 0.0);
        assertEquals("lon", sac.evlo, eventLon, 0.0);
        assertEquals("dep", sac.evdp, eventDepth, 0.0);
        assertEquals("mag", sac.mag, eventMag, 0.0);
        assertEquals("imagtyp", sac.imagtyp, 54);
        assertEquals("ievtyp", sac.ievtyp, 40);
        assertEquals("lcalda", sac.lcalda, 1);
    }

    @Test
    public void testSetEventHeaderEventBeforeBegin() throws Exception {
        DateTime eventTime = new DateTime(2007, 5, 12, 7, 41, 4, 874, DateTimeZone.UTC);
        double eventLat = -40.60804d;
        double eventLon = 176.13933d;
        double eventDepth = 17946.3d;  // meters
        double eventMag = 4.389d;
        int magType = 54;
        int evType = 40;

        SacTimeSeries sac = new SacTimeSeries();

        sac.nzyear = 2007;
        sac.nzjday = 132;
        sac.nzhour = 7;
        sac.nzmin = 49;
        sac.nzsec = 59;
        sac.nzmsec = 997;

        sac.b = 0.000000e+00d;
        sac.e = 1.799990e+03;

        sac.iztype = SacTimeSeries.IB;

        sac = SacHeaders.setEventHeader(sac, eventTime, eventLat, eventLon, eventDepth, eventMag, magType, evType);

        assertEquals("year", sac.nzyear, 2007);
        assertEquals("month", sac.nzjday, 132);
        assertEquals("hour", sac.nzhour, 7);
        assertEquals("minute", sac.nzmin, 41);
        assertEquals("sec", sac.nzsec, 4);
        assertEquals("msec", sac.nzmsec, 874);
        assertEquals("b", sac.b, 5.351230e+02d, 0.0000001);
        assertEquals("e", sac.e, 2.335113e+03d, 0.0000001);
        assertEquals("iztype", sac.iztype, SacTimeSeries.IO);
        assertEquals("lat", sac.evla, eventLat, 0.0);
        assertEquals("lon", sac.evlo, eventLon, 0.0);
        assertEquals("dep", sac.evdp, eventDepth, 0.0);
        assertEquals("mag", sac.mag, eventMag, 0.0);
        assertEquals("imagtyp", sac.imagtyp, 54);
        assertEquals("ievtyp", sac.ievtyp, 40);
        assertEquals("lcalda", sac.lcalda, 1);
    }

    @Test
    public void testSetEventHeaderEventSameAsBegin() throws Exception {
        DateTime eventTime = new DateTime(2007, 5, 12, 7, 41, 4, 874, DateTimeZone.UTC);
        double eventLat = -40.60804d;
        double eventLon = 176.13933d;
        double eventDepth = 17946.3d;  // meters
        double eventMag = 4.389d;
        int magType = 54;
        int evType = 40;

        SacTimeSeries sac = new SacTimeSeries();

        sac.nzyear = 2007;
        sac.nzjday = 132;
        sac.nzhour = 7;
        sac.nzmin = 41;
        sac.nzsec = 4;
        sac.nzmsec = 874;

        sac.b = 0.000000e+00d;
        sac.e = 1.799990e+03;

        sac.iztype = SacTimeSeries.IB;

        sac = SacHeaders.setEventHeader(sac, eventTime, eventLat, eventLon, eventDepth, eventMag, magType, evType);

        assertEquals("year", sac.nzyear, 2007);
        assertEquals("month", sac.nzjday, 132);
        assertEquals("hour", sac.nzhour, 7);
        assertEquals("minute", sac.nzmin, 41);
        assertEquals("sec", sac.nzsec, 4);
        assertEquals("msec", sac.nzmsec, 874);
        assertEquals("b", sac.b, 0.000000e+00d, 0.0000001);
        assertEquals("e", sac.e, 1.799990e+03, 0.0000001);
        assertEquals("iztype", sac.iztype, SacTimeSeries.IO);
        assertEquals("lat", sac.evla, eventLat, 0.0);
        assertEquals("lon", sac.evlo, eventLon, 0.0);
        assertEquals("dep", sac.evdp, eventDepth, 0.0);
        assertEquals("mag", sac.mag, eventMag, 0.0);
        assertEquals("imagtyp", sac.imagtyp, 54);
        assertEquals("ievtyp", sac.ievtyp, 40);
        assertEquals("lcalda", sac.lcalda, 1);
    }

    @Test
    public void testSetEventHeaderQuakeMl() throws Exception {

        DateTime eventTime = new DateTime(2007, 5, 12, 7, 41, 4, 874, DateTimeZone.UTC);
        double eventLat = -40.60804d;
        double eventLon = 176.13933d;
        double eventDepth = 17946.3d;  // meters
        double eventMag = 4.389d;
        int magType = 54;

        SacTimeSeries sac = new SacTimeSeries();

        sac.nzyear = 2007;
        sac.nzjday = 132;
        sac.nzhour = 7;
        sac.nzmin = 29;
        sac.nzsec = 59;
        sac.nzmsec = 997;

        sac.b = 0.000000e+00d;
        sac.e = 1.799990e+03;

        sac.iztype = SacTimeSeries.IB;

        Quakeml quakeml = new QuakemlFactory().getQuakeml(SacHeadersTest.class.getResourceAsStream("quakeml_2732452.xml"), null);

        sac = SacHeaders.setEventHeader(sac, quakeml);

        assertEquals("year", sac.nzyear, 2007);
        assertEquals("month", sac.nzjday, 132);
        assertEquals("hour", sac.nzhour, 7);
        assertEquals("minute", sac.nzmin, 41);
        assertEquals("sec", sac.nzsec, 4);
        assertEquals("msec", sac.nzmsec, 874);
        assertEquals("b", sac.b, -6.648770e+02d, 0.0);
        assertEquals("e", sac.e, 1.135113e+03, 0.0);
        assertEquals("iztype", sac.iztype, SacTimeSeries.IO);
        assertEquals("lat", sac.evla, eventLat, 0.0);

        assertEquals("lon", sac.evlo, eventLon, 0.0);
        assertEquals("dep", sac.evdp, eventDepth, 0.0);
        assertEquals("mag", sac.mag, eventMag, 0.0);
        assertEquals("imagtyp", sac.imagtyp, 54);
        assertEquals("ievtyp", sac.ievtyp, 40);
        assertEquals("lcalda", sac.lcalda, 1);
    }

    @Test
    public void testSetPicks() {
        List<SacPhasePick> phasePicks = new ArrayList<SacPhasePick>();

        phasePicks.add(new SacPhasePick("P3", 103.0d));
        phasePicks.add(new SacPhasePick("P4", 104.0));
        phasePicks.add(new SacPhasePick("P5", 105.0));
        phasePicks.add(new SacPhasePick("P6", 106.0));
        phasePicks.add(new SacPhasePick("P0", 100.0));
        phasePicks.add(new SacPhasePick("P1", 101.0));
        phasePicks.add(new SacPhasePick("P2", 102.0));
        phasePicks.add(new SacPhasePick("P7", 107.0));
        phasePicks.add(new SacPhasePick("P8", 108.0));
        phasePicks.add(new SacPhasePick("P9", 109.0));
        phasePicks.add(new SacPhasePick("P10", 110.0));

        SacTimeSeries sac = new SacTimeSeries();

        sac = SacHeaders.setHeaderPhasePicks(sac, phasePicks);

        assertEquals("P0", sac.kt0, "P0");
        assertEquals("P0 t", sac.t0, 100.0d, 0.0);
        assertEquals("P1", sac.kt1, "P1");
        assertEquals("P1 t", sac.t1, 101.0d, 0.0);
        assertEquals("P2", sac.kt2, "P2");
        assertEquals("P2 t", sac.t2, 102.0d, 0.0);
        assertEquals("P3", sac.kt3, "P3");
        assertEquals("P3 t", sac.t3, 103.0d, 0.0);
        assertEquals("P4", sac.kt4, "P4");
        assertEquals("P4 t", sac.t4, 104.0d, 0.0);
        assertEquals("P5", sac.kt5, "P5");
        assertEquals("P5 t", sac.t5, 105.0d, 0.0);
        assertEquals("P6", sac.kt6, "P6");
        assertEquals("P6 t", sac.t6, 106.0d, 0.0);
        assertEquals("P7", sac.kt7, "P7");
        assertEquals("P7 t", sac.t7, 107.0d, 0.0);
        assertEquals("P8", sac.kt8, "P8");
        assertEquals("P8 t", sac.t8, 108.0d, 0.0);
        assertEquals("P9", sac.kt9, "P9");
        assertEquals("P9 t", sac.t9, 109.0d, 0.0);
    }

    @Test
    public void testSetPhasePicks() throws Exception {
        Quakeml quakeml = new QuakemlFactory().getQuakeml(SacHeadersTest.class.getResourceAsStream("quakeml_2732452.xml"), null);

        SacTimeSeries sac = new SacTimeSeries();
        sac.knetwk = "NZ";
        sac.kstnm = "TSZ";
        sac.kcmpnm = "HHN";

        sac = SacHeaders.setPhasePicks(sac, quakeml);

        assertEquals("P", sac.kt0, "S* mc");
        assertEquals("P t", sac.t0, 17.001d, 0.0);

        sac = new SacTimeSeries();

        assertEquals("P", sac.kt0, "-12345  ");
        assertEquals("P t", sac.t0, -12345.0, 0.0);
    }

    @Test
    public void testSetPhasePicksXed() throws Exception {
        // Checks that a pick with zero weight gets a mode of rejected.
        Quakeml quakeml = new QuakemlFactory().getQuakeml(SacHeadersTest.class.getResourceAsStream("quakeml_2732452.xml"), null);

        SacTimeSeries sac = new SacTimeSeries();
        sac.knetwk = "NZ";
        sac.kstnm = "HOWZ";
        sac.kcmpnm = "EHZ";

        sac = SacHeaders.setPhasePicks(sac, quakeml);

        assertEquals("P", sac.kt0, "P* ar");
        assertEquals("P t", sac.t0, 12.194d, 0.0);
    }

    @Test
    public void testSacEventType() {
        assertEquals("earthquake", 40, SacHeaders.sacEventType("earthquake"));
        assertEquals("explosion", 79, SacHeaders.sacEventType("explosion"));
        assertEquals("quarry blast", 70, SacHeaders.sacEventType("quarry blast"));
        assertEquals("chemical explosion", 43, SacHeaders.sacEventType("chemical explosion"));
        assertEquals("nuclear explosion", 37, SacHeaders.sacEventType("nuclear explosion"));
        assertEquals("landslide", 82, SacHeaders.sacEventType("landslide"));
        assertEquals("debris avalanch", 82, SacHeaders.sacEventType("debris avalanche"));
        assertEquals("rockslide", 82, SacHeaders.sacEventType("rockslide"));
        assertEquals("mine collapse", 82, SacHeaders.sacEventType("mine collapse"));
        assertEquals("volcanic eruption", 82, SacHeaders.sacEventType("volcanic eruption"));
        assertEquals("meteor impact", 82, SacHeaders.sacEventType("meteor impact"));
        assertEquals("plane crash", 82, SacHeaders.sacEventType("plane crash"));
        assertEquals("building collapse", 82, SacHeaders.sacEventType("building collapse"));
        assertEquals("sonic boom", 82, SacHeaders.sacEventType("sonic boom"));
        assertEquals("not existing", 86, SacHeaders.sacEventType("not existing"));
        assertEquals("null", 86, SacHeaders.sacEventType("null"));
        assertEquals("other", 44, SacHeaders.sacEventType("other"));
        assertEquals("somthing made up", 44, SacHeaders.sacEventType("other"));
        assertEquals("quarry blast multiple spaces", 70, SacHeaders.sacEventType(" quarry  blast"));
    }

    @Test
    public void testSacMagType() {
        assertEquals("MB", 52, SacHeaders.sacMagType("MB"));
        assertEquals("MS", 53, SacHeaders.sacMagType("MS"));
        assertEquals("ML", 54, SacHeaders.sacMagType("ML"));
        assertEquals("MW", 55, SacHeaders.sacMagType("MW"));
        assertEquals("MD", 56, SacHeaders.sacMagType("MD"));
        assertEquals("MX", 57, SacHeaders.sacMagType("MX"));
        assertEquals("MB", 57, SacHeaders.sacMagType("something made up"));
    }

    @Test
    public void testSetChannelHeader() {
        ChannelMetaData metaData = new ChannelMetaData("test", "test", "test", "test");
        metaData.setLatitude(-41.28576d);
        metaData.setLongitude(174.76802d);
        metaData.setElevation(0.0d);
        metaData.setDepth(0.0d);
        metaData.setAzimuth(0.0d);
        metaData.setDip(-90.0d);

        SacTimeSeries sac = new SacTimeSeries();
        sac.stla = -41.28576d;
        sac.stlo = 174.76802d;
        sac.stel = 138.0d;
        sac.stdp = 0.0;
        sac.cmpaz = 0.0;
        sac.cmpinc = 0.0;

        SacTimeSeries result = SacHeaders.setChannelHeader(sac, metaData);

        assertEquals("File", sac, result);
        assertEquals("Lat", sac.stla, result.stla, 0.0);
        assertEquals("Lon", sac.stlo, result.stlo, 0.0);
        assertEquals("Elev", sac.stel, result.stel, 0.0);
        assertEquals("Depth", sac.stdp, result.stdp, 0.0);
        assertEquals("Azimuth", sac.cmpaz, result.cmpaz, 0.0);
        assertEquals("Inc", sac.cmpinc, result.cmpinc, 0.0);
    }
}
