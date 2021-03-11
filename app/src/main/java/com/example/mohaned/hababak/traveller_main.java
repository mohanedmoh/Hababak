package com.example.mohaned.hababak;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohaned.hababak.Adapter.companion_adapter;
import com.example.mohaned.hababak.DB.AirlinesReaderContract;
import com.example.mohaned.hababak.DB.FlightsReaderContract;
import com.example.mohaned.hababak.DB.PlacesReaderContract;
import com.example.mohaned.hababak.Models.companion_info;
import com.example.mohaned.hababak.Models.place;
import com.example.mohaned.hababak.Network.Iokihttp;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.hbb20.CountryCodePicker;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tooltip.Tooltip;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class traveller_main extends AppCompatActivity implements OnMapReadyCallback,TimePickerDialog.OnTimeSetListener,DatePickerDialog.OnDateSetListener {
    protected GoogleMap mMap;

    private String date;
    private String time;
    private LIFOqueue lifo;
    public DatePickerDialog dpd,dpd2;
    public Calendar now;
    ArrayList<companion_info> dataModels;
    ListView listView;
    private static companion_adapter adapter;
    private View traveller_include,companion_include,payment_include,package_include,success_process_include;
    private boolean main=true;
    private boolean last=false;
    String latitude, longitude = "";
    private Iokihttp okhttp;
    final double[] price = new double[2];
    Intent intent;
    private int sudanese_counter, other_counter, adults, kids;
    private int packageType,paymentMethod,req_type=1;
    private double pickedPrice;
    SharedPreferences shared;
    private boolean skip_companion, skip_pickup = false;
    private int req_id;
    List<Object> list;
    View before = null;
    View after = null;
    int companion_no=0;
    CountryCodePicker ccp;
    int PERMISSION_ID=111;
    FusedLocationProviderClient mFusedLocationClient;
    private String pickup, name, passport, nationality, age, airline, flight_no, destination, flight_date, flight_time, country_code, phone, companions_no, luggage_no, medical_note;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveller_main);
        init();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        date= year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
        if(view.getTag()=="Datepickerdialog"){
            TimePickerDialog tpd = TimePickerDialog.newInstance(traveller_main.this, Calendar.HOUR_OF_DAY, Calendar.MINUTE, false);
        tpd.show(getFragmentManager(), "Timepickerdialog");}
        else if(view.getTag()=="Datepickerdialog2"){
            ((TextView)( traveller_include.findViewById(R.id.traveller_age))).setText(date);
        }
    }
    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        time = hourOfDay+":"+minute+":"+second;
        ((TextView)traveller_include.findViewById(R.id.datetimeTextView)).setText(date+" "+time);
    }
    public void setList(int count) {
        System.out.println("11");
        View view=findViewById(R.id.companion_include);
        listView = view.findViewById(R.id.companion_list);
        System.out.println("22");
        JSONObject jsonObject;

        dataModels= new ArrayList<>();
        if(count==0){}
        else {
            // System.out.println("33"+y);
            for (int x = 0; x < count; x++) {
                companion_info dataModel = new companion_info();
                dataModel.setRow_id(x);
                dataModels.add(dataModel);
            }

            adapter = new companion_adapter(dataModels, getApplicationContext(),getFragmentManager());

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    companion_info dataModel = dataModels.get(position);
                    //   intent.putExtra();
                   // startActivity(intent);
                }
            });
            listView.setAdapter(adapter);
        }
    }
    private void openNextForm() throws JSONException, ParseException {
        System.out.println(lifo.scndsize()+"=SIZE="+lifo.size());


        if(lifo.size()!=0) {
            before = findViewById((Integer) lifo.scndpeek());
            after = null;

            if (before.getId() == R.id.traveler_main_form) {
                if(validateMainForm()){

                    String co_string = ((EditText) before.findViewById(R.id.companion_no)).getText().toString();
                    companion_no=Integer.parseInt(co_string.equals("") ?"0":co_string);

                    if (companion_no==0){
                        skip_companion=true;
                        after = findViewById((Integer) lifo.pop());
                        countPassengers();
                        setPrice(adults, kids, req_type);

                    } else{
                        after = findViewById((Integer) lifo.pop());
                        setList(companion_no);
                        skip_companion=false;
                        animateLayout(before, after);

                    }
                } else{
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.fill_error),Toast.LENGTH_LONG).show();
                    return;
                }
            } else if (before.getId() == R.id.companion_include) {
                if(validateCompanions(companion_no)){
                    System.out.println("INSIDE PACKAGE");
                    //after =  findViewById((Integer) lifo.pop());
                    countPassengers();
                    setPrice(adults, kids, req_type);
                } else{
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.fill_error),Toast.LENGTH_LONG).show();
                    return;
                }
            } else if (before.getId() == R.id.service_packages_include) {
                changeTotal();
                sendReq();
            } else if (before.getId() == R.id.payment_methods_include) {
                setPayment();
                if (paymentMethod == 2) {
                    Intent online_payment = new Intent(getApplicationContext(), online_payment.class);
                    shared.edit().putString("price", String.valueOf(pickedPrice)).apply();
                    startActivityForResult(online_payment, 202);
                } else {
                    after = findViewById((Integer) lifo.pop());
                }
            } else{
                after =  findViewById((Integer) lifo.pop());
                animateLayout(before, after);
            }
        } else {

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 202 && resultCode == 1) {
            after = findViewById((Integer) lifo.pop());
            animateLayout(before, after);
        } else{
            hideLoading();
        }
    }
    public void animateLayout(final View before, final View after){
        Animator.AnimatorListener animatorListener=new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                System.out.println("SHOW "+after.getId());
                before.setVisibility(View.GONE);
                after.setVisibility(View.VISIBLE);
                //  finalAfter.animate().alpha(1f).setDuration(700);

                main = false;
                if (lifo.size() == 0) {
                    findViewById(R.id.title_rel).setVisibility(View.GONE);
                    findViewById(R.id.next_linear).setVisibility(View.GONE);
                    findViewById(R.id.main_traveller).setBackground(getResources().getDrawable(R.drawable.lastbg));
                    last = true;
                    if (req_type == 1) {
                        ((TextView) success_process_include.findViewById(R.id.msg)).setText(getString(R.string.please_deliever_departure));
                    } else if (req_type == 2) {
                        ((TextView) success_process_include.findViewById(R.id.msg)).setText(getString(R.string.please_deliever_arrival));
                    } else if (req_type == 3) {
                        ((TextView) success_process_include.findViewById(R.id.msg)).setText(getString(R.string.please_deliever_medical));
                    }
                }



            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        before.animate().alpha(0f).setDuration(0).setListener(animatorListener);
        //hideLoading();
    }
    private void setPayment() {
        JSONObject json=new JSONObject();
        JSONObject subJSON=new JSONObject();
        try {
            subJSON.put("id",req_id);
            subJSON.put("req_type",1);
            subJSON.put("payment_method",paymentMethod);
            json.put("method","setPayment");
            json.put("data",subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(okhttp.isNetworkConnected(getApplicationContext())) {
            showLoading();
            okhttp.post(getString(R.string.lara_url) + "setPayment", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("FAIL");
                    hideLoading();

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {
                            JSONObject resJSON = new JSONObject(responseStr);
                            //   JSONObject subresJSON = new JSONObject(resJSON.getString("data"));
                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                if (paymentMethod == 1) {
                                    animateLayout(before, after);
                                }
                                hideLoading();

                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                onBackPressed();
                                hideLoading();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Response=" + responseStr);

                    } else {
                        System.out.println("Response=" + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                        hideLoading();
                    }
                }
            });
        }
    }
    private void countPassengers() throws ParseException {
        sudanese_counter = other_counter = adults = kids = 0;

        String co_string=((EditText)traveller_include.findViewById(R.id.companion_no)).getText().toString();
        int companion_no=Integer.parseInt(co_string.equals("") ?"0":co_string);
        int current_year = now.get(Calendar.YEAR);
        String travellerBirthDate = ((TextView) traveller_include.findViewById(R.id.traveller_age)).getText().toString().split("-")[0];
        int traveller_birth_year = Integer.parseInt(travellerBirthDate);
        System.out.println(current_year + "TRAVELLERAGE=" + traveller_birth_year);

        if (current_year - traveller_birth_year > 7) {
            adults++;
        } else {
            kids++;
        }
        for (int x = 0; x < companion_no; x++) {
            String companionBirthDate = ((TextView) companion_include.findViewWithTag("companion" + x).findViewById(R.id.companion_age)).getText().toString().split("-")[0];
            int companion_age = Integer.parseInt(companionBirthDate);
            System.out.println("COMPANION" + x + "AGE=" + companion_age);
            if (current_year - companion_age > 7) {
                adults++;
            } else {
                kids++;
            }
        }
       /* if (((Spinner) findViewById(R.id.nationality)).getSelectedItemId() == 0) {
            sudanese_counter++;
        } else {
            other_counter++;
        }
        for (int x=0;x < companion_no;x++){
            if(((Spinner)(companion_include.findViewWithTag("companion"+x)).findViewById(R.id.companion_nationality)).getSelectedItemId()==0){
                sudanese_counter++;
            }
            else{
                other_counter++;
            }
        }*/
    }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());
        }
    };
    private void showFields(int i) {
        switch (i) {
            case 1: {
            }
            break;
            case 2: {
                ((CheckBox) traveller_include.findViewById(R.id.pickupC)).setText(getResources().getString(R.string.drop_luggage));
                ((TextView)findViewById(R.id.title)).setText(getResources().getString(R.string.arrival));
                traveller_include.findViewById(R.id.flight_num).setVisibility(View.VISIBLE);
                traveller_include.findViewById(R.id.traveller_flight_no).setVisibility(View.GONE);
                traveller_include.findViewById(R.id.arrival_luggage).setVisibility(View.VISIBLE);
                ((MaterialEditText)traveller_include.findViewById(R.id.traveller_destination)).setText("Khartoum");
            }
            break;
            case 3: {
                ((TextView)findViewById(R.id.title)).setText(getResources().getString(R.string.sick_travel));
                traveller_include.findViewById(R.id.patient_note).setVisibility(View.VISIBLE);
            }
            break;
        }
    }
    private void setPrice(int sudanese_no, int other_no, int service_type) {
        JSONObject json=new JSONObject();
        JSONObject subJSON=new JSONObject();
        System.out.println("adults=" + adults + "kids=" + kids);
        try {
            subJSON.put("type",service_type);
            subJSON.put("adults", sudanese_no);
            subJSON.put("kids", other_no);
            json.put("method","getPrice");
            json.put("data",subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(okhttp.isNetworkConnected(getApplicationContext())) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.linear_main).setVisibility(View.GONE);
                    showLoading();
                }
            });
            okhttp.post(getString(R.string.lara_url) + "getPrice", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("FAIL");
                    hideLoading();
                    onBackPressed();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {

                            if (response.isSuccessful()) {
                                String responseStr = null;
                                try {
                                    responseStr = response.body().string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    JSONObject resJSON = new JSONObject(responseStr);
                                    JSONObject subresJSON = new JSONObject(resJSON.getString("data"));
                                    if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                        price[0] = subresJSON.getDouble("classic");
                                        price[1] = subresJSON.getDouble("vip");
                                        ((TextView) package_include.findViewById(R.id.classic_price)).setText(getString(R.string.priceString) + " " + price[0] + " " + getString(R.string.SDG));
                                        ((TextView) package_include.findViewById(R.id.vip_price)).setText(getString(R.string.priceString) + " " + price[1] + " " + getString(R.string.SDG));
                                        after = findViewById((Integer) lifo.pop());
                                        animateLayout(before, after);
                                        hideLoading();
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        onBackPressed();
                                        hideLoading();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("Response=" + responseStr);

                            } else {
                                System.out.println("Response=" + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                                hideLoading();
                                onBackPressed();
                            }
                        }
            });
        }
    }
    public void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        shared = this.getSharedPreferences("com.example.mohaned.hababak", Context.MODE_PRIVATE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        okhttp = new Iokihttp();
        traveller_include = findViewById(R.id.traveler_main_form);
        companion_include = findViewById(R.id.companion_include);
        payment_include = findViewById(R.id.payment_methods_include);
        package_include = findViewById(R.id.service_packages_include);
        success_process_include = findViewById(R.id.success_process_include);
        intent = getIntent();
        req_type = intent.getIntExtra("type", 1);
        changeMsg(req_type);
        showFields(req_type);
ccp=traveller_include.findViewById(R.id.ccp);
        Spinner nationality = traveller_include.findViewById(R.id.nationality);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.nationality_list, R.layout.simple_spinner_item_custom);
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nationality.setAdapter(adapter);
        now = Calendar.getInstance();
        dpd = DatePickerDialog.newInstance(
                traveller_main.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd2 = DatePickerDialog.newInstance(
                traveller_main.this,
                now.get(Calendar.DATE),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        traveller_include.findViewById(R.id.traveller_age).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dpd2.show(getFragmentManager(), "Datepickerdialog2");
            }
        });
        traveller_include.findViewById(R.id.traveller_age_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dpd2.show(getFragmentManager(), "Datepickerdialog2");
            }
        });
        traveller_include.findViewById(R.id.datetimeTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        traveller_include.findViewById(R.id.flight_date_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        findViewById(R.id.next_form).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    openNextForm();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.back_form).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        package_include.findViewById(R.id.classic_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePackageLinearBg(view);
            }
        });
        package_include.findViewById(R.id.vip_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePackageLinearBg(view);
            }
        });

        payment_include.findViewById(R.id.cash_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePriceLinearBg(view);
            }
        });
        payment_include.findViewById(R.id.bank_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePriceLinearBg(view);
            }
        });

        success_process_include.findViewById(R.id.done_req).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //Intent intent=new Intent()
            }
        });


        //setList(4);
        list = new ArrayList<Object>();
        list.add(R.id.traveler_main_form);
        list.add(R.id.companion_include);
        list.add(R.id.service_packages_include);
        list.add(R.id.payment_methods_include);
        list.add(R.id.map_include);
        list.add(R.id.success_process_include);


        lifo = new LIFOqueue(list);
        lifo.pop();
        getAirlines();
        getFlightNumbers(0);
        setTooltip();
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
            place placeM=new place();
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
    private void fillMap(ArrayList<place> places){
        for(int x=0;x<places.size();x++){
            putMarker(places.get(x).getName(),Double.valueOf(places.get(x).getLatitude()),Double.valueOf(places.get(x).getLongitude()));
        }
    }
    private void putMarker(String name,double latit,double longit){
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
    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }
    private boolean validateMainForm() {
        String[] phoneArray = validate();
        if (phoneArray[0] != null)
            phoneArray[0] = (phoneArray[0].replace("+", "")).trim();
        name = ((EditText) traveller_include.findViewById(R.id.traveller_name)).getText().toString();
        passport = ((EditText) traveller_include.findViewById(R.id.traveller_passport)).getText().toString();
        nationality = String.valueOf(((Spinner) findViewById(R.id.nationality)).getSelectedItemId() - 1);
        age = ((TextView) traveller_include.findViewById(R.id.traveller_age)).getText().toString();
        airline = ((StringWithTag) (((Spinner) traveller_include.findViewById(R.id.traveller_airline)).getSelectedItem())).value;
        flight_no = req_type == 2 ? ((EditText) traveller_include.findViewById(R.id.flight_num)).getText().toString() : ((StringWithTag) (((Spinner) traveller_include.findViewById(R.id.traveller_flight_no)).getSelectedItem())).value;
        int airline_position = (((Spinner) traveller_include.findViewById(R.id.traveller_airline)).getSelectedItemPosition());
        int flight_no_position = (((Spinner) traveller_include.findViewById(R.id.traveller_flight_no)).getSelectedItemPosition());

        destination = ((EditText) traveller_include.findViewById(R.id.traveller_destination)).getText().toString();
        flight_date = date;
        flight_time = time;
        country_code = phoneArray[0];
        phone = phoneArray[1];
        companions_no = ((EditText) traveller_include.findViewById(R.id.companion_no)).getText().toString();
        luggage_no = ((EditText) traveller_include.findViewById(R.id.arrival_luggage_no)).getText().toString().equals("") ? "0" : ((EditText) traveller_include.findViewById(R.id.arrival_luggage_no)).getText().toString();
        medical_note = ((EditText) traveller_include.findViewById(R.id.medical_note)).getText().toString();
        pickup = ((CheckBox) (traveller_include.findViewById(R.id.pickupC))).isChecked() ? "0" : "1";

        skip_pickup = !((CheckBox) (traveller_include.findViewById(R.id.pickupC))).isChecked();

        if (name.isEmpty() || passport.isEmpty() || nationality.isEmpty() || age.isEmpty() || airline_position == 0 || (flight_no_position == 0 && req_type != 2) || (req_type == 2 && flight_no.isEmpty()) || destination.isEmpty() || flight_date.isEmpty() || flight_time.isEmpty()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_error), Toast.LENGTH_LONG).show();
            return false;
        } else if (req_type == 2 && luggage_no.equals("")) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_error), Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latitude = String.valueOf(location.getLatitude());
                                    longitude = String.valueOf(location.getLongitude());
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }
    private boolean checkPermissions(){
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Granted. Start getting the location information
            }
        }
    }
    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }
    public void getFlightNumbers(int airline_id) {
        FlightsReaderContract.FeedReaderDbHelper dbHelper = new FlightsReaderContract.FeedReaderDbHelper(getApplicationContext());

        SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                FlightsReaderContract.FeedEntry.GId,
                FlightsReaderContract.FeedEntry.name,
        };


