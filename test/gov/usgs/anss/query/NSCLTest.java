/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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