/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
}
