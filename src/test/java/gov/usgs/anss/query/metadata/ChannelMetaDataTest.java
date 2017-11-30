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
package gov.usgs.anss.query.metadata;

import gov.usgs.anss.query.NSCL;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author geoffc
 */
public class ChannelMetaDataTest {

    private static MetaDataServerMock metaDataServer;
    private static MetaDataQuery mdq;
    private static DateTimeZone tz = DateTimeZone.forID("UTC");

    public ChannelMetaDataTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {

        metaDataServer = new MetaDataServerMock();

        mdq = new MetaDataQuery(metaDataServer);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetStationMetaData() {

        metaDataServer.loadPAZFile(new String[]{"NZWEL  HHZ10"}, new String[]{"/test-data/gov/usgs/anss/query/metadata/NZWEL__HHZ10.sac.pz"});

        ChannelMetaData md = mdq.getChannelMetaData(new NSCL("NZ", "WEL  ", "HHZ", "10"),
                new DateTime(2009, 1, 1, 11, 11, 11, 0, tz));
        assertTrue("Network ", md.getNetwork().equals("NZ"));
        assertTrue("Code ", md.getCode().equals("WEL  "));
        assertTrue("component", md.getChannel().equals("HHZ"));
        assertTrue("location ", md.getLocation().equals("10"));
        assertEquals("longitude", 174.76802d, md.getLongitude(), 0);
        assertEquals("latitude", -41.28576d, md.getLatitude(), 0);
        assertEquals("elevation", 138.0d, md.getElevation(), 0);
        assertEquals("dip", -90.0d, md.getDip(), 0);
        assertEquals("azimuth", 0.0d, md.getAzimuth(), 0);
    }

    @Test
    public void testGetStationMetaDataNull() {
        metaDataServer.loadPAZFile(new String[]{"NZWEL  HHZ10"}, new String[]{"/test-data/gov/usgs/anss/query/metadata/NZWEL__HHZ10.sac.pz"});
        ChannelMetaData md = mdq.getChannelMetaData(new NSCL("NZ", "ZZZZZ", "HHZ", "10"),
                new DateTime(2009, 1, 1, 11, 11, 11, 0, tz));
        assertTrue("Network ", md.getNetwork().equals("NZ"));
        assertTrue("Code ", md.getCode().equals("ZZZZZ"));
        assertTrue("component", md.getChannel().equals("HHZ"));
        assertTrue("location ", md.getLocation().equals("10"));
        assertEquals("longitude", Double.MIN_VALUE, md.getLongitude(), 0);
        assertEquals("latitude", Double.MIN_VALUE, md.getLatitude(), 0);
        assertEquals("elevation", Double.MIN_VALUE, md.getElevation(), 0);
        assertEquals("dip", Double.MIN_VALUE, md.getDip(), 0);
        assertEquals("azimuth", Double.MIN_VALUE, md.getAzimuth(), 0);
    }
}
