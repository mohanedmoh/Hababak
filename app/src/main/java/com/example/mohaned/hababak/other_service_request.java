package com.example.mohaned.hababak;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohaned.hababak.Network.Iokihttp;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.hbb20.CountryCodePicker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class other_service_request extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,DatePickerDialog.OnDateSetListener{
    String pickedPrice;
    LIFOqueue lifo;
    ArrayList<Object> list;
    boolean main=true;
    boolean last=false;
    int req_type;
    View other_req_form, payment_include, success_inlcude;
    ArrayList <String> passport_image,visa_image=null;
    String dateS,timeS,req_id;
    public DatePickerDialog dpd;
    public Calendar now;
    int req_code;
    private Iokihttp okhttp;
    private int paymentMethod=1;
    String traveller_name,traveller_passport,traveller_destination,traveller_visa_type,traveller_have_natCard,traveller_job,flight_date,flight_time,country_code,phone;
    SharedPreferences shared;
    CountryCodePicker ccp;
    View after,before = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_service_request);
init();

    }
    private void init(){
        okhttp=new Iokihttp();

        shared = this.getSharedPreferences("com.example.mohaned.hababak", Context.MODE_PRIVATE);

        req_type=getIntent().getIntExtra("type",2);
        other_req_form=findViewById(R.id.other_form_include);
        // passport_include=findViewById(R.id.passport_image_include);
        //visa_include=findViewById(R.id.visa_image_include);
        payment_include=findViewById(R.id.payment_include);
        success_inlcude=findViewById(R.id.success_process_include);
        ccp=other_req_form.findViewById(R.id.ccp);


        now = Calendar.getInstance();
        dpd = DatePickerDialog.newInstance(
                other_service_request.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        other_req_form.findViewById(R.id.datetimeTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        other_req_form.findViewById(R.id.flight_date_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dpd.show(getFragmentManager(), "Datepickerdialog");
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

        showFields(req_type);

        findViewById(R.id.next_form).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    openNextActivity();

            }
        });
        findViewById(R.id.back_form).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        other_req_form.findViewById(R.id.passport_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                req_code=101;
                openCam(req_code);
            }
        });
        other_req_form.findViewById(R.id.passport_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                req_code=101;
                openCam(req_code);
            }
        });

        other_req_form.findViewById(R.id.visa_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                req_code=102;
                openCam(req_code);
            }
        });
        other_req_form.findViewById(R.id.visa_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                req_code=102;
                openCam(req_code);
            }
        });
        success_inlcude.findViewById(R.id.done_req).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void openCam(int code) {
        Pix.start(other_service_request.this, code, 1);
    }

    public void changePriceLinearBg(View view){
        CardView p1 = payment_include.findViewById(R.id.cashC);
        CardView p2 = payment_include.findViewById(R.id.bankC);
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

    private void uploadImage(int code, String image1, String name1,String image2,String name2) throws IOException {
        File f1 = new File(getApplicationContext().getCacheDir(), name1);
        f1.createNewFile();
        OutputStream os1 = new BufferedOutputStream(new FileOutputStream(f1));

        File imageString1 = new File(image1);
        Bitmap bitmap1 = new BitmapDrawable(getApplicationContext().getResources(), imageString1.getAbsolutePath()).getBitmap();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 70, os1);
        os1.close();
        File f2 = new File(getApplicationContext().getCacheDir(), name2);
        f2.createNewFile();
        OutputStream os2 = new BufferedOutputStream(new FileOutputStream(f1));

        File imageString2 = new File(image2);
        Bitmap bitmap2 = new BitmapDrawable(getApplicationContext().getResources(), imageString2.getAbsolutePath()).getBitmap();
        bitmap2.compress(Bitmap.CompressFormat.JPEG, 70, os2);
        os2.close();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                changeStatusPost(false, R.string.upload_image_status);
            }
        });
        okhttp.uploadImage(req_id,"2",f1, name1, f2, name2, getResources().getString(R.string.lara_url) + "upload", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (okhttp.retry <= 3) {
                    call.clone();
                    okhttp.retry++;
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideLoading();
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.try_later),Toast.LENGTH_LONG).show();
                        }
                    });
                }
                System.out.println("FAIL");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideLoading();
                okhttp.retry=0;
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    try {
                        JSONObject resJSON = new JSONObject(responseStr);
                        // JSONObject subresJSON = new JSONObject(resJSON.getString("data"));
                        if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                            before= findViewById((Integer) lifo.scndpeek());
                            after = null;
                            after = findViewById((Integer) lifo.pop());
                            animateLayout(before,after);

                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    Toast.makeText(getApplicationContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                                }
                            });
                            onBackPressed();
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
    private void changeImage(String imageString,View view,int id){
        File f = new File(imageString);
        Bitmap d = new BitmapDrawable(getApplicationContext().getResources(), f.getAbsolutePath()).getBitmap();
        //Bitmap scaled = com.fxn.utility.Utility.getScaledBitmap(512, com.fxn.utility.Utility.getExifCorrectedBitmap(f));
        Bitmap scaled = com.fxn.utility.Utility.getScaledBitmap(512, d);
        ((ImageView)view.findViewById(id)).setImageBitmap(scaled);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(other_service_request.this, req_code,1);
                } else {
                    Toast.makeText(other_service_request.this, "Approve permissions to open ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        dateS= year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
        TimePickerDialog tpd = TimePickerDialog.newInstance(other_service_request.this, Calendar.HOUR_OF_DAY, Calendar.MINUTE, false);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        timeS = hourOfDay+":"+minute+":"+second;
        ((TextView)other_req_form.findViewById(R.id.datetimeTextView)).setText(dateS+" "+timeS);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == other_service_request.RESULT_OK && requestCode == 101) {
            passport_image = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            changeImage(passport_image.get(0), other_req_form, R.id.passport_image_view);



        } else if (resultCode == other_service_request.RESULT_OK && requestCode == 102) {
            visa_image = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            changeImage(visa_image.get(0), other_req_form, R.id.visa_image_view);
        }
        else  if (requestCode == 202 && resultCode == 1) {
            after = findViewById((Integer) lifo.pop());
            animateLayout(before, after);
        } else{
            hideLoading();
        }
    }
    protected String[] validate() {
        String []phone=new String[2];
        phone[0]=ccp.getSelectedCountryCode();
        phone[1]=((EditText)other_req_form.findViewById(R.id.phone)).getText().toString();
        return phone;
    }
    private void showFields(int type){
        list=new ArrayList<Object>();
        switch (type){
            case 1:{
                ((TextView)findViewById(R.id.title)).setText(getResources().getString(R.string.exit_visa));
                other_req_form.findViewById(R.id.visitor_linear).setVisibility(View.VISIBLE);
                other_req_form.findViewById(R.id.national_linear).setVisibility(View.VISIBLE);
            }break;
            case 2:{((TextView)findViewById(R.id.title)).setText(getResources().getString(R.string.yellow_fever));
            other_req_form.findViewById(R.id.visa_card).setVisibility(View.GONE);
            }break;
            case 3: {((TextView)findViewById(R.id.title)).setText(getResources().getString(R.string.national_service));
                other_req_form.findViewById(R.id.national_linear).setVisibility(View.GONE);
            }break;
        }
        if (type==2){
            list.add(R.id.other_form_include);
            list.add(R.id.payment_include);
            list.add(R.id.success_process_include);
        }
        else if(type==1 || type==3){
            list.add(R.id.other_form_include);
            //list.add(R.id.passport_image_include);
            // list.add(R.id.visa_image_include);
            list.add(R.id.payment_include);
            list.add(R.id.success_process_include);
        }
        lifo=new LIFOqueue(list);
        lifo.pop();
    }
    private boolean validateForm(){

        String[] phoneArray = validate();
        if (phoneArray[0] != null)
            phoneArray[0] = (phoneArray[0].replace("+", "")).trim();
        traveller_name=((EditText)(other_req_form.findViewById(R.id.traveller_name))).getText().toString();
        traveller_passport=((EditText)(other_req_form.findViewById(R.id.traveller_passport))).getText().toString();
        traveller_destination=((EditText)(other_req_form.findViewById(R.id.traveller_destination))).getText().toString();
        int selected_visa_id=((RadioGroup)(other_req_form.findViewById(R.id.visa_group))).getCheckedRadioButtonId();

        traveller_visa_type=(selected_visa_id==R.id.visitor?"1":"2");

        traveller_have_natCard= ((CheckBox)(other_req_form.findViewById(R.id.have_natCard))).isChecked() ?"1":"2";
        traveller_job=((EditText)(other_req_form.findViewById(R.id.traveller_job))).getText().toString();
        flight_date=dateS;
        flight_time=timeS;
        country_code=phoneArray[0];
        phone=phoneArray[1];
        if(traveller_name.isEmpty() || traveller_passport.isEmpty() || traveller_destination.isEmpty() || traveller_job.isEmpty() || flight_date.isEmpty() || flight_time.isEmpty() || country_code.isEmpty() || phone.isEmpty() ){
            return false;
        }
        else if(req_type==1 & traveller_have_natCard.isEmpty()){
            return false;
        } else return passport_image != null && (visa_image != null || req_type == 2);

    }
    private synchronized void setPayment(){
        JSONObject json=new JSONObject();
        JSONObject subJSON=new JSONObject();
        try {
            subJSON.put("id",req_id);
            subJSON.put("req_type",2);
            subJSON.put("payment_method",paymentMethod);
            json.put("method","setPayment");
            json.put("data",subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(okhttp.isNetworkConnected(getApplicationContext())) {
            changeStatusPost(true,R.string.app_name);
            showLoading();

            okhttp.post(getString(R.string.lara_url) + "setPayment", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.try_later),Toast.LENGTH_SHORT).show();
                            hideLoading();
                        }
                    });
                    System.out.println("FAIL");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    hideLoading();
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
    private void sendReq(){
        changeStatusPost(false,R.string.submit_status);
        showLoading();
        //final double[] price = new double[2];
        JSONObject json=new JSONObject();
        JSONObject subJSON=new JSONObject();


        try {
            subJSON.put("device",1);
            subJSON.put("type",req_type);
            subJSON.put("login_type",1);
            subJSON.put("customers_id", shared.getString("user_id", ""));
            subJSON.put("name",traveller_name);
            subJSON.put("passport",traveller_passport);
            subJSON.put("flight_date",flight_date);
            subJSON.put("flight_time",flight_time);
            subJSON.put("country_code",country_code);
            subJSON.put("phone",phone);
            subJSON.put("destination",traveller_destination);
            subJSON.put("haveNCard",traveller_have_natCard);
            subJSON.put("job",traveller_job);
            //subJSON.put("price",pickedPrice);
            System.out.println("permit=" + traveller_visa_type);
            subJSON.put("permit", traveller_visa_type);
            // subJSON.put("companions",companionArray);
            json.put("method","otherRequest");
            json.put("data",subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(okhttp.isNetworkConnected(getApplicationContext())) {
            okhttp.post(getString(R.string.lara_url) + "otherRequest", json.toString(), new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    if (okhttp.retry <= 3) {
                        call.clone();
                        okhttp.retry++;
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideLoading();
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.try_later),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    System.out.println("FAIL");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    okhttp.retry=0;
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {
                            JSONObject resJSON = new JSONObject(responseStr);
                            JSONObject subresJSON = new JSONObject(resJSON.getString("data"));
                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                req_id = subresJSON.getString("id");
                                pickedPrice = subresJSON.getString("price");
                                changeTotal(subresJSON.getString("price"));
                                uploadImages();
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {

                                        Toast.makeText(getApplicationContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                onBackPressed();
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
    private void uploadImages(){
    try {
        uploadImage(101, passport_image.get(0), "passport_image",visa_image.get(0),"permit_image");
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    private void openNextActivity() {
        if (lifo.size()==1) {

           /* if(req_type==1) {
                ((TextView) success_process_include.findViewById(R.id.msg)).setText(getString(R.string.please_deliever_departure));
            } else if(req_type==2){
                ((TextView) success_process_include.findViewById(R.id.msg)).setText(getString(R.string.please_deliever_arrival));
            } else if(req_type==3){
                ((TextView) success_process_include.findViewById(R.id.msg)).setText(getString(R.string.please_deliever_medical));
            }*/
        }
        if(lifo.size()!=0){
            before = findViewById((Integer) lifo.scndpeek());
            after = null;
            if(before.getId()==R.id.other_form_include){
                if(validateForm()) {
                    sendReq();
                }
                else{
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.fill_error),Toast.LENGTH_LONG).show();
                    return;
                }
            }
            else if(before.getId()==R.id.payment_include ){

                setPayment();
                if (paymentMethod == 2) {
                    Intent online_payment = new Intent(getApplicationContext(), online_payment.class);
                    shared.edit().putString("price", String.valueOf(pickedPrice)).apply();
                    startActivityForResult(online_payment, 202);
                }
                else {
                    after = findViewById((Integer) lifo.pop());
                }
            }
            else if(before.getId()==R.id.success_process_include){

            }
            else{
                after =  findViewById((Integer) lifo.pop());
                animateLayout(before, after);
            }
           // animateLayout(before,after);
        }
    }
    public void onBackPressed() {
        if(main){
            finish();
        }
        else if(last){
            finish();
        }
        else{
            findViewById(R.id.next_linear).setVisibility(View.VISIBLE);
            View after = findViewById((Integer) lifo.scndpop());
            View before = findViewById((Integer) lifo.scndpeek());
            before.setVisibility(View.VISIBLE);
            after.setVisibility(View.GONE);
            before.setAlpha(1f);
        }
        if (lifo.scndsize()==1){
            main=true;
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.title_rel).setVisibility(View.GONE);
                            findViewById(R.id.next_linear).setVisibility(View.GONE);
                            findViewById(R.id.main_traveller).setBackground(getResources().getDrawable(R.drawable.lastbg));
                            last = true;
                        }
                    });

                }



            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        before.animate().alpha(0f).setDuration(700).setListener(animatorListener);
        hideLoading();
    }
    private void changeStatusPost(boolean hide,int text){
        TextView status=findViewById(R.id.post_status);
        if(hide){
            status.setVisibility(View.INVISIBLE);
        }
        else status.setVisibility(View.VISIBLE);

        status.setText(getResources().getString(text));
    }
    public void showLoading(){
        final View main=findViewById(R.id.layout);
        final View loading=findViewById(R.id.loading);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                main.setClickable(false);
                loading.setVisibility(View.VISIBLE);
                main.setVisibility(View.GONE);
            }
        });
    }
    public void hideLoading(){
        final View main=findViewById(R.id.layout);
        final View loading=findViewById(R.id.loading);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                main.setClickable(true);
                loading.setVisibility(View.GONE);
                main.setVisibility(View.VISIBLE);
            }
        });
    }
    private void changeTotal(String price) {
        ((TextView) payment_include.findViewById(R.id.total)).setText(price + getResources().getString(R.string.SDG));
    }
    private void changeMsg(int type) {
        TextView msg = success_inlcude.findViewById(R.id.msg);

        switch (type) {
            case 1: {
                msg.setText(getResources().getString(R.string.exit_visa_msg));
            }
            break;
            case 2: {
                msg.setText(getResources().getString(R.string.yellow_favor_msg));
            }
            break;
            case 3: {
                msg.setText(getResources().getString(R.string.nat_card_msg));
            }
            break;
        }
    }
}
