package com.example.sparepartmotorahasshonda.ui.product;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparepartmotorahasshonda.API.ProductService;
import com.example.sparepartmotorahasshonda.API.RetrofitClient;
import com.example.sparepartmotorahasshonda.Adapter.AllProductAdapter;
import com.example.sparepartmotorahasshonda.Adapter.ProductAdapter;
import com.example.sparepartmotorahasshonda.Login;
import com.example.sparepartmotorahasshonda.Model.Order;
import com.example.sparepartmotorahasshonda.Model.Product;
import com.example.sparepartmotorahasshonda.R;
import com.example.sparepartmotorahasshonda.ui.home.HomeFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductFragment extends Fragment {
    RecyclerView recyclerView;
    private AllProductAdapter viewAdapter;
    private final List<Product> listProduct = new ArrayList<>();
    private final List<Order> orderProducts = new ArrayList<>();
    SharedPreferences sharedPreferences;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product,container,false);
        recyclerView = view.findViewById(R.id.rvAllProducts);
        viewAdapter = new AllProductAdapter(getContext(),this,listProduct);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(viewAdapter);
        fetchProduct();
        return view;
    }

    private void fetchProduct(){
        ProductService productService = RetrofitClient.getClient().create(ProductService.class);
        Call<Product[]> call = productService.getProducts();
        call.enqueue(new Callback<Product[]>() {
            @Override
            public void onResponse(Call<Product[]> call, Response<Product[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product[] products = response.body();
                    listProduct.clear();
                    listProduct.addAll(Arrays.asList(products));
                    if (viewAdapter ==null){
                        viewAdapter = new AllProductAdapter(getContext(), ProductFragment.this, listProduct);
                        recyclerView.setAdapter(viewAdapter);
                    } else {
                        viewAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getContext(), "GAGAL: Mengambil Data", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Product[]> call, Throwable t) {
                Toast.makeText(getContext(), "GAGAL: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addToCart(Product product) {
        SharedPreferences loginPreferences = getActivity().getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        boolean isLoggedIn = loginPreferences.contains("username");
        if (!isLoggedIn){
            Intent loginIntent = new Intent(getContext(), Login.class);
            startActivity(loginIntent);
            Toast.makeText(getContext(), "PLEASE LOGIN FIRST", Toast.LENGTH_SHORT).show();
        }else{
            sharedPreferences = getActivity().getSharedPreferences("OrderPreference", Context.MODE_PRIVATE);
            sharedPreferences.contains("orderProduct");
            if (sharedPreferences.contains("orderProduct")){
                Gson gson = new Gson();
                String jsonText = sharedPreferences.getString("orderProduct", null);
                Order[] products = gson.fromJson(jsonText, Order[].class);
                orderProducts.clear();
                for (Order order : products) {
                    orderProducts.add(order);
                }
            }
            boolean isHasItem = false;
            for (Order order:orderProducts){
                if (order.getId().equals(product.getId())){
                    int Items = order.getQty()+1;
                    int Total = order.getTotalOrder()+product.getHargaJual();
                    if (product.getStock()>=Items){
                        order.setQty(Items);
                        order.setTotalOrder(Total);
                    }
                    isHasItem = true;
                    break;
                }
            }
            if (!isHasItem){
                orderProducts.add(new Order(
                        product.getId(),
                        product.getName(),
                        product.getHargaJual(),
                        product.getImages(),
                        product.getKategori(),
                        product.getStock(),
                        1,
                        product.getHargaJual()
                ));
            }
            Gson gson = new Gson();
            String jsonText = gson.toJson(orderProducts);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("orderProduct",jsonText);
            editor.apply();
            Toast.makeText(getContext(), "Success add to cart", Toast.LENGTH_SHORT).show();
        }
    }
}
