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
