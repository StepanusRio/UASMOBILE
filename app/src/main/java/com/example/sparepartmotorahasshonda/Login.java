package com.example.sparepartmotorahasshonda;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sparepartmotorahasshonda.API.RetrofitClient;
import com.example.sparepartmotorahasshonda.API.UserService;
import com.example.sparepartmotorahasshonda.ui.home.HomeFragment;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {
    TextInputEditText EtEmail,EtPassword;
    Button BtnLogin;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        BtnLogin = (Button) findViewById(R.id.btnLogin);
        EtEmail = (TextInputEditText) findViewById(R.id.EtEmail);
        EtPassword = (TextInputEditText) findViewById(R.id.EtPassword);
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(view.getContext());
                pd.setTitle("Proses Login. . .");
                pd.setMessage("Tunggu sebentar");
                pd.setCancelable(true);
                pd.setIndeterminate(true);
                pd.show();
                progressLogin(EtEmail.getText().toString(),EtPassword.getText().toString());
            }
        });
    }

    void progressLogin (String temail,String tpassword){
        final String Base_URL = "http://192.168.33.174/APIUTS/";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Base_URL).addConverterFactory(GsonConverterFactory.create()).build();
        UserService api = retrofit.create(UserService.class);
        if (!isEmailValid(EtEmail.getText().toString())) {
            AlertDialog.Builder msg = new AlertDialog.Builder(Login.this);

            msg.setMessage("Email tidak Valid").setNegativeButton("Retry",null)
                    .create().show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    msg.create().dismiss();
                }
            }, 3000);
            return;
        }
        api.login(temail,tpassword).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    if (json.getString("status").equals("1")) {
                        Toast.makeText(Login.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder msg = new AlertDialog.Builder(Login.this);
                        // Save To Shared Preferences
                        SharedPreferences sharedPreferences = getSharedPreferences("LoginPreferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", json.getJSONObject("data").getString("username"));
                        editor.apply();
                        finish();
                        Toast.makeText(Login.this, "Username saved: " + json.getJSONObject("data").getString("username"), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }else {
                        AlertDialog.Builder msg = new AlertDialog.Builder(Login.this);
                        msg.setMessage("Login gagal")
                                .setNegativeButton("Retry",null).create().show();
                        EtEmail.setText("");
                        EtPassword.setText("");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                msg.create().dismiss();
                            }
                        }, 3000);
                    }
                    pd.dismiss();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("Info Login", "onFailure: Login Gagal"+t.toString());
                pd.dismiss();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder msg = new AlertDialog.Builder(Login.this);
                        msg.setMessage("Login gagal").setNegativeButton("Retry", null).create().show();
                    }
                }, 3000);
            }
        });
    }

    public boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression ="^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            isValid=true;
        }
        return  isValid;
    }
}