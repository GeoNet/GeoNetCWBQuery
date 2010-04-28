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