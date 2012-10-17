package nz.org.geonet.quake.domain.geojson;

//~--- non-JDK imports --------------------------------------------------------

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: geoffc
 * Date: 8/27/11
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureCollection {
    private List<Feature> features;

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }
}
