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

package gov.usgs.anss.query.filefactory;

import edu.sc.seis.TauP.SacTimeSeries;
import gov.usgs.anss.query.cwb.data.CWBDataServerMSEEDMock;
import gov.usgs.anss.query.metadata.MetaDataServerMock;
import gov.usgs.anss.seed.MiniSeed;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author richardg
 */
public class SacFileFactoryEmptySetTest {
    @Test
    public void testMakeTimeSeriesFromEmptySet() throws Exception {
		SacFileFactory sacFileFactory = new SacFileFactory();
		CWBDataServerMSEEDMock cwbServer = new CWBDataServerMSEEDMock();
		cwbServer.loadMSEEDFiles(new String[]{});

		sacFileFactory.setCWBDataServer(cwbServer);
		sacFileFactory.setMetaDataServer(new MetaDataServerMock());

		SacTimeSeries expResult = null;
		SacTimeSeries result = sacFileFactory.makeTimeSeries(
				new TreeSet<MiniSeed>(),
				new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC),
				1800d, //duration
				new Integer(-12345), //fill
				true, //gaps
				true  //trim
				);

		assertEquals("expected null", result, expResult);
	}
}