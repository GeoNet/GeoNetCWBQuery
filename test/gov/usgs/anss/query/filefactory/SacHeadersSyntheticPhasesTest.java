/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.filefactory;

import edu.sc.seis.TauP.SacTimeSeries;
import java.util.ArrayList;
import java.util.List;
import nz.org.geonet.quakeml.v1_0_1.client.QuakemlFactory;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

/**
 *
 * @author geoffc
 */
public class SacHeadersSyntheticPhasesTest {

    private static SacTimeSeries sac;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @Before
    public void setup() {
        sac = new SacTimeSeries();
        sac.evla = -40.60804d;
        sac.evlo = 176.13933d;
        sac.evdp = 17946.3d;
        sac.stla = -41.28576d;
        sac.stlo = 174.76802d;
        sac.cmpinc = 0.0d;
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testGetSyntheticPhasesVerticalComponent() {
        List<SacPhasePick> expected = new ArrayList<SacPhasePick>();

        expected.add(new SacPhasePick("P", 22.390350136363693));
        expected.add(new SacPhasePick("Pn", 22.391044325111253));
        expected.add(new SacPhasePick("P", 22.833306118473626));
        expected.add(new SacPhasePick("P", 23.508144460604914));
        expected.add(new SacPhasePick("p", 23.899170974844367));
        expected.add(new SacPhasePick("P", 23.993632868688543));
        expected.add(new SacPhasePick("PKiKP", 991.5016026786577));


        List<SacPhasePick> result = SacHeaders.getSyntheticPhases(sac, false, null);
        assertEquals("Didn't get expected picks.", expected, result);
    }

    @Test
    public void testGetSyntheticPhasesVerticalComponentIasp91() {
        List<SacPhasePick> expected = new ArrayList<SacPhasePick>();

        expected.add(new SacPhasePick("P", 22.390350136363693));
        expected.add(new SacPhasePick("Pn", 22.391044325111253));
        expected.add(new SacPhasePick("P", 22.833306118473626));
        expected.add(new SacPhasePick("P", 23.508144460604914));
        expected.add(new SacPhasePick("p", 23.899170974844367));
        expected.add(new SacPhasePick("P", 23.993632868688543));
        expected.add(new SacPhasePick("PKiKP", 991.5016026786577));


        List<SacPhasePick> result = SacHeaders.getSyntheticPhases(sac, false, "iasp91");
        assertEquals("Didn't get expected picks.", expected, result);
    }

    @Test
    public void testGetSyntheticPhasesVerticalComponentPrem() {
        List<SacPhasePick> expected = new ArrayList<SacPhasePick>();

        expected.add(new SacPhasePick("P", 19.989381543745672));
        expected.add(new SacPhasePick("Pn", 19.990032166825504));
        expected.add(new SacPhasePick("p", 21.548613158370102));
        expected.add(new SacPhasePick("P", 21.699585194403593));
        expected.add(new SacPhasePick("PKiKP", 988.9516669274939));


        List<SacPhasePick> result = SacHeaders.getSyntheticPhases(sac, false, "prem");
        assertEquals("Didn't get expected picks.", expected, result);
    }

    @Test
    public void testGetSyntheticPhasesVerticalComponentAk135() {
        List<SacPhasePick> expected = new ArrayList<SacPhasePick>();

        expected.add(new SacPhasePick("P", 22.39035013636366));
        expected.add(new SacPhasePick("Pn", 22.391044325111217));
        expected.add(new SacPhasePick("P", 22.83330611847372));
        expected.add(new SacPhasePick("P", 23.509910605502245));
        expected.add(new SacPhasePick("p", 23.899480850195985));
        expected.add(new SacPhasePick("P", 24.001451375125665));
        expected.add(new SacPhasePick("PKiKP", 991.7118920250937));

        List<SacPhasePick> result = SacHeaders.getSyntheticPhases(sac, false, "ak135");
        assertEquals("Didn't get expected picks.", expected, result);
    }

    @Test
    public void testGetSyntheticPhasesVerticalComponentBadModel() {
        assertTrue("Didn't get expected picks.", SacHeaders.getSyntheticPhases(sac, false, "not-a-model").isEmpty());
    }

    @Test
    public void testGetSyntheticPhasesExtendedVerticalComponent() {
        List<SacPhasePick> expected = new ArrayList<SacPhasePick>();

        expected.add(new SacPhasePick("P", 22.390350136363693d));
        expected.add(new SacPhasePick("Pn", 22.391044325111253d));
        expected.add(new SacPhasePick("P", 22.833306118473626d));
        expected.add(new SacPhasePick("P", 23.508144460604914d));
        expected.add(new SacPhasePick("p", 23.899170974844367d));
        expected.add(new SacPhasePick("P", 23.993632868688543d));
        expected.add(new SacPhasePick("pP", 25.646257200426277d));
        expected.add(new SacPhasePick("pP", 25.714030216184245d));
        expected.add(new SacPhasePick("pP", 26.695250744217986d));
        expected.add(new SacPhasePick("pP", 26.970571965556022d));
        expected.add(new SacPhasePick("sP", 28.079749973621787d));
        expected.add(new SacPhasePick("sP", 28.8149515070317d));
        expected.add(new SacPhasePick("sP", 29.123366881076567d));
        expected.add(new SacPhasePick("sP", 29.399448796704572d));
        expected.add(new SacPhasePick("sP", 29.86831462479237d));
        expected.add(new SacPhasePick("PcP", 508.2546124540107d));
        expected.add(new SacPhasePick("PKiKP", 991.5016026786577d));
        expected.add(new SacPhasePick("pPKiKP", 997.6899704375961d));
        expected.add(new SacPhasePick("sPKiKP", 999.9369439264844d));

        List<SacPhasePick> result = SacHeaders.getSyntheticPhases(sac, true, null);
        assertEquals("Didn't get expected picks.", expected, result);
    }

    @Test
    public void testGetSyntheticPhasesHorzontalComponent() {

        sac.cmpinc = 90.0d;
        List<SacPhasePick> expected = new ArrayList<SacPhasePick>();

        expected.add(new SacPhasePick("S", 39.336879719734235));
        expected.add(new SacPhasePick("Sn", 39.337656666113965));
        expected.add(new SacPhasePick("S", 39.518038256462916));
        expected.add(new SacPhasePick("S", 40.69713316858711));
        expected.add(new SacPhasePick("s", 41.25364884040999));
        expected.add(new SacPhasePick("S", 41.42226956863093));


        List<SacPhasePick> result = SacHeaders.getSyntheticPhases(sac, false, null);
        assertEquals("Didn't get expected picks.", expected, result);
    }

    @Test
    public void testGetSyntheticPhasesExtendedHorzontalComponent() {
        sac.cmpinc = 90.0d;
        List<SacPhasePick> expected = new ArrayList<SacPhasePick>();

        expected.add(new SacPhasePick("S", 39.336879719734235));
        expected.add(new SacPhasePick("Sn", 39.337656666113965));
        expected.add(new SacPhasePick("S", 39.518038256462916));
        expected.add(new SacPhasePick("S", 40.69713316858711));
        expected.add(new SacPhasePick("s", 41.25364884040999));
        expected.add(new SacPhasePick("S", 41.42226956863093));
        expected.add(new SacPhasePick("sS", 44.29531965722775));
        expected.add(new SacPhasePick("sS", 44.38746778298414));
        expected.add(new SacPhasePick("sS", 46.419921436590144));
        expected.add(new SacPhasePick("sS", 46.63650532570043));
        expected.add(new SacPhasePick("ScS", 930.358728616058));

        List<SacPhasePick> result = SacHeaders.getSyntheticPhases(sac, true, null);
        assertEquals("Didn't get expected picks.", expected, result);
    }

    @Test
    public void testGetSyntheticPhasesUnknownComponent() {

        sac = new SacTimeSeries();
        sac.evla = -40.60804d;
        sac.evlo = 176.13933d;
        sac.evdp = 17946.3d;
        sac.stla = -41.28576d;
        sac.stlo = 174.76802d;

        List<SacPhasePick> expected = new ArrayList<SacPhasePick>();

        expected.add(new SacPhasePick("P", 22.390350136363693));
        expected.add(new SacPhasePick("Pn", 22.391044325111253));
        expected.add(new SacPhasePick("P", 22.833306118473626));
        expected.add(new SacPhasePick("P", 23.508144460604914));
        expected.add(new SacPhasePick("p", 23.899170974844367));
        expected.add(new SacPhasePick("P", 23.993632868688543));
        expected.add(new SacPhasePick("pP", 25.646257200426277));
        expected.add(new SacPhasePick("pP", 25.714030216184245));
        expected.add(new SacPhasePick("PP", 25.966209035371627));
        expected.add(new SacPhasePick("PP", 25.995564026206477));
        expected.add(new SacPhasePick("pP", 26.695250744217986));
        expected.add(new SacPhasePick("pP", 26.970571965556022));
        expected.add(new SacPhasePick("sP", 28.079749973621787));
        expected.add(new SacPhasePick("sP", 28.8149515070317));
        expected.add(new SacPhasePick("sP", 29.123366881076567));
        expected.add(new SacPhasePick("sP", 29.399448796704572));
        expected.add(new SacPhasePick("sP", 29.86831462479237));
        expected.add(new SacPhasePick("S", 39.336879719734235));
        expected.add(new SacPhasePick("Sn", 39.337656666113965));
        expected.add(new SacPhasePick("S", 39.518038256462916));
        expected.add(new SacPhasePick("S", 40.69713316858711));
        expected.add(new SacPhasePick("s", 41.25364884040999));
        expected.add(new SacPhasePick("S", 41.42226956863093));
        expected.add(new SacPhasePick("sS", 44.29531965722775));
        expected.add(new SacPhasePick("sS", 44.38746778298414));
        expected.add(new SacPhasePick("sS", 46.419921436590144));
        expected.add(new SacPhasePick("sS", 46.63650532570043));
        expected.add(new SacPhasePick("PcP", 508.2546124540107));
        expected.add(new SacPhasePick("ScP", 718.1704736019561));
        expected.add(new SacPhasePick("ScS", 930.358728616058));
        expected.add(new SacPhasePick("PKiKP", 991.5016026786577));
        expected.add(new SacPhasePick("pPKiKP", 997.6899704375961));
        expected.add(new SacPhasePick("sPKiKP", 999.9369439264844));
        expected.add(new SacPhasePick("PKIKKIKP", 1909.7764102162655));
        expected.add(new SacPhasePick("SKIKKIKP", 2119.67917397117));
        expected.add(new SacPhasePick("PKIKPPKIKP", 2421.0362718423635));

        List<SacPhasePick> result = SacHeaders.getSyntheticPhases(sac, false, null);
        assertEquals("Didn't get expected picks.", expected, result);

        result = SacHeaders.getSyntheticPhases(sac, false, null);
        assertEquals("Didn't get expected picks extended phases.", expected, result);
    }

    @Test
    public void testGetSyntheticPhasesReduceTriplicatedPhases() {
        List<SacPhasePick> input = new ArrayList<SacPhasePick>();

        input.add(new SacPhasePick("S", 39.336879719734235));
        input.add(new SacPhasePick("Sn", 39.337656666113965));
        input.add(new SacPhasePick("S", 39.518038256462916));
        input.add(new SacPhasePick("S", 40.69713316858711));
        input.add(new SacPhasePick("s", 41.25364884040999));
        input.add(new SacPhasePick("S", 41.42226956863093));
        input.add(new SacPhasePick("sS", 44.29531965722775));
        input.add(new SacPhasePick("sS", 44.38746778298414));
        input.add(new SacPhasePick("sS", 46.419921436590144));
        input.add(new SacPhasePick("sS", 46.63650532570043));
        input.add(new SacPhasePick("ScS", 930.358728616058));

        List<SacPhasePick> expected = new ArrayList<SacPhasePick>();

        expected.add(new SacPhasePick("S", 39.336879719734235));
        expected.add(new SacPhasePick("Sn", 39.337656666113965));
        expected.add(new SacPhasePick("s", 41.25364884040999));
        expected.add(new SacPhasePick("sS", 44.29531965722775));
        expected.add(new SacPhasePick("ScS", 930.358728616058));

        List<SacPhasePick> result = SacHeaders.reduceTriplicatedPhases(input);

        for (SacPhasePick phase : result) {
            System.out.println(String.format("%s %s", phase.getPhaseName(), phase.getTimeAfterOriginInSeconds()));
        }

        assertEquals("Didn't get expected picks.", expected, result);
    }

    @Test
    public void testSetSyntheticPhasesInsufficentHeaders() {

        sac = new SacTimeSeries();

        assertTrue("Found unexpected picks when insufficient header set 1.", SacHeaders.getSyntheticPhases(sac, false, null).isEmpty());
        sac.evla = -40.60804d;
        assertTrue("Found unexpected picks when insufficient header set 2.", SacHeaders.getSyntheticPhases(sac, false, null).isEmpty());
        sac.evlo = 176.13933d;
        assertTrue("Found unexpected picks when insufficient header set 3.", SacHeaders.getSyntheticPhases(sac, false, null).isEmpty());
        sac.evdp = 17946.3d;
        assertTrue("Found unexpected picks when insufficient header set 4.", SacHeaders.getSyntheticPhases(sac, false, null).isEmpty());
        sac.stla = -41.28576d;
        assertTrue("Found unexpected picks when insufficient header set 5.", SacHeaders.getSyntheticPhases(sac, false, null).isEmpty());
        sac.stlo = 174.76802d;
        assertFalse("Didn't get picks when enough header values should be set.", SacHeaders.getSyntheticPhases(sac, false, null).isEmpty());
    }

    @Test
    public void testComponentOrientationToPhaseGroup() {
        sac = new SacTimeSeries();
        assertEquals("Basic", "ttbasic", SacHeaders.componentOrientationToPhaseGroup(sac, false));
        assertEquals("Basic", "ttbasic", SacHeaders.componentOrientationToPhaseGroup(sac, true));
        sac.cmpinc = 0.0d;
        assertEquals("Basic P", "ttp", SacHeaders.componentOrientationToPhaseGroup(sac, false));
        assertEquals("Extended P", "ttp+", SacHeaders.componentOrientationToPhaseGroup(sac, true));
        sac.cmpinc = 90.0d;
        assertEquals("Basic S", "tts", SacHeaders.componentOrientationToPhaseGroup(sac, false));
        assertEquals("Extended S", "tts+", SacHeaders.componentOrientationToPhaseGroup(sac, true));
    }

    @Test
    public void testSetSyntheticPicksVertical() {
        sac = SacHeaders.setPhasePicks(sac, false, null);

        assertEquals("pick 0 name", "P", sac.kt0);
        assertEquals("pick 0 time", 22.390350136363693, sac.t0, Math.ulp((float) sac.t0));

        assertEquals("pick 1 name", "Pn", sac.kt1);
        assertEquals("pick 1 time", 22.391044325111253, sac.t1, Math.ulp((float) sac.t1));

        assertEquals("pick 2 name", "p", sac.kt2);
        assertEquals("pick 2 time", 23.899170974844367, sac.t2, Math.ulp((float) sac.t2));

        assertEquals("pick 3 name", "PKiKP", sac.kt3);
        assertEquals("pick 3 time", 991.5016026786577, sac.t3, Math.ulp((float) sac.t3));

        assertEquals("pick 4 name", "-12345  ", sac.kt4);
        assertEquals("pick 4 time", -12345.0, sac.t4, Math.ulp((float) sac.t4));
    }

    @Test
    public void testSetSyntheticPhasesHorzontalComponent() {
        sac.cmpinc = 90.0d;
        sac = SacHeaders.setPhasePicks(sac, false, null);

        assertEquals("pick 0 name", "S", sac.kt0);
        assertEquals("pick 0 time", 39.336879719734235, sac.t0, Math.ulp((float) sac.t0));

        assertEquals("pick 1 name", "Sn", sac.kt1);
        assertEquals("pick 1 time", 39.337656666113965, sac.t1, Math.ulp((float) sac.t1));

        assertEquals("pick 2 name", "s", sac.kt2);
        assertEquals("pick 2 time", 41.25364884040999, sac.t2, Math.ulp((float) sac.t2));

        assertEquals("pick 3 name", "-12345  ", sac.kt3);
        assertEquals("pick 3 time", -12345.0, sac.t3, Math.ulp((float) sac.t3));
    }

    @Test
    public void testSetSyntheticPhasesAndQuakeMlHorzontalComponent() throws Exception {
        sac.cmpinc = 90.0d;

        sac.kstnm = "TSZ";
        sac.knetwk = "NZ";
        sac.kcmpnm = "HHN";
        sac.khole = "10";

        Quakeml quakeml = new QuakemlFactory().getQuakeml(SacHeadersSyntheticPhasesTest.class.getResourceAsStream("/gov/usgs/anss/query/filefactory/quakeml_2732452.xml"), null);

        sac = SacHeaders.setPhasePicks(sac, quakeml, false, null);

        assertEquals("pick 0 name", "S* mc", sac.kt0);
        assertEquals("pick 0 time", 17.001, sac.t0, Math.ulp((float) sac.t0));

        assertEquals("pick 1 name", "S", sac.kt1);
        assertEquals("pick 1 time", 39.336879719734235, sac.t1, Math.ulp((float) sac.t1));

        assertEquals("pick 2 name", "Sn", sac.kt2);
        assertEquals("pick 2 time", 39.337656666113965, sac.t2, Math.ulp((float) sac.t2));

        assertEquals("pick 3 name", "s", sac.kt3);
        assertEquals("pick 3 time", 41.25364884040999, sac.t3, Math.ulp((float) sac.t3));

        assertEquals("pick 4 name", "-12345  ", sac.kt4);
        assertEquals("pick 4 time", -12345.0, sac.t4, Math.ulp((float) sac.t4));
    }

    @Test
    public void testSetSyntheticPicksAndQuakeMlVertical() throws Exception {
        sac.kstnm = "TRWZ";
        sac.knetwk = "NZ";
        sac.kcmpnm = "EHZ";
        sac.khole = "10";

        Quakeml quakeml = new QuakemlFactory().getQuakeml(SacHeadersSyntheticPhasesTest.class.getResourceAsStream("/gov/usgs/anss/query/filefactory/quakeml_2732452.xml"), null);

        sac = SacHeaders.setPhasePicks(sac, quakeml, false, null);

        assertEquals("pick 0 name", "P* ac", sac.kt0);
        assertEquals("pick 0 time", 16.884, sac.t0, Math.ulp((float) sac.t0));

        assertEquals("pick 1 name", "P", sac.kt1);
        assertEquals("pick 1 time", 22.390350136363693, sac.t1, Math.ulp((float) sac.t1));

        assertEquals("pick 2 name", "Pn", sac.kt2);
        assertEquals("pick 2 time", 22.391044325111253, sac.t2, Math.ulp((float) sac.t2));

        assertEquals("pick 3 name", "p", sac.kt3);
        assertEquals("pick 3 time", 23.899170974844367, sac.t3, Math.ulp((float) sac.t3));

        assertEquals("pick 4 name", "PKiKP", sac.kt4);
        assertEquals("pick 4 time", 991.5016026786577, sac.t4, Math.ulp((float) sac.t4));

        assertEquals("pick 5 name", "-12345  ", sac.kt5);
        assertEquals("pick 5 time", -12345.0, sac.t5, Math.ulp((float) sac.t5));
    }
}
