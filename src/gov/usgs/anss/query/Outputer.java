/*
 * Outputer.java
 *
 * Created on April 20, 2006, 4:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import gov.usgs.anss.edge.*;
import java.util.ArrayList;
import java.io.IOException;
import gov.usgs.anss.seed.MiniSeed;
import gov.usgs.anss.util.*;

/**
 *
 * @author davidketchum
 */
abstract public class Outputer {

    /** Creates a new instance of Outputer */
    public Outputer() {
    }

    /** the main routine gives an UNSORTED list in blks.  If it needs to be sorted call
     * Collections.sort(blks);
     */
    abstract public void makeFile(String comp, String filename, String mask, ArrayList<MiniSeed> blks,
            java.util.Date beg, double duration, String[] args) throws IOException;
}
