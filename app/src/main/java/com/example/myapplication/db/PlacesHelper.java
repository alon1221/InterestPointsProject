package com.example.myapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by avira on 22/09/2016.
 */

public class PlacesHelper extends SQLiteOpenHelper {

    public static final String TABLE1_NAME = "places";
    public static final String COL2_ID = "_id";
    public static final String COL3_REFERENCE = "reference";
    public static final String COL4_IMAGE = "image";
    public static final String COL5_PHOTO_REFERENCE = "photo_reference";
    public static final String COL6_NAME = "name";
    public static final String COL7_ADDRESS = "address";
    public static final String COL8_LAT = "lat";
    public static final String COL9_LNG = "lng";
    public static final String COL10_PHONE = "phone";
    public static final String COL11_PLACE_ID = "place_id";


    public PlacesHelper(Context context) {
        super(context, "places.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE %1$s ( %2$s INTEGER PRIMARY KEY AUTOINCREMENT, %3$s TEXT, %4$s TEXT, %5$s TEXT, %6$s TEXT, %7$s TEXT, %8$s REAL, %9$s TEXT, %10$s TEXT, %11$s TEXT)";
        db.execSQL(String.format(sql, TABLE1_NAME, COL2_ID, COL3_REFERENCE, COL4_IMAGE, COL5_PHOTO_REFERENCE, COL6_NAME, COL7_ADDRESS, COL8_LAT, COL9_LNG, COL10_PHONE, COL11_PLACE_ID));

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
