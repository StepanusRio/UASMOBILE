package com.example.sparepartmotorahasshonda.API;

import com.example.sparepartmotorahasshonda.Model.ResponseUploadImage;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ImageUpload{
    @Multipart
    @POST("upload_profile_image.php")
    Call<ResponseUploadImage> uploadImageProfile(@Part MultipartBody.Part image);

}