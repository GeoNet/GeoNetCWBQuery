/*
 * Copyright 2010, Institute of Geological & Nuclear Sciences Ltd or
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
