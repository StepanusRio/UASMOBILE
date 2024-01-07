package com.example.sparepartmotorahasshonda.API;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CostService {
    @FormUrlEncoded
    @POST("cost")
    @Headers({
            "content-type: application/x-www-form-urlencoded",
            "key:937e09043e17709df5919ffa659d2d45"
    })
    Call<ResponseBody> cekongkir(
            @Field("origin") String origin,
            @Field("destination") String destination,
            @Field("weight") int weight,
            @Field("courier") String jne
            );
    @GET("get_province.php")
    Call<ResponseBody> get_provinsi();
    @GET("get_city.php")
    Call<ResponseBody> get_kota(@Query("province_id") int province_id);
}
