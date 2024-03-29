package com.example.sparepartmotorahasshonda.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitAPI {
    public static final String BASE_URL="http://192.168.32.230/APIUTS/";
    private static Retrofit retrofit = null;
    public static Retrofit API(){
        if (retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
