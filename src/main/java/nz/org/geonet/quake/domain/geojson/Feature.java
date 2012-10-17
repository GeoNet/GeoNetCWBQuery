package nz.org.geonet.quake.domain.geojson;

//~--- non-JDK imports --------------------------------------------------------

/**
 * Created by IntelliJ IDEA.
 * User: geoffc
 * Date: 8/25/11
 * Time: 6:54 PM
 * To change this template use File | Settings | File Templates.
 */
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Feature {
    private Geometry geometry;
    private Properties properties;

    /**
     *
     * @return
     */
    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
