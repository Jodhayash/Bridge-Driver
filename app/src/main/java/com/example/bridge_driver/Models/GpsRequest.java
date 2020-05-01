package com.example.bridge_driver.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// used by add_gps request
public class GpsRequest {
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lon")
    @Expose
    private double lon;

    public GpsRequest(){

    }
    public GpsRequest(double lat, double lon){
        this.lat=lat;
        this.lon=lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(double lat) { this.lat = lat; }

    public Double getLon() {
        return lon;
    }

    public void setLon(double lon) { this.lon = lon; }

}
