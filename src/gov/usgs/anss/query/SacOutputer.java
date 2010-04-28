/*
 * Copyright 2006, United States Geological Survey or
 * third-party contributors as indicated by the @author tags.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package gov.usgs.anss.query;

import java.io.IOException;
import java.util.ArrayList;
import gov.usgs.anss.seed.*;
import edu.sc.seis.TauP.SacTimeSeries;
import gov.usgs.anss.query.cwb.data.CWBDataServer;
import gov.usgs.anss.query.cwb.data.CWBDataServerMSEED;
import gov.usgs.anss.query.filefactory.SacFileFactory;
import gov.usgs.anss.query.metadata.MetaDataServerImpl;

/**
 *
 * @author davidketchum
 */
public class SacOutputer extends Outputer {

    static {
        logger.fine("$Id$");
    }
    private final SacFileFactory sacFF;
    private Integer fill = SacTimeSeries.INT_UNDEF;
    private boolean gaps = true;
    private boolean trim = false;
    private final CWBDataServer cwbServer;

    /** Creates a new instance of SacOutputer */
    public SacOutputer(EdgeQueryOptions options) {
        this.options = options;
        this.sacFF = new SacFileFactory();
        this.cwbServer = new CWBDataServerMSEED(options.host, options.port);
        sacFF.setCWBDataServer(this.cwbServer);
        sacFF.setMetaDataServer(new MetaDataServerImpl(
                QueryProperties.getNeicMetadataServerIP(),
                QueryProperties.getNeicMetadataServerPort()));
        parseExtras(options);
    }

    public void parseExtras(EdgeQueryOptions options) {
        for (int i = 0; i < options.extraArgs.size(); i++) {
            if (options.extraArgs.get(i).equals("-fill")) {
                this.fill = new Integer(options.extraArgs.get(i + 1));
            }
            if (options.extraArgs.get(i).equals("-nogaps")) {
                this.fill = 2147000000;
                this.gaps = false;
            }
            if (options.extraArgs.get(i).equals("-nometa")) {
                if (options.getSynthetic() != null) {
                    logger.severe("Synthetic phase calculations require the use of a MetaData server.");
                }
                this.sacFF.setMetaDataServer(null);
            }
            if (options.extraArgs.get(i).equals("-sactrim")) {
                this.trim = true;
            }
        }
    }

    public void doQuery() {

        sacFF.setQuakeML(options.getEvent());
        sacFF.setSynthetic(options.getSynthetic());
        sacFF.setCustomEvent(options.getCustomEvent());
        sacFF.setPicks(options.picks);
        sacFF.setFill(this.fill);
        sacFF.setGaps(this.gaps);
        sacFF.setTrim(this.trim);
        sacFF.setPzunit(options.sacpz ? options.pzunit : null);
        sacFF.setExtendedPhases(options.extendedPhases);

        sacFF.makeFiles(
                options.getBeginWithOffset(),
                options.getDuration(),
                options.getSeedname(),
                options.filemask);
    }

    public void makeFile(NSCL nscl, String filename,
            ArrayList<MiniSeed> blks) throws IOException {
        // This is deliberately left empty.
    }
}
