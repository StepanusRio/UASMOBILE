package com.example.sparepartmotorahasshonda.Adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sparepartmotorahasshonda.API.CheckoutService;
import com.example.sparepartmotorahasshonda.API.ImageUpload;
import com.example.sparepartmotorahasshonda.API.RetrofitAPI;
import com.example.sparepartmotorahasshonda.Model.Checkout;
import com.example.sparepartmotorahasshonda.Model.ResponseUpdateStatus;
import com.example.sparepartmotorahasshonda.Model.ResponseUploadImage;
import com.example.sparepartmotorahasshonda.OrderHistory;
import com.example.sparepartmotorahasshonda.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {
    private Context context;
    private String imageProof;
    private Uri selectedImageUri;
    List<Checkout> orderHistory;
    private OrderHistory orderHistoryActivity;
    private static final String[] PERMISSIONS_STORAGE={
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    int REQUEST_GALLERY=100;
    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedImageUri(Uri uri) {
        this.selectedImageUri = uri;
        notifyDataSetChanged(); // Notify adapter that data has changed
    }
    public OrderHistoryAdapter(Context context, List<Checkout> orderHistory,OrderHistory orderHistoryActivity) {
        this.context = context;
        this.orderHistory = orderHistory;
        this.orderHistoryActivity = orderHistoryActivity;
    }

    @NonNull
    @Override
    public OrderHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history,parent,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull OrderHistoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Checkout orderHistory = this.orderHistory.get(position);
        holder.tvIdOrder.setText(orderHistory.getIdOrder());
        holder.tvTanggalOrder.setText(orderHistory.getTanggalCheckout());
        holder.tvAddressOrder.setText(orderHistory.getAlamat());
        holder.tvKotaOrder.setText(orderHistory.getKota());
        holder.tvProvinsiOrder.setText(orderHistory.getProvinsi());
        holder.PaymentMethod.setText(orderHistory.getPaymentMethod());
        holder.tvStatusPayment.setText(orderHistory.getStatusPayment());
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        formatRupiah.setCurrency(Currency.getInstance("IDR"));
        holder.tvSubtotal.setText(formatRupiah.format(orderHistory.getSubtotal()));
        holder.tvOngkir.setText(formatRupiah.format(orderHistory.getOngkir()));
        holder.tvTotalHarga.setText(formatRupiah.format(orderHistory.getTotalHarga()));
        if (orderHistory.getImageProof() != null) {
            Glide.with(context)
                    .load("http://192.168.1.14/APIUTS/images/trx/"+orderHistory.getImageProof())
                    .into(holder.transactionProof);
        }
        if (orderHistory.getImageProofURI() != null){
            Glide.with(context)
                    .load(orderHistory.getImageProofURI())
                    .into(holder.transactionProof);
        }
        switch (orderHistory.getStatusPayment()) {
            case "Belum Dibayar":
                holder.tvStatusPayment.setTextColor(context.getResources().getColor(R.color.red_progress));
                holder.btnUploadTransactionProof.setBackgroundColor(context.getResources().getColor(R.color.black));
                holder.transactionProof.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        orderHistoryActivity.startActivityForResult(Intent.createChooser(intent, "Open Gallery"), REQUEST_GALLERY);
                        orderHistoryActivity.setClickedItemPosition(position);
                    }
                });
                holder.btnUploadTransactionProof.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BitmapDrawable draw = (BitmapDrawable) holder.transactionProof.getDrawable();
                        Bitmap bitmap = draw.getBitmap();
                        FileOutputStream outputStream;
                        File sdCard = Environment.getExternalStorageDirectory();
                        File dir = new File(sdCard.getAbsolutePath() + "/Pictures/upload");
                        dir.mkdirs();
                        String filename = String.format("%d.jpg", System.currentTimeMillis());
                        Log.i("INFO FILE NAME", "Filename: " + filename);
                        File outFile = new File(dir, filename);
                        try {
                            outputStream = new FileOutputStream(outFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            outputStream.flush();
                            outputStream.close();
                            String pathImage = outFile.getAbsolutePath();
                            File imageFile = new File(pathImage);
                            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-file"), imageFile);
                            MultipartBody.Part partImage = MultipartBody.Part.createFormData("imageProof", imageFile.getName(), requestBody);
                            ImageUpload uploadService = RetrofitAPI.API().create(ImageUpload.class);
                            uploadService.uploadImageProof(partImage).enqueue(new Callback<ResponseUploadImage>() {
                                @Override
                                public void onResponse(Call<ResponseUploadImage> call, Response<ResponseUploadImage> response) {
                                    imageProof = response.body().getImageUploaded();
                                     updateCheckoutData(orderHistory.getIdOrder(),imageProof);
                                }

                                @Override
                                public void onFailure(Call<ResponseUploadImage> call, Throwable t) {

                                }
                            });

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                break;
            case "Diproses":
                holder.tvStatusPayment.setTextColor(context.getResources().getColor(R.color.blue_progress));
                holder.btnUploadTransactionProof.setClickable(false);
                holder.btnUploadTransactionProof.setBackgroundColor(context.getResources().getColor(R.color.grey));
                holder.btnUploadTransactionProof.setText("Sedang dalam proses pengecekan...");
                break;
            case "Dikirim":
                holder.btnUploadTransactionProof.setClickable(false);
                holder.btnUploadTransactionProof.setText("Paket diserahkan ke kurir...");
                holder.btnUploadTransactionProof.setBackgroundColor(context.getResources().getColor(R.color.grey));
                holder.tvStatusPayment.setTextColor(context.getResources().getColor(R.color.yellow_progress));
                break;
            case "Selesai":
                holder.btnUploadTransactionProof.setClickable(false);
                holder.btnUploadTransactionProof.setText("Sudah Bayar...");
                holder.btnUploadTransactionProof.setBackgroundColor(context.getResources().getColor(R.color.grey));
                holder.tvStatusPayment.setTextColor(context.getResources().getColor(R.color.green_progress));
                break;
        }
    }

    private void updateCheckoutData(String idOrder, String imageProof) {
        CheckoutService api = RetrofitAPI.API().create(CheckoutService.class);
        Call<ResponseUpdateStatus> call = api.updateStatusPayment(idOrder,imageProof);
        call.enqueue(new Callback<ResponseUpdateStatus>() {
            @Override
            public void onResponse(Call<ResponseUpdateStatus> call, Response<ResponseUpdateStatus> response) {
                if (response.isSuccessful()){
                    Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseUpdateStatus> call, Throwable t) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return orderHistory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdOrder, tvTanggalOrder, tvAddressOrder, tvKotaOrder, tvProvinsiOrder, PaymentMethod, tvStatusPayment, tvSubtotal, tvOngkir, tvTotalHarga;
        Button btnUploadTransactionProof;
        ImageView transactionProof;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvIdOrder = (TextView) itemView.findViewById(R.id.tvIdOrder);
            this.transactionProof = (ImageView) itemView.findViewById(R.id.transactionProof);
            this.tvTanggalOrder = (TextView) itemView.findViewById(R.id.tvTanggalOrder);
            this.tvAddressOrder = (TextView) itemView.findViewById(R.id.tvAddressOrder);
            this.tvKotaOrder = (TextView) itemView.findViewById(R.id.tvKotaOrder);
            this.tvProvinsiOrder = (TextView) itemView.findViewById(R.id.tvProvinsiOrder);
            this.PaymentMethod = (TextView) itemView.findViewById(R.id.PaymentMethod);
            this.tvStatusPayment = (TextView) itemView.findViewById(R.id.tvStatusPayment);
            this.tvSubtotal = (TextView) itemView.findViewById(R.id.tvSubtotal);
            this.tvOngkir = (TextView) itemView.findViewById(R.id.tvOngkir);
            this.tvTotalHarga = (TextView) itemView.findViewById(R.id.tvTotalHarga);
            this.btnUploadTransactionProof = (Button) itemView.findViewById(R.id.btnUploadTransactionProof);
        }
//        public void setImage(Uri uri) {
//            this.transactionProof.setImageURI(uri);
//        }

    }
}
