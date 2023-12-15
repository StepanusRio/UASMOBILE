package com.example.sparepartmotorahasshonda.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://192.168.1.12:3000/";
    private static final String BASE_URL_USER = "http://192.168.1.12/APIUTS";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        };
        return retrofit;
    }
    public static Retrofit getUser(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL_USER)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        };
        return retrofit;
    }
}
