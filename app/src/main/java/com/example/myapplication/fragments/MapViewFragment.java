package com.example.myapplication.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.MainActivity;
import com.example.myapplication.activities.MapsActivity;
import com.example.myapplication.adapters.PlacesAdapter;
import com.example.myapplication.module.Place;
import com.example.myapplication.module.TagSaver;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMyLocationButtonClickListener {

    public static final String ACTION_PLACES_BROADCAST = "com.example.myapplication.fragments.placesbroadcast";

    private MapView mapView;
    private GoogleMap map;
    private Context context;
    private LocationManager locationManager;
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map_view, container, false);
        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        sp = PreferenceManager.getDefaultSharedPreferences(context);

        // register reciver
        PlaceReciverToShowOnMap receiver = new PlaceReciverToShowOnMap();
        IntentFilter filter = new IntentFilter(PlacesAdapter.PlaceHolder.ACTION_SHOW_PLACE_ON_MAP);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
    }

    private void afterMapReady() {
        if (MainActivity.isTablet) {
            // on tablet
        } else {
            // on phone
            Intent in = ((MapsActivity) context).getIntent();
            if (in.getDoubleExtra("lat", 0) != 0 || in.getDoubleExtra("lng", 0) != 0) {

                LatLng latlng = new LatLng(in.getDoubleExtra("lat", 0), in.getDoubleExtra("lng", 0));
                putMarkerOnMap(latlng, in.getStringExtra("name"), in.getStringExtra("address"));

            } else {
                //if "show favs on map" pressed
                ArrayList<Place> places = in.getParcelableArrayListExtra("places");
                if (places != null && !places.isEmpty()) {
                    showArrayOnMap(places);
                }

            }
        }


    }

    public void showArrayOnMap(ArrayList<Place> places) {
        Place p;
        LatLng latlng;
        ArrayList<Marker> markers = new ArrayList<>();
        for (int i = 0; i < places.size(); i++) {
            p = places.get(i);
            latlng = new LatLng(p.getLat(), p.getLng());

            markers.add(map.addMarker(new MarkerOptions().position(latlng).title(p.getName()).snippet(p.getAddress())));

        }
        // Zoom To Fill All Markers
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 200; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.animateCamera(cu);
    }


    public void putMarkerOnMap(LatLng latlng, String name, String address) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
        map.addMarker(new MarkerOptions().position(latlng).title(name)
                .snippet(address));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(this);
        map.setOnMapLongClickListener(this);
        map.setOnMyLocationButtonClickListener(this);
        afterMapReady();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        //TODO find out how to open a new dialog with the information and actions.
        Toast.makeText(getContext(), "Title: " + marker.getTitle() + " Snippet: " + marker.getSnippet(), Toast.LENGTH_SHORT).show();
        return false;
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        TagSaver.setTag3(getTag());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        map.addMarker(new MarkerOptions().position(latLng).title("lon: " + latLng.longitude + "\nlat: " + latLng.latitude));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    public class PlaceReciverToShowOnMap extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            map.clear();

            double lat = intent.getDoubleExtra("lat", 0);
            double lng = intent.getDoubleExtra("lng", 0);
            LatLng latlng;
            latlng = new LatLng(lat, lng);
            if (lat == 0 || lng == 0) {
                Log.e("ERROR", "got lat = 0 or lng = 0 onRecive PlaceReciverToShowOnMap ");
            }
            putMarkerOnMap(latlng, intent.getStringExtra("name"), intent.getStringExtra("address"));


        }
    }


}