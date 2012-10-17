package nz.org.geonet.quake.domain.geojson;

//~--- non-JDK imports --------------------------------------------------------

/**
 * Created by IntelliJ IDEA.
 * User: geoffc
 * Date: 8/27/11
 * Time: 5:53 PM
 * To change this template use File | Settings | File Templates.
 */
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * The properties of the feature - these are mainly the
 * non spatial properties of the quake information.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Properties {
    private double depth;
    private double magnitude;
    private String origintime;
    private String publicid;
    private String status;
    private String updatetime;

    /**
     * @return The origin time as a JSON formatted string.
     */
    public String getOrigintime() {
        return origintime;
    }

    public void setOrigintime(String origintime) {
        this.origintime = origintime;
    }

    /**
     * @return   The update time as a JSON formatted string.
     */
    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    /**
     * @return     The earthquake magnitude.
     */
    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    /**
     *
     * @return The earthquake depth (km).
     */
    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    /**
     *
     * @return The earthquake status (automatic etc).
     */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return  The earthquake publicid.  Uniquely identifies the event.
     */
    public String getPublicid() {
        return publicid;
    }

    public void setPublicid(String publicid) {
        this.publicid = publicid;
    }
}
