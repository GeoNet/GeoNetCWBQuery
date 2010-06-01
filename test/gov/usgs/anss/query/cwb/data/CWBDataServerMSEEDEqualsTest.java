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

package gov.usgs.anss.query.cwb.data;

import junit.framework.TestCase;
import com.gargoylesoftware.base.testing.EqualsTester;

public class CWBDataServerMSEEDEqualsTest extends TestCase {

	public void testEquals() {

		String hostA = "cwb.geonet.org.nz";
		int portA = 2061;

		String hostB = "cwb-pub.cr.usgs.gov";
		int portB = 2061;

		CWBDataServerMSEED a = new CWBDataServerMSEED(hostA, portA);
		CWBDataServerMSEED b = new CWBDataServerMSEED(hostA, portA);
		CWBDataServerMSEED c = new CWBDataServerMSEED(hostB, portB);
		CWBDataServerMSEED d = new CWBDataServerMSEED(hostA, portA) {
		};

		new EqualsTester(a, b, c, d);
	}
}
