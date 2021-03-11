package com.example.mohaned.hababak;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.mohaned.hababak.DB.PlacesReaderContract;
import com.example.mohaned.hababak.Models.place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class map_main extends AppCompatActivity  implements OnMapReadyCallback {
    public static Location location;

    protected GoogleMap mMap;
    Intent intent;
    String provider;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_main);

        intent=getIntent();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    private void put_points(){

    }

    public void getPlaces() {
        PlacesReaderContract.FeedReaderDbHelper dbHelper = new PlacesReaderContract.FeedReaderDbHelper(getApplicationContext());

        SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                PlacesReaderContract.FeedEntry.GId,
                PlacesReaderContract.FeedEntry.name,
                PlacesReaderContract.FeedEntry.country_code,
                PlacesReaderContract.FeedEntry.phone,
                PlacesReaderContract.FeedEntry.latitude,
                PlacesReaderContract.FeedEntry.longitude
        };


// How you want the results sorted in the resulting Cursor
        String sortOrder =
                PlacesReaderContract.FeedEntry.name + " DESC";

        Cursor cursor = db.query(
                PlacesReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        ArrayList<place> places = new ArrayList<>();
        while (cursor.moveToNext()) {
            place placeM = new place();
            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(PlacesReaderContract.FeedEntry.GId));
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(PlacesReaderContract.FeedEntry.name));
            String country_code = cursor.getString(
                    cursor.getColumnIndexOrThrow(PlacesReaderContract.FeedEntry.country_code));
            String phone = cursor.getString(
                    cursor.getColumnIndexOrThrow(PlacesReaderContract.FeedEntry.phone));
            String latitude = cursor.getString(
                    cursor.getColumnIndexOrThrow(PlacesReaderContract.FeedEntry.latitude));
            String longitude = cursor.getString(
                    cursor.getColumnIndexOrThrow(PlacesReaderContract.FeedEntry.longitude));
            placeM.setId(String.valueOf(id));
            placeM.setName(name);
            placeM.setCountry_code(country_code);
            placeM.setPhone(phone);
            placeM.setLatitude(latitude);
            placeM.setLongitude(longitude);
            places.add(placeM);
        }
        cursor.close();
        fillMap(places);
    }

    private void fillMap(ArrayList<place> places) {
        for (int x = 0; x < places.size(); x++) {
            putMarker(places.get(x).getName(), Double.valueOf(places.get(x).getLatitude()), Double.valueOf(places.get(x).getLongitude()));
        }
    }

    private void putMarker(String name, double latit, double longit) {
        LatLng sydney = new LatLng(latit, longit);
        int height = 130;
        int width = 130;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.mappin);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        mMap.addMarker(new MarkerOptions().position(sydney)
                .title(name).icon(smallMarkerIcon));
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney;
        getPlaces();
            // Add a marker in Sydney and move the camera
            sydney = new LatLng(15.6181252, 32.5038003);
            //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10));
    }
}
