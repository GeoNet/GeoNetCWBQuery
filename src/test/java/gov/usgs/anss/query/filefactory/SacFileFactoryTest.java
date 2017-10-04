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
import edu.sc.seis.TauP.SacTimeSeriesTestUtil;
import gov.usgs.anss.query.CustomEvent;
import gov.usgs.anss.query.cwb.data.CWBDataServerMSEEDMock;
import gov.usgs.anss.query.metadata.MetaDataServerMock;
import nz.org.geonet.simplequakeml.domain.Event;
import nz.org.geonet.simplequakeml.domain.Pick;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author geoffc
 */
@RunWith(Parameterized.class)
public class SacFileFactoryTest {

    @Parameters
    public static Collection data() throws Exception {
        ArrayList picks = new ArrayList();
        picks.add(new Pick("S*", "manual", "confirmed", "2007-05-12T07:41:21.875Z", 0.29f, "NZ", "TSZ", null, "HHN"));

        Event event1 = new Event("smi:geonet.org.nz/event/2737452g", "earthquake", "GNS", "2007-05-12T07:41:04.874Z", -40.60804f, 176.13933f
                , 17.9463f, 4.389f, "ML", picks);

        return Arrays.asList(new Object[][]{
                    {
                        new CWBDataServerMSEEDMock(),
                        new MetaDataServerMock(),
                        "NZMRZ..HH.10",
                        "%N.sac",
                        true, // output expected
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.ms", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.ms", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.ms"},
                        new String[]{"NZMRZ  HHZ10", "NZMRZ  HHN10", "NZMRZ  HHE10"},
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.sac.pz", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.sac.pz", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.sac.pz"},
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.sac"},
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        "nm",
                        true, // paz files expected
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.sac.pz", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.sac.pz", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.sac.pz"},
                        null, //quakml
                        false, //picks
                        null, //customEvent
                        null, //synthetic
                        false //extendedPhases
                    },
                    { // Will have metadata but no paz files due to null unit.
                        new CWBDataServerMSEEDMock(),
                        new MetaDataServerMock(),
                        "NZMRZ..HH.10",
                        "%N.sac",
                        true, // output expected
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.ms", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.ms", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.ms"},
                        new String[]{"NZMRZ  HHZ10", "NZMRZ  HHN10", "NZMRZ  HHE10"},
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.sac.pz", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.sac.pz", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.sac.pz"},
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.sac"},
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        null,
                        false, // paz files expected
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.sac.pz", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.sac.pz", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.sac.pz"},
                        null, //quakml
                        false, //picks
                        null, //customEvent
                        null, //synthetic
                        false //extendedPhases
                    },
                    { // No meta-data
                        new CWBDataServerMSEEDMock(),
                        null,
                        "NZMRZ..HH.10",
                        "%N.sac",
                        true, // output expected
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.ms", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.ms", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.ms"},
                        null,
                        null,
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/no-meta/NZMRZ__HHZ10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-meta/NZMRZ__HHN10.sac", "/test-data/gov/usgs/anss/query/filefactory/no-meta/NZMRZ__HHE10.sac"},
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        "nm",
                        false, // paz files expected
                        new String[]{"null"},
                        null, //quakml
                        false, //picks
                        null, //customEvent
                        null, //synthetic
                        false //extendedPhases
                    },
                    { // No sac if gaps - shouldn't be any
                        new CWBDataServerMSEEDMock(),
                        new MetaDataServerMock(),
                        "NZMRZ..HH.10",
                        "%N.sac",
                        true, // output expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.ms",
                            "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.ms", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.ms"},
                        new String[]{"NZMRZ  HHZ10", "NZMRZ  HHN10", "NZMRZ  HHE10"},
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.sac.pz",
                            "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.sac.pz", "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.sac.pz"},
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
                        null, //quakml
                        false, //picks
                        null, //customEvent
                        null, //synthetic
                        false //extendedPhases
                    },
                    { // MS has gaps should produce null sac.
                        new CWBDataServerMSEEDMock(),
                        new MetaDataServerMock(),
                        "NZBFZ..HHE10",
                        "%N.sac",
                        false, // output expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/gaps/NZBFZ__HHE10.ms"},
                        new String[]{"NZBFZ  HHE10"},
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/gaps/NZBFZ__HHE10.sac.pz"},
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/gaps/NZBFZ__HHE10.sac"},
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        false, //gaps
                        true, //trim
                        "nm",
                        false, // paz files expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/gaps/NZBFZ__HHE10.sac.pz",},
                        null, //quakml
                        false, //picks
                        null, //customEvent
                        null, //synthetic
                        false //extendedPhases
                    },
                    { // MS has gaps but we allow them in the sac.
                        new CWBDataServerMSEEDMock(),
                        new MetaDataServerMock(),
                        "NZBFZ..HHE10",
                        "%N.sac",
                        true, // output expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/gaps/NZBFZ__HHE10.ms"},
                        new String[]{"NZBFZ  HHE10"},
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/gaps/NZBFZ__HHE10.sac.pz"},
                        new String[]{"/test-data/gov/usgs/anss/query/filefactory/gaps/NZBFZ__HHE10.sac"},
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        "nm",
                        true, // paz files expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/gaps/NZBFZ__HHE10.sac.pz",},
                        null, //quakml
                        false, //picks
                        null, //customEvent
                        null, //synthetic
                        false //extendedPhases
                    },
                    { // Event data.
                        new CWBDataServerMSEEDMock(),
                        new MetaDataServerMock(),
                        "NZTSZ..HHN10",
                        "%z%y%M%D%h%m.%s.%c.%l.%n.sac",
                        true, // output expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.ms"},
                        new String[]{"NZTSZ  HHN10"},
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.sac.pz"},
                        new String[]{"/gov/usgs/anss/query/filefactory/event/200705120730.TSZ.HHN.10.NZ.sac"},
                        new DateTime(2007, 5, 12, 7, 30, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        "nm",
                        true, // paz files expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.sac.pz",},
                        event1,
                        true, //picks
                        null, //customEvent
                        null, //synthetic
                        false //extendedPhases
                    },
                    { // Event data from quakeml but don't add picks.
                        new CWBDataServerMSEEDMock(),
                        new MetaDataServerMock(),
                        "NZTSZ..HHN10",
                        "%z%y%M%D%h%m.%s.%c.%l.%n.sac",
                        true, // output expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.ms"},
                        new String[]{"NZTSZ  HHN10"},
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.sac.pz"},
                        new String[]{"/gov/usgs/anss/query/filefactory/event/no-picks/200705120730.TSZ.HHN.10.NZ.sac"},
                        new DateTime(2007, 5, 12, 7, 30, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        "nm",
                        true, // paz files expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.sac.pz",},
                        event1,
                        false, //picks
                        null, //customEvent
                        null, //synthetic
                        false //extendedPhases
                    },
                    { // Event data from quakeml but only add picks from iasp91.
                        new CWBDataServerMSEEDMock(),
                        new MetaDataServerMock(),
                        "NZTSZ..HHN10",
                        "%z%y%M%D%h%m.%s.%c.%l.%n.sac",
                        true, // output expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.ms"},
                        new String[]{"NZTSZ  HHN10"},
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.sac.pz"},
                        new String[]{"/gov/usgs/anss/query/filefactory/event/syn-only/200705120730.TSZ.HHN.10.NZ.sac"},
                        new DateTime(2007, 5, 12, 7, 30, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        "nm",
                        true, // paz files expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.sac.pz",},
                        event1,
                        false, //picks
                        null, //customEvent
                        "iasp91", //synthetic
                        false //extendedPhases
                    },
                    { // Event data and picks from quakeml and picks from iasp91.
                        new CWBDataServerMSEEDMock(),
                        new MetaDataServerMock(),
                        "NZTSZ..HHN10",
                        "%z%y%M%D%h%m.%s.%c.%l.%n.sac",
                        true, // output expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.ms"},
                        new String[]{"NZTSZ  HHN10"},
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.sac.pz"},
                        new String[]{"/gov/usgs/anss/query/filefactory/event/quakeml-and-syn/200705120730.TSZ.HHN.10.NZ.sac"},
                        new DateTime(2007, 5, 12, 7, 30, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        "nm",
                        true, // paz files expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.sac.pz",},
                        event1,
                        true, //picks
                        null, //customEvent
                        "iasp91", //synthetic
                        false //extendedPhases
                    },
                    { // Event data from quakeml but only add extended picks from iasp91. Not really far enough away to
                        // get extra phases.
                        new CWBDataServerMSEEDMock(),
                        new MetaDataServerMock(),
                        "NZTSZ..HHN10",
                        "%z%y%M%D%h%m.%s.%c.%l.%n.sac",
                        true, // output expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.ms"},
                        new String[]{"NZTSZ  HHN10"},
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.sac.pz"},
                        new String[]{"/gov/usgs/anss/query/filefactory/event/syn-only-extended/200705120730.TSZ.HHN.10.NZ.sac"},
                        new DateTime(2007, 5, 12, 7, 30, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        "nm",
                        true, // paz files expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.sac.pz",},
                        event1,
                        false, //picks
                        null, //customEvent
                        "iasp91", //synthetic
                        true //extendedPhases
                    },
                    { // No quakeml and custom event add extended picks from iasp91.
                        new CWBDataServerMSEEDMock(),
                        new MetaDataServerMock(),
                        "NZTSZ..HHN10",
                        "%z%y%M%D%h%m.%s.%c.%l.%n.sac",
                        true, // output expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.ms"},
                        new String[]{"NZTSZ  HHN10"},
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.sac.pz"},
                        new String[]{"/gov/usgs/anss/query/filefactory/event/syn-only/200705120730.TSZ.HHN.10.NZ.sac"},
                        new DateTime(2007, 5, 12, 7, 30, 0, 0, DateTimeZone.UTC),
                        1800d, //duration
                        new Integer(-12345), //fill
                        true, //gaps
                        true, //trim
                        "nm",
                        true, // paz files expected
                        new String[]{
                            "/test-data/gov/usgs/anss/query/filefactory/event/NZTSZ__HHN10.sac.pz",},
                        null, // no quamkeml
                        false, //picks
                        new CustomEvent(new DateTime(2007, 5, 12, 7, 41, 4, 874, DateTimeZone.UTC), -40.60804, 176.13933, 17.9463, 4.389, SacHeaders.SacMagType.ML, SacHeaders.SacEventType.EARTHQUAKE), //customEvent
                        "iasp91", //synthetic
                        false //extendedPhases
                    },});
    }
    private CWBDataServerMSEEDMock cwbServer;
    private MetaDataServerMock mdServer;
    private String nsclSelectString;
    private String fileMask;
    private boolean sacFilesExpected;
    private String[] mseedFiles;
    private String[] nscls;
    private String[] pazFiles;
    private String[] expectedSacFiles;
    private DateTime begin;
    private double duration;
    private Integer fill;
    private boolean trim;
    private String pazUnits;
    private boolean pazFilesExpected;
    String[] expectedPazFiles;
    private Event event;
    private boolean gaps;
    private boolean picks;
    private CustomEvent customEvent;
    private String synthetic;
    private boolean extendedPhases;
    private ArrayList<SacTimeSeries> expectedSac;
    private Iterator<SacTimeSeries> expectedSacIter;

    public SacFileFactoryTest(CWBDataServerMSEEDMock cwbServer, MetaDataServerMock mdServer, String nsclSelectString, String fileMask, boolean outputExpected, String[] mseedFiles, String[] nscls, String[] pazFiles, String[] expectedSacFiles, DateTime begin, double duration, Integer fill, boolean gaps, boolean trim, String pazUnits, boolean pazFilesExpected, String[] expectedPazFiles, Event event, boolean picks, CustomEvent customEvent, String synthetic, boolean extendedPhases) throws Exception {
        this.cwbServer = cwbServer;
        this.mdServer = mdServer;
        this.nsclSelectString = nsclSelectString;
        this.fileMask = fileMask;
        this.mseedFiles = mseedFiles;
        this.nscls = nscls;
        this.pazFiles = pazFiles;
        this.sacFilesExpected = outputExpected;
        this.expectedSacFiles = expectedSacFiles;
        this.begin = begin;
        this.duration = duration;
        this.fill = fill;
        this.trim = trim;
        this.pazUnits = pazUnits;
        this.pazFilesExpected = pazFilesExpected;
        this.expectedPazFiles = expectedPazFiles;
        this.event = event;
        this.gaps = gaps;
        this.picks = picks;
        this.customEvent = customEvent;
        this.synthetic = synthetic;
        this.extendedPhases = extendedPhases;
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
    public void testMakeFiles() throws Exception {

        cwbServer.loadMSEEDFiles(mseedFiles);

        if (mdServer != null) {
            mdServer.loadPAZFile(nscls, pazFiles);
        }

        SacFileFactory sacFileFactory = new SacFileFactory();
        sacFileFactory.setCWBDataServer(cwbServer);
        sacFileFactory.setMetaDataServer(mdServer);
        sacFileFactory.setCustomEvent(this.customEvent);
        sacFileFactory.setSynthetic(this.synthetic);
        sacFileFactory.setTrim(this.trim);
        sacFileFactory.setFill(this.fill);
        sacFileFactory.setGaps(this.gaps);
        sacFileFactory.setPzunit(this.pazUnits);
        sacFileFactory.setEvent(this.event);
        sacFileFactory.setPicks(picks);

        sacFileFactory.makeFiles(begin, duration, nsclSelectString, folder.getRoot().getAbsolutePath() + File.separator + fileMask);

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
                SacTimeSeriesTestUtil.compareSacTimeSeries(result, expResult);
            }
        }
    }
}

