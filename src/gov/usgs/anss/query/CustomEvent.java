package gov.usgs.anss.query;

import gov.usgs.anss.query.filefactory.SacHeaders.SacEventType;
import gov.usgs.anss.query.filefactory.SacHeaders.SacMagType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class CustomEvent {

    private DateTime eventTime;
    private Double eventLat;
    private Double eventLon;
    private Double eventDepth;
    private Double eventMag;
    private SacMagType eventMagType = SacMagType.MX;
    private SacEventType eventType = SacEventType.NULL;
    private static String beginFormat = "YYYY/MM/dd HH:mm:ss";
    private static String beginFormatDoy = "YYYY,DDD-HH:mm:ss";
	private static String millisFormat = ".SSS";
	private static DateTimeFormatter parseMillisFormat = DateTimeFormat.forPattern(millisFormat);
    private static DateTimeFormatter parseBeginFormat = new DateTimeFormatterBuilder()
			.appendPattern(beginFormat)
			.appendOptional(parseMillisFormat.getParser())
			.toFormatter()
			.withZone(DateTimeZone.forID("UTC"));
    private static DateTimeFormatter parseBeginFormatDoy = new DateTimeFormatterBuilder()
			.appendPattern(beginFormatDoy)
			.appendOptional(parseMillisFormat.getParser())
			.toFormatter()
			.withZone(DateTimeZone.forID("UTC"));

    /**
     * @return the eventTime
     */
    public DateTime getEventTime() {
        return eventTime;
    }

    public CustomEvent(DateTime eventTime, Double eventLat, Double eventLon, Double eventDepth, Double eventMag, SacMagType eventMagType, SacEventType eventType) {
        this.eventTime = eventTime;
        this.eventLat = eventLat;
        this.eventLon = eventLon;
        this.eventDepth = eventDepth;
        this.eventMag = eventMag;
        this.eventMagType = eventMagType;
        this.eventType = eventType;
    }

    public CustomEvent() {
    }

    /**
     * @param eventTime the eventTime to set
     */
    public void setEventTime(DateTime eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * @param eventTime the eventTime to set
     */
    public void setEventTime(String eventTime) throws IllegalArgumentException {
        this.eventTime = null;

        try {
            this.eventTime = parseBeginFormat.parseDateTime(eventTime);
        } catch (Exception e) {
        }

        if (eventTime == null) {
            try {
                this.eventTime = parseBeginFormatDoy.parseDateTime(eventTime);
            } catch (Exception e) {
            }
        }

        // TODO Would be ideal if this error contained any range errors from
        // parseDateTime but this is hard with the two attempts at parsing.
        if (eventTime == null) {
            throw new IllegalArgumentException(
					"Error parsing begin time.  Allowable formats " +
                    "are: " + beginFormat + "[" + millisFormat + "] or " +
					beginFormatDoy + "[" + millisFormat + "]");
        }
    }

    /**
     * @return the eventLat
     */
    public Double getEventLat() {
        return eventLat;
    }

    /**
     * @param eventLat the eventLat to set
     */
    public void setEventLat(Double eventLat) {
        this.eventLat = eventLat;
    }

    /**
     * @param eventLat the eventLat to set
     */
    public void setEventLat(String eventLat) {
        setEventLat(Double.parseDouble(eventLat));
    }

    /**
     * @return the eventLon
     */
    public Double getEventLon() {
        return eventLon;
    }

    /**
     * @param eventLon the eventLon to set
     */
    public void setEventLon(Double eventLon) {
        this.eventLon = eventLon;
    }

    /**
     * @param eventLat the eventLat to set
     */
    public void setEventLon(String eventLon) {
        setEventLon(Double.parseDouble(eventLon));
    }

    /**
     * @return the eventDepth in m
     */
    public Double getEventDepth() {
		if (eventDepth != null) {
			return eventDepth * 1000.0d;
		}

        return eventDepth;
    }

    /**
     * @param eventDepth the eventDepth to set
     */
    public void setEventDepth(Double eventDepth) {
        this.eventDepth = eventDepth;
    }

    /**
     * @param eventDepth the eventDepth to set
     */
    public void setEventDepth(String eventDepth) {
        setEventDepth(Double.parseDouble(eventDepth));
    }

    /**
     * @return the eventMag
     */
    public Double getEventMag() {
        return eventMag;
    }

    /**
     * @param eventMag the eventMag to set
     */
    public void setEventMag(Double eventMag) {
        this.eventMag = eventMag;
    }

    /**
     * @param eventMag the eventMag to set
     */
    public void setEventMag(String eventMag) {
        setEventMag(Double.parseDouble(eventMag));
    }

    /**
     * @return the eventMagType
     */
    public SacMagType getEventMagType() {
        return eventMagType;
    }

    /**
     * @param eventMagType the eventMagType to set
     */
    public void setEventMagType(SacMagType eventMagType) {
        this.eventMagType = eventMagType;
    }

    /**
     * @param eventMagType the eventMagType to set
     */
    public void setEventMagType(String eventMagType) {
        setEventMagType(SacMagType.valueOf(eventMagType));
    }

    /**
     * @return the eventType
     */
    public SacEventType getEventType() {
        return eventType;
    }

    /**
     * @param eventType the eventType to set
     */
    public void setEventType(SacEventType eventType) {
        this.eventType = eventType;
    }

    /**
     * @param eventType the eventType to set
     */
    public void setEventType(String eventType) {
        setEventType(SacEventType.valueOf(eventType));
    }
}
