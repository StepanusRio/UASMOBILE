package com.example.sparepartmotorahasshonda;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sparepartmotorahasshonda.API.CheckoutService;
import com.example.sparepartmotorahasshonda.API.ImageUpload;
import com.example.sparepartmotorahasshonda.API.RetrofitAPI;
import com.example.sparepartmotorahasshonda.Adapter.OrderHistoryAdapter;
import com.example.sparepartmotorahasshonda.Model.Checkout;
import com.example.sparepartmotorahasshonda.Model.ResponseUploadImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistory extends AppCompatActivity {
    RecyclerView recyclerView;
    private OrderHistoryAdapter historyAdapter;
    private int clickedItemPosition = -1;
    String idOrders;
    private final List<Checkout> orderHistory = new ArrayList<>();
    private static final String[] PERMISSIONS_STORAGE={
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    int REQUEST_GALLERY=100;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        verifyStoragePermission(OrderHistory.this);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY){
                Uri uri = data.getData();
                File file = new File(uri.getPath());
                historyAdapter.setSelectedImageUri(uri);
                if (clickedItemPosition != -1) {
                    Checkout selectedOrder = orderHistory.get(clickedItemPosition);
                    selectedOrder.setImageProofURI(uri);
                    Toast.makeText(this, "Uri: "+uri, Toast.LENGTH_SHORT).show();
                    historyAdapter.setSelectedImageUri(uri); // update adapter dengan URI gambar yang dipilih
                    historyAdapter.notifyItemChanged(clickedItemPosition); // update tampilan item yang sesuai
                }
            }
        }
    }


    private void verifyStoragePermission(OrderHistory orderHistory) {
        int permission = ActivityCompat.checkSelfPermission(orderHistory, Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.i("info Permission", "verifyStoragePermission: "+permission+" "+ PackageManager.PERMISSION_GRANTED);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("INFO FAILURE PERMISSION", "verifyStoragePermission: Tidak mendapatkan Ijin");
            ActivityCompat.requestPermissions(orderHistory,PERMISSIONS_STORAGE,1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        recyclerView = (RecyclerView) findViewById(R.id.rvOrderHistory);
        historyAdapter = new OrderHistoryAdapter(getApplicationContext(),orderHistory,this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(historyAdapter);
        SharedPreferences loginPreferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        String username = loginPreferences.getString("username", null); // "" is the default value if "username" doesn't exist
        fetchHistory(username);
    }
    private void fetchHistory(String username){
        CheckoutService api = RetrofitAPI.API().create(CheckoutService.class);
        Call<Checkout[]> call = api.HistoryOrder(username);
        call.enqueue(new Callback<Checkout[]>() {
            @Override
            public void onResponse(Call<Checkout[]> call, Response<Checkout[]> response) {
                Checkout[] checkoutsHistory = response.body();
                orderHistory.clear();
                orderHistory.addAll(Arrays.asList(checkoutsHistory));
                if (historyAdapter == null){
                    historyAdapter = new OrderHistoryAdapter(getApplicationContext(),orderHistory,OrderHistory.this);
                    recyclerView.setAdapter(historyAdapter);
                }else{
                    historyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Checkout[]> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "GAGAL: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setClickedItemPosition(int position) {
        clickedItemPosition = position;
    }
}