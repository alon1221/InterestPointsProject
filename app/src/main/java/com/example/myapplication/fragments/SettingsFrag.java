package com.example.myapplication.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.example.myapplication.R;

public class SettingsFrag extends PreferenceFragment implements Preference.OnPreferenceChangeListener {


    private ListPreference listDistancePref, listRadiusPref;

    public static final String DISTANCE_KEY = "distances_key";
    public static final String RADIUS_KEY = "radius_key";
    public static final String DEF_DISTANCE_VALUE = "Kilometers";
    public static final String DEF_RADIUS_VALUE = "500";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // add the preferences from the xml file!
        addPreferencesFromResource(R.xml.prefs_screen);
        PreferenceManager manager = getPreferenceManager();

        listDistancePref = (ListPreference) manager.findPreference(DISTANCE_KEY);

        listRadiusPref = (ListPreference) manager.findPreference(RADIUS_KEY);


        if (listDistancePref.getValue().equals("")) {
            listDistancePref.setValue(DEF_DISTANCE_VALUE);
        }

        if (listRadiusPref.getValue().equals("")) {
            listDistancePref.setValue(DEF_RADIUS_VALUE);
        }

        listDistancePref.setOnPreferenceChangeListener(this);
        listRadiusPref.setOnPreferenceChangeListener(this);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        listDistancePref.setSummary("Distance: " + sp.getString(DISTANCE_KEY, DEF_DISTANCE_VALUE));
        listRadiusPref.setSummary("Radius: " + sp.getString(RADIUS_KEY, "500") + " meters");

    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case DISTANCE_KEY:
                listDistancePref.setSummary("Distance: " + newValue);
                break;
            case RADIUS_KEY:
                listRadiusPref.setSummary("Radius: " + newValue);
                break;
        }
        return true;
    }

}
