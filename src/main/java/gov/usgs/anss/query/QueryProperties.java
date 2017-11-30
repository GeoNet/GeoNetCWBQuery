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

import java.util.ResourceBundle;

/**
 *
 * @author geoffc
 * @updated Howard Wu 29/09/2017
 */
public class QueryProperties {

    private static ResourceBundle props = ResourceBundle.getBundle("geonetCwbQuery");

    public static String getUsage() {
        return props.getString("usage");
    }

    public static String getFDSNBase() {
        return props.getString("fdsn-base");
    }

    public static String getFDSNEventQueryUrl() {
        return getFDSNBase()+"/event/1/query";
    }

    public static String getFDSNStationQueryUrl() {
        return getFDSNBase()+"/station/1/query";
    }

    public static String getFDSNDataselectQueryUrl() {
        return getFDSNBase()+"/dataselect/1/query";
    }
}
