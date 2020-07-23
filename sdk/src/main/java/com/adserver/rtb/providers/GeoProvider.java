package com.adserver.rtb.providers;

import android.content.Context;
import android.location.Location;

import com.adserver.rtb.attributes.LocationType;
import com.adserver.rtb.request.models.Geo;

public class GeoProvider {

    private Double latitude;
    private Double longitude;
    private String country;
    private String region;
    private String city;
    private String zipCode;
    private int locationType;

    public GeoProvider(Context context, Location location) {
        if (location != null) {
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
            this.locationType = LocationType.GPS;
        }
    }

    public Geo getGeo() {
        Geo geo = new Geo();
        geo.lat = getLatitude();
        geo.lon = getLongitude();
        return geo;
    }

    private Double getLatitude() {
        return latitude;
    }

    private Double getLongitude() {
        return longitude;
    }

    private String getCountry() {
        return country;
    }

    private String getRegion() {
        return region;
    }

    private String getCity() {
        return city;
    }

    private String getZipCode() {
        return zipCode;
    }

    private int getLocationType() {
        return locationType;
    }
}
