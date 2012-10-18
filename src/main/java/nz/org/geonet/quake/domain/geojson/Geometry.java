package nz.org.geonet.quake.domain.geojson;

//~--- non-JDK imports --------------------------------------------------------

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: geoffc
 * Date: 8/28/11
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Geometry {
    private ArrayList<Double> coordinates;

    public ArrayList<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<Double> coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * @return   The longitude of the feature.
     */
    public double getLongitude() {
        return coordinates.get(0).doubleValue();
    }

    /**
     * @return  The latitude of the feature.
     */
    public double getLatitude() {
        return coordinates.get(1).doubleValue();
    }
}
