package com.example.myapplication.tasks;

import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.example.myapplication.fragments.ListFrag;
import com.example.myapplication.module.Place;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by avira on 06/10/2016.
 */

public class LoadJsonPlacesTask extends AsyncTask<String, Void, ArrayList<Place>> {

    private onPlacesLoadedFromJsonListener listener;

    public LoadJsonPlacesTask(onPlacesLoadedFromJsonListener listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<Place> doInBackground(String... params) {
        String placesJSONstring = params[0];
        Type type = new TypeToken<ArrayList<Place>>() {
        }.getType();
        ArrayList<Place> places;
        try {
            places = new Gson().fromJson(placesJSONstring, type);

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            // if there is an error with the json syntax removing the json from SP
            // probably can be caused only when there is an update on the code and changing the
            // Place variables types.
            PreferenceManager.getDefaultSharedPreferences(((ListFrag) listener).getContext()).edit()
                    .remove(ListFrag.KEY_LAST_PLACES_SEARCH).apply();
            return null;

        }
        return places;
    }

    @Override
    protected void onPostExecute(ArrayList<Place> places) {
        listener.onPlacesLoadedFromJson(places);
    }

    public interface onPlacesLoadedFromJsonListener {
        void onPlacesLoadedFromJson(ArrayList<Place> places);
    }
}
