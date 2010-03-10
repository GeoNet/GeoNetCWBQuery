/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.sc.seis.TauP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.Arrays;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Basic serialization test for SacTimeSeriesTest.
 * @author richardg
 */
public class SacTimeSeriesTestUtil {

    public SacTimeSeriesTestUtil() {
    }

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

	@Test
	public void serializationTest() throws Exception {
		MessageDigest digest = MessageDigest.getInstance("MD5");

		InputStream in = SacTimeSeries.class.getResourceAsStream("/sac-data/test-one/NZMRZ__HHN10.sac");

		File originalFile = File.createTempFile("sac_serialization", null);
		originalFile.deleteOnExit();
		OutputStream out = new FileOutputStream(originalFile);

		//Copy and md5 at the same time
		byte[] b = new byte[8192];

		int len;
		while ((len = in.read(b)) != -1) {
			digest.update(b, 0, len);
			out.write(b, 0, len);
		}
		byte[] md51 = digest.digest();
		out.close();

		SacTimeSeries sac = new SacTimeSeries();
		sac.read(originalFile);
		
		File newFile = File.createTempFile("sac_serialization", null);
		newFile.deleteOnExit();

		sac.write(newFile);
		in = new FileInputStream(newFile);

		while ((len = in.read(b)) != -1) {
			digest.update(b, 0, len);
		}
		byte[] md52 = digest.digest();

//		System.out.println(toHexString(md51));
//		System.out.println(toHexString(md52));

		assertArrayEquals(md51, md52);
	}

	public static SacTimeSeries loadSacTimeSeriesFromClasspath(String filename) throws IOException {
		InputStream in = SacTimeSeries.class.getResourceAsStream(filename);

		File file = File.createTempFile("sac_serialization", null);
		file.deleteOnExit();
		OutputStream out = new FileOutputStream(file);

		//Copy and md5 at the same time
		byte[] b = new byte[8192];

		int len;
		while ((len = in.read(b)) != -1) {
			out.write(b, 0, len);
		}
		out.close();

		SacTimeSeries sac = new SacTimeSeries();
		sac.read(file);
		return sac;
	}

	public static boolean sacTimeSeriesAreEqual(SacTimeSeries s1, SacTimeSeries s2) throws Exception {
		return Arrays.equals(getSacTimeSeriesAsByteArray(s1), getSacTimeSeriesAsByteArray(s2));
	}

	private static byte[] getSacTimeSeriesAsByteArray(SacTimeSeries sac) throws Exception {
		int len = sac.data_offset + sac.npts * 4;
		if (sac.leven == SacTimeSeries.FALSE ||
				sac.iftype == SacTimeSeries.IRLIM ||
				sac.iftype == SacTimeSeries.IAMPH) {
			len = sac.data_offset + sac.npts * 8;
		}
		byte[] bf = new byte[len];
		ByteBuffer buf = ByteBuffer.wrap(bf);
		buf.position(0);
		buf.order(ByteOrder.nativeOrder());
		sac.writeHeader(buf);
		sac.writeData(buf);
		return bf;
	}

	public static String toHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer(bytes.length * 2 + 1);
		for (byte b : bytes) {
			int v = b & 0xff;
			sb.append(hexChars[v >> 4]);
			sb.append(hexChars[v & 0xf]);
		}
		return sb.toString();
	}
	public static final char[] hexChars = {
		'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'
	};
}
