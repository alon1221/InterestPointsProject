package com.example.myapplication.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by avira on 22/09/2016.
 */

public class PlacesProvider extends ContentProvider {

    private PlacesHelper helper;
    public static final Uri CONTENT_URI = Uri.parse("content://com.example.myapplication/" + PlacesHelper.TABLE1_NAME);

    @Override
    public boolean onCreate() {
        helper = new PlacesHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(uri.getLastPathSegment(), projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long id = db.insert(uri.getLastPathSegment(), null, values);
        // notify listener that the data changed in the uri
        getContext().getContentResolver().notifyChange(uri, null);
        Toast.makeText(getContext(), "insert succesfully", Toast.LENGTH_SHORT).show();
        return uri.withAppendedPath(uri, id + "");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.delete(uri.getLastPathSegment(), selection, selectionArgs);
        // notify listener that the data changed in the uri
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
