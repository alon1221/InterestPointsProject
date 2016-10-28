package com.example.myapplication.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.MainActivity;
import com.example.myapplication.activities.MapsActivity;
import com.example.myapplication.db.PlacesHelper;
import com.example.myapplication.db.PlacesProvider;
import com.example.myapplication.fragments.SettingsFrag;
import com.example.myapplication.module.Place;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

/**
 * the recycler view adapter
 */
public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceHolder> {

    public static final String ACTION_SEND_NEW_FAV = "action_new_fav";
    private ArrayList<Place> places = new ArrayList<>();
    private Context context;
    private int onCreateCounter;
    private boolean isFavList = false;


    public PlacesAdapter(ArrayList<Place> places, Context context, boolean isFavList) {
        this.places = places;
        this.context = context;
        this.isFavList = isFavList;
    }

    public PlacesAdapter(ArrayList<Place> places, Context context) {
        this.places = places;
        this.context = context;
    }


    public PlacesAdapter(Context context) {
        this.context = context;
    }


    public void setPlaces(ArrayList<Place> places) {
        this.places = places;
    }

    @Override
    public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        onCreateCounter++;
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.list_card_item, parent, false);
        if (isFavList) {
            v.findViewById(R.id.txtFavs).setVisibility(View.GONE);
            v.findViewById(R.id.txtFavs).setClickable(false);
            v.findViewById(R.id.txtDis).setVisibility(View.GONE);
        }
        Log.i("Debug", onCreateCounter + " :onCreateViewHolder");
        return new PlaceHolder(v);
    }

    @Override
    public void onBindViewHolder(PlaceHolder holder, int position) {
        Place p = places.get(position);
        holder.imgCard.setImageBitmap(StringToBitMap(p.getImageBase64()));
        holder.txtName.setText(String.format("Name: %s", p.getName()));
        holder.setDistanceText(p.getDistance());
        holder.txtAddress.setText(String.format("Address: %s", p.getAddress()));

    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public int getItemCount() {
        if (places == null) {
            return 0;
        }
        return places.size();
    }

    public ArrayList<Place> getPlaces() {
        return places;
    }

    public void deleteAll() {
        places.clear();
        notifyDataSetChanged();
    }

    public Place getPlaceById(String placeId) {
        for (int i = 0; i < places.size(); i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Objects.equals(places.get(i).getPlaceId(), placeId)) {
                    return places.get(i);
                }
            }
        }
        return null;
    }


    public class PlaceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public static final String ACTION_SHOW_PLACE_ON_MAP = "actionShowPlaceOnMap";
        private ImageView imgCard;
        private TextView txtName;
        private TextView txtDis;
        private TextView txtAddress;


        public PlaceHolder(View view) {
            super(view);
            imgCard = (ImageView) view.findViewById(R.id.imgCard);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtDis = (TextView) view.findViewById(R.id.txtDis);
            txtAddress = (TextView) view.findViewById(R.id.txtAddress);

            view.setOnClickListener(this);
            view.findViewById(R.id.txtShare).setOnClickListener(this);
            view.findViewById(R.id.txtFavs).setOnClickListener(this);
            view.findViewById(R.id.card_view).setOnClickListener(this);
        }

        public void bind(Place place) {
            if (place == null) {
                Log.e("Error", "arrived bind on PlaceHolder in PlaceAdapter while recived null place");
                return;
            }
            if (place.getImageBase64() == null) {
                Log.d("debug", "arrived bind on PlaceHolder in PlaceAdapter while recived place.getImageBase64 is null");
            }
        }




        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // if Clicked txtFavs - add to favorites
                case R.id.txtFavs:
                    // Get the place related to the clicked location.

                    Place placeToFavs = places.get(getAdapterPosition());
                    // add place to favorites
                    addFav(placeToFavs);
                    break;
                case R.id.txtShare:
                    // Using Share intent
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "look at this place : \n "
                            + txtName.getText().toString() + "\n"
                            + txtAddress.getText().toString());
                    sendIntent.setType("text/plain");
                    context.startActivity(Intent.createChooser(sendIntent, "Title"));
                    Toast.makeText(v.getContext(), "share, Clicked :" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.card_view:
                    if (MainActivity.isTablet) {
                        Intent in = new Intent();
                        in.setAction(ACTION_SHOW_PLACE_ON_MAP);
                        Place place = places.get(getAdapterPosition());
                        Toast.makeText(context,"name: "+ place.getName(), Toast.LENGTH_SHORT).show();
                        in.putExtra("lat", place.getLat());
                        in.putExtra("lng", place.getLng());
                        in.putExtra("name", place.getName());
                        in.putExtra("address", place.getAddress());
                        LocalBroadcastManager.getInstance(context).sendBroadcast(in);

                    } else {
                        Intent in = new Intent(context, MapsActivity.class);
                        Place place = places.get(getAdapterPosition());
                        in.putExtra("lat", place.getLat());
                        in.putExtra("lng", place.getLng());
                        in.putExtra("name", place.getName());
                        in.putExtra("address", place.getAddress());
                        context.startActivity(in);
                    }
                    break;

            }
        }

        private void addFav(Place placeToFavs) {
            ContentValues values = new ContentValues();
            values.put(PlacesHelper.COL3_REFERENCE, placeToFavs.getReference());
            values.put(PlacesHelper.COL4_IMAGE, placeToFavs.getImageBase64());
            values.put(PlacesHelper.COL5_PHOTO_REFERENCE, placeToFavs.getPhotoReference());
            values.put(PlacesHelper.COL6_NAME, placeToFavs.getName());
            values.put(PlacesHelper.COL7_ADDRESS, placeToFavs.getAddress());
            values.put(PlacesHelper.COL8_LAT, placeToFavs.getLat());
            values.put(PlacesHelper.COL9_LNG, placeToFavs.getLng());
            values.put(PlacesHelper.COL10_PHONE, placeToFavs.getPhone());
            values.put(PlacesHelper.COL11_PLACE_ID, placeToFavs.getPlaceId());
            context.getContentResolver().insert(PlacesProvider.CONTENT_URI, values);


            //Send The Place to FavFrag to add it to the list.
            Intent in = new Intent();
            in.setAction(ACTION_SEND_NEW_FAV);
            in.putExtra("place", placeToFavs);
            LocalBroadcastManager.getInstance(context).sendBroadcast(in);
        }

        public void setDistanceText(double dis) {
            //FORMAT DISTANCE
            String distance = PreferenceManager.getDefaultSharedPreferences(context).getString(SettingsFrag.DISTANCE_KEY, "kilometers");
            DecimalFormat df = new DecimalFormat("0.00");
            df.setMaximumFractionDigits(4);

            if (distance.equals("kilometers")) {
                txtDis.setText("Distance: " + df.format(dis) + " " + distance);
            } else {
                dis = dis* 0.62137; // this is in miles
                txtDis.setText("Distance: " + df.format(dis) + " " + distance);
            }
        }
    }

}
