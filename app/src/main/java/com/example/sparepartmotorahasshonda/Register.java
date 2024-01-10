package com.example.sparepartmotorahasshonda;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.sparepartmotorahasshonda.API.RetrofitAPI;
import com.example.sparepartmotorahasshonda.API.UserService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    EditText etUsername,etEmail,etPassword;
    Button register;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        register = (Button) findViewById(R.id.btnProcessReg);
        back=(ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processReg(etEmail.getText().toString(),etUsername.getText().toString(),etPassword.getText().toString());
            }
        });

    }

    public boolean isEmailValid(String email){
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

    private void processReg(String email, String username, String password) {
        UserService api = RetrofitAPI.API().create(UserService.class);
        if (!isEmailValid(etEmail.getText().toString())) {
            AlertDialog.Builder msg = new AlertDialog.Builder(this);

            msg.setMessage("Email tidak Valid").setNegativeButton("Retry",null)
                    .create().show();
            return;
        }
        api.register(username,email,password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    if (json.getString("status").toString().equals("1")){
                        if (json.getString("result").toString().equals("1")){
                            AlertDialog.Builder msg = new AlertDialog.Builder(Register.this);

                            msg.setMessage("Register berhasil")
                                    .setPositiveButton("Ok",null).create().show();
                            Intent intent = new Intent(Register.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            AlertDialog.Builder msg = new AlertDialog.Builder(Register.this);

                            msg.setMessage("Simpan Gagal").setNegativeButton("Retry",null)
                                    .create().show();
                            etUsername.setText("");
                            etPassword.setText("");
                            etEmail.setText("");
                            etUsername.setFocusable(true);
                        }
                    }else{
                        AlertDialog.Builder msg = new AlertDialog.Builder(Register.this);

                        msg.setMessage("User sudah terdaftar").setNegativeButton("Retry",null)
                                .create().show();
                    }
                }catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}