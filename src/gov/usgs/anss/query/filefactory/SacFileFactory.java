/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.filefactory;

import edu.sc.seis.TauP.SacTimeSeries;
import gov.usgs.anss.query.CustomEvent;
import gov.usgs.anss.query.NSCL;
import gov.usgs.anss.query.ZeroFilledSpan;
import gov.usgs.anss.query.cwb.data.CWBDataServer;
import gov.usgs.anss.query.metadata.ChannelMetaData;
import gov.usgs.anss.query.metadata.MetaDataQuery;
import gov.usgs.anss.query.metadata.MetaDataServer;
import gov.usgs.anss.query.outputter.Filename;
import gov.usgs.anss.seed.MiniSeed;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;
import org.joda.time.DateTime;

/**
 *
 * @author geoffc
 */
public class SacFileFactory {

    private static final Logger logger = Logger.getLogger(SacFileFactory.class.getName());
    private CWBDataServer cwbServer = null;
    private MetaDataServer metaDataServer = null;
    private Quakeml quakeml = null;
    private String synthetic = null;
    private CustomEvent customEvent = null;
    private String pzunit = null;
    private boolean picks = true;
    private boolean extendedPhases = false;
    private boolean gaps = true;
    private boolean trim = false;
    private Integer fill = SacTimeSeries.INT_UNDEF;

    static {
        logger.fine("$Id$");
    }

    public void setCWBDataServer(CWBDataServer cwbServer) {
        this.cwbServer = cwbServer;
    }

    public void setMetaDataServer(MetaDataServer metaDataServer) {
        this.metaDataServer = metaDataServer;
    }

    public void setQuakeML(Quakeml quakeml) {
        this.quakeml = quakeml;
    }

    public void setSynthetic(String synthetic) {
        this.synthetic = synthetic;
    }

    public void setCustomEvent(CustomEvent customEvent) {
        this.customEvent = customEvent;
    }

    public void setExtendedPhases(boolean extendedPhases) {
        this.extendedPhases = extendedPhases;
    }

    public void setFill(Integer fill) {
        this.fill = fill;
    }

    public void setGaps(boolean gaps) {
        this.gaps = gaps;
    }

    public void setPicks(boolean picks) {
        this.picks = picks;
    }

    public void setPzunit(String pzunit) {
        this.pzunit = pzunit;
    }

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    public void makeFiles(
            DateTime begin,
            double duration,
            String nsclSelectString,
            String mask) {
        cwbServer.query(begin, duration, nsclSelectString);
        if (cwbServer.hasNext()) {
            do {
                SacTimeSeries sac = makeTimeSeries(
                        cwbServer.getNext(),
                        begin,
                        duration,
                        this.fill,
                        this.gaps,
                        this.trim);
                if (sac != null) {
                    if (this.quakeml != null) {
                        SacHeaders.setEventHeader(sac, this.quakeml);
                        if (this.picks) {
                            if (this.synthetic == null) {
                                SacHeaders.setPhasePicks(sac, this.quakeml);
                            } else {
                                SacHeaders.setPhasePicks(sac, this.quakeml, this.extendedPhases, this.synthetic);
                            }
                        } else {
                            if (this.synthetic != null) {
                                SacHeaders.setPhasePicks(sac, this.extendedPhases, this.synthetic);
                            }
                        }
                    } else {
                        if (this.customEvent != null) {
                            SacHeaders.setEventHeader(
                                    sac,
                                    this.customEvent.getEventTime(),
                                    this.customEvent.getEventLat(),
                                    this.customEvent.getEventLon(),
                                    this.customEvent.getEventDepth(),
                                    this.customEvent.getEventMag(),
                                    this.customEvent.getEventMagType().magNum(),
                                    this.customEvent.getEventType().eventTypeNum());
                        }
                        if (this.synthetic != null) {
                            SacHeaders.setPhasePicks(sac, this.extendedPhases, this.synthetic);
                        }
                    }

                    outputFile(sac, begin, mask, this.pzunit);
                } else {
                    // TODO logger message about null data
                }
            } while (cwbServer.hasNext());
        } else {
            logger.info(
                    String.format(
                    "No matching data for \"%s\", at begin time %s, duration %.2fs, on %s:%d",
                    nsclSelectString,
                    begin.toString("YYYY/MM/dd HH:mm:ss"), duration,
                    cwbServer.getHost(), cwbServer.getPort()));
        }
    }

