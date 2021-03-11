package com.example.mohaned.hababak;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mohaned.hababak.Network.Iokihttp;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class password extends AppCompatActivity {
    SharedPreferences shared;
    private Iokihttp okhttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        init();
    }

    private void init() {
        shared = this.getSharedPreferences("com.example.mohaned.hababak", Context.MODE_PRIVATE);
        okhttp = new Iokihttp();
        int type = getIntent().getIntExtra("type", 1);
        if (type == 1) {
            opennewaccount();
        } else {
            openpassword();
        }
        findViewById(R.id.new_account_include).findViewById(R.id.new_user_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    verifyUserInfo();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.enter_password_include).findViewById(R.id.verify_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPassword();
            }
        });
    }

    public void animateLayout(final View before, final View after) {
        Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                System.out.println("SHOW " + after.getId());
                before.setVisibility(View.GONE);
                after.setVisibility(View.VISIBLE);
                //  finalAfter.animate().alpha(1f).setDuration(700);


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

    private void opennewaccount() {
        final View password = findViewById(R.id.enter_password_include);
        final View newaccount = findViewById(R.id.new_account_include);
        animateLayout(password, newaccount);
    }

    private void openpassword() {
        final View password = findViewById(R.id.enter_password_include);
        final View newaccount = findViewById(R.id.new_account_include);
        animateLayout(newaccount, password);
    }

    private void sendUserInfo(final String username, String email, String password) throws NoSuchAlgorithmException {
        JSONObject json = new JSONObject();
        JSONObject subJSON = new JSONObject();

        try {
            subJSON.put("id", shared.getString("user_id", ""));
            subJSON.put("username", username);
            subJSON.put("email", email);
            subJSON.put("password", password);
            json.put("method", "signup");
            json.put("data", subJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (okhttp.isNetworkConnected(getApplicationContext())) {
            showLoading();
            okhttp.post(getString(R.string.lara_url) + "signup", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    hideLoading();
                    System.out.println("FAIL");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    hideLoading();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();

                        try {
                            JSONObject resJSON = new JSONObject(responseStr);

                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {

                                if (Integer.parseInt(resJSON.get("code").toString()) == 1) {
                                    shared.edit().putBoolean("login", true).apply();
                                    shared.edit().putString("username", username).apply();
                                    openHome();
                                } else if (Integer.parseInt(resJSON.get("code").toString()) == 23000) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), getString(R.string.diplicate_email), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
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

    private void openHome() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void verifyUserInfo() throws NoSuchAlgorithmException {
        View newAccount = findViewById(R.id.new_account_include);
        MaterialEditText username = (MaterialEditText) newAccount.findViewById(R.id.username);
        MaterialEditText email = (MaterialEditText) newAccount.findViewById(R.id.email);
        MaterialEditText password = (MaterialEditText) newAccount.findViewById(R.id.password);
        if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_error), Toast.LENGTH_SHORT).show();
        } else {
            sendUserInfo(username.getText().toString(), email.getText().toString(), encrypt(password.getText().toString()));
        }

    }

    private String encrypt(String password) throws NoSuchAlgorithmException {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(password.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void verifyPassword() {
        View password_include = findViewById(R.id.enter_password_include);
        EditText password = (EditText) password_include.findViewById(R.id.password);
        if (password.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_error), Toast.LENGTH_SHORT).show();
        } else {
            JSONObject json = new JSONObject();
            JSONObject subJSON = new JSONObject();
            try {
                System.out.println(password.getText().toString()+"Encrypt signup"+ encrypt(password.getText().toString()));

                subJSON.put("password", encrypt(password.getText().toString()));
                subJSON.put("id", shared.getString("user_id", ""));
                json.put("method", "login");
                json.put("data", subJSON);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            if (okhttp.isNetworkConnected(getApplicationContext())) {
                showLoading();
                okhttp.post(getString(R.string.lara_url) + "login", json.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        hideLoading();
                        System.out.println("FAIL");
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

                                    if (Integer.parseInt(subresJSON.get("status").toString()) == 1 && Integer.parseInt(resJSON.get("code").toString()) == 0) {
                                        shared.edit().putBoolean("login", true).apply();
                                        shared.edit().putString("username", subresJSON.getString("username")).apply();
                                        shared.edit().putString("email", subresJSON.getString("email")).apply();

                                        //shared.edit().putString("user_id", subresJSON.getString("id")).apply();
                                        openHome();
                                    } else if (Integer.parseInt(resJSON.get("code").toString()) == 2) {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), getString(R.string.wrong_password), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("inside run");
                findViewById(R.id.layout).setVisibility(View.VISIBLE);
                findViewById(R.id.loading).setVisibility(View.GONE);
                System.out.println("after inside run");

            }

        });
        System.out.println("after run");
    }
}
