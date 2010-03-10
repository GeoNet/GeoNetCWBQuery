/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
