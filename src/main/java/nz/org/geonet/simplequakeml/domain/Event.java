/*
 * Copyright 2013, Institute of Geological & Nuclear Sciences Ltd or
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
 */



package nz.org.geonet.simplequakeml.domain;

//~--- non-JDK imports --------------------------------------------------------

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

/**
 * Event information, drawn from the preferred origin and magnitude.
 *
 * @author: Geoff Clitheroe
 * Date: 5/2/13
 * Time: 4:14 PM
 */
@Root(strict = false)
public class Event {
    @Element(required = false)
    private String agencyID;
    @Element
    private float depth;
    @Element
    private float latitude;
    @Element
    private float longitude;
    @Element
    private float magnitude;
    @Element(required = false)
    private String magnitudeType;
    @ElementList(required = false)
    private List<Pick> picks;
    @Element
    private String publicID;
    @Element
    private String time;
    @Element(required = false)
    private String type;

    public Event() {
        super();
    }

    public Event(String publicID, String type, String agencyID, String time, float latitude, float longitude,
                 float depth, float magnitude, String magnitudeType, List<Pick> picks) {
        this.publicID = publicID;
        this.type = type;
        this.agencyID = agencyID;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.depth = depth;
        this.magnitude = magnitude;
        this.magnitudeType = magnitudeType;
        this.picks = picks;
    }

    /**
     *
     * @return the event type e.g., earthquake.  Can return null.
     */
    public String getType() {
        return type;
    }

    /**
     * The publicID for the event from the underlying XML.  May need further processing to
     * extract the event ID.
     *
     * @return the event publicID e.g., smi:scs/0.6/2013p321497
     */
    public String getPublicID() {
        return publicID;
    }

    /**
     *
     * @return  the eventID extracted from the publicID e.g, 2013p321497
     */
    public String getEventID() {
        String[] publicIDParts = publicID.split("/");

        return publicIDParts[publicIDParts.length - 1];
    }

    /**
     *
     * @return the agencyID from the event.  Can be null.
     */
    public String getAgencyID() {
        return agencyID;
    }

    /**
     *
     * @return the time of the preferred Origin in string format direct from the XML e.g., 2013-04-29T22:53:47.168392Z
     */
    public String getTimeString() {
        return time;
    }

    /**
     *
     * @return the time of the preferred Origin.  Note Joda Time is only precise to milliseconds,
     * there may be a precision loss compared to getTimeString().
     */
    public DateTime getTime() {
        return new DateTime(this.time).withZone(DateTimeZone.UTC);
    }

    /**
     *
     * @return the latitude from the preferred Origin.
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     *
     * @return the longitude from the preferred Origin.
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     *
     * @return the depth from the preferred Origin.
     */
    public float getDepth() {
        return depth;
    }

    /**
     *
     * @return the magnitude from the preferred magnitude.
     */
    public float getMagnitude() {
        return magnitude;
    }

    /**
     *
     * @return the magnitude type from the preferred magnitude. Can be null.
     */
    public String getMagnitudeType() {
        return magnitudeType;
    }

    /**
     *
     * @return the picks associated with the preferred Origin.  May be empty.
     */
    public List<Pick> getPicks() {
        return (picks == null)
               ? new ArrayList<Pick>()
               : this.picks;
    }
}
