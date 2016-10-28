package com.example.myapplication.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.myapplication.module.Search;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

/**
 * This task gets a Search reference
 * and return Json string with the result
 */
public class GetPlacesTask extends AsyncTask<Search, Void, String> {

    private OnPlacesDownloadedListener listener;

    public GetPlacesTask(OnPlacesDownloadedListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Search... params) {
        //DOWNLOAD    key = AIzaSyAffDVLihmDYSWhvNrZVxSSrQ6nXDSdd8E
        HttpsURLConnection connection = null;
        BufferedReader reader = null;
        Search search = params[0];
        LatLng latLng = search.getLatLng();
        String radius = search.getRadius();

        try {
            String query = URLEncoder.encode(search.getQwery(), "utf-8");

            String urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%1$s,%2$s&radius=10000&keyword=%4$s&key=AIzaSyAffDVLihmDYSWhvNrZVxSSrQ6nXDSdd8E";
            URL url = new URL(String.format(urlString, latLng.latitude, latLng.longitude, radius, query));


            connection = (HttpsURLConnection) url.openConnection();
            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                Log.e("ERROR","Connection Error, url , server or connection are bad");
                return "No Connection";
            }
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            do {
                line = reader.readLine();
                builder.append(line);
            }
            while (line != null);

            return builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "No Connection";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onPlacesReceived(s);
    }

    public interface OnPlacesDownloadedListener {
        void onPlacesReceived(String result);
    }
}
