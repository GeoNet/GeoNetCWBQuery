/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.anss.query.metadata;

import gov.usgs.anss.query.NSCL;
import org.joda.time.DateTime;

/**
 *
 * @author geoffc
 */
public interface MetaDataServer {

    String getResponseData(NSCL nscl, DateTime date, String pzunit);

}
