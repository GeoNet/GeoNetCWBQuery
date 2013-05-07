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

import nz.org.geonet.simplequakeml.domain.Event;
import nz.org.geonet.simplequakeml.domain.Pick;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author geoffc
 */
public class QuakeMLQueryTest {

    public QuakeMLQueryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testGetPhases() throws Exception {
        ArrayList picks = new ArrayList();
        picks.add(new Pick("P", "automatic", "accepted", "2013-04-29T22:53:47.168392Z", 1.5f, "NZ", "BFZ", null, "HHN"));

        Event event = new Event("smi:scs/0.6/2013p321497", null, "agency", "2013-04-29T22:53:41.038153Z", -40.0f, 178.0f
                , 20.0f, 4.1f, "Magnitide", picks);

        List<NSCL> expected = new ArrayList<NSCL>();

        expected.add(NSCL.stringToNSCL("NZBFZ  HHN.."));

        List<NSCL> result = QuakeMLQuery.getPhases(event);

        assertEquals("nscls", expected, result);

    }
}
