package com.example.sparepartmotorahasshonda.ui.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparepartmotorahasshonda.Adapter.OrderAdapter;
import com.example.sparepartmotorahasshonda.Model.Order;
import com.example.sparepartmotorahasshonda.R;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class OrderFragment extends Fragment {
    private RecyclerView rvOrder;
    private TextView tvSubTotal,tvTotalHarga;
    private final List<Order> orderProducts = new ArrayList<>();
    private OrderAdapter orderAdapter;
    private SharedPreferences sharedPreferences;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order,container,false);
        setUp(view);
        loadOrder();
        return view;
    }

    private void loadOrder() {
        rvOrder.setLayoutManager(new LinearLayoutManager(getActivity()));
        sharedPreferences = requireActivity().getSharedPreferences("OrderPreference", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("orderProduct")){
            if (!orderProducts.isEmpty()){
                orderProducts.clear();
            }
            Gson gson = new Gson();
            String jsonText = sharedPreferences.getString("orderProduct", null);
            Integer total = 0;
            Order[] products = gson.fromJson(jsonText,Order[].class);
            for (Order order:products){
                orderProducts.add(order);
                total += order.getTotalOrder();
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                formatRupiah.setCurrency(Currency.getInstance("IDR"));
                tvSubTotal.setText(formatRupiah.format(total));
            }
            orderAdapter = new OrderAdapter(this, getActivity(),orderProducts);
            rvOrder.setAdapter(orderAdapter);
        }
    }

    private void setUp(View view){
        rvOrder = view.findViewById(R.id.rvOrderList);
        tvSubTotal = view.findViewById(R.id.tvSubTotalHarga);
        tvTotalHarga=view.findViewById(R.id.tvTotalHarga);
    }
    @SuppressLint("SetTextI18n")
    public void deleteProduct(int position) {
        if (sharedPreferences.contains("orderProduct")){
            if (position >= 0 && position < orderProducts.size()){
                orderProducts.remove(position);
                orderAdapter.notifyItemRemoved(position);
                orderAdapter.notifyItemRangeChanged(position,orderProducts.size());
                Gson gson = new Gson();
                String updateOrderList = gson.toJson(orderProducts);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("orderProduct",updateOrderList);
                editor.apply();
                Integer Ordertotal = 0;
                Order[] products = gson.fromJson(updateOrderList,Order[].class);
                for (Order order:products){
                    Ordertotal += order.getTotalOrder();
                }
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                formatRupiah.setCurrency(Currency.getInstance("IDR"));
                tvSubTotal.setText(formatRupiah.format(Ordertotal));
            }
        }
    }
}