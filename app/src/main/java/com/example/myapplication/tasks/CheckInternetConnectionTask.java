package com.example.myapplication.tasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by avira on 08/10/2016.
 */

public class CheckInternetConnectionTask extends AsyncTask<Context, Void, Boolean> {

    private OnConnectionCheckedListener listener;

    public CheckInternetConnectionTask(OnConnectionCheckedListener listener) {
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Context... params) {
        Context c= params[0];
        if (isNetworkAvailable(c)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("Tag", "Error checking internet connection", e);
            }
        } else {
            Log.d("Tag", "No network available!");
        }
        return false;
    }

    private boolean isNetworkAvailable(Context c) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @Override
    protected void onPostExecute(Boolean isNetworkAvailable) {
        listener.onConnectionChecked(isNetworkAvailable);
    }

    public interface OnConnectionCheckedListener {
        void onConnectionChecked(Boolean isNetworkAvailable);
    }
}
