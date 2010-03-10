/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.cwb.holdings;

import org.joda.time.DateTime;

/**
 *
 * @author geoffc
 */
public interface CWBHoldingsServer {

    /**
     *
     * @param begin
     * @param duration
     * @return
     */
    String listChannels(DateTime begin, Double duration);
}
