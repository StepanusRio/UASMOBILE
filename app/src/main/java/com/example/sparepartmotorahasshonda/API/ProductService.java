package com.example.sparepartmotorahasshonda.API;

import com.example.sparepartmotorahasshonda.Model.Product;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ProductService {
    @GET("/api")
    Call<Product[]> getProducts();
}
