package com.example.mohaned.hababak;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohaned.hababak.Network.Iokihttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Calendar;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import adil.dev.lib.materialnumberpicker.dialog.NumberPickerDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class online_payment extends AppCompatActivity {
    NumberPickerDialog EXM, EXY;
    String pickedPrice;
    View EXMView, EXYView;
    String cardNumber, ipin, EXMValue = "", EXYValue = "";
    SharedPreferences shared;
    Button pay;
    private Iokihttp okhttp;
    String uuidS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_payment);
        okhttp = new Iokihttp();
        shared = this.getSharedPreferences("com.example.mohaned.hababak", Context.MODE_PRIVATE);
        pickedPrice = shared.getString("price", "0.0");//getIntent().getExtras().getString("price");
        changeTotal();
        EXMView = findViewById(R.id.EXMonth);
        EXYView = findViewById(R.id.EXYear);
        EXMView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EXM.show();
            }
        });
        EXYView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EXY.show();
            }
        });
        pay = (Button) findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    try {
                        pay();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        setDialogs();
    }

    public void setDialogs() {
        EXM = new NumberPickerDialog(online_payment.this, 01, 12, new NumberPickerDialog.NumberPickerCallBack() {
            @Override
            public void onSelectingValue(int value) {
                ((TextView) EXMView).setText(String.valueOf(value));
                Toast.makeText(online_payment.this, "Selected Month " + value, Toast.LENGTH_SHORT).show();
                EXMValue = String.valueOf(value);
            }
        });

        EXY = new NumberPickerDialog(online_payment.this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.YEAR) + 15, new NumberPickerDialog.NumberPickerCallBack() {
            @Override
            public void onSelectingValue(int value) {
                ((TextView) EXYView).setText(String.valueOf(value));
                Toast.makeText(online_payment.this, "Selected Year " + value, Toast.LENGTH_SHORT).show();
                EXYValue = String.valueOf(value);
            }
        });
        //dialog.show();
        //  dialog.show();
    }

    private void changeTotal() {
        ((TextView) findViewById(R.id.priceAsk)).setText(getResources().getString(R.string.priceAsk) + " " + pickedPrice + getResources().getString(R.string.SDG));
    }

    private boolean validateForm() {
        boolean check = true;
        EditText card = (EditText) findViewById(R.id.cardNumber);
        EditText ipinV = (EditText) findViewById(R.id.ipin);
        if (card.getText().toString().isEmpty() || ipinV.getText().toString().isEmpty() || EXYValue.equals("") || EXMValue.equals("")) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_error), Toast.LENGTH_SHORT).show();
            check = false;
        } else if (card.getText().toString().length() < 16) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.card_number_error), Toast.LENGTH_SHORT).show();
            check = false;
        } else if (ipinV.getText().toString().length() < 4) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.ipin_error), Toast.LENGTH_SHORT).show();
            check = false;
        }
        return check;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void pay() throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        pay.setEnabled(false);
        pay.setClickable(false);
        pay.setVisibility(View.INVISIBLE);
        EditText card = (EditText) findViewById(R.id.cardNumber);
        EditText ipinV = (EditText) findViewById(R.id.ipin);
        cardNumber = card.getText().toString();
        ipin = ipinV.getText().toString();
        JSONObject json = new JSONObject();
        JSONObject subJSON = new JSONObject();
        try {
            json.put("PAN", cardNumber);
            json.put("IPIN", encrypt());
            json.put("expDate", (EXYValue.substring(2, 4)) + (EXMValue.length() == 1 ? "0" + EXMValue : EXMValue));
            json.put("tranAmount", pickedPrice);
            json.put("toCard", "5818588880475558");
            json.put("uuid", uuidS);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        if (okhttp.isNetworkConnected(getApplicationContext())) {
            okhttp.post(getResources().getString(R.string.payment_url) + "cardTransfer", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    System.out.println("FAIL");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pay.setEnabled(true);
                            pay.setClickable(true);
                            pay.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        System.out.println("Response=" + responseStr);
                        JSONObject resJSON = null;
                        try {
                            resJSON = new JSONObject(responseStr);
                            if (Integer.parseInt(resJSON.get("responseCode").toString()) == 0) {
                                final JSONObject finalResJSON = resJSON;

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            Toast.makeText(getApplicationContext(), finalResJSON.getString("responseMessage"), Toast.LENGTH_LONG).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        finishPay(1);
                                    }
                                });
                            } else {

                                final JSONObject finalResJSON1 = resJSON;
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            pay.setEnabled(true);
                                            pay.setClickable(true);
                                            pay.setVisibility(View.VISIBLE);
                                            Toast.makeText(getApplicationContext(), finalResJSON1.getString("responseMessage") + "    " + getString(R.string.try_later), Toast.LENGTH_LONG).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Response=" + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                    }
                }
            });
        }

    }

    private void finishPay(final int result) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setResult(result);
                finish();
            }
        }, 3000);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishPay(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String encrypt() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException, NoSuchProviderException {
        //com.eaio.uuid.UUID uuid = new com.eaio.uuid.UUID();
        uuidS = UUID.randomUUID().toString();
        System.out.println("UUID=" + uuidS);
        String cleraIpin = uuidS + ipin;
        String PK = shared.getString("PK", "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==");// ;
        System.out.println("KEY=" + PK);
        byte[] PKbyte = Base64.getDecoder().decode(PK);

        X509EncodedKeySpec s = new X509EncodedKeySpec(PKbyte);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key key = keyFactory.generatePublic(s);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encStr = cipher.doFinal(cleraIpin.getBytes());

        String encString = (new String(Base64.getEncoder().encode(encStr)));
        System.out.println(encString + "))))))))))))))))))))");
        return encString;
    }

}
