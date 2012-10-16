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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author richardg
 */
public class CustomEventTest {

    public CustomEventTest() {
    }

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	/**
	 * Test of getEventDepth method, of class CustomEvent.
	 */
	@Test
	public void testGetEventDepth() {
		System.out.println("getEventDepth");
		CustomEvent instance = new CustomEvent();
		Double expResult = null;
		Double result = instance.getEventDepth();
		assertEquals(expResult, result);
	}

	/**
	 * Test of setEventDepth method, of class CustomEvent.
	 */
	@Test
	public void testSetEventDepth_Double() {
		System.out.println("setEventDepth");
		Double eventDepth = 12.0;
		CustomEvent instance = new CustomEvent();
		instance.setEventDepth(eventDepth);
		assertEquals("Double", 12000.0, instance.getEventDepth(), 0.0);

		instance.setEventDepth("25");
		assertEquals("String", 25000.0, instance.getEventDepth(), 0.0);
	}
}