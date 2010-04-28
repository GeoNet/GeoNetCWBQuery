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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author geoffc
 */
public class QueryPropertiesTest {

    public QueryPropertiesTest() {
    }

    @Test
    public void testGetGeoNetCwbIP() {
        String expResult = "cwb.geonet.org.nz";
        String result = QueryProperties.getGeoNetCwbIP();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetGeoNetCwbPort() {
        int expResult = 80;
        int result = QueryProperties.getGeoNetCwbPort();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetNeicMetadataServerIP() {
        String expResult = "cwb-pub.cr.usgs.gov";
        String result = QueryProperties.getNeicMetadataServerIP();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetNeicMetadataServerPort() {
        int expResult = 2052;
        int result = QueryProperties.getNeicMetadataServerPort();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetNeicCwbIP() {
        String expResult = "cwb-pub.cr.usgs.gov";
        String result = QueryProperties.getNeicCwbIP();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetNeicCwbPort() {
        int expResult = 2061;
        int result = QueryProperties.getNeicCwbPort();
        assertEquals(expResult, result);
    }
}