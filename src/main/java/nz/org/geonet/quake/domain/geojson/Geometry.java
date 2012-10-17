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
    private ArrayList<Float> coordinates;

    public ArrayList<Float> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<Float> coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * @return   The longitude of the feature.
     */
    public float getLongitude() {
        return coordinates.get(0).floatValue();
    }

    /**
     * @return  The latitude of the feature.
     */
    public float getLatitude() {
        return coordinates.get(1).floatValue();
    }
}
