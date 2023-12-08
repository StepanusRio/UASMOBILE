package com.example.sparepartmotorahasshonda.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparepartmotorahasshonda.API.ProductService;
import com.example.sparepartmotorahasshonda.API.RetrofitClient;
import com.example.sparepartmotorahasshonda.Adapter.ProductAdapter;
import com.example.sparepartmotorahasshonda.Model.Order;
import com.example.sparepartmotorahasshonda.Model.Product;
import com.example.sparepartmotorahasshonda.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private List<Product> listProduct = new ArrayList<>();
    private  List<Order> orderProducts = new ArrayList<>();
    private ProductAdapter viewAdapter;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        recyclerView = view.findViewById(R.id.rvProducts);
        viewAdapter = new ProductAdapter(getContext(),this,listProduct);
        SearchView searchView = view.findViewById(R.id.searchView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(viewAdapter);
        fetchProduct();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                viewAdapter.filter(s);
                return false;
            }
        });
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
                        viewAdapter = new ProductAdapter(getContext(), HomeFragment.this, listProduct);
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
    public void addToCart(Product listProduct) {
        sharedPreferences = getActivity().getSharedPreferences("OrderPreference", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonText = sharedPreferences.getString("orderProduct", null);

        if (jsonText != null) {
            Order[] orders = gson.fromJson(jsonText, Order[].class);
            orderProducts.addAll(Arrays.asList(orders));
        }

        boolean isExisting = false;
        for (Order order : orderProducts) {
            if (order.getId() != null && listProduct.getId() != null &&
                    order.getId().equals(listProduct.getId())) {
                int updatedQty = order.getQty() + 1;
                int updatedTotal = order.getTotalOrder() + listProduct.getHargaJual();
                if (listProduct.getStock() >= updatedQty) {
                    order.setQty(updatedQty);
                    order.setTotalOrder(updatedTotal);
                    isExisting = true;
                    break;
                }
            }
        }

        if (!isExisting) {
            Order newOrder = new Order();
            newOrder.setId(listProduct.getId());
            newOrder.setName(listProduct.getName());
            newOrder.setHargaJual(listProduct.getHargaJual());
            newOrder.setImages(listProduct.getImages());
            newOrder.setStock(listProduct.getStock());
            newOrder.setQty(1);
            newOrder.setTotalOrder(listProduct.getHargaJual());
            orderProducts.add(newOrder);
        }

        String updatedJsonText = gson.toJson(orderProducts);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("orderProduct", updatedJsonText);
        editor.apply();

        Toast.makeText(getContext(), "Success add to cart", Toast.LENGTH_SHORT).show();
    }
}