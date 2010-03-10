package gov.usgs.anss.query.filefactory;

import edu.sc.seis.TauP.SacTimeSeries;
import gov.usgs.anss.query.cwb.data.CWBDataServerMSEEDMock;
import gov.usgs.anss.query.metadata.MetaDataServerMock;
import gov.usgs.anss.seed.MiniSeed;
import java.util.TreeSet;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author richardg
 */
public class SacFileFactoryEmptySetTest {
    @Test
    public void testMakeTimeSeriesFromEmptySet() throws Exception {
		SacFileFactory sacFileFactory = new SacFileFactory();
		CWBDataServerMSEEDMock cwbServer = new CWBDataServerMSEEDMock("dummy", 666);
		cwbServer.loadMSEEDFiles(new String[]{});

		sacFileFactory.setCWBDataServer(cwbServer);
		sacFileFactory.setMetaDataServer(new MetaDataServerMock("dummy", 666));

		SacTimeSeries expResult = null;
		SacTimeSeries result = sacFileFactory.makeTimeSeries(
				new TreeSet<MiniSeed>(),
				new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC),
				1800d, //duration
				new Integer(-12345), //fill
				true, //gaps
				true, //trim
				null //quakml
				);

		assertEquals("expected null", result, expResult);
	}
}