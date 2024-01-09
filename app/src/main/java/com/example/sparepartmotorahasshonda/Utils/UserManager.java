package com.example.sparepartmotorahasshonda.Utils;

import android.content.Context;

import com.example.sparepartmotorahasshonda.API.RetrofitAPI;
import com.example.sparepartmotorahasshonda.API.UserService;
import com.example.sparepartmotorahasshonda.Model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserManager {
    public static void CheckUserLogin(String username, Context context, UserLoginListener listener){
        UserService api = RetrofitAPI.API().create(UserService.class);
        Call<ResponseBody> call = api.getUserLogin(username);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        JSONArray resultArray = jsonResponse.getJSONArray("result");

                        if (resultArray.length() > 0) {
                            JSONObject userObject = resultArray.getJSONObject(0);
                            // Extract user data
                            User user = new User();
                            user.setUsername(userObject.optString("username"));
                            user.setPassword(userObject.optString("password"));
                            user.setStatus(userObject.optString("status"));
                            user.setAlamat(userObject.optString("alamat"));
                            user.setKota(userObject.optString("kota"));
                            user.setProvinsi(userObject.optString("provinsi"));
                            user.setKode_pos(userObject.optString("kode_pos"));
                            user.setEmail(userObject.optString("email"));
                            user.setImage(userObject.optString("image"));

                            // Inform listener about successful login
                            listener.onUserLoginSuccess(user,context);
                        }
                    } else {
                        // Inform listener about login failure
                        listener.onUserLoginFailure("Login failed");
                    }
                } catch (JSONException | IOException e) {
                    // Inform listener about login failure due to exception
                    listener.onUserLoginFailure("Exception: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    public interface UserLoginListener{
        void onUserLoginSuccess(User user,Context context);
        void onUserLoginFailure(String errorMessage);
    }
}
