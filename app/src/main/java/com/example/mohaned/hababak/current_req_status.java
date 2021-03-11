package com.example.mohaned.hababak;

import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohaned.hababak.Adapter.travellers_adapter;
import com.example.mohaned.hababak.Models.req_info;
import com.example.mohaned.hababak.Models.traveller_info;
import com.example.mohaned.hababak.Network.Iokihttp;
import com.kofigyan.stateprogressbar.StateProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class current_req_status extends AppCompatActivity {
    Boolean timerPage = false;
    Iokihttp okhttp;
    public ListView listView;
    public ArrayList<req_info> dataModels;
    public req_status_list_adapter adapter;
    private long timeCountInMilliSeconds ;
    boolean main=true;

    private ProgressBar progressBarCircle;
    private TextView textViewTime;
    private CountDownTimer countDownTimer;
    SharedPreferences shared;
    public ListView travellerslistView;
    public ArrayList<traveller_info> travellersDataModels;
    public travellers_adapter travellerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_req_status);
        init();

    }

    private void init() {
        shared = this.getSharedPreferences("com.example.mohaned.hababak", Context.MODE_PRIVATE);
        // method call to initialize the views
        okhttp = new Iokihttp();
        initViews();
        System.out.println(shared.getString("user_id", "2"));
        if (shared.getString("user_id", "2") != "2") {
            getList(Integer.parseInt(shared.getString("user_id", "")));
        }
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        totalHeight -= view.getMeasuredHeight();
        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void setTimerValues(Long value) {
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = value;
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
                    @Override
                    public void onTick(final long millisUntilFinished) {

                        textViewTime.setText(hmsTimeFormatter(millisUntilFinished));


                        progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

                    }

                    @Override
                    public void onFinish() {

                        textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                        // call to initialize the progress bar values
                        setProgressBarValues();
                    }

                }.start();
                countDownTimer.start();
            }
        });

    }

    private void setProgressBarValues() {

        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }

    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d:%02d",TimeUnit.MILLISECONDS.toDays(milliSeconds),
                TimeUnit.MILLISECONDS.toHours(milliSeconds) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliSeconds)),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }

    /**
     * method to initialize the views
     */
    private void initViews() {
        progressBarCircle = findViewById(R.id.progressBarCircle);
        textViewTime = findViewById(R.id.textViewTime);
        appendInit();
    }

    public void setList(JSONArray companionsA) throws JSONException {
        System.out.println("11");
        listView=findViewById(R.id.reqTypeList);
        System.out.println("22");
        dataModels= new ArrayList<>();
        if (companionsA.length() != 0) {
            System.out.println("length=" + companionsA.length());
            for (int x = 0; x < companionsA.length(); x++) {
                req_info dataModel = new req_info();
                System.out.println("ID=" + ((JSONObject) companionsA.get(x)).getString("id"));
                dataModel.setId(((JSONObject)companionsA.get(x)).getString("id"));
                dataModel.setFlight_date(((JSONObject) companionsA.get(x)).getString("flight_date"));
                dataModel.setFlight_time(((JSONObject) companionsA.get(x)).getString("flight_time"));
                dataModel.setDate(((JSONObject)companionsA.get(x)).getString("creation_date"));
                dataModel.setDestination(((JSONObject)companionsA.get(x)).getString("destination"));
                dataModel.setTraveller_name(((JSONObject) companionsA.get(x)).getString("name"));
                dataModel.setPassport(((JSONObject)companionsA.get(x)).getString("passport"));
                dataModel.setFlag(((JSONObject) companionsA.get(x)).getString("flag"));
                dataModel.setType(((JSONObject) companionsA.get(x)).getString("type"));
                dataModel.setPrice(((JSONObject) companionsA.get(x)).getString("price"));
                dataModel.setPhone(((JSONObject) companionsA.get(x)).getString("country_code") + " " + ((JSONObject) companionsA.get(x)).getString("phone"));
                dataModel.setStatus(((JSONObject)companionsA.get(x)).getString("status"));
                if (((JSONObject) companionsA.get(x)).getInt("flag") == 1) {
                    dataModel.setCompanion_no(((JSONObject) companionsA.get(x)).getString("companions_no"));
                    dataModel.setPackage_info(((JSONObject) companionsA.get(x)).getString("package"));
                    dataModel.setAirline(((JSONObject) companionsA.get(x)).getString("airline"));
                    dataModel.setFlight_no(((JSONObject) companionsA.get(x)).getString("flight_no"));
                    dataModel.setBirth_date(((JSONObject) companionsA.get(x)).getString("age"));
                    dataModel.setNationality(((JSONObject) companionsA.get(x)).getString("nationality"));
                } else {
                    dataModel.setHaveNat(((JSONObject) companionsA.get(x)).getString("has_ns"));
                    dataModel.setProfession(((JSONObject) companionsA.get(x)).getString("job"));
                    dataModel.setVisa(((JSONObject) companionsA.get(x)).getString("permit"));
                }
                if (((JSONObject) companionsA.get(x)).getInt("type") == 2 && ((JSONObject) companionsA.get(x)).getInt("flag") == 1) {
                    dataModel.setLuggage_no(((JSONObject) companionsA.get(x)).getString("luggages_no"));
                } else if (((JSONObject) companionsA.get(x)).getInt("type") == 3 && ((JSONObject) companionsA.get(x)).getInt("flag") == 1) {
                    dataModel.setLuggage_no(((JSONObject) companionsA.get(x)).getString("treatment_details"));
                }

                dataModels.add(dataModel);
            }
            adapter = new current_req_status.req_status_list_adapter(dataModels, getApplicationContext());


            runOnUiThread(new Runnable() {
                public void run() {
                    listView.setAdapter(adapter);
                }
            });
        }
    }

    public void getTime(final req_info dataModel,View view){
        JSONObject json=new JSONObject();
        JSONObject subJSON=new JSONObject();
        try {

            json.put("method","getStatus");
            subJSON.put("type",dataModel.getFlag());
            subJSON.put("id",dataModel.getId());
            json.put("data",subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(okhttp.isNetworkConnected(getApplicationContext())) {
            showLoading();
            okhttp.post(getResources().getString(R.string.lara_url) + "getStatus", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    hideLoading();
                    System.out.println("FAIL");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                        }
                    });
                    //  hideLoading();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    hideLoading();
                    // hideLoading();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {
                            JSONObject resJSON = new JSONObject(responseStr);
                            JSONObject subresJSON = new JSONObject(resJSON.getString("data"));
                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                Long counter=TimeUnit.DAYS.toMillis(Long.parseLong(subresJSON.get("d").toString()))+TimeUnit.HOURS.toMillis(Long.parseLong(subresJSON.get("h").toString()))+TimeUnit.MINUTES.toMillis(Long.parseLong(subresJSON.get("m").toString()))+TimeUnit.SECONDS.toMillis(Long.parseLong(subresJSON.get("s").toString()));
                                int status=Integer.parseInt(dataModel.getStatus());
                                openTimer(status,Integer.parseInt(dataModel.getType()),Integer.parseInt(dataModel.getFlag()),counter);
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
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

    public void onBackPressed() {

        if(main){
            finish();
        }
        else{
            if (timerPage) {
                try {
                    countDownTimer.cancel();
                } catch (Exception e) {
                }
                timerPage = false;
            }
            final View req_list =  findViewById(R.id.req_list);
            final View status_linear =  findViewById(R.id.status_linear);
            final View req_details = findViewById(R.id.req_details);
            final View req_details2 = findViewById(R.id.service_details);



            req_list.setVisibility(View.VISIBLE);
            status_linear.setVisibility(View.GONE);
            req_details.setVisibility(View.GONE);
            req_details2.setVisibility(View.GONE);

            main=true;
            req_list.setAlpha(1f);
        }
    }

    private StateProgressBar.StateNumber getStateNumber(int number){
        switch (number)
        {
            case 1:{return StateProgressBar.StateNumber.ONE;}
            case 2:{return StateProgressBar.StateNumber.TWO;}
            default:{return StateProgressBar.StateNumber.THREE;}
        }
    }

    private void openTimer(int status,int type,int flag,Long counter){
        StateProgressBar stateProgressBar=findViewById(R.id.status_bar);

        String[] description=new String[3];
        description[0]=getResources().getString(R.string.status1);
        description[1]=getResources().getString(R.string.status2);
        description[2]=getResources().getString(R.string.status3);

        stateProgressBar.setStateDescriptionData(description);
        stateProgressBar.setCurrentStateNumber(getStateNumber(status));

        changeMsg(status,type,flag);
        setTimerValues(counter);
        startCountDownTimer();
       // app:spb_currentStateNumber="three"

        final View req_list =  findViewById(R.id.req_list);
        final View status_linear = findViewById(R.id.status_linear);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                req_list.setVisibility(View.GONE);
                status_linear.setVisibility(View.VISIBLE);
                status_linear.animate().alpha(1f).setDuration(700);
                main=false;
            }
        });


    }

    private void changeMsg(int status,int type,int flag){
        TextView msg=findViewById(R.id.status_msg);
        if(flag==1){
            switch (type){
                case 1:{
                    switch (status){
                        case 1:{
                            msg.setText(getResources().getString(R.string.req1_status1));
                        }break;
                        case 2:{
                            msg.setText(getResources().getString(R.string.req1_status2));
                        }break;
                        case 3:{
                            msg.setText(getResources().getString(R.string.req1_status3));
                        }break;
                    }
                }break;
                case 2:{
                    switch (status){
                        case 1:{
                            msg.setText(getResources().getString(R.string.req2_status1));
                        }break;
                        case 2:{
                            msg.setText(getResources().getString(R.string.req2_status2));
                        }break;
                        case 3:{
                            msg.setText(getResources().getString(R.string.req2_status3));
                        }break;
                    }
                }break;
                case 3:{
                    switch (status){
                        case 1:{
                            msg.setText(getResources().getString(R.string.req3_status1));
                        }break;
                        case 2:{
                            msg.setText(getResources().getString(R.string.req3_status2));
                        }break;
                        case 3:{
                            msg.setText(getResources().getString(R.string.req3_status3));
                        }break;
                    }
                }break;
                case 4:{
                    switch (status){
                        case 1:{
                            msg.setText(getApplicationContext().getResources().getString(R.string.req4_status1));
                        }break;
                        case 2:{
                            msg.setText(getApplicationContext().getResources().getString(R.string.req4_status2));
                        }break;
                        case 3:{
                            msg.setText(getApplicationContext().getResources().getString(R.string.req4_status3));
                        }break;
                    }
                }break;
            }
        }
        else if (flag==2){
            switch (type){
                case 1:{
                    switch (status){
                        case 1:{
                            msg.setText(getResources().getString(R.string.serv1_status1));
                        }break;
                        case 2:{
                            msg.setText(getResources().getString(R.string.serv1_status2));
                        }break;
                        case 3:{
                            msg.setText(getResources().getString(R.string.serv1_status3));
                        }break;
                    }
                }break;
                case 2:{
                    switch (status){
                        case 1:{
                            msg.setText(getResources().getString(R.string.serv2_status1));
                        }break;
                        case 2:{
                            msg.setText(getResources().getString(R.string.serv2_status2));
                        }break;
                        case 3:{
                            msg.setText(getResources().getString(R.string.serv2_status3));
                        }break;
                    }
                }break;
                case 3:{
                    switch (status){
                        case 1:{
                            msg.setText(getResources().getString(R.string.serv3_status1));
                        }break;
                        case 2:{
                            msg.setText(getResources().getString(R.string.serv3_status2));
                        }break;
                        case 3:{
                            msg.setText(getResources().getString(R.string.serv3_status3));
                        }break;
                    }
                }break;
            }
        }
    }

    public void getTravellerList(String id, String flag) {
        JSONArray jsonArray = new JSONArray();
        JSONObject json = new JSONObject();
        JSONObject subJSON = new JSONObject();
        try {
            subJSON.put("id", id);
            subJSON.put("type", flag);
            json.put("method", "getRequest");
            json.put("data", subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (okhttp.isNetworkConnected(getApplicationContext())) {
            showLoading();
            okhttp.post(getApplicationContext().getResources().getString(R.string.lara_url) + "getRequest", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    hideLoading();
                    System.out.println("FAIL");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                            //  hideLoading();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    hideLoading();
                    // hideLoading();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        System.out.println(responseStr + "trrrrraaaaaaaaaaaveeeeeeeel");
                        try {
                            JSONObject resJSON = new JSONObject(responseStr);
                            JSONObject subresJSON = new JSONObject(resJSON.getString("data"));
                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                JSONArray companions = new JSONArray(subresJSON.get("travellers").toString());
                                setTravellersList(companions);
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();

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

    private void getList(int id) {
        System.out.println("ID IS : " + id);
        JSONObject json = new JSONObject();
        JSONObject subJSON = new JSONObject();
        try {

            json.put("method", "currentRequests");
            subJSON.put("customers_id", id);
            json.put("data", subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (okhttp.isNetworkConnected(getApplicationContext())) {
            showLoading();
            okhttp.post(getResources().getString(R.string.lara_url) + "currentRequests", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    hideLoading();
                    System.out.println("FAIL");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                        }
                    });
                    //  hideLoading();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    hideLoading();
                    // hideLoading();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {
                            JSONObject resJSON = new JSONObject(responseStr);
                            JSONObject subresJSON = new JSONObject(resJSON.getString("data"));
                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                JSONArray requests = new JSONArray(subresJSON.get("requests").toString());

                                System.out.println("LENGTH=" + requests.length());
                                setList(requests);
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
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

    public void setTravellersList(JSONArray travellersList) throws JSONException {
        System.out.println("11");
        travellerslistView = findViewById(R.id.main_profile_include).findViewById(R.id.req_travellers_list);
        System.out.println("22");
        JSONObject jsonObject;

        travellersDataModels = new ArrayList<>();
        if (travellersList.length() == 0) {
        } else {
            findViewById(R.id.main_profile_include).findViewById(R.id.passenger_info).setVisibility(View.VISIBLE);
            // System.out.println("33"+y);
            for (int x = 0; x < travellersList.length(); x++) {
                traveller_info dataModel = new traveller_info();
                System.out.println("111111111111111111111111111111115" + ((JSONObject) travellersList.get(x)).getString("name"));
                dataModel.setTraveller_name(((JSONObject) travellersList.get(x)).getString("name"));
                dataModel.setAge(((JSONObject) travellersList.get(x)).getString("age"));
                dataModel.setPassport_no(((JSONObject) travellersList.get(x)).getString("passport"));

                travellersDataModels.add(dataModel);
            }

            travellerAdapter = new travellers_adapter(travellersDataModels, getApplicationContext());

            travellerslistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    traveller_info dataModel = travellersDataModels.get(position);

                    //   intent.putExtra();
                    // startActivity(intent);
                }
            });


            current_req_status.this.runOnUiThread(new Runnable() {
                public void run() {
                    travellerslistView.setAdapter(travellerAdapter);
                    setListViewHeightBasedOnChildren(travellerslistView);
                }
            });
        }
    }

    private void appendInit() {
        View view = findViewById(R.id.main_profile_include);
        int[] i = {R.id.order_noT, R.id.dateT, R.id.statusT, R.id.typeT, R.id.nameT, R.id.passportT, R.id.nationalityT, R.id.bdateT, R.id.airlineT, R.id.fnoT, R.id.companionT, R.id.priceT, R.id.packageT, R.id.medicalT, R.id.destinationT, R.id.fdateT,
                R.id.ftimeT};
        appendToText(i, view);
        view = findViewById(R.id.other_profile_include);
        int[] r = {R.id.order_noT, R.id.dateT, R.id.statusT, R.id.typeT, R.id.nameT, R.id.passportT, R.id.nationalityT, R.id.permitT, R.id.destinationT, R.id.jobT, R.id.fdateT,
                R.id.ftimeT, R.id.priceT};
        appendToText(r, view);
    }

    private void setInfo(req_info model) {


        if (Integer.parseInt(model.getFlag()) == 1) {
            View view = findViewById(R.id.main_profile_include);

            ((TextView) view.findViewById(R.id.req_id)).setText(model.getId());
            ((TextView) view.findViewById(R.id.req_date)).setText(model.getDate());
            ((TextView) view.findViewById(R.id.req_type)).setText(getReqType(model.getFlag(), model.getType()));
            ((TextView) view.findViewById(R.id.req_status)).setText(getStatusString(Integer.parseInt(model.getStatus())));
            ((TextView) view.findViewById(R.id.req_name)).setText(model.getTraveller_name());
            ((TextView) view.findViewById(R.id.p_no)).setText(model.getPassport());
            ((TextView) view.findViewById(R.id.req_nationality)).setText(getNationality(Integer.parseInt(model.getNationality())));
            ((TextView) view.findViewById(R.id.req_birthdate)).setText(model.getBirth_date());
            ((TextView) view.findViewById(R.id.req_destination)).setText(model.getDestination());
            ((TextView) view.findViewById(R.id.req_airline)).setText(model.getAirline());
            ((TextView) view.findViewById(R.id.req_flight_date)).setText(model.getFlight_date());
            ((TextView) view.findViewById(R.id.req_flight_time)).setText(model.getFlight_time());
            ((TextView) view.findViewById(R.id.req_flight_no)).setText(model.getFlight_no());
            ((TextView) view.findViewById(R.id.req_phone)).setText(model.getPhone());
            ((TextView) view.findViewById(R.id.req_companion)).setText(model.getCompanion_no());
            ((TextView) view.findViewById(R.id.req_price)).setText(model.getPrice() + " " + getResources().getString(R.string.SDG));
            ((TextView) view.findViewById(R.id.req_package)).setText(getPackage(Integer.parseInt(model.getPackage_info())));

            if (Integer.parseInt(model.getType()) == 2) {
                ((TextView) view.findViewById(R.id.req_luggage)).setText(model.getLuggage_no());
            } else if (Integer.parseInt(model.getType()) == 3) {
                ((TextView) view.findViewById(R.id.req_medical_details)).setText(model.getMedical_details());
            }
        } else if (Integer.parseInt(model.getFlag()) == 2) {

            View view = findViewById(R.id.other_profile_include);

            ((TextView) view.findViewById(R.id.req_id)).setText(model.getId());
            ((TextView) view.findViewById(R.id.req_date)).setText(model.getDate());
            ((TextView) view.findViewById(R.id.req_type)).setText(getReqType(model.getFlag(), model.getType()));
            ((TextView) view.findViewById(R.id.req_status)).setText(getStatusString(Integer.parseInt(model.getStatus())));
            ((TextView) view.findViewById(R.id.req_name)).setText(model.getTraveller_name());
            ((TextView) view.findViewById(R.id.p_no)).setText(model.getPassport());
            ((TextView) view.findViewById(R.id.req_permit)).setText(getPermit(Integer.parseInt(model.getVisa())));
            ((TextView) view.findViewById(R.id.req_destination)).setText(model.getDestination());
            ((TextView) view.findViewById(R.id.req_profession)).setText(model.getProfession());
            ((TextView) view.findViewById(R.id.req_flight_date)).setText(model.getFlight_date());
            ((TextView) view.findViewById(R.id.req_flight_time)).setText(model.getFlight_time());
            ((TextView) view.findViewById(R.id.req_phone)).setText(model.getPhone());
            ((TextView) view.findViewById(R.id.req_price)).setText(model.getPrice() == null ? "0" : model.getPrice() + " " + getResources().getString(R.string.SDG));
        }
    }

    private void removeRequest(final String flag, final String reqId) {
        System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        final AlertDialog.Builder builder = new AlertDialog.Builder(current_req_status.this);
        builder.setMessage(getString(R.string.delete_verfication) + reqId)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        JSONObject json = new JSONObject();
                        JSONObject subJSON = new JSONObject();
                        try {

                            json.put("method", "deleteRequest");
                            subJSON.put("flag", flag);
                            subJSON.put("id", reqId);
                            json.put("data", subJSON);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (okhttp.isNetworkConnected(getApplicationContext())) {
                            okhttp.post(getResources().getString(R.string.lara_url) + "deleteRequest", json.toString(), new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    System.out.println("FAIL");
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    //  hideLoading();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    // hideLoading();
                                    if (response.isSuccessful()) {
                                        String responseStr = response.body().string();
                                        try {
                                            JSONObject resJSON = new JSONObject(responseStr);
                                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                                finish();
                                                Intent i = new Intent(getApplicationContext(), current_req_status.class);
                                                startActivity(i);
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
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
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void openDetailsLayout(req_info model) {
        final View mainV = findViewById(R.id.req_list);
        View req_details = null;
        if (Integer.parseInt(model.getFlag()) == 1) {
            req_details = findViewById(R.id.req_details);
            System.out.println("1111111111111" + req_details);
            getTravellerList(model.getId(), model.getFlag());
        } else if (Integer.parseInt(model.getFlag()) == 2)
            req_details = findViewById(R.id.service_details);

        else {
            return;
        }
        System.out.println("2222222222222" + req_details);
        setInfo(model);
        final View finalReq_details = req_details;
        System.out.println("333333333333333" + req_details);
        System.out.println("444444444444444" + finalReq_details);
        mainV.animate().alpha(0f).setDuration(700).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mainV.setVisibility(View.GONE);
                finalReq_details.setVisibility(View.VISIBLE);
                finalReq_details.animate().alpha(1f).setDuration(700);
                main = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private String getPermit(int permit) {
        switch (permit) {
            case 1: {
                return getApplicationContext().getResources().getString(R.string.visitor);
            }
            case 2: {
                return getApplicationContext().getResources().getString(R.string.resident);
            }
            default: {
                return "";
            }
        }
    }

    private String getStatusString(int status) {
        System.out.println("STAAAAAAAAAAAATUS" + status);
        String statusS = "";
        switch (status) {
            case 1: {
                statusS = getApplicationContext().getResources().getString(R.string.status1);
            }
            break;
            case 2: {
                statusS = getApplicationContext().getResources().getString(R.string.status2);
            }
            break;
            case 3: {
                statusS = getApplicationContext().getResources().getString(R.string.status3);
            }
            break;
        }
        return statusS;
    }

    private String getReqType(String flag, String req_type) {
        String req_type_s = "";
        System.out.println(flag + "________________" + req_type);
        if (Integer.parseInt(flag) == 1) {
            switch (Integer.parseInt(req_type)) {
                case 1: {
                    req_type_s = getApplicationContext().getResources().getString(R.string.departure);
                }
                break;
                case 2: {
                    req_type_s = getApplicationContext().getResources().getString(R.string.arrival);
                }
                break;
                case 3: {
                    req_type_s = getApplicationContext().getResources().getString(R.string.medical_details);
                }
                break;
            }
        } else {
            switch (Integer.parseInt(req_type)) {
                case 1: {
                    req_type_s = getApplicationContext().getResources().getString(R.string.exit_visa);
                }
                break;
                case 2: {
                    req_type_s = getApplicationContext().getResources().getString(R.string.yellow_fever);
                }
                break;
                case 3: {
                    req_type_s = getApplicationContext().getResources().getString(R.string.national_service);
                }
                break;
            }
        }
        return req_type_s;
    }

    private String getPackage(int packageS) {
        String package_s = "";
        switch (packageS) {
            case 1: {
                package_s = getApplicationContext().getResources().getString(R.string.classic);
            }
            break;
            case 2: {
                package_s = getApplicationContext().getResources().getString(R.string.vip);
            }
            break;
        }
        return package_s;
    }

    private String getNationality(int nationality) {
        if (nationality == 0) {
            return getApplicationContext().getResources().getString(R.string.sudanese);
        } else if (nationality == 1) {
            return getApplicationContext().getResources().getString(R.string.foreigner);
        } else
            return "";
    }

    private void appendToText(int[] id, View view) {
        for (int i = 0; i < id.length; i++) {
            TextView textView = view.findViewById(id[i]);
            textView.append(" :");
        }
    }

    public void showLoading() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("inside run");
                findViewById(R.id.layout).setVisibility(View.GONE);
                findViewById(R.id.loading).setVisibility(View.VISIBLE);
                System.out.println("after inside run");

            }

        });
        System.out.println("after run");


    }

    public void hideLoading() {
        System.out.println("inside hide");

        final View main = findViewById(R.id.layout);
        final ProgressBar loading = findViewById(R.id.loading);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                main.setClickable(true);
                main.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
            }
        });
    }

    public class req_status_list_adapter extends ArrayAdapter<req_info> implements View.OnClickListener {
        private Context mContext;
        private int lastPosition = -1;
        private ViewHolder viewHolder;


        public req_status_list_adapter(ArrayList<req_info> data, Context context) {
            super(context, R.layout.req_status, data);
            System.out.println("DATAMODELS INSIDE ADAPTER=" + data.size());
            this.mContext = context;

        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            // Get the data item for this position
            final req_info dataModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            // view lookup cache stored in tag

            final View result;
            if (convertView == null) {


                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.req_status, parent, false);

                viewHolder.req_id = convertView.findViewById(R.id.req_id);
                // viewHolder.datetime = convertView.findViewById(R.id.date_status);
                viewHolder.req_status = convertView.findViewById(R.id.req_status);
                viewHolder.req_type = convertView.findViewById(R.id.req_type);
                viewHolder.status = convertView.findViewById(R.id.rel_status);
                viewHolder.more = convertView.findViewById(R.id.rel_more);
                viewHolder.delete = convertView.findViewById(R.id.deleteBtn);


                result = convertView;


                System.out.println("Position=" + position);
                convertView.setTag(viewHolder);


            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result = convertView;
            }
            assert dataModel != null;

            viewHolder.req_id.setText(dataModel.getId());

            //   viewHolder.datetime.setText(dataModel.getDate());
            viewHolder.req_status.setText(getStatusString(Integer.parseInt(dataModel.getStatus())));
            viewHolder.req_status.setTextColor(getColor(Integer.parseInt(dataModel.getStatus())));
            System.out.println(dataModel.getFlag() + "FLAAAAAAAAAg" + dataModel.getType());
            viewHolder.req_type.setText(getReqType(dataModel.getFlag(), dataModel.getType()));
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    1.5f
            );
            if (Integer.parseInt(dataModel.getStatus()) != 1) {
                System.out.println("STATUS=======" + dataModel.getStatus());
                viewHolder.delete.setVisibility(View.GONE);
                viewHolder.status.setLayoutParams(param);
                viewHolder.more.setLayoutParams(param);

            }
            viewHolder.status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    timerPage = true;
                    getTime(dataModel, view);
                }
            });
            viewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDetailsLayout(dataModel);
                }
            });
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeRequest(dataModel.getFlag(), dataModel.getId());
                }
            });

            lastPosition = position;

          /*  viewHolder.txtLocation.setText(dataModel.getArea());
            viewHolder.txtPrice.setText(dataModel.getPrice());
            viewHolder.txtType.setText(dataModel.getType());
          */
            // new LoadImageTask(this).execute(dataModel.getImage());
            return convertView;
        }

        @Override
        public void onClick(View v) {

        }

        private class ViewHolder {
            TextView req_id, datetime, req_status, req_type, order_noT, dateT, statusT, typeT;
            RelativeLayout status, more, delete;


        }

        public int getColor(int status) {
            switch (status) {
                case 1: {
                    return getResources().getColor(R.color.started);
                }
                case 2: {
                    return getResources().getColor(R.color.under_process);
                }
                case 3: {
                    return getResources().getColor(R.color.success_green);
                }
                default:
                    return getResources().getColor(R.color.primary_text);
            }

        }


    }
}
