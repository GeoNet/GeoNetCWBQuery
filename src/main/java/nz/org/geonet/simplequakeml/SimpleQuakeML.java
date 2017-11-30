package nz.org.geonet.simplequakeml;

//~--- non-JDK imports --------------------------------------------------------

import nz.org.geonet.simplequakeml.domain.Event;

import java.io.InputStream;

//~--- JDK imports ------------------------------------------------------------

/**
 * Created with IntelliJ IDEA.
 *
 * @author: Geoff Clitheroe
 * Date: 5/3/13
 * Time: 1:37 PM
 */
public interface SimpleQuakeML {

    /**
     * Parses QuakeML from the input stream and returns an Event object.
     *
     * @param inputStream of a QuakeML.
     * @return an Event representation of the QuakeML.
     * @throws Exception if there are problems parsing the XML or there is insufficient information
     * to hydrate the Event.
     */
    Event read(InputStream inputStream) throws Exception;

    /**
     * Fetches QuakeML from a URL and returns an Event object.
     *
     * @param url to fetch QuakeML from.
     * @return an Event representation of the QuakeML.
     * @throws Exception if there are problems fetching or parsing the XML, or there is insufficient information
     * to hydrate the Event.
     */
    Event read(String url) throws Exception;
}
