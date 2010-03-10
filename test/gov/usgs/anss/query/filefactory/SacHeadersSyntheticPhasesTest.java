/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.filefactory;

import edu.sc.seis.TauP.SacTimeSeries;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
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

        expected.add(new SacPhasePick("P iasp91", 22.390350136363693));
        expected.add(new SacPhasePick("Pn iasp91", 22.391044325111253));
        expected.add(new SacPhasePick("P iasp91", 22.833306118473626));
        expected.add(new SacPhasePick("P iasp91", 23.508144460604914));
        expected.add(new SacPhasePick("p iasp91", 23.899170974844367));
        expected.add(new SacPhasePick("P iasp91", 23.993632868688543));
        expected.add(new SacPhasePick("PKiKP iasp91", 991.5016026786577));


        List<SacPhasePick> result = SacHeaders.getSyntheticPhases(sac, false, null);
        assertEquals("Didn't get exptect picks.", expected, result);
    }

    @Test
    public void testGetSyntheticPhasesVerticalComponentIasp91() {
        List<SacPhasePick> expected = new ArrayList<SacPhasePick>();

        expected.add(new SacPhasePick("P iasp91", 22.390350136363693));
        expected.add(new SacPhasePick("Pn iasp91", 22.391044325111253));
        expected.add(new SacPhasePick("P iasp91", 22.833306118473626));
        expected.add(new SacPhasePick("P iasp91", 23.508144460604914));
        expected.add(new SacPhasePick("p iasp91", 23.899170974844367));
        expected.add(new SacPhasePick("P iasp91", 23.993632868688543));
        expected.add(new SacPhasePick("PKiKP iasp91", 991.5016026786577));


        List<SacPhasePick> result = SacHeaders.getSyntheticPhases(sac, false, "iasp91");
        assertEquals("Didn't get exptect picks.", expected, result);
    }

    @Test
    public void testGetSyntheticPhasesVerticalComponentPrem() {
        List<SacPhasePick> expected = new ArrayList<SacPhasePick>();

        expected.add(new SacPhasePick("P prem", 19.989381543745672));
        expected.add(new SacPhasePick("Pn prem", 19.990032166825504));
        expected.add(new SacPhasePick("p prem", 21.548613158370102));
        expected.add(new SacPhasePick("P prem", 21.699585194403593));
        expected.add(new SacPhasePick("PKiKP prem", 988.9516669274939));


        List<SacPhasePick> result = SacHeaders.getSyntheticPhases(sac, false, "prem");
        assertEquals("Didn't get exptect picks.", expected, result);
    }

    @Test
    public void testGetSyntheticPhasesVerticalComponentAk135() {
        List<SacPhasePick> expected = new ArrayList<SacPhasePick>();

        expected.add(new SacPhasePick("P ak135", 22.39035013636366));
        expected.add(new SacPhasePick("Pn ak135", 22.391044325111217));
        expected.add(new SacPhasePick("P ak135", 22.83330611847372));
        expected.add(new SacPhasePick("P ak135", 23.509910605502245));
        expected.add(new SacPhasePick("p ak135", 23.899480850195985));
        expected.add(new SacPhasePick("P ak135", 24.001451375125665));
        expected.add(new SacPhasePick("PKiKP ak135", 991.7118920250937));

        List<SacPhasePick> result = SacHeaders.getSyntheticPhases(sac, false, "ak135");
        assertEquals("Didn't get exptect picks.", expected, result);
    }

    @Test
    public void testGetSyntheticPhasesVerticalComponentBadModel() {
        assertTrue("Didn't get exptect picks.", SacHeaders.getSyntheticPhases(sac, false, "not-a-model").isEmpty());
    }

    @Test
    public void testGetSyntheticPhasesExtendedVerticalComponent() {
        List<SacPhasePick> expected = new ArrayList<SacPhasePick>();

        expected.add(new SacPhasePick("P iasp91", 22.390350136363693d));
        expected.add(new SacPhasePick("Pn iasp91", 22.391044325111253d));
        expected.add(new SacPhasePick("P iasp91", 22.833306118473626d));
        expected.add(new SacPhasePick("P iasp91", 23.508144460604914d));
        expected.add(new SacPhasePick("p iasp91", 23.899170974844367d));
        expected.add(new SacPhasePick("P iasp91", 23.993632868688543d));
        expected.add(new SacPhasePick("pP iasp91", 25.646257200426277d));
        expected.add(new SacPhasePick("pP iasp91", 25.714030216184245d));
        expected.add(new SacPhasePick("pP iasp91", 26.695250744217986d));
        expected.add(new SacPhasePick("pP iasp91", 26.970571965556022d));
        expected.add(new SacPhasePick("sP iasp91", 28.079749973621787d));
        expected.add(new SacPhasePick("sP iasp91", 28.8149515070317d));
        expected.add(new SacPhasePick("sP iasp91", 29.123366881076567d));
        expected.add(new SacPhasePick("sP iasp91", 29.399448796704572d));
        expected.add(new SacPhasePick("sP iasp91", 29.86831462479237d));
        expected.add(new SacPhasePick("PcP iasp91", 508.2546124540107d));
        expected.add(new SacPhasePick("PKiKP iasp91", 991.5016026786577d));
        expected.add(new SacPhasePick("pPKiKP iasp91", 997.6899704375961d));
        expected.add(new SacPhasePick("sPKiKP iasp91", 999.9369439264844d));

        List<SacPhasePick> result = SacHeaders.getSyntheticPhases(sac, true, null);
        assertEquals("Didn't get exptect picks.", expected, result);
    }

    @Test
    public void testGetSyntheticPhasesHorzontalComponent() {

        sac.cmpinc = 90.0d;
        List<SacPhasePick> expected = new ArrayList<SacPhasePick>();

        expected.add(new SacPhasePick("S iasp91", 39.336879719734235));
        expected.add(new SacPhasePick("Sn iasp91", 39.337656666113965));
        expected.add(new SacPhasePick("S iasp91", 39.518038256462916));
        expected.add(new SacPhasePick("S iasp91", 40.69713316858711));
        expected.add(new SacPhasePick("s iasp91", 41.25364884040999));
        expected.add(new SacPhasePick("S iasp91", 41.42226956863093));


        List<SacPhasePick> result = SacHeaders.getSyntheticPhases(sac, false, null);
        assertEquals("Didn't get exptect picks.", expected, result);
    }

    @Test
    public void testGetSyntheticPhasesExtendedHorzontalComponent() {
        sac.cmpinc = 90.0d;
        List<SacPhasePick> expected = new ArrayList<SacPhasePick>();

        expected.add(new SacPhasePick("S iasp91", 39.336879719734235));
        expected.add(new SacPhasePick("Sn iasp91", 39.337656666113965));
        expected.add(new SacPhasePick("S iasp91", 39.518038256462916));
        expected.add(new SacPhasePick("S iasp91", 40.69713316858711));
        expected.add(new SacPhasePick("s iasp91", 41.25364884040999));
        expected.add(new SacPhasePick("S iasp91", 41.42226956863093));
        expected.add(new SacPhasePick("sS iasp91", 44.29531965722775));
        expected.add(new SacPhasePick("sS iasp91", 44.38746778298414));
        expected.add(new SacPhasePick("sS iasp91", 46.419921436590144));
        expected.add(new SacPhasePick("sS iasp91", 46.63650532570043));
        expected.add(new SacPhasePick("ScS iasp91", 930.358728616058));

        List<SacPhasePick> result = SacHeaders.getSyntheticPhases(sac, true, null);
        assertEquals("Didn't get exptect picks.", expected, result);
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

        expected.add(new SacPhasePick("P iasp91", 22.390350136363693));
        expected.add(new SacPhasePick("Pn iasp91", 22.391044325111253));
        expected.add(new SacPhasePick("P iasp91", 22.833306118473626));
        expected.add(new SacPhasePick("P iasp91", 23.508144460604914));
        expected.add(new SacPhasePick("p iasp91", 23.899170974844367));
        expected.add(new SacPhasePick("P iasp91", 23.993632868688543));
        expected.add(new SacPhasePick("pP iasp91", 25.646257200426277));
        expected.add(new SacPhasePick("pP iasp91", 25.714030216184245));
        expected.add(new SacPhasePick("PP iasp91", 25.966209035371627));
        expected.add(new SacPhasePick("PP iasp91", 25.995564026206477));
        expected.add(new SacPhasePick("pP iasp91", 26.695250744217986));
        expected.add(new SacPhasePick("pP iasp91", 26.970571965556022));
        expected.add(new SacPhasePick("sP iasp91", 28.079749973621787));
        expected.add(new SacPhasePick("sP iasp91", 28.8149515070317));
        expected.add(new SacPhasePick("sP iasp91", 29.123366881076567));
        expected.add(new SacPhasePick("sP iasp91", 29.399448796704572));
        expected.add(new SacPhasePick("sP iasp91", 29.86831462479237));
        expected.add(new SacPhasePick("S iasp91", 39.336879719734235));
        expected.add(new SacPhasePick("Sn iasp91", 39.337656666113965));
        expected.add(new SacPhasePick("S iasp91", 39.518038256462916));
        expected.add(new SacPhasePick("S iasp91", 40.69713316858711));
        expected.add(new SacPhasePick("s iasp91", 41.25364884040999));
        expected.add(new SacPhasePick("S iasp91", 41.42226956863093));
        expected.add(new SacPhasePick("sS iasp91", 44.29531965722775));
        expected.add(new SacPhasePick("sS iasp91", 44.38746778298414));
        expected.add(new SacPhasePick("sS iasp91", 46.419921436590144));
        expected.add(new SacPhasePick("sS iasp91", 46.63650532570043));
        expected.add(new SacPhasePick("PcP iasp91", 508.2546124540107));
        expected.add(new SacPhasePick("ScP iasp91", 718.1704736019561));
        expected.add(new SacPhasePick("ScS iasp91", 930.358728616058));
        expected.add(new SacPhasePick("PKiKP iasp91", 991.5016026786577));
        expected.add(new SacPhasePick("pPKiKP iasp91", 997.6899704375961));
        expected.add(new SacPhasePick("sPKiKP iasp91", 999.9369439264844));
        expected.add(new SacPhasePick("PKIKKIKP iasp91", 1909.7764102162655));
        expected.add(new SacPhasePick("SKIKKIKP iasp91", 2119.67917397117));
        expected.add(new SacPhasePick("PKIKPPKIKP iasp91", 2421.0362718423635));

        List<SacPhasePick> result = SacHeaders.getSyntheticPhases(sac, false, null);
        assertEquals("Didn't get exptect picks.", expected, result);

        result = SacHeaders.getSyntheticPhases(sac, false, null);
        assertEquals("Didn't get exptect picks extended phases.", expected, result);
    }

    @Test
    public void testGetSyntheticPhasesReduceTriplicatedPhases() {
        List<SacPhasePick> input = new ArrayList<SacPhasePick>();

        input.add(new SacPhasePick("S iasp91", 39.336879719734235));
        input.add(new SacPhasePick("Sn iasp91", 39.337656666113965));
        input.add(new SacPhasePick("S iasp91", 39.518038256462916));
        input.add(new SacPhasePick("S iasp91", 40.69713316858711));
        input.add(new SacPhasePick("s iasp91", 41.25364884040999));
        input.add(new SacPhasePick("S iasp91", 41.42226956863093));
        input.add(new SacPhasePick("sS iasp91", 44.29531965722775));
        input.add(new SacPhasePick("sS iasp91", 44.38746778298414));
        input.add(new SacPhasePick("sS iasp91", 46.419921436590144));
        input.add(new SacPhasePick("sS iasp91", 46.63650532570043));
        input.add(new SacPhasePick("ScS iasp91", 930.358728616058));

        List<SacPhasePick> expected = new ArrayList<SacPhasePick>();

        expected.add(new SacPhasePick("S iasp91", 39.336879719734235));
        expected.add(new SacPhasePick("Sn iasp91", 39.337656666113965));
        expected.add(new SacPhasePick("s iasp91", 41.25364884040999));
        expected.add(new SacPhasePick("sS iasp91", 44.29531965722775));
        expected.add(new SacPhasePick("ScS iasp91", 930.358728616058));

        List<SacPhasePick> result = SacHeaders.reduceTriplicatedPhases(input);

        for (SacPhasePick phase : result) {
            System.out.println(String.format("%s %s", phase.getPhaseName(), phase.getTimeAfterOriginInSeconds()));
        }

        assertEquals("Didn't get exptect picks.", expected, result);
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
}
