package com.example.sparepartmotorahasshonda.API;

import com.example.sparepartmotorahasshonda.Model.ResponseUpdateUser;
import com.example.sparepartmotorahasshonda.Model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserService {
    @FormUrlEncoded
    @POST("auth/login.php")
    Call<ResponseBody> login(@Field("email") String email,@Field("password") String password);

    @GET("getuser.php")
    Call<ResponseBody> getUserLogin(@Query("username") String username);

    @FormUrlEncoded
    @POST("profile_update.php")
    Call<ResponseUpdateUser> updateUserProfile(@Field("username") String username,
                                               @Field("alamat")String alamat,
                                               @Field("email") String email,
                                               @Field("provinsi") String provinsi,
                                               @Field("kota") String kota,
                                               @Field("kode_pos") String kode_pos,
                                               @Field("image") String image
                                               );
}
