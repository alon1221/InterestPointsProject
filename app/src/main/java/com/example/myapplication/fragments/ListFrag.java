package com.example.myapplication.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.MainActivity;
import com.example.myapplication.adapters.PlacesAdapter;
import com.example.myapplication.module.Place;
import com.example.myapplication.module.Search;
import com.example.myapplication.tasks.CheckInternetConnectionTask;
import com.example.myapplication.tasks.GetImageTask;
import com.example.myapplication.tasks.GetPlacesTask;
import com.example.myapplication.tasks.LoadJsonPlacesTask;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.example.myapplication.fragments.SettingsFrag.RADIUS_KEY;

public class ListFrag extends Fragment implements GetImageTask.onImageDownloadedListener,
        LoadJsonPlacesTask.onPlacesLoadedFromJsonListener, LocationListener, GetPlacesTask.OnPlacesDownloadedListener {

    /**
     * KEY_LAST_PLACES_SEARCH
     */
    public static final String KEY_LAST_PLACES_SEARCH = "key_last_places";
    private PlacesAdapter adapter;
    private RecyclerView recyclerView;
    Context context;
    View v;
    private LocationManager locationManager;
    private Search search;
    LatLng myLastLoc;
    private SharedPreferences sp;
    ArrayList<Place> places;
    private LinearLayoutManager mLinearLayoutManager;

    public ListFrag() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        sp = PreferenceManager.getDefaultSharedPreferences(context);


        PlacesReciver receiver = new PlacesReciver();
        IntentFilter filter = new IntentFilter(MapViewFragment.ACTION_PLACES_BROADCAST);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);

        CheckInternetConnectionTask task = new CheckInternetConnectionTask(new CheckInternetConnectionTask.OnConnectionCheckedListener() {
            @Override
            public void onConnectionChecked(Boolean isNetworkAvailable) {
                if (isNetworkAvailable) {
                    Toast.makeText(context, "Connection Available", Toast.LENGTH_SHORT).show();
                    findPlacesByLoc(new Search(null, "", sp.getString(RADIUS_KEY, "500")));
                } else

                {
                    Toast.makeText(context, "Connection UnAvailable", Toast.LENGTH_SHORT).show();
                }
                getLastSearchFromSharedPrefrences();
            }
        }
        );
        task.execute(context);
    }

    @Override
    public void onStart() {
        super.onStart();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_list, container, false);


        adapter = new PlacesAdapter(context);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewListFrag);

        mLinearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(adapter);


        // Recive the last Search when Creating the view.
        // if its the first time it wouldn't do nothing.
        return v;

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

    public void setListPlacesAdapter(ArrayList<Place> places) {
        // coming to this method from main activity.
        // places -
        if (context == null) {
            Log.e("ERROR", "arrived setListPlacesAdapter on ListFrag while adapter is null");
            return;
        }
        if (adapter == null) {
            Log.e("ERROR", "arrived setListPlacesAdapter on ListFrag while adapter is null");
            return;

        }
        if (recyclerView == null) {
            Log.e("ERROR", "arrived setListPlacesAdapter on ListFrag while recyclerView is null");
            return;
        }
        this.places = places;
        adapter = new PlacesAdapter(places, context);
        recyclerView.setAdapter(adapter);
        for (int i = 0; i < adapter.getPlaces().size(); i++) {
            Place p = adapter.getPlaces().get(i);
            if (p.getImageBase64() == null && p.getPhotoReference() != null) {
                Log.d("Place", "name- " + p.getName() + "Photo Reference- " + p.getPhotoReference() + "\nImage: " + p.getImageBase64());
                GetImageTask task = new GetImageTask(this);
                task.execute(p);
            }

        }
    }

    @Override
    public void onImageReceived(Place place) {

        Log.i("onImageRecived", "image Recived");
        adapter.notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        if (adapter == null) {
            return;
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPlacesLoadedFromJson(ArrayList<Place> places) {
        if (adapter == null) {
            Log.e("Error", "onPlacesLoadedFromJson, ListFrag");
            Log.i("Error", "Cant load places to a null adapter");
            return;

        }
        setListPlacesAdapter(places);
        notifyDataSetChanged();
    }

    public void findPlacesByLoc(Search search) {
//        map.clear();
        this.search = search;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MainActivity.REQUEST_PERMISSION_LOCATION);
            return;

        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);

    }

    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.ACCESS_FINE_LOCATION))

                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MainActivity.REQUEST_PERMISSION_LOCATION);
            return;
        }
        Toast.makeText(context, "LocationChanged", Toast.LENGTH_SHORT).show();

        double lat, lng;

        lat = location.getLatitude();
        lng = location.getLongitude();
        myLastLoc = new LatLng(lat, lng);
        search.setLatLng(myLastLoc);
        SearchPlaces(search);
    }


    public void SearchPlaces(Search search) {
        if (context == null) {
            return;
        }

        LatLng latlng = search.getLatLng();
        GetPlacesTask task = new GetPlacesTask(this); // Create New GetPlacesTask Task
        task.execute(search); // execute task - look at on0PlacesReceived for Result Handling

    }

    @Override
    public void onPlacesReceived(String resultJson) {

        if (context == null) {
            Log.e("ERROR", "context Null onPlacesRecived ListFrag");
            return;
        }
        if (resultJson.equals("No Connection")) {
            Toast.makeText(context, "No Connection", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("Debug", "onPlaceReceived, ListFrag");


        try {
            ArrayList<Place> places = new ArrayList<>();


            JSONObject obj = new JSONObject(resultJson);
            JSONArray array = obj.getJSONArray("results");
            for (int i = 0; i < array.length(); i++) {

                JSONObject result = array.getJSONObject(i);
                JSONObject geometry = result.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");
                double distance;

                //TODO put location outside for prefBoost
                Location myLoc = new Location("");
                myLoc.setLatitude(myLastLoc.latitude);
                myLoc.setLongitude(myLastLoc.longitude);
                Location placeLoc = new Location("");
                placeLoc.setLatitude(lat);
                placeLoc.setLongitude(lng);
                distance = myLoc.distanceTo(placeLoc) / 1000;

                String name = result.getString("name");
                String place_id = result.getString("place_id");
                String reference = result.getString("reference");
                String address = result.getString("vicinity");

                JSONObject photo;
                JSONArray photos;
                String photo_reference = null;
                try {  //if there is no photo.
                    photos = result.getJSONArray("photos");
                    photo = photos.getJSONObject(0);
                    photo_reference = photo.getString("photo_reference");

                } catch (JSONException ignored) {
                }
                Log.i("" + name, "" + photo_reference);
                places.add(new Place(place_id, reference, photo_reference, name, address, lat, lng, distance, ""));
            }
            if (places.isEmpty() || places.size() == 0) {
                Toast.makeText(context, "No Results", Toast.LENGTH_SHORT).show();
                return;
            }
            // Saving the search as json to Shared Prefrences for use later
            String placesJSONstring = new Gson().toJson(places);
            sp.edit().putString(KEY_LAST_PLACES_SEARCH, placesJSONstring).apply();

            setListPlacesAdapter(places);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getLastSearchFromSharedPrefrences() {
        String placesJSON = PreferenceManager.getDefaultSharedPreferences(context).
                getString(KEY_LAST_PLACES_SEARCH, null);
        if (placesJSON != null) {
            if (!placesJSON.equals("")) {
                LoadJsonPlacesTask task = new LoadJsonPlacesTask(this);
                task.execute(placesJSON);
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    // RECIVES PLACES WHEN MAPVIEWFRAGMENT ENDS GETTING PLACES
    public class PlacesReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String placesJsonString = intent.getStringExtra("places");
            if (placesJsonString == null) {
                Log.e("ERROR", "ListFrag.PlacesReciver.onRecive placesJsonString is null");
                return;
            }
            Type type = new TypeToken<ArrayList<Place>>() {
            }.getType();


            ArrayList<Place> places = new Gson().fromJson(placesJsonString, type);
            setListPlacesAdapter(places);

        }

    }


    //UNUSED METHODS

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
