package com.example.myapplication.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.example.myapplication.module.Place;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by avira on 20/09/2016.
 */
public class GetImageTask extends AsyncTask<Place, Void, Place> {
    //https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU&key=

    private onImageDownloadedListener listener;

    public GetImageTask(onImageDownloadedListener listener) {
        this.listener = listener;
    }

    @Override
    protected Place doInBackground(Place... params) {
        Place p = params[0];
        String photo_reference = p.getPhotoReference();
        if(photo_reference == null){
            Log.i("Debug","No Image for this place");
            return null;
        }
        HttpsURLConnection connection = null;
        BufferedReader reader = null;
        String urlString = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=%1$s&key=AIzaSyAffDVLihmDYSWhvNrZVxSSrQ6nXDSdd8E";
        try {
            URL url = new URL(String.format(urlString, photo_reference));
            connection = (HttpsURLConnection) url.openConnection();
            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                Log.i("ERROR","No Connection");
                return null;
            }
            Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());

            p.setImageBase64(BitMapToString(bitmap));
        } catch (IOException e) {
            e.printStackTrace();
        }


        return p;
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    @Override
    protected void onPostExecute(Place p) {

        listener.onImageReceived(p);

    }

    public interface onImageDownloadedListener {
        void onImageReceived(Place p);
    }
}
