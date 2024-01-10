package com.example.sparepartmotorahasshonda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sparepartmotorahasshonda.API.ProductService;
import com.example.sparepartmotorahasshonda.API.RetrofitClient;
import com.example.sparepartmotorahasshonda.Model.Order;
import com.example.sparepartmotorahasshonda.Model.Product;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailProduct extends AppCompatActivity {
    ImageView ProductImage;
    TextView ProductName,ProductKategori,ProductStok,ProductPrice;
    Button addToCart;
    SharedPreferences sharedPreferences;
    private List<Order> orderProducts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);
        // Initialize Id
        ProductImage = (ImageView) findViewById(R.id.ProductImage);
        ProductName = (TextView) findViewById(R.id.ProductName);
        ProductKategori = (TextView) findViewById(R.id.ProductKategori);
        ProductStok = (TextView) findViewById(R.id.ProductStok);
        ProductPrice = (TextView) findViewById(R.id.ProductPrice);
        addToCart = (Button) findViewById(R.id.addToCart);
        // Retrieve Id From Fragment
        Intent intent =getIntent();
        String sparepartId = intent.getStringExtra("sparepartId");
        fetchData(sparepartId);
    }

    private void fetchData(String sparepartId) {
        ProductService api = RetrofitClient.getClient().create(ProductService.class);
        Call<Product> call = api.getProduct(sparepartId);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                Product product = response.body();
                ProductName.setText(product.getName());
                ProductKategori.setText(product.getKategori().getLabel());
                ProductStok.setText(String.valueOf(product.getStock()));
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                formatRupiah.setCurrency(Currency.getInstance("IDR"));
                ProductPrice.setText(formatRupiah.format(product.getHargaJual()));
                if (product.getImages() != null && product.getImages().length > 0) {
                    String imageUrl = product.getImages()[0].getUrl(); // Assuming you want to load the first image
                    Glide.with(DetailProduct.this)
                            .load(imageUrl)
                            .into(ProductImage);
                }
                addToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addToCart(product);
                    }
                });
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {

            }
        });
    }

    public void addToCart(Product product) {
        SharedPreferences loginPreferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        boolean isLoggedIn = loginPreferences.contains("username");
        if (!isLoggedIn){
            Intent loginIntent = new Intent(this,Login.class);
            startActivity(loginIntent);
            Toast.makeText(this, "PLEASE LOGIN FIRST", Toast.LENGTH_SHORT).show();
        }else{
            sharedPreferences = this.getSharedPreferences("OrderPreference", Context.MODE_PRIVATE);
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
            Toast.makeText(this, "Success add to cart", Toast.LENGTH_SHORT).show();
        }
    }
}