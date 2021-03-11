package com.example.mohaned.hababak;

import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohaned.hababak.Network.Iokihttp;
import com.goodiebag.pinview.Pinview;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Login extends AppCompatActivity {



    protected Button mBtnLink;
    private boolean main=true;
    private Iokihttp okhttp;
    private Button verify,verify_pin;
    SharedPreferences shared;
    boolean doubleBackToExitPressedOnce=false;
    int exist = 1;
    CountryCodePicker ccp;
    View phone_include;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        okhttp=new Iokihttp();
        //avi=findViewById(R.id.avi);
        verify=findViewById(R.id.btn_send);
        verify_pin=findViewById(R.id.verify_pin);
        phone_include=findViewById(R.id.phoneLayout);
        verify_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify_pin(view);
            }
        });
        phone_include.findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send();
            }
        });
        ccp = phone_include.findViewById(R.id.ccp);
        shared = this.getSharedPreferences("com.example.mohaned.hababak", Context.MODE_PRIVATE);
    }
   /* public void verify_number(){
        String token = FirebaseInstanceId.getInstance().getToken();
    }*/


    protected void send() {
        EditText mPhoneEdit=phone_include.findViewById(R.id.phone);
        String[] phone;
        phone = validate();
        if (phone == null) {
            mPhoneEdit.requestFocus();
            mPhoneEdit.setError(getString(R.string.label_error_incorrect_phone));
            return;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        final String[] finalPhone = phone;
        builder.setMessage(getString(R.string.insure_dialog)+"\n"+phone[0]+" "+phone[1]+"\n"+getString(R.string.is_this_ok))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showLoading();

                        JSONObject json=new JSONObject();
                        JSONObject subJSON=new JSONObject();
                        try {
                            System.out.println("country code number before:"+finalPhone[0]);
                            finalPhone[0]=(finalPhone[0].replace("+","")).trim();
                            subJSON.put("country_code", finalPhone[0]);
                            subJSON.put("phone",finalPhone[1]);
                            json.put("method","sendPin");
                            json.put("data",subJSON);
                            System.out.println(json.toString() + "LLLLLLLLLLLLLLLLLL");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(okhttp.isNetworkConnected(getApplicationContext())) {
                            okhttp.post(getString(R.string.lara_url) + "sendPin", json.toString(), new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    System.out.println("FAIL");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    hideLoading();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    hideLoading();
                                    if (response.isSuccessful()) {
                                        String responseStr = response.body().string();
                                        try {


                                            JSONObject resJSON = new JSONObject(responseStr);

                                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                                final JSONObject subresJSON = new JSONObject(resJSON.getString("data"));
                                                exist = subresJSON.getInt("exist");
                                                shared.edit().putString("user_id", subresJSON.getString("id")).apply();
                                                shared.edit().putString("phone_key", finalPhone[0]).apply();
                                                shared.edit().putString("phone", finalPhone[1]).apply();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            Toast.makeText(getApplicationContext(), subresJSON.getString("pin"), Toast.LENGTH_LONG).show();
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });

                                                openpinLayout();

                                            } else {
                                                hideLoading();
                                                runOnUiThread(new Runnable() {
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
                                        System.out.println("Response=" + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj" + response.toString());
                                    }
                                }

                            });
                        }

                    }
                })
                .setNegativeButton(R.string.edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();


    }


    protected String[] validate() {
       String []phone=new String[2];
       phone[0]=ccp.getSelectedCountryCode();
       phone[1]=((EditText)phone_include.findViewById(R.id.phone)).getText().toString();
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
    public void openpinLayout(){
        final View phoneLayout = findViewById(R.id.phoneLayout);
        final View otcLayout = findViewById(R.id.otcLayout);
        TextView enter_otp = findViewById(R.id.enter_otp);
        enter_otp.setText(getString(R.string.enter_verfication)+": "+shared.getString("phone_key","")+""+shared.getString("phone",""));
        animateLayout(phoneLayout,otcLayout);
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



            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        before.animate().alpha(0f).setDuration(700).setListener(animatorListener);
    }
    @Override
    public void onBackPressed() {
        if(main){
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finish();
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
        else{
            final View phoneLayout = findViewById(R.id.phoneLayout);
            final View otcLayout = findViewById(R.id.otcLayout);



            phoneLayout.setVisibility(View.VISIBLE);
            otcLayout.setVisibility(View.GONE);

            main=true;
            phoneLayout.setAlpha(1f);
        }
    }

    public void openPassword() {
        Intent intent = new Intent(getApplicationContext(), password.class);
        intent.putExtra("type", exist);
        startActivity(intent);
        // finish();
    }
    private void verify_pin(View view){
        String token = FirebaseInstanceId.getInstance().getToken();
        Pinview pin=findViewById(R.id.pinView);
        JSONObject json=new JSONObject();
        JSONObject subJSON=new JSONObject();
        if(pin.getValue().isEmpty()){
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.fill_error),Toast.LENGTH_LONG).show();
            return;
        }
        try {
            System.out.println("PIN="+pin.getValue());
            subJSON.put("id", shared.getString("user_id",""));
            subJSON.put("token",token);
            subJSON.put("pin",pin.getValue());
            subJSON.put("device",1);
            json.put("method","verifyPin");
            json.put("data",subJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        showLoading();
        if(okhttp.isNetworkConnected(getApplicationContext())) {
            okhttp.post(getString(R.string.lara_url) + "verifyPin", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("FAIL");
                    hideLoading();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    hideLoading();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();

                        try {
                            JSONObject resJSON = new JSONObject(responseStr);

                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                openPassword();
                            } else {
                                runOnUiThread(new Runnable() {
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
    public void showLoading(){
        final View main=findViewById(R.id.login_layout);
        final ProgressBar loading=findViewById(R.id.login_loading);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                main.setClickable(false);

                main.setVisibility(View.GONE);

                loading.setVisibility(View.VISIBLE);
            }
        });
    }
    public void hideLoading(){
        final View main=findViewById(R.id.login_layout);
        final ProgressBar loading=findViewById(R.id.login_loading);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                main.setClickable(true);

                main.setVisibility(View.VISIBLE);

                loading.setVisibility(View.GONE);
            }
        });
    }
}
