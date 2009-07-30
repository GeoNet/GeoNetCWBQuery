/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.anss.query;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author richardg
 */
public class ZeroFilledSpanLeftPadTest {

	@Test
	public void testLeftPadNormal() {
		assertEquals("\"12345\",10 fails to return \"     12345\".","     12345", ZeroFilledSpan.leftPad("12345",10));
}

	@Test
	public void testLeftPadSameLength() {
		assertEquals("\"12345\",5 fails to return \"12345\".","12345", ZeroFilledSpan.leftPad("12345",5));
}

	@Test
	public void testLeftPadTrim() {
		assertEquals("\"123456789\",5 fails to return \"12345\".","12345", ZeroFilledSpan.leftPad("123456789",5));
	}
}
