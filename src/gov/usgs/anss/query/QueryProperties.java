/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import java.util.ResourceBundle;

/**
 *
 * @author geoffc
 */
public class QueryProperties {

    private static ResourceBundle props = ResourceBundle.getBundle("resources.geonetCwbQuery");

    public static String getGeoNetCwbIP() {
        return props.getString("geonet-cwb-ip");
    }

    public static int getGeoNetCwbPort() {
        return Integer.parseInt(props.getString("geonet-cwb-port"));
    }

    public static String getNeicCwbIP() {
        return props.getString("neic-cwb-ip");
    }

    public static int getNeicCwbPort() {
        return Integer.parseInt(props.getString("neic-cwb-port"));
    }

    public static String getNeicMetadataServerIP() {
        return props.getString("neic-metadata-server-ip");
    }

    public static int getNeicMetadataServerPort() {
        return Integer.parseInt(props.getString("neic-metadata-server-port"));
    }

    public static String getUsage() {
        return props.getString("usage");
    }
}
