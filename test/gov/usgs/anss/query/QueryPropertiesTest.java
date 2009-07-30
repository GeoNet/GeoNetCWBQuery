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