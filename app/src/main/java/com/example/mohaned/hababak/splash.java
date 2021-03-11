package com.example.mohaned.hababak;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.mohaned.hababak.DB.AirlinesReaderContract;
import com.example.mohaned.hababak.DB.FlightsReaderContract;
import com.example.mohaned.hababak.DB.PlacesReaderContract;
import com.example.mohaned.hababak.Network.Iokihttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class splash extends Activity {
    private static int splash_time_out = 0;
    Iokihttp iokihttp;
    SharedPreferences shared;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iokihttp = new Iokihttp();
        setContentView(R.layout.activity_splash);
        shared = this.getSharedPreferences("com.example.mohaned.hababak", Context.MODE_PRIVATE);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                if (checkCounter()) getVersion();
                else check();
            }
        }, splash_time_out);

    }
    public void getVersion() {
        JSONObject json = new JSONObject();
        JSONObject subJSON = new JSONObject();
        try {

            json.put("method", "getSystem");
            json.put("data", subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iokihttp.isNetworkConnected(getApplicationContext())) {
            iokihttp.post(getString(R.string.lara_url) + "getSystem", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("FAIL"+e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {


                            JSONObject resJSON = new JSONObject(responseStr);

                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                final JSONArray subresJSON = resJSON.getJSONArray("data");
                                checkVersion(Double.valueOf(shared.getString("version", "0")), subresJSON.getJSONObject(0).getDouble("version"));
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Response=" + responseStr);

                    } else {
                        System.out.println("Response=" + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                    }
                }

            });
        }
    }
    public void checkVersion(double localVersion, double serverVersion) {
        System.out.println("SYSTEM V=" + serverVersion + "Local V=" + localVersion);
        if (serverVersion > localVersion) {
            getAirlinesAndFlights(serverVersion);
        } else {
            check();
        }
    }

    private boolean checkCounter() {
        int counter = shared.getInt("counter", 0);
        Double version = Double.parseDouble(shared.getString("version", "0"));

        if (version < 1) return true;
        if (counter == 0) {
            shared.edit().putInt("counter", ++counter).apply();
            return true;
        } else if (counter == 3) {
            shared.edit().putInt("counter", 0).apply();
            return false;
        } else {
            shared.edit().putInt("counter", ++counter).apply();
            return false;
        }
    }
    public void getAirlinesAndFlights(final double serverVersion) {
        JSONObject json = new JSONObject();
        JSONObject subJSON = new JSONObject();
        try {
            json.put("method", "getAirlines");
            json.put("data", subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iokihttp.isNetworkConnected(getApplicationContext())) {
            iokihttp.post(getString(R.string.lara_url) + "getAirlines", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("FAIL");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {
                            JSONObject resJSON = new JSONObject(responseStr);
                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                final JSONObject subresJSON = resJSON.getJSONObject("data");
                                fillAirlines(subresJSON.getJSONArray("airlines"));
                                fillPlaces(subresJSON.getJSONArray("places"));
                                fillFlights(subresJSON.getJSONArray("flights"));
                                shared.edit().putString("version", String.valueOf(serverVersion)).apply();
                                check();
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Response=" + responseStr);

                    } else {
                        System.out.println("Response=" + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                    }
                }
            });
        }
    }
    public void fillAirlines(JSONArray airlines) throws JSONException {

        AirlinesReaderContract.FeedReaderDbHelper dbHelper = new AirlinesReaderContract.FeedReaderDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int deletedRows = db.delete(AirlinesReaderContract.FeedEntry.TABLE_NAME, null, null);
        } catch (SQLException e) {
            dbHelper.onCreate(db);
        }
// Create a new map of values, where column names are the keys
        for (int x = 0; x < airlines.length(); x++) {
            JSONObject airline = airlines.getJSONObject(x);
            ContentValues values = new ContentValues();
            values.put(AirlinesReaderContract.FeedEntry.GId, airline.getString("id"));
            values.put(AirlinesReaderContract.FeedEntry.name, airline.getString("name"));

// Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(AirlinesReaderContract.FeedEntry.TABLE_NAME, null, values);
        }
    }
    public void fillPlaces(JSONArray places) throws JSONException {
        System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFF" + places.toString());
        PlacesReaderContract.FeedReaderDbHelper dbHelper = new PlacesReaderContract.FeedReaderDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int deletedRows = db.delete(PlacesReaderContract.FeedEntry.TABLE_NAME, null, null);
        } catch (SQLException e) {
            dbHelper.onCreate(db);
        }
// Create a new map of values, where column names are the keys
        for (int x = 0; x < places.length(); x++) {
            JSONObject place = places.getJSONObject(x);
            ContentValues values = new ContentValues();
            values.put(PlacesReaderContract.FeedEntry.GId, place.getString("id"));
            values.put(PlacesReaderContract.FeedEntry.name, place.getString("name"));
            values.put(PlacesReaderContract.FeedEntry.phone, place.getString("phone"));
            values.put(PlacesReaderContract.FeedEntry.country_code, place.getString("country_code"));
            values.put(PlacesReaderContract.FeedEntry.latitude, place.getString("latitude"));
            values.put(PlacesReaderContract.FeedEntry.longitude, place.getString("longitude"));


// Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(PlacesReaderContract.FeedEntry.TABLE_NAME, null, values);
        }
    }
    public void fillFlights(JSONArray flights) throws JSONException {

        FlightsReaderContract.FeedReaderDbHelper dbHelper = new FlightsReaderContract.FeedReaderDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int deletedRows = db.delete(FlightsReaderContract.FeedEntry.TABLE_NAME, null, null);
        } catch (SQLException e) {
            dbHelper.onCreate(db);
        }
// Create a new map of values, where column names are the keys
        for (int x = 0; x < flights.length(); x++) {
            JSONObject airline = flights.getJSONObject(x);
            ContentValues values = new ContentValues();
            values.put(FlightsReaderContract.FeedEntry.GId, airline.getString("id"));
            values.put(FlightsReaderContract.FeedEntry.name, airline.getString("flight_number"));
            values.put(FlightsReaderContract.FeedEntry.airline_id, airline.getString("airline_id"));
// Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(FlightsReaderContract.FeedEntry.TABLE_NAME, null, values);
        }
    }
    public void check(){
        if (shared.getBoolean("login", false)) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(i);
            finish();
        }
        else{
            Intent i=new Intent(getApplicationContext(),Login.class);
            startActivity(i);
            finish();
        }
    }
}
