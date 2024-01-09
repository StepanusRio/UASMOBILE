package com.example.sparepartmotorahasshonda.API;

import com.example.sparepartmotorahasshonda.Model.Checkout;
import com.example.sparepartmotorahasshonda.Model.Order;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CheckoutService {
    @FormUrlEncoded
    @POST("checkout.php")
    Call<ResponseBody> Checkout(
            @Field("idOrder") String idOrder,
            @Field("username") String username,
            @Field("Alamat") String alamat,
            @Field("Kota") String kota,
            @Field("Provinsi") String provinsi,
            @Field("Subtotal") int subtotal,
            @Field("Ongkir") int ongkir,
            @Field("TotalHarga") int totalharga,
            @Field("PaymentMethod") String paymentmethod,
            @Field("TanggalCheckout") String tanggalcheckout,
            @Field("StatusPayment") String statuspayment
    );
}
