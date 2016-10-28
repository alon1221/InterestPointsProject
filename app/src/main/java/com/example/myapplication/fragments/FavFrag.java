package com.example.myapplication.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.adapters.PlacesAdapter;
import com.example.myapplication.db.PlacesHelper;
import com.example.myapplication.module.Place;

import java.util.ArrayList;

public class FavFrag extends Fragment {


    private Context context;
    private PlacesAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Place> favPlaces;

    public FavFrag() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;



        PlaceToAdd receiver = new PlaceToAdd();
        IntentFilter filter = new IntentFilter(PlacesAdapter.ACTION_SEND_NEW_FAV);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (favPlaces == null) {
            favPlaces = new ArrayList<>();
        }
        View v = inflater.inflate(R.layout.fragment_fav, container, false);
        adapter = new PlacesAdapter(favPlaces, context, true);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewFavFrag);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        return v;

    }

    public void setAdapterWithCursor(Cursor c) {
        if (favPlaces == null) {
            favPlaces = new ArrayList<>();
        }else{
            favPlaces.clear();
        }
c.moveToFirst();
        while (c.moveToNext()) {
            String reference = c.getString(c.getColumnIndex(PlacesHelper.COL3_REFERENCE));
            String image = c.getString(c.getColumnIndex(PlacesHelper.COL4_IMAGE));
            String photo_reference = c.getString(c.getColumnIndex(PlacesHelper.COL5_PHOTO_REFERENCE));
            String name = c.getString(c.getColumnIndex(PlacesHelper.COL6_NAME));
            String address = c.getString(c.getColumnIndex(PlacesHelper.COL7_ADDRESS));
            double lat = c.getDouble(c.getColumnIndex(PlacesHelper.COL8_LAT));
            double lng = c.getDouble(c.getColumnIndex(PlacesHelper.COL9_LNG));
            String phone = c.getString(c.getColumnIndex(PlacesHelper.COL10_PHONE));
            String place_id = c.getString(c.getColumnIndex(PlacesHelper.COL11_PLACE_ID));
            favPlaces.add(new Place(place_id, reference, image, photo_reference, name, address, lat, lng, phone, -1));
        }


        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }


    public void setFavPlacesAdapter(ArrayList<Place> places) {
        favPlaces = places;
        if (context == null) {
            Log.e("ERROR", "arrived setListPlacesAdapter on FavFrag while adapter is null");
            return;
        }
        if (adapter == null) {
            Log.e("ERROR", "arrived to setFavPlacesAdapter on FavFrag , while adapter is null");
            return;
        }
        if (recyclerView == null) {
            Log.e("ERROR", "arrived to setFavPlacesAdapter on FavFrag , while recyclerView is null");
            return;
        }
        adapter.setPlaces(favPlaces);
        adapter.notifyDataSetChanged();
    }

    public void deleteList() {
        adapter.deleteAll();
    }

    public void notifyDataSetChanged() {
        //This method is for notify from main activity
        if (adapter == null) {
            return;
        }
        adapter.notifyDataSetChanged();
    }


    public ArrayList<Place> getFavourites() {
        for (int i = 0; i < favPlaces.size(); i++) {
            // if the image remain string there is an error
            favPlaces.get(i).setImageBase64(null);
        }
        return favPlaces;
    }

    /**
     * Reciver to get Place from ListFrag to add to the list of FavFrag.
     */
    private class PlaceToAdd extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Recived Place from ListFrag , Adding it to the list, (already added to provider)
            Place p = intent.getParcelableExtra("place");
            if (p == null) {
                Log.e("ERROR", "ListFrag.PlacesReciver.onRecive place is null");
                return;
            }
            favPlaces.add(p);
            adapter.notifyDataSetChanged();
        }
    }


}
