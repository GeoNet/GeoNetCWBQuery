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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author richardg
 */
public class NSCLTest {

	/**
	 * Test of getNetwork method, of class NSCL.
	 */
	@Test
	public void testGetMethods() {
		System.out.println("getMethods");
		NSCL nscl = NSCL.stringToNSCL("NZWEL  HHZ10");

		assertEquals("network", "NZ", nscl.getNetwork());
		assertEquals("station", "WEL  ", nscl.getStation());
		assertEquals("channel", "HHZ", nscl.getChannel());
		assertEquals("location", "10", nscl.getLocation());
	}

	/**
	 * Test error case in setNetwork
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetNetworkError() {
		System.out.println("setNetworkError");
		NSCL nscl = NSCL.stringToNSCL("NZWEL  HHZ10");
		nscl.setNetwork("NZL");
	}

	/**
	 * Test error case in setStation
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetStationError() {
		System.out.println("setStationError");
		NSCL nscl = NSCL.stringToNSCL("NZWEL  HHZ10");
		nscl.setStation("NZWELblah");
	}

	/**
	 * Test error case in setChannel
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetChannelError() {
		System.out.println("setChannelError");
		NSCL nscl = NSCL.stringToNSCL("NZWEL  HHZ10");
		nscl.setChannel("HHZ10");
	}

	/**
	 * Test error case in setLocation
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetLocationError() {
		System.out.println("setLocationError");
		NSCL nscl = NSCL.stringToNSCL("NZWEL  HHZ10");
		nscl.setLocation("10blah");
	}

	/**
	 * Test of stringToNSCL method, of class NSCL.
	 */
	@Test
	public void testStringToNSCL() {
		System.out.println("stringToNSCL");
		String input = "NZWEL  BHZ10";
		String n="NZ", s="WEL  ", c="BHZ", l="10";
		NSCL expResult = new NSCL(n,s,c,l);
		NSCL result = NSCL.stringToNSCL(input);
		assertEquals(expResult, result);
	}

	/**
	 * Test error case in setLocation
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testStringToNSCLErrorTooLong() {
		System.out.println("stringToNSCLErrorTooLong");
		NSCL nscl = NSCL.stringToNSCL("NZWEL  HHZ10 ");
	}

	/**
	 * Test error case in setLocation
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testStringToNSCLErrorTooShort() {
		System.out.println("stringToNSCLErrorTooShort");
		NSCL nscl = NSCL.stringToNSCL("NZWEL");
	}

	/**
	 * Test of equals method, of class NSCL.
	 */
	@Test
	public void testEquals() {
		System.out.println("equals");
		Object obj = null;
		NSCL instance = new NSCL("NZ", "WEL  ", "BHZ", "10");
		boolean expResult = false;
		boolean result = instance.equals(obj);
		assertEquals("in-equal when other SeedName is null.", expResult, result);

		obj = new NSCL("NZ", "WEL  ", "BHZ", "10");
		expResult = true;
		result = instance.equals(obj);
		assertEquals("equal when all NSCL components are equal.", expResult, result);

		obj = new NSCL("  ", "WEL  ", "BHZ", "10");
		expResult = false;
		result = instance.equals(obj);
		assertEquals("unmatching network.", expResult, result);

		obj = new NSCL("NZ", "     ", "BHZ", "10");
		expResult = false;
		result = instance.equals(obj);
		assertEquals("unmatching station.", expResult, result);

		obj = new NSCL("NZ", "WEL  ", "   ", "10");
		expResult = false;
		result = instance.equals(obj);
		assertEquals("unmatching channel.", expResult, result);

		obj = new NSCL("NZ", "WEL  ", "BHZ", "  ");
		expResult = false;
		result = instance.equals(obj);
		assertEquals("unmatching location.", expResult, result);
	}

	/**
	 * Test of toString method, of class NSCL.
	 */
	@Test
	public void testToString() {
		System.out.println("toString");
		String input = "NZWEL  HHZ10";
		
		NSCL nscl = NSCL.stringToNSCL(input);
		assertEquals(input, nscl.toString());
	}

}