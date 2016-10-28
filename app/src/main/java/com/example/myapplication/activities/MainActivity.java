package com.example.myapplication.activities;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapters.ViewPagerAdapter;
import com.example.myapplication.db.PlacesProvider;
import com.example.myapplication.fragments.FavFrag;
import com.example.myapplication.fragments.ListFrag;
import com.example.myapplication.fragments.MapViewFragment;
import com.example.myapplication.module.Search;
import com.example.myapplication.module.TagSaver;
import com.example.myapplication.recivers.PowerConnectedReciver;
import com.example.myapplication.recivers.PowerDisconnectedReciver;

import java.io.ByteArrayOutputStream;

import static com.example.myapplication.fragments.SettingsFrag.RADIUS_KEY;


//MY API CODE AIzaSyAffDVLihmDYSWhvNrZVxSSrQ6nXDSdd8E
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>
        , SearchView.OnQueryTextListener {


    /**
     * REQUEST_PERMISSION_LOCATION
     * this constant is the request code for the premission request for location
     * Used in:
     * ListFrag.findPlacesByLoc to set the request code
     * ListFrag.onLocationChanged ot set the request code
     * onRequestPermissionsResult to handle the response of the user.*/
    public static final int REQUEST_PERMISSION_LOCATION = 1;

    /**
     * REQUEST_CODE_ACTIVITY_SETTINGS
     * this constant is the request code for starting the settings activity
     * Used in:
     * MainActivity.onOptionsItemSelected when selecting Settings option
     * MainActivity.onActivityResult to check if returned from Settings activity for
     *                              restarting the app to make settings changed.*/
    private static final int REQUEST_CODE_ACTIVITY_SETTINGS = 22;

    //PREFRENCES
    /**
     * This field used to save the reference to the default shared preferences
     * Used in:
     * MainActivity.onCreate to set the reference to the default shared preferences
     * MainActivity.onOptionsItemSelected when user choose action_search_loc to get the radius
     *                                    choosed by user to start a new search by location
     * MainActivity.onQueryTextSubmit when user choose action_search_loc to get the radius
     *                                choosed by user to start a new search by query
     * MainActivity.onRequestPermissionsResult to start a new search if user granted permission
     * */
    private SharedPreferences sp;

    /**
     * this field used to save reference to the tabLayout View
     * Used in:
     * MainActivity.onCreate to set the reference to the View (with findViewById(R.id.tabs)
     *                       and to use setupWithViewPager
     * */
    public TabLayout tabLayout;

    //FRAMENTS
    /**
     * This field used to save reference to the ListFrag.
     * Used in:
     * MainActivity.onCreate to set the reference to the View if its the first time the activity created
     *                       we set the reference to new ListFrag(), if not we set it with findFragmentById()
     * MainActivity.setupViewPager to add the fragment to the ViewPagerAdapter
     * MainActivity.onOptionsItemSelected when action_search_loc is selected to start a new search
     *                                     (listFrag.findPlaceByLoc())
     * MainActivity.onQueryTextSubmit to start a new search with (listFrag.findPlacesByLoc()).
     * MainActivity.onRequestPermissionsResult to start a new search if the user granted permission
     * */
    private ListFrag listFrag;

    /**
     * This field used to save reference to the FavFrag.
     * Used in:
     * MainActivity.onCreate to set the reference to the View if its the first time the activity created
     *                       we set the reference to new ListFrag(), if not we set it with findFragmentById()
     * MainActivity.setupViewPager to add the fragment to the ViewPagerAdapter
     * MainActivity.onOptionsItemSelected when action_delete_favs is selected to delete favs with
     *                                    favFrag.deleteList().
     * MainActivity.onOptionsItemSelected hen action_show_favs_on_map is selected to get favourites
     *                                    list from fragment
     */
    private FavFrag favFrag;

    /**
     * This field used to save reference to the mapViewFragment used ONLY ON TABLET
     * Used in:
     * MainActivity.onCreate to set the reference to the fragment (with findFragmentById)
     *                       if it is not null after findFragmentById we know we r on tablet.
     *                       Used also if the phone rotated, we get the reference back by the tag.
     * MainActivity.onOptionsItemSelected when action_show_favs_on_map is selected on menu by user
     *                                    to show all favs on map. with mapViewFragment.showArrayOnMap()
     *********************************************************************************************
     * Other Functions related to this fragment is passed with BroadCast from fragment to fragment
     * or from adapter to fragment.
     *********************************************************************************************
     * */
    private MapViewFragment mapViewFragment;

    /**
     * this reference use to save reference to the searchView
     * Used in:
     * MainActivity.onCreateOptionsMenu to set the searchView
     * MainActivity.onOptionsItemSelected when action_search_loc is selected, to clear query
     * MainActivity.onRequestPermissionsResult to get query to start a search if premission granted
     * */
    private SearchView searchView;

    public static boolean isTablet;


    //Recivers
    /**
     * This reciver gets called when the user connects the power
     * */
    private PowerConnectedReciver connectedReciver;
    /**
     * This reciver gets called when the user disconnects the power
     * */
    private PowerDisconnectedReciver disconnectedReciver;




    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sp = PreferenceManager.getDefaultSharedPreferences(this);


        // if container is null after this findViewById its a phone, else its a tablet.
        // container is a layout with the map fragment inside it. exists only on tablet layout.
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mapViewFragment = (MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.mapViewFrag);
        if (mapViewFragment != null) {
            //On Tablet
            Log.i("Device", "Tablet");
            isTablet = true;
        } else {
            Log.i("Device", "Phone");
            isTablet = false;
        }
        if (savedInstanceState != null) {
            String tag1 = TagSaver.getTag1();
            String tag2 = TagSaver.getTag2();
            String tag3 = TagSaver.getTag3();
            if (tag1 != null)
                favFrag = (FavFrag) getSupportFragmentManager().findFragmentByTag(tag1);
            if (tag2 != null)
                listFrag = (ListFrag) getSupportFragmentManager().findFragmentByTag(tag2);
            if (tag3 != null)
                mapViewFragment = (MapViewFragment) getSupportFragmentManager().findFragmentByTag(tag3);
        } else {

            // savedInstanceState = null , means its first time activity starts
            favFrag = new FavFrag();
            listFrag = new ListFrag();


        }
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //TABS CODE

        getLoaderManager().initLoader(11, null, this);





        connectedReciver = new PowerConnectedReciver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        registerReceiver(connectedReciver, filter);

        disconnectedReciver = new PowerDisconnectedReciver();
        IntentFilter filter1 = new IntentFilter(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(disconnectedReciver, filter1);


    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());


        viewPagerAdapter.addFragment(favFrag, getString(R.string.Favs));
        viewPagerAdapter.addFragment(listFrag, getString(R.string.List));


        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //inflate main menu
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Create Search Service
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //listeners for Search
        searchView.setOnQueryTextListener(this);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent in = new Intent(this, SettingsActivity.class);
                startActivityForResult(in, REQUEST_CODE_ACTIVITY_SETTINGS);
                break;
            case R.id.action_search_loc:
                Log.i("Log", "Search by current location pressed");
                //if pressed the search my location - clear the query and the search will be without querys.
                searchView.setQuery("", true);
                listFrag.findPlacesByLoc(new Search(null, "", sp.getString(RADIUS_KEY, "500")));

                break;
            case R.id.action_delete_favs:
                favFrag.deleteList();
                getContentResolver().delete(PlacesProvider.CONTENT_URI, null, null);
                break;
            case R.id.action_show_favs_on_map:
                // Show Favourites on map
                if (favFrag.getFavourites().isEmpty()) {
                    Toast.makeText(this, "no favourites", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (isTablet) {
                    mapViewFragment.showArrayOnMap(favFrag.getFavourites());
                } else {
                    Intent intent = new Intent(this, MapsActivity.class);
                    intent.putParcelableArrayListExtra("places", favFrag.getFavourites());
                    startActivity(intent);

                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onQueryTextSubmit(final String query) {
        listFrag.findPlacesByLoc(new Search(null, query, sp.getString(RADIUS_KEY, "500")));
        return false;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, PlacesProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null){
            Log.i("onLoadFinished","Cursor data is null");
            return;
        }
        favFrag.setAdapterWithCursor(data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
                    listFrag.findPlacesByLoc(new Search(null, searchView.getQuery().toString(), sp.getString(RADIUS_KEY, "500")));
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ACTIVITY_SETTINGS:
                // Restarting activity so changes will be updated.
                finish();
                Intent i = getIntent();
                startActivity(i);
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectedReciver);
        unregisterReceiver(disconnectedReciver);
    }


    //UNUSED METHODS

    public static boolean isTablet() {
        return isTablet;
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
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
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


}

