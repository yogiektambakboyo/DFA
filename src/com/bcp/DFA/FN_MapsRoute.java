package com.bcp.DFA;
//by Haseem Saheed

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class FN_MapsRoute {
    private String name;
    private final List<LatLng> points;
    private List<FN_MapsSegment> segments;
    private String copyright;
    private String warning;
    private String country;
    private int length;
    private String polyline;
    private String durationText;
    private String distanceText;
    private String endAddressText;

    public String getEndAddressText() {
        return endAddressText;
    }

    public void setEndAddressText(String endAddressText) {
        this.endAddressText = endAddressText;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public String getDistanceText() {
        return distanceText;
    }

    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    public void setSegments(List<FN_MapsSegment> segments) {
        this.segments = segments;
    }

    public FN_MapsRoute() {
        points = new ArrayList<LatLng>();
        segments = new ArrayList<FN_MapsSegment>();
    }

    public void addPoint(final LatLng p) {
        points.add(p);
    }

    public void addPoints(final List<LatLng> points) {
        this.points.addAll(points);
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void addSegment(final FN_MapsSegment s) {
        segments.add(s);
    }

    public List<FN_MapsSegment> getSegments() {
        return segments;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param copyright the copyright to set
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    /**
     * @return the copyright
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * @param warning the warning to set
     */
    public void setWarning(String warning) {
        this.warning = warning;
    }

    /**
     * @return the warning
     */
    public String getWarning() {
        return warning;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }


    /**
     * @param polyline the polyline to set
     */
    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }

    /**
     * @return the polyline
     */
    public String getPolyline() {
        return polyline;
    }

}

