package com.example.sparepartmotorahasshonda.ui.order;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparepartmotorahasshonda.API.CheckoutService;
import com.example.sparepartmotorahasshonda.API.CostService;
import com.example.sparepartmotorahasshonda.API.RetrofitAPI;
import com.example.sparepartmotorahasshonda.Adapter.OrderAdapter;
import com.example.sparepartmotorahasshonda.Model.Checkout;
import com.example.sparepartmotorahasshonda.Model.Kota;
import com.example.sparepartmotorahasshonda.Model.Order;
import com.example.sparepartmotorahasshonda.Model.Provinsi;
import com.example.sparepartmotorahasshonda.Model.User;
import com.example.sparepartmotorahasshonda.R;
import com.example.sparepartmotorahasshonda.Utils.UserManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderFragment extends Fragment implements UserManager.UserLoginListener {
    private RecyclerView rvOrder;
    private TextView tvSubTotal,tvTotalHarga,tvDelivery;
    private EditText tvAddress;
    Button btnCheckOngkir,btnCheckout;
    Provinsi provinsi;
    Kota kota;
    Spinner spinprovinsi,spinkota;
    ArrayList<String> province_name = new ArrayList<>();
    ArrayList<String> city_name = new ArrayList<>();
    ArrayList<Integer> province_id = new ArrayList<>();
    ArrayList<Integer> city_id = new ArrayList<>();
    int id_kota_tujuan,Ongkir,subtotal;
    String KotaTujuan,ProvinsiTujuan;
    private final List<Order> orderProducts = new ArrayList<>();
    private OrderAdapter orderAdapter;
    private SharedPreferences sharedPreferences;
    RadioGroup radioGroup;
    RadioButton cod,transfer;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order,container,false);
        setUp(view);
        loadOrder();
        loadProvinsi();
        SharedPreferences loginPreferences = getActivity().getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        String username = loginPreferences.getString("username", null); // "" is the default value if "username" doesn't exist
        if (username!=null) {
            CheckUserLogin(username);
        }else{
            tvAddress.setText("");
        }
        spinprovinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                int kode=0;
                Log.i("[TOUCH SPIN PROV]", "onItemSelected: "+province_name.get(position)+" "+"Kode: "+province_id.get(position));
                loadKota(province_id.get(position));
                ProvinsiTujuan = province_name.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinkota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                id_kota_tujuan = city_id.get(position);
                KotaTujuan = city_name.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnCheckOngkir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckOngkir("399",""+id_kota_tujuan,1,"jne");
            }
        });
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Data Yang Dapat Kota Tujuan (Id), Provinsi Tujuan (Id), Total Price (Int), SubTotal (Int), Ongkir, Payment Methode
                String idOrder = generateOrderId();
                String address = tvAddress.getText().toString();
                // Checkout Date
                Calendar calendar = Calendar.getInstance();
                Date currentDate = calendar.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String checkoutDate = dateFormat.format(currentDate);
                // Payment Methode && Status
                String PaymentMethode ="COD";
                String StatusPayment = "Belum Dibayar";
                if (cod.isChecked()){
                    PaymentMethode="COD";
                    StatusPayment="Diproses";
                }
                if (transfer.isChecked()){
                    StatusPayment=StatusPayment;
                    PaymentMethode="TRANSFER";
                }
                int TotalHarga = calculatePrice();
                // Checkout Data to store in database
                CheckoutToDatabase(
                        idOrder,
                        username,
                        address,
                        KotaTujuan,
                        ProvinsiTujuan,
                        subtotal,
                        Ongkir,
                        TotalHarga,
                        PaymentMethode,
                        checkoutDate,
                        StatusPayment);
            }
        });
        return view;
    }

    private void CheckoutToDatabase(
            String idOrder,
            String username,
            String alamat,
            String kota,
            String provinsi,
            int subtotal,
            int ongkir,
            int totalharga,
            String paymentmethod,
            String tanggalcheckout,
            String statuspayment) {
        CheckoutService api = RetrofitAPI.API().create(CheckoutService.class);
        api.Checkout(idOrder,username,alamat,kota,provinsi,subtotal,ongkir,totalharga,paymentmethod,tanggalcheckout,statuspayment)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            Toast.makeText(getContext(), ""+json.getString("message").toString(), Toast.LENGTH_SHORT).show();
                            removeOrderProductSharedPreferences();
                        } catch (IOException |JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: "+t, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String generateOrderId() {
        SharedPreferences idPreferences = getActivity().getSharedPreferences("OrderIdPreferences", Context.MODE_PRIVATE);
        int transactionCount = idPreferences.getInt("transactionCount", 0);
        transactionCount++;

        SharedPreferences.Editor editor = idPreferences.edit();
        editor.putInt("transactionCount", transactionCount);
        editor.apply();

        return "TRX" + String.format("%03d", transactionCount);
    }
    // Check User Login
    public void CheckUserLogin(String username){
        UserManager.CheckUserLogin(username,getContext(), this);
    }
    public void onUserLoginSuccess(User user,Context context) {
        tvAddress.setText(user.getAlamat());
    }

    @Override
    public void onUserLoginFailure(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    // SetUp ID
    private void setUp(View view){
        rvOrder = view.findViewById(R.id.rvOrderList);
        tvSubTotal = view.findViewById(R.id.tvSubTotalHarga);
        tvTotalHarga=view.findViewById(R.id.tvTotalHarga);
        spinkota = view.findViewById(R.id.spinKota);
        spinprovinsi = view.findViewById(R.id.spinProvinsi);
        tvDelivery=view.findViewById(R.id.tvDelivery);
        btnCheckOngkir=view.findViewById(R.id.btnCheckOngkir);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        tvAddress = view.findViewById(R.id.tvAddress);
        radioGroup = view.findViewById(R.id.rdG);
        cod =view.findViewById(R.id.rdCOD);
        transfer=view.findViewById(R.id.rdTransfer);
    }
    // Load Province
    private void loadProvinsi(){
        CostService api = RetrofitAPI.API().create(CostService.class);
        Call<ResponseBody> call = api.get_provinsi();
        Log.i("[INFO LOAD PROVINSI]", "loadProvinsi: CEK LOAD");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    provinsi = new Provinsi();
                    for(int i=0;i<json.getJSONArray("result").length();i++){
                        provinsi.province_id=Integer.parseInt(json.getJSONArray("result").getJSONObject(i).get("province_id").toString());
                        provinsi.province_name=json.getJSONArray("result").getJSONObject(i).get("province_name").toString();
                        province_name.add(provinsi.getProvince_name());
                        province_id.add(provinsi.getProvince_id());
                    }
                    Log.i("[CHECK RESPONSE]", "onResponse: "+json.get("result").toString());
                    Log.i("[Info Length]", "onResponse: "+json.getJSONArray("result").length());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,province_name);
                    spinprovinsi.setAdapter(adapter);
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("[FAILURE]", "onFailure: "+t.getMessage());
            }
        });
    };
    // Load City
    private void loadKota(int province_id){
        CostService api = RetrofitAPI.API().create(CostService.class);
        Call<ResponseBody> call = api.get_kota(province_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    kota = new Kota();
                    city_id.clear();
                    city_name.clear();
                    for (int i=0;i<json.getJSONArray("result").length();i++){
                        kota.city_id=Integer.parseInt(json.getJSONArray("result").getJSONObject(i).get("city_id").toString());
                        kota.city_name=json.getJSONArray("result").getJSONObject(i).get("city_name").toString();
                        city_name.add(kota.getCity_name());
                        city_id.add(kota.getCity_id());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,city_name);
                    spinkota.setAdapter(adapter);
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    private void removeOrderProductSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("OrderPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("orderProduct");
        editor.apply();
        // Load orders again after removing the entry
        loadOrder();
    }
    // Load Order Shared preferences
    private void loadOrder() {
        rvOrder.setLayoutManager(new LinearLayoutManager(getActivity()));
        sharedPreferences = requireActivity().getSharedPreferences("OrderPreference", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("orderProduct")){
            if (!orderProducts.isEmpty()){
                orderProducts.clear();
            }
            Gson gson = new Gson();
            String jsonText = sharedPreferences.getString("orderProduct", null);
            Integer Subtotal = 0;
            Order[] products = gson.fromJson(jsonText,Order[].class);
            for (Order order:products){
                orderProducts.add(order);
                Subtotal += order.getTotalOrder();
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                formatRupiah.setCurrency(Currency.getInstance("IDR"));
                subtotal = (int) Subtotal;
                tvSubTotal.setText(formatRupiah.format(Subtotal));
            }
            orderAdapter = new OrderAdapter(this, getActivity(),orderProducts);
            rvOrder.setAdapter(orderAdapter);
            orderAdapter.notifyDataSetChanged();
        }
    }
    // Calculating TotalPrice
    // Check Delivery Tax
    private void CheckOngkir(String asal, String tujuan, int berat, String kurir) {
        final String BASE_URL_RAJA_ONGKIR = "https://api.rajaongkir.com/starter/";
        ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setCancelable(true);
        pd.setMessage("Sedang mengambil data ...");
        pd.setProgress(0);
        pd.setMax(100);
        pd.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_RAJA_ONGKIR)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CostService api = retrofit.create(CostService.class);
        api.cekongkir(asal, tujuan, berat, kurir).enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray cost = new JSONArray();
                    cost = json.getJSONObject("rajaongkir").
                            getJSONArray("results")
                            .getJSONObject(0)
                            .getJSONArray("costs")
                            .getJSONObject(1)
                            .getJSONArray("cost");
                    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                    Ongkir = (int) cost.getJSONObject(0).get("value");
                    formatRupiah.setCurrency(Currency.getInstance("IDR"));
                    tvDelivery.setText(formatRupiah.format(Ongkir));
                    int Total = calculatePrice();
                    tvTotalHarga.setText(formatRupiah.format(Total));
                    pd.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    pd.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Cek Ongkir Gagal"+t.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }
    private int calculatePrice() {
        int total = Ongkir;
        for (Order order : orderProducts) {
            total += order.getTotalOrder();
        }
        return total;
    }
    // Delete Product
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
                Integer Subtotal = 0;
                Order[] products = gson.fromJson(updateOrderList,Order[].class);
                for (Order order:products){
                    Subtotal += order.getTotalOrder();
                }
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                formatRupiah.setCurrency(Currency.getInstance("IDR"));
                tvSubTotal.setText(formatRupiah.format(Subtotal));
                subtotal = (int) Subtotal;
                int Total = calculatePrice();
                tvTotalHarga.setText(formatRupiah.format(Total));
            }
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            formatRupiah.setCurrency(Currency.getInstance("IDR"));
            tvTotalHarga.setText(formatRupiah.format(0));
        }
    }
}