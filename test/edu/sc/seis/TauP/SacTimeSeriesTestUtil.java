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

    public static void compareSacTimeSeries(SacTimeSeries expected, SacTimeSeries result) {
        assertEquals("length ", expected.y.length, result.y.length);

        for (int i = 0; i < expected.y.length; i++) {
            assertEquals("data " + i, expected.y[i], result.y[i], 0.0);
        }

        assertEquals("nvhdr", expected.nvhdr, result.nvhdr);
        assertEquals("b", expected.b, result.b, Math.ulp((float) result.b));
        assertEquals("e", expected.e, result.e, Math.ulp((float) result.e));
        assertEquals("iftype", expected.iftype, result.iftype);
        assertEquals("leven", expected.leven, result.leven);
        assertEquals("delta", expected.delta, result.delta, Math.ulp((float) result.delta));  // Slight discrepancy
        assertEquals("depmin", expected.depmin, result.depmin, 0.0);
        assertEquals("depmax", expected.depmax, result.depmax, 0.0);

        assertEquals("nzyear", expected.nzyear, result.nzyear);
        assertEquals("nzjday", expected.nzjday, result.nzjday);
        assertEquals("nzhour", expected.nzhour, result.nzhour);
        assertEquals("nzmin", expected.nzmin, result.nzmin);
        assertEquals("nzsec", expected.nzsec, result.nzsec);
        assertEquals("nzmsec", expected.nzmsec, result.nzmsec, Math.ulp((float) result.nzmsec));

        assertEquals("iztype", expected.iztype, result.iztype);

        assertEquals("knetwk", expected.knetwk, result.knetwk);
        assertEquals("kstnm", expected.kstnm, result.kstnm);
        assertEquals("kcmpn", expected.kcmpnm, result.kcmpnm);
        assertEquals("khole", expected.khole, result.khole);

        assertEquals("Lat", expected.stla, result.stla, Math.ulp((float) result.stla));
        assertEquals("Lon", expected.stlo, result.stlo, Math.ulp((float) result.stlo));
        assertEquals("Elev", expected.stel, result.stel, 0.0);
        assertEquals("Depth", expected.stdp, result.stdp, 0.0);
        assertEquals("Azimuth", expected.cmpaz, result.cmpaz, 0.0);
        assertEquals("Inc", expected.cmpinc, result.cmpinc, 0.0);

        assertEquals("event lat", expected.evla, result.evla, Math.ulp((float) result.evla));
        assertEquals("event lon", expected.evlo, result.evlo, Math.ulp((float) result.evlo));
        assertEquals("event dep", expected.evdp, result.evdp, Math.ulp((float) result.evdp));
        assertEquals("event mag", expected.mag, result.mag, Math.ulp((float) result.mag));
        assertEquals("imagtyp", expected.imagtyp, result.imagtyp);
        assertEquals("ievtyp", expected.ievtyp, result.ievtyp);
        assertEquals("lcalda", expected.lcalda, result.lcalda);

        assertEquals("Phase 0", expected.kt0, result.kt0);
        assertEquals("Phase t0", expected.t0, result.t0, Math.ulp((float) result.t0));
        assertEquals("Phase 1", expected.kt1, result.kt1);
        assertEquals("Phase t1", expected.t1, result.t1, Math.ulp((float) result.t0));
        assertEquals("Phase 2", expected.kt2, result.kt2);
        assertEquals("Phase t2", expected.t2, result.t2, Math.ulp((float) result.t0));
        assertEquals("Phase 3", expected.kt3, result.kt3);
        assertEquals("Phase t3", expected.t3, result.t3, Math.ulp((float) result.t0));
        assertEquals("Phase 4", expected.kt4, result.kt4);
        assertEquals("Phase t4", expected.t4, result.t4, Math.ulp((float) result.t0));
        assertEquals("Phase 5", expected.kt5, result.kt5);
        assertEquals("Phase t5", expected.t5, result.t5, Math.ulp((float) result.t0));
        assertEquals("Phase 6", expected.kt6, result.kt6);
        assertEquals("Phase t6", expected.t6, result.t6, Math.ulp((float) result.t0));
        assertEquals("Phase 7", expected.kt7, result.kt7);
        assertEquals("Phase t7", expected.t7, result.t7, Math.ulp((float) result.t0));
        assertEquals("Phase 8", expected.kt8, result.kt8);
        assertEquals("Phase t8", expected.t8, result.t8, Math.ulp((float) result.t0));
        assertEquals("Phase 9", expected.kt9, result.kt9);
        assertEquals("Phase t9", expected.t9, result.t9, Math.ulp((float) result.t0));

        assertEquals("kuser0 - velocity model", expected.kuser0, result.kuser0);

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
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };
}
