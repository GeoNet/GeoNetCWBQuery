/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import java.util.ArrayList;
import java.util.List;
import nz.org.geonet.quakeml.v1_0_1.client.QuakemlFactory;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
        Quakeml quakeml = new QuakemlFactory().getQuakeml(QuakeMLQueryTest.class.getResourceAsStream("/gov/usgs/anss/query/filefactory/quakeml_2732452.xml"), null);

        List<NSCL> expected = new ArrayList<NSCL>();

        expected.add(NSCL.stringToNSCL("NZBFZ  HHN.."));
        expected.add(NSCL.stringToNSCL("NZTSZ  HHN.."));
        expected.add(NSCL.stringToNSCL("NZTRWZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZWPHZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZWPHZ EHN.."));
        expected.add(NSCL.stringToNSCL("NZCAW  EHZ.."));
        expected.add(NSCL.stringToNSCL("NZMSWZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZMTW  EHZ.."));
        expected.add(NSCL.stringToNSCL("NZPAWZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZPXZ  HHZ.."));
        expected.add(NSCL.stringToNSCL("NZPXZ  HHE.."));
        expected.add(NSCL.stringToNSCL("NZBKZ  HHZ.."));
        expected.add(NSCL.stringToNSCL("NZWAZ  HHZ.."));
        expected.add(NSCL.stringToNSCL("NZWBFS HN2.."));
        expected.add(NSCL.stringToNSCL("NZWBFS HNZ.."));
        expected.add(NSCL.stringToNSCL("NZWEL  HNZ.."));
        expected.add(NSCL.stringToNSCL("NZBHW  EHZ.."));
        expected.add(NSCL.stringToNSCL("NZKIW  EHZ.."));
        expected.add(NSCL.stringToNSCL("NZMOVZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZMRW  EHZ.."));
        expected.add(NSCL.stringToNSCL("NZTUVZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZWNVZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZDUWZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZKRVZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZNGZ  EHZ.."));
        expected.add(NSCL.stringToNSCL("NZOTVZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZTCW  EHZ.."));
        expected.add(NSCL.stringToNSCL("NZTWVZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZWPVZ HHZ.."));
        expected.add(NSCL.stringToNSCL("NZWTVZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZPKVZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZVRZ  HHZ.."));
        expected.add(NSCL.stringToNSCL("NZMTVZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZSNZO HHZ.."));
        expected.add(NSCL.stringToNSCL("NZTRVZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZDVHS HN1.."));
        expected.add(NSCL.stringToNSCL("NZDVHS HNZ.."));
        expected.add(NSCL.stringToNSCL("NZNNZ  HHZ.."));
        expected.add(NSCL.stringToNSCL("NZMRZ  HHZ.."));
        expected.add(NSCL.stringToNSCL("NZMRZ  HHN.."));
        expected.add(NSCL.stringToNSCL("NZTSZ  HHZ.."));
        expected.add(NSCL.stringToNSCL("NZFWVZ HHZ.."));
        expected.add(NSCL.stringToNSCL("NZCMWZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZHATZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZTUWZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZBSWZ EHZ.."));
        expected.add(NSCL.stringToNSCL("NZHOWZ EHZ.."));

        List<NSCL> result = QuakeMLQuery.getPhases(quakeml);

        assertEquals("nscls", expected, result);

    }
}
