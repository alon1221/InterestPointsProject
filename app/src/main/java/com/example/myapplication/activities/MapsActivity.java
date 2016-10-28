package com.example.myapplication.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.myapplication.R;
import com.example.myapplication.fragments.MapViewFragment;
import com.google.android.gms.maps.GoogleMap;

public class MapsActivity extends FragmentActivity  {

    private GoogleMap mMap;
    MapViewFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (MapViewFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


    }



}