    public SacTimeSeries makeTimeSeries(
            TreeSet<MiniSeed> miniSeed,
            DateTime begin,
            double duration,
            Integer fill,
            boolean gaps,
            boolean trim) {
        // This logic isn't strictly the same as SacOutputter.
        if (!gaps && fill == null) {
            fill = 2147000000;
        }

        // Use the span to populate a sac file

        TimeZone tz = TimeZone.getTimeZone("GMT+0");
        TimeZone.setDefault(tz);
        GregorianCalendar start = new GregorianCalendar();
        start.setTimeInMillis(begin.getMillis());

        // build the zero filled area (either with exact limits or with all blocks)
        ZeroFilledSpan span = new ZeroFilledSpan(new ArrayList(miniSeed), start, duration, fill);
        if (span.getRate() <= 0.00) {
            return null;         // There is no real data to put in SAC
        }

        NSCL nscl = NSCL.stringToNSCL(miniSeed.first().getSeedName());

        logger.fine("ZeroSpan=" + span.toString());

        int noval = span.getNMissingData();

        if (!gaps && span.hasGapsBeforeEnd()) {
            logger.warning("  ** " + nscl.toString() + " has gaps - discarded # missing =" + noval);
            return null;
        }

        //ZeroFilledSpan span = new ZeroFilledSpan(blks);

        SacTimeSeries sac = new SacTimeSeries();
        sac.npts = span.getNsamp();

        // Set the byteOrder based on native architecture and sac statics
        sac.nvhdr = 6;                // Only format supported
        sac.b = 0.;           // beginning time offsed
        sac.e = ((span.getNsamp() - 1) / span.getRate());
        sac.iftype = SacTimeSeries.ITIME;
        sac.leven = SacTimeSeries.TRUE;
        sac.delta = (1. / span.getRate());
        sac.depmin = span.getMin();
        sac.depmax = span.getMax();
        sac.nzyear = span.getStart().get(Calendar.YEAR);
        sac.nzjday = span.getStart().get(Calendar.DAY_OF_YEAR);
        sac.nzhour = span.getStart().get(Calendar.HOUR_OF_DAY);
        sac.nzmin = span.getStart().get(Calendar.MINUTE);
        sac.nzsec = span.getStart().get(Calendar.SECOND);
        sac.nzmsec = span.getStart().get(Calendar.MILLISECOND);
        sac.iztype = SacTimeSeries.IB;

        sac.knetwk = nscl.getNetwork().replaceAll("_", "").trim();
        sac.kstnm = nscl.getStation().replaceAll("_", "").trim();
        sac.kcmpnm = nscl.getChannel().replaceAll("_", "").trim();
        sac.khole = nscl.getLocation().replaceAll("_", "").trim();

        logger.finer("Sac stla=" + sac.stla + " stlo=" + sac.stlo + " stel=" + sac.stel + " cmpaz=" + sac.cmpaz + " cmpinc=" + sac.cmpinc + " stdp=" + sac.stdp);
        sac.y = new double[span.getNsamp()];   // allocate space for data
        int nodata = 0;
        for (int i = 0; i < span.getNsamp(); i++) {
            sac.y[i] = span.getData(i);
            if (sac.y[i] == fill) {
                nodata++;
                //if(nodata <3) logger.finest(i+" nodata len="+span.getNsamp());
            }
        }
        if (nodata > 0) {
            logger.finest("#No data points = " + nodata + " fill=" + fill + " npts=" + sac.npts);
        }
        if (trim) {
            int trimmed = sac.trimNodataEnd(fill);
            if (trimmed > 0) {
                logger.info(trimmed + " data points trimmed from end containing no data");
            }
        }

        if (metaDataServer != null) {
            MetaDataQuery mdq = new MetaDataQuery(metaDataServer);
            ChannelMetaData md = mdq.getChannelMetaData(nscl, begin);
            sac = SacHeaders.setChannelHeader(sac, md);
        }

        return sac;
    }

    // TODO: move the getSACResponse to outputPZ or something.
    protected void outputFile(
            SacTimeSeries timeSeries,
            DateTime begin,
            String mask,
            String pzunit) {

        NSCL nscl = new NSCL(timeSeries.knetwk,
                timeSeries.kstnm,
                timeSeries.kcmpnm,
                timeSeries.khole);

        String filename = Filename.makeFilename(mask, nscl, begin);
        if (mask.equals("%N")) {
            filename += ".sac";
        }
        filename = filename.replaceAll("[__]", "_");

        try {
            timeSeries.write(filename);
            if (pzunit != null && metaDataServer != null) {
                MetaDataQuery mdq = new MetaDataQuery(metaDataServer);
                mdq.getSACResponse(nscl, begin, pzunit, filename + ".pz");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SacFileFactory.class.getName()).log(Level.SEVERE,
                    "File not found writing to SAC", ex);
        } catch (IOException ex) {
            Logger.getLogger(SacFileFactory.class.getName()).log(Level.SEVERE,
                    "IO exception writing to SAC", ex);
        }
    }
}
