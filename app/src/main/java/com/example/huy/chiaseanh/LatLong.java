package com.example.huy.chiaseanh;

import java.io.Serializable;

public class LatLong implements Serializable{
    private double lat;
    private double lng;

    public LatLong()
    {

    }
    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
