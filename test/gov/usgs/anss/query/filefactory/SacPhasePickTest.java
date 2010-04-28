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

import com.gargoylesoftware.base.testing.EqualsTester;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author geoffc
 */
public class SacPhasePickTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testEquals() {

        SacPhasePick a = new SacPhasePick("P", 10.1d);
        SacPhasePick b = new SacPhasePick("P", 10.1d);
        SacPhasePick c = new SacPhasePick("S", 11.1d);
        SacPhasePick d = new SacPhasePick() {};
        d.setPhaseName("P");
        d.setTimeAfterOriginInSeconds(10.1);

        new EqualsTester(a, b, c, d);
    }
}
