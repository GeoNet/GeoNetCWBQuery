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
import org.simpleframework.xml.Root;

/**
 * Picks that have been associated with the origin.
 *
 * @author: Geoff Clitheroe
 * Date: 5/3/13
 * Time: 8:07 AM
 */
@Root(strict = false)
public class Pick {
    @Element(required = false)
    private String channel;
    @Element(required = false)
    private String location;
    @Element(required = false)
    private String mode;
    @Element
    private String network;
    @Element
    private String phase;
    @Element
    private String station;
    @Element(required = false)
    private String status;
    @Element
    private String time;
    @Element(required = false)
    private float weight;

    public Pick() {
        super();
    }

    public Pick(String phase, String mode, String status, String time, float weight, String network, String station,
                String location, String channel) {
        this.phase = phase;
        this.mode = mode;
        this.status = status;
        this.time = time;
        this.weight = weight;
        this.network = network;
        this.station = station;
        this.location = location;
        this.channel = channel;
    }

    public String getPhase() {
        return phase;
    }

    /**
     *
     * @return may be null
     */
    public String getMode() {
        return mode;
    }

    /**
     *
     * @return may be null
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @return the time of the pick in string format direct from the XML e.g., 2013-04-29T22:53:47.168392Z
     */
    public String getTimeString() {
        return time;
    }

    /**
     *
     * @return the time of the pick.  Note Joda Time is only precise to milliseconds,
     * there may be a precision loss compared to getTimeString().
     */
    public DateTime getTime() {
        return new DateTime(this.time).withZone(DateTimeZone.UTC);
    }

    /**
     *
     * @return the weight applied to the pick in the origin.  Returns 0.0 if undefined in the XML.
     */
    public float getWeight() {
        return weight;
    }

    /**
     * Part of the waveform ID for the waveform that the pick was recorded on.
     *
     * @return the network part of the waveform id e.g., NZ.
     */
    public String getNetwork() {
        return network;
    }

    /**
     * Part of the waveform ID for the waveform that the pick was recorded on.
     *
     * @return the station part of the waveform id e.g., WPHZ.
     */
    public String getStation() {
        return station;
    }

    /**
     * Part of the waveform ID for the waveform that the pick was recorded on.
     *
     * @return the location part of the waveform id e.g., 10.  Can be null
     */
    public String getLocation() {
        return location;
    }

    /**
     * Part of the waveform ID for the waveform that the pick was recorded on.
     *
     * @return the channel part of the waveform id e.g., EHZ.  Can be null
     */
    public String getChannel() {
        return channel;
    }
}
