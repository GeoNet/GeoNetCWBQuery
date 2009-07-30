/*
 * SacPZ.java
 *
 * Created on February 21, 2008, 12:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package gov.usgs.anss.query;
import java.io.IOException;
import java.io.PrintWriter;
import gov.usgs.anss.util.StaSrv;
import java.util.logging.Logger;

/**
 *
 * @author davidketchum
 */
public class SacPZ {

    private String pzunit;
    private StaSrv stasrv;
	protected static final Logger logger = Logger.getLogger(SacPZ.class.getName());
	static {logger.fine("$Id$");}

    /** Creates a new instance of SacPZ
     * @param stahost The host to use for metadata, if null or "", it uses cwb-pub
     *@param unit The unit of the desired response 'nm' or 'um'
     */
    public SacPZ(String stahost, String unit) {
        pzunit = unit;
        stasrv = new StaSrv(stahost, QueryProperties.getNeicMetadataServerPort());
    }

    /** get a resonse string - if the MDS is not yet up it will wait for it to get up
     *@param lastComp The component name to get from the MetaDataServer
     *@param time String representation of the time
     *@return The response from the MDS
     */
    public String getSACResponse(String lastComp, String time) {
        String s = stasrv.getSACResponse(lastComp, time, pzunit);
        int loop = 0;
        while (s.indexOf("MetaDataServer not up") >= 0) {
            if (loop++ % 15 == 1) {
                logger.warning("MetaDataServer is not up - waiting for connection");
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            s = stasrv.getSACResponse(lastComp, time, pzunit);
        }
        return s;
    }

    /** get a resonse string and write it to the filename stub
     *@param lastComp The component name to get from the MetaDataServer
     *@param time String representation of the time
     *@param filename the filename stub to write to (.pz will be concatenated at the end)
     *@return The response from the MDS
     */
    public String getSACResponse(String lastComp, String time, String filename) {
        String s = getSACResponse(lastComp, time);
        writeSACPZ(filename, s);
        return s;
    }

    /** write out the string as a PZ - not normally called by user
     *@param filename The filename stub to write to (.pz will be added at end)
     *@param s The resonse string to write (normally gotten by )
     */
    private void writeSACPZ(String filename, String s) {
        try {
            PrintWriter fout = new PrintWriter(filename + ".pz");
            fout.write(s);
            fout.close();
        } catch (IOException e) {
            logger.severe("OUtput error writing sac response file " + filename + ".resp e=" + e.getMessage());
        }
    }
}
