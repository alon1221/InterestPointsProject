package com.example.myapplication.module;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by avira on 20/09/2016.
 */
public class Search implements Serializable {
    private LatLng latLng;
    private String qwery;
    private String Radius;

    public Search(LatLng latLng, String qwery, String radius) {
        this.latLng = latLng;
        this.qwery = qwery;
        Radius = radius;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getQwery() {
        return qwery;
    }

    public void setQwery(String qwery) {
        this.qwery = qwery;
    }

    public String getRadius() {
        return Radius;
    }

    public void setRadius(String radius) {
        Radius = radius;
    }
}
