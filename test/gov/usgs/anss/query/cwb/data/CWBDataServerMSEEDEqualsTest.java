package gov.usgs.anss.query.cwb.data;

import junit.framework.TestCase;
import com.gargoylesoftware.base.testing.EqualsTester;

public class CWBDataServerMSEEDEqualsTest extends TestCase {

	public void testEquals() {

		String hostA = "cwb.geonet.org.nz";
		int portA = 80;

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
