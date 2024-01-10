package com.example.sparepartmotorahasshonda.API;

import com.example.sparepartmotorahasshonda.Model.Product;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductService {
    @GET("/api")
    Call<Product[]> getProducts();

    @GET("api/sparepart/{sparepartId}")
    Call<Product> getProduct(@Path("sparepartId") String sparepartId);
}
