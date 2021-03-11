package com.example.mohaned.hababak;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohaned.hababak.DB.AirlinesReaderContract;
import com.example.mohaned.hababak.Network.Iokihttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,profile.OnFragmentInteractionListener,history.OnFragmentInteractionListener, home.OnFragmentInteractionListener, aboutus.OnFragmentInteractionListener{
    private ActionBar actionBar;
    DrawerLayout drawer;
    boolean home=true;
    SharedPreferences shared;
    boolean doubleBackToExitPressedOnce=false;
    private Iokihttp okhttp;
    Fragment fragment = null;
    Class fragmentClass = null;
    Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        okhttp = new Iokihttp();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // actionBar = getActionBar();
        //actionBar.setHomeButtonEnabled(false);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.side_nav_bar);

        shared = this.getSharedPreferences("com.example.mohaned.hababak", Context.MODE_PRIVATE);
        setSupportActionBar(toolbar);
        //change the name and the phone
        changeNavLabels();
        Fragment fragment = null;
        Class fragmentClass = null;
        Bundle b=new Bundle();
        home h = new home();


        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (savedInstanceState == null) {

        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent,h).commit();
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();


        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setHomeAsUpIndicator(R.drawable.drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getPK();
    }
    @Override
    public void onBackPressed() {
        System.out.println(home+"HOOOOOOOOOOOOOOOOOOMMMMMMEEEEEEe");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
                if(home) {
                    if (doubleBackToExitPressedOnce) {
                        super.onBackPressed();
                        finish();
                        return;
                    }
                }
                else{
                    MenuItem item=((NavigationView)findViewById(R.id.nav_view)).getMenu().getItem(0);
                    onNavigationItemSelected(item);
                    return;
                }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.press_back), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 3000);
        }
    }


    public void printDB() {
        AirlinesReaderContract.FeedReaderDbHelper dbHelper = new AirlinesReaderContract.FeedReaderDbHelper(getApplicationContext());

        SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                AirlinesReaderContract.FeedEntry.GId,
                AirlinesReaderContract.FeedEntry.name
        };


// How you want the results sorted in the resulting Cursor
        String sortOrder =
                AirlinesReaderContract.FeedEntry.name + " DESC";

        Cursor cursor = db.query(
                AirlinesReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        List itemIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(AirlinesReaderContract.FeedEntry._ID));
            itemIds.add(itemId);
        }
        cursor.close();
        for (int x = 0; x < itemIds.size(); x++) {
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%" + itemIds.get(x));
        }
    }

    private void getPK() {
        if (okhttp.isNetworkConnected(getApplicationContext())) {
            okhttp.post(getResources().getString(R.string.payment_url) + "key", "", new Callback() {
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

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {


                            JSONObject resJSON = new JSONObject(responseStr);

                            if (Integer.parseInt(resJSON.get("responseCode").toString()) == 0) {
                                System.out.println("@@@@@@@@@@@@@@" + resJSON.getString("pubKeyValue"));
                                shared.edit().putString("PK", resJSON.getString("pubKeyValue")).apply();


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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void changeNavLabels(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ((TextView)headerView.findViewById(R.id.side_name)).setText(shared.getString("username","No name"));
        ((TextView)headerView.findViewById(R.id.side_phone)).setText(shared.getString("phone_key","")+" "+shared.getString("phone","No phone"));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            if(drawer.isDrawerOpen(Gravity.START)) {
                drawer.closeDrawer(Gravity.START);
            }else{
                drawer.openDrawer(Gravity.START);
            }

        }else if (item.getItemId()== R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        fragment = null;
        fragmentClass = null;

        if (id == R.id.nav_profile) {
            fragmentClass = profile.class;
            changeToolbarBackground();
            home=false;

        } else if (id == R.id.nav_home) {
            toolbar.setBackgroundColor(Color.TRANSPARENT);
            fragmentClass = home.class;
            home=true;

        }else if (id == R.id.nav_aboutus) {
            changeToolbarBackground();

            fragmentClass = aboutus.class;
            home=false;

        }
        else if (id == R.id.nav_history) {
            changeToolbarBackground();

            fragmentClass = history.class;
            home = false;

        }else if (id == R.id.nav_sign_out) {
            shared.edit().putBoolean("login", false).apply();
            Intent i=new Intent(getApplicationContext(),Login.class);
            startActivity(i);
            finish();
        }

        if(fragmentClass!=null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeToolbarBackground() {
        shared.edit().putInt("items", shared.getInt("items", 1) * 5).apply();
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

}
