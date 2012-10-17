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
package gov.usgs.anss.query;

import gov.usgs.anss.query.EdgeQueryOptions.OutputType;
import gov.usgs.anss.query.filefactory.SacHeaders.SacEventType;
import gov.usgs.anss.query.filefactory.SacHeaders.SacMagType;
import java.io.Reader;
import java.util.Date;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Basic unit testing of EdgeQueryOptions.
 * @author richardg
 */
public class EdgeQueryOptionsTest {

	/**
	 * Test of isFileMode method, of class EdgeQueryOptions.
	 */
	@Test
	public void testIsFileMode() {
		System.out.println("isFileMode");
		EdgeQueryOptions instance = new EdgeQueryOptions(new String[]{"-f", "file.txt"});
		assertTrue("File mode should return true", instance.isFileMode());
	}

	/**
	 * Test of isListQuery method, of class EdgeQueryOptions.
	 */
	@Test
	public void testIsListQuery() {
		System.out.println("isListQuery");
		EdgeQueryOptions instance = new EdgeQueryOptions(new String[]{"-ls"});
		assertTrue("File mode should return true", instance.isListQuery());
		instance = new EdgeQueryOptions(new String[]{"-lsc"});
		assertTrue("File mode should return true", instance.isListQuery());
	}


	@Test
	public void testEventFlags() {
		System.out.println("testEventFlags");
		EdgeQueryOptions instance = new EdgeQueryOptions(new String[]{
			"-event:time", "2010/02/25 04:38:37",
			"-event:lat", "-46.07472",
			"-event:lon", "166.1996",
			"-event:depth", "12.00000",
			"-event:mag", "5.427000",
			"-event:magtype", "ML",
			"-event:type", "EARTHQUAKE",
		});

		assertEquals("-event:time", new DateTime(2010, 2, 25, 4, 38, 37, 0, DateTimeZone.UTC), instance.getCustomEvent().getEventTime());
		assertEquals("-event:lat", -46.07472, (double) instance.getCustomEvent().getEventLat(), 0);
		assertEquals("-event:lon", 166.1996, (double) instance.getCustomEvent().getEventLon(), 0);
		assertEquals("-event:depth", 12000.0, (double) instance.getCustomEvent().getEventDepth(), 0);
		assertEquals("-event:mag", 5.427, (double) instance.getCustomEvent().getEventMag(), 0);
		assertEquals("-event:magtype", SacMagType.ML, instance.getCustomEvent().getEventMagType());
		assertEquals("-event:type", SacEventType.EARTHQUAKE, instance.getCustomEvent().getEventType());


		// Try again with a different order
		instance = new EdgeQueryOptions(new String[]{
			"-event:lat", "-46.07472",
			"-event:lon", "166.1996",
			"-event:time", "2010/02/25 04:38:37",
			"-event:depth", "12",
			"-event:magtype", "ML",
			"-event:type", "EARTHQUAKE",
			"-event:mag", "5.427",
		});

		assertEquals("-event:time", new DateTime(2010, 2, 25, 4, 38, 37, 0, DateTimeZone.UTC), instance.getCustomEvent().getEventTime());
		assertEquals("-event:lat", -46.07472, (double) instance.getCustomEvent().getEventLat(), 0);
		assertEquals("-event:lon", 166.1996, (double) instance.getCustomEvent().getEventLon(), 0);
		assertEquals("-event:depth", 12000, (double) instance.getCustomEvent().getEventDepth(), 0);
		assertEquals("-event:mag", 5.427, (double) instance.getCustomEvent().getEventMag(), 0);
		assertEquals("-event:magtype", SacMagType.ML, instance.getCustomEvent().getEventMagType());
		assertEquals("-event:type", SacEventType.EARTHQUAKE, instance.getCustomEvent().getEventType());

		// Test some nulls and defaults
		instance = new EdgeQueryOptions(new String[]{
					"-event:time", "2010/02/25 04:38:37",
					"-s", "NZ.....HHZ.."
		});

		assertEquals("-event:time", new DateTime(2010, 2, 25, 4, 38, 37, 0, DateTimeZone.UTC), instance.getCustomEvent().getEventTime());
		assertEquals("-event:lat", null, instance.getCustomEvent().getEventLat());
		assertEquals("-event:lon", null, instance.getCustomEvent().getEventLon());
		assertEquals("-event:depth", null, instance.getCustomEvent().getEventDepth());
		assertEquals("-event:mag", null, instance.getCustomEvent().getEventMag());
		assertEquals("-event:magtype", SacMagType.MX, instance.getCustomEvent().getEventMagType());
		assertEquals("-event:type", SacEventType.NULL, instance.getCustomEvent().getEventType());

	}


	@Test
	public void testSynthFlags() {
		System.out.println("testSynthFlags");
		// First test the defaults
		EdgeQueryOptions instance = new EdgeQueryOptions(new String[]{
			"-event", "geonet:32266622",
		});

		assertEquals("picks", true, instance.picks);
		assertEquals("synthetic", null, instance.getSynthetic());
		assertEquals("extended phases", false, instance.extendedPhases);
		
		
		// Now add some synth flags
		instance = new EdgeQueryOptions(new String[]{
			"-event", "geonet:32266622",
			"-nopicks", "-synthetic", "-extended-phases",
		});

		assertEquals("picks", false, instance.picks);
		assertEquals("synthetic", "iasp91", instance.getSynthetic());
		assertEquals("extended phases", true, instance.extendedPhases);


		// Now test synthetic arse
		instance = new EdgeQueryOptions(new String[]{
			"-event", "geonet:32266622",
			"-synthetic:arse",
		});

		assertEquals("synthetic", "arse", instance.getSynthetic());
	}

    @Test
    public void testGeojsonSC3Event() {
        System.out.println("testGeojsonSC3Event");

        /**
         * Test against known event 2012p618953, response should be:
         * {
         *   "type":"FeatureCollection",
         *   "features":[
         *     {
         *       "type":"Feature",
         *       "id":"quake.2012p618953",
         *       "geometry":{
         *         "type":"Point",
         *         "coordinates":[
         *           174.46893,
         *           -41.032227
         *         ]
         *       },
         *       "geometry_name":"origin_geom",
         *       "properties":{
         *         "publicid":"2012p618953",
         *         "origintime":"2012-08-17 00:18:12.209000",
         *         "depth":64.04492,
         *         "magnitude":2.3195808,
         *         "status":"automatic",
         *         "agency":"WEL(GNS_Primary)",
         *         "updatetime":"2012-08-17 00:20:31.343000"
         *       }
         *     }
         *   ],
         *   "crs":{
         *     "type":"EPSG",
         *     "properties":{
         *       "code":"4326"
         *     }
         *   }
         * }
         */

        EdgeQueryOptions instance = new EdgeQueryOptions(new String[]{
                "-event", "geonet:2012p618953",
        });

        assertNotNull("GeoJson Custom Event", instance.getCustomEvent());

        DateTime ot = new DateTime(2012, 8, 17, 0, 18, 12, 209, DateTimeZone.UTC);
        assertEquals("OriginTime", ot, instance.getBegin());

        // Depth in metres.
        assertEquals("Depth", 64044.92, instance.getCustomEvent().getEventDepth(), Math.ulp(instance.getCustomEvent().getEventDepth()));

        assertEquals("Magnitude", Double.valueOf(2.3195808), instance.getCustomEvent().getEventMag());

        assertEquals("Lat", Double.valueOf(-41.032227), instance.getCustomEvent().getEventLat());
        assertEquals("Lon", Double.valueOf(174.46893), instance.getCustomEvent().getEventLon());
    }
}
