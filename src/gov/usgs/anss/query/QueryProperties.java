/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String getQuakeMlUri(String authority) {
        return props.getString(authority + ".quakeml-uri");
    }

    public static List<String> getQuakeMlAuthorities() {
		List<String> auths = new ArrayList<String>();
		Pattern p = Pattern.compile("^(.*)\\.quakeml-uri$");
        
		Enumeration<String> keys = props.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();

			Matcher m = p.matcher(key);
			if (m.matches()) {
				auths.add(m.group(1));
			}
		}
		
		return auths;
    }
}
