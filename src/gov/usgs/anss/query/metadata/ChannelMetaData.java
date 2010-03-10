/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.metadata;

import gov.usgs.anss.query.NSCL;

/**
 *
 * @author geoffc
 */
public class ChannelMetaData {

    private String code;

    /**
     * Get the value of stationCode
     *
     * @return the value of stationCode
     */
    public String getCode() {
        return code;
    }
    private String network;

    /**
     * Get the value of network
     *
     * @return the value of network
     */
    public String getNetwork() {
        return network;
    }
    private String channel;

    /**
     * Get the value of channel
     *
     * @return the value of channel
     */
    public String getChannel() {
        return channel;
    }
    private String location;

    /**
     * Get the value of location
     *
     * @return the value of location
     */
    public String getLocation() {
        return location;
    }
    private double latitude = Double.MIN_VALUE;

    /**
     * Get the value of latitude
     *
     * @return the value of latitude
     */
    public double getLatitude() {
        return latitude;
    }
    private double longitude = Double.MIN_VALUE;

    /**
     * Get the value of longitude
     *
     * @return the value of longitude
     */
    public double getLongitude() {
        return longitude;
    }
    private double elevation = Double.MIN_VALUE;

    /**
     * Get the value of elevation
     *
     * @return the value of elevation
     */
    public double getElevation() {
        return elevation;
    }
    private double azimuth = Double.MIN_VALUE;

    /**
     * Get the value of azimuth
     *
     * @return the value of azimuth
     */
    public double getAzimuth() {
        return azimuth;
    }
    private double dip = Double.MIN_VALUE;

    /**
     * Get the value of dip
     *
     * @return the value of dip
     */
    public double getDip() {
        return dip;
    }

    /**
     * Set the value of dip
     *
     * @param dip new value of dip
     */
    public void setDip(double dip) {
        this.dip = dip;
    }
    private double depth = Double.MIN_VALUE;

    /**
     * Get the value of depth
     *
     * @return the value of depth
     */
    public double getDepth() {
        return depth;
    }

    /**
     * Set the value of depth
     *
     * @param depth new value of depth
     */
    public void setDepth(double depth) {
        this.depth = depth;
    }

    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }

    public ChannelMetaData(String network, String code, String channel, String location) {
        this.code = code;
        this.network = network;
        this.channel = channel;
        this.location = location;
    }

    public ChannelMetaData(NSCL nscl) {
        this.code = nscl.getStation();
        this.network = nscl.getNetwork();
        this.channel = nscl.getChannel();
        this.location = nscl.getLocation();
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
