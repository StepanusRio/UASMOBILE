package com.example.sparepartmotorahasshonda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sparepartmotorahasshonda.API.RetrofitAPI;
import com.example.sparepartmotorahasshonda.API.UserService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends AppCompatActivity {
    EditText etNewPassword;
    Button changeNow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        etNewPassword = (EditText) findViewById(R.id.etNewPassword);
        changeNow = (Button) findViewById(R.id.changeNow);
        Intent intent =getIntent();
        String username = intent.getStringExtra("username");
        changeNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeP(username,etNewPassword.getText().toString());
            }
        });
    }

    private void changeP(String username, String password) {
        UserService api = RetrofitAPI.API().create(UserService.class);
        Call<ResponseBody> call = api.changePassword(username,password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}