// How you want the results sorted in the resulting Cursor
        String sortOrder =
                FlightsReaderContract.FeedEntry.name + " DESC";
        String selection = FlightsReaderContract.FeedEntry.airline_id + " = ? ";
        String[] selectionArgs = {String.valueOf(airline_id)};

        Cursor cursor = db.query(
                FlightsReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        ArrayList<StringWithTag> flights = new ArrayList<>();
        flights.add(new StringWithTag(getResources().getString(R.string.flight_number), "0"));
        while (cursor.moveToNext()) {
            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FlightsReaderContract.FeedEntry.GId));
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(FlightsReaderContract.FeedEntry.name));
            flights.add(new StringWithTag(name, String.valueOf(id)));
        }
        cursor.close();
        fillFlightNumbers(flights);
    }
    public void getAirlines() {
        AirlinesReaderContract.FeedReaderDbHelper dbHelper = new AirlinesReaderContract.FeedReaderDbHelper(getApplicationContext());

        SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
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
        ArrayList<StringWithTag> airlines = new ArrayList<>();
        airlines.add(new StringWithTag(getResources().getString(R.string.airline), "0"));
        while (cursor.moveToNext()) {
            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(AirlinesReaderContract.FeedEntry.GId));
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(AirlinesReaderContract.FeedEntry.name));
            airlines.add(new StringWithTag(name, String.valueOf(id)));
        }
        cursor.close();
        fillAirlines(airlines);
    }
    public void fillAirlines(final ArrayList airlines) {

        final Spinner airline = traveller_include.findViewById(R.id.traveller_airline);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_item_custom, airlines);
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        airline.setAdapter(adapter);
        airline.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("@@" + Integer.valueOf(((StringWithTag) (airlines.get(i))).key) + "ff");
                if (Integer.valueOf(((StringWithTag) (airlines.get(i))).key) != 0) {
                    getFlightNumbers(Integer.valueOf(((StringWithTag) (airlines.get(i))).key));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public void fillFlightNumbers(final ArrayList flightNumbers) {
        final Spinner flights = traveller_include.findViewById(R.id.traveller_flight_no);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_item_custom, flightNumbers);
        flights.setAdapter(adapter);
    }
    private void changeTotal() {
        ((TextView) payment_include.findViewById(R.id.total)).setText(pickedPrice + getResources().getString(R.string.SDG));
    }
    private synchronized void sendReq() throws JSONException {
        JSONArray companionArray = getCompanions(adults + kids - 1);
        final double[] price = new double[2];
        JSONObject json=new JSONObject();
        JSONObject subJSON=new JSONObject();
        try {
            subJSON.put("device",1);
            subJSON.put("type",req_type);
            subJSON.put("login_type",1);
            subJSON.put("customers_id", shared.getString("user_id", ""));
            subJSON.put("name",name);
            subJSON.put("passport",passport);
            subJSON.put("nationality",nationality);
            subJSON.put("age",age);
            subJSON.put("airline",airline);
            subJSON.put("flight_no",flight_no);
            subJSON.put("flight_date",flight_date);
            subJSON.put("flight_time",flight_time);
            subJSON.put("country_code",country_code);
            subJSON.put("phone",phone);
            subJSON.put("companions_no",companions_no);
            subJSON.put("destination",destination);
            subJSON.put("price",pickedPrice);
            System.out.println("Package Type" + packageType);
            subJSON.put("package",packageType);
            subJSON.put("payment_method",paymentMethod);
            subJSON.put("companions",companionArray);
            System.out.println("COMPANIONS " + companionArray.toString());
            subJSON.put("luggages_no", luggage_no);
            subJSON.put("treatment_details", medical_note);
            subJSON.put("latitude", latitude);
            subJSON.put("longitude", longitude);
            subJSON.put("pickup", pickup);
            json.put("method","saveRequest");
            json.put("data",subJSON);
            System.out.println("LLLLLL" + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(okhttp.isNetworkConnected(getApplicationContext())) {
            showLoading();
            okhttp.post(getString(R.string.lara_url) + "saveRequest", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("FAIL");
                    hideLoading();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {
                            JSONObject resJSON = new JSONObject(responseStr);
                            JSONObject subresJSON = new JSONObject(resJSON.getString("data"));
                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                req_id = subresJSON.getInt("id");
                                after = findViewById((Integer) lifo.pop());

                                animateLayout(before, after);
                                hideLoading();
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                        hideLoading();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Response=" + responseStr);

                    } else {
                        System.out.println("Response=" + response + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                        hideLoading();

                    }
                }
            });
        }
    }
    public void onBackPressed() {
        if (main) {
            finish();
        } else if(last){
            finish();
        }
        else {
            findViewById(R.id.next_linear).setVisibility(View.VISIBLE);
            View after = findViewById((Integer) lifo.scndpop());
            View before = findViewById((Integer) lifo.scndpeek());
            if (before.getId() == R.id.success_process_include) {
                findViewById(R.id.main_traveller).setBackgroundColor(getResources().getColor(R.color.bgwhite));
            }
            if (after.getId() == R.id.service_packages_include && skip_companion) {
                after.setVisibility(View.GONE);
                after = findViewById((Integer) lifo.scndpop());
                before = findViewById((Integer) lifo.scndpeek());
            }

            final View finalBefore = before;
            final View finalAfter = after;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalBefore.setVisibility(View.VISIBLE);
                    finalAfter.setVisibility(View.GONE);
                }
            });


            before.setAlpha(1f);
        }
        if (lifo.scndsize() == 1) {
            //Button back = findViewById(R.id.back_form);
            // back.setVisibility(View.GONE);
            main = true;
        }
    }
    public void changePriceLinearBg(View view){
        CardView p1=payment_include.findViewById(R.id.cashC);
        CardView p2=payment_include.findViewById(R.id.bankC);
        CheckBox bankC = payment_include.findViewById(R.id.bank_check);
        CheckBox cashC = payment_include.findViewById(R.id.cash_check);
        TextView selected = payment_include.findViewById(R.id.payment_selected);


        if (view.getId() == bankC.getId()) {
            selected.setText(getResources().getString(R.string.online_bank));
            bankC.setChecked(true);
            cashC.setChecked(false);
            bankC.setBackgroundColor(getResources().getColor(R.color.success_green));
            cashC.setBackgroundColor(getResources().getColor(R.color.white));
            paymentMethod=2;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                p1.setElevation(13f);
                p2.setElevation(0f);
            }
        } else if (view.getId() == cashC.getId()) {
            selected.setText(getResources().getString(R.string.cash));
            cashC.setChecked(true);
            bankC.setChecked(false);
            cashC.setBackgroundColor(getResources().getColor(R.color.success_green));
            bankC.setBackgroundColor(getResources().getColor(R.color.trans60));
            paymentMethod=1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                p1.setElevation(0f);
                p2.setElevation(13f);
            }

        }
    }
    private void setTooltip() {
        final Tooltip.Builder[] builder = new Tooltip.Builder[1];
        package_include.findViewById(R.id.vip_feature).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder[0] = new Tooltip.Builder(view, R.style.Tooltip2)
                        .setCancelable(true)
                        .setDismissOnClick(false)
                        .setCornerRadius(20f)
                        .setText(getResources().getString(R.string.vip_features));
                builder[0].show();
            }
        });
        package_include.findViewById(R.id.classic_feature).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder[0] = new Tooltip.Builder(view, R.style.Tooltip2)
                        .setCancelable(true)
                        .setDismissOnClick(false)
                        .setCornerRadius(20f)
                        .setText(getResources().getString(R.string.classic_features));
                builder[0].show();
            }
        });
    }
    public void changePackageLinearBg(View view) {
        View p1 = package_include.findViewById(R.id.package1);
        View p2 = package_include.findViewById(R.id.package2);
        CheckBox vipC = package_include.findViewById(R.id.vip_check);
        CheckBox classicC = package_include.findViewById(R.id.classic_check);
        TextView selected = package_include.findViewById(R.id.package_selected);

        if (view.getId() == classicC.getId()) {
            selected.setText(getResources().getString(R.string.classic));
            vipC.setChecked(false);
            classicC.setChecked(true);
            classicC.setBackgroundColor(getResources().getColor(R.color.success_green));
            vipC.setBackgroundColor(getResources().getColor(R.color.white));
            packageType = 1;
            pickedPrice = price[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                p1.setElevation(3f);
                p2.setElevation(0f);
            }

        } else if (view.getId() == vipC.getId()) {
            selected.setText(getResources().getString(R.string.vip));
            vipC.setChecked(true);
            classicC.setChecked(false);
            vipC.setBackgroundColor(getResources().getColor(R.color.success_green));
            classicC.setBackgroundColor(getResources().getColor(R.color.white));
            packageType = 2;
            pickedPrice = price[1];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                p1.setElevation(0f);
                p2.setElevation(3f);
            }

        }
        System.out.println(pickedPrice + "__________________________________");
    }
    private JSONArray getCompanions(int companionCount) throws JSONException {
        JSONArray jsonArray=new JSONArray();
        JSONObject jsonObject;
        String companion_name,companion_passport,companion_nationality,companion_age;
        for(int x=0;x<companionCount;x++){
            companion_name=((EditText)(companion_include.findViewWithTag("companion"+x).findViewById(R.id.companion_name))).getText().toString();
            companion_passport=((EditText)(companion_include.findViewWithTag("companion"+x).findViewById(R.id.companion_passport))).getText().toString();
            companion_nationality=String.valueOf(((Spinner) companion_include.findViewWithTag("companion"+x).findViewById(R.id.companion_nationality)).getSelectedItemId());
            companion_age=((TextView)(companion_include.findViewWithTag("companion"+x).findViewById(R.id.companion_age))).getText().toString();
            jsonObject =new JSONObject();
            jsonObject.put("name",companion_name);
            jsonObject.put("passport",companion_passport);
            jsonObject.put("age",companion_age);
            jsonObject.put("nationality",companion_nationality);
            jsonArray.put(x,jsonObject);
        }
        System.out.println("Companion_json="+jsonArray);
        return jsonArray;
    }
    private Boolean validateCompanions(int companionCount) throws JSONException {
        Boolean valid=true;
        System.out.println("NAAAAME"+companionCount);
        String companion_name,companion_passport,companion_nationality,companion_age;
        for(int x=0;x<companionCount;x++){
            companion_name=((EditText)(companion_include.findViewWithTag("companion"+x).findViewById(R.id.companion_name))).getText().toString();
            companion_passport=((EditText)(companion_include.findViewWithTag("companion"+x).findViewById(R.id.companion_passport))).getText().toString();
            companion_nationality=String.valueOf(((Spinner) companion_include.findViewWithTag("companion"+x).findViewById(R.id.companion_nationality)).getSelectedItemId());
            companion_age=((TextView)(companion_include.findViewWithTag("companion"+x).findViewById(R.id.companion_age))).getText().toString();
            System.out.println("NAAAAME"+companion_name);
           if (companion_name.isEmpty() || companion_passport.isEmpty() || companion_nationality.isEmpty() || companion_age.isEmpty()){
               valid=false;
               break;
           }
        }

        return valid;
    }
    private static class StringWithTag {
        public String key;
        public String value;

        public StringWithTag(String value, String key) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney;

        // Add a marker in Sydney and move the camera
        sydney = new LatLng(15.6181252, 32.5038003);
        getPlaces();
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10));
    }
    protected String[] validate() {
        String []phone=new String[2];
        phone[0]=ccp.getSelectedCountryCode();
        phone[1]=((EditText)traveller_include.findViewById(R.id.phone)).getText().toString();
        return phone;
    }
    protected void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
    protected void showKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
    private void changeMsg(int type) {
        TextView msg = success_process_include.findViewById(R.id.msg);

        switch (type) {
            case 1: {
                msg.setText(getResources().getString(R.string.departure_msg));
            }
            break;
            case 2: {
                msg.setText(getResources().getString(R.string.arrival_msg));
            }
            break;
            case 3: {
                msg.setText(getResources().getString(R.string.medical_msg));
            }break;
        }
    }
    public void showLoading(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("inside run");
                findViewById(R.id.linear_main).setVisibility(View.GONE);
                findViewById(R.id.main_loading).setVisibility(View.VISIBLE);
                System.out.println("after inside run");

            }

        });
        System.out.println("after run");


    }
    public void hideLoading(){
        System.out.println("inside hide");

        final View main=findViewById(R.id.linear_main);
        final ProgressBar loading=findViewById(R.id.main_loading);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                main.setClickable(true);
                main.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    main.setForeground(getDrawable(android.R.color.transparent));
                }
                loading.setVisibility(View.GONE);
            }
        });
    }
}
