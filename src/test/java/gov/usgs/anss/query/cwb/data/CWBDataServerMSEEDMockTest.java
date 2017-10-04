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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author geoffc
 */
public class CWBDataServerMSEEDMockTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testServerMock() {

        CWBDataServerMSEEDMock cwbServer = new CWBDataServerMSEEDMock();
        String[] filenames = {
            "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHE10.ms",
            "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHN10.ms",
            "/test-data/gov/usgs/anss/query/filefactory/no-gaps/NZMRZ__HHZ10.ms"
        };

        cwbServer.loadMSEEDFiles(filenames);
       
        assertEquals("has records", cwbServer.hasNext(), true);
        assertEquals("First record", "NZMRZ  HHE10", cwbServer.getNext().first().getSeedName());
        TreeSet set = cwbServer.getNext();
        set = cwbServer.getNext();
        assertEquals("More records", cwbServer.hasNext(), false);
    }
}
