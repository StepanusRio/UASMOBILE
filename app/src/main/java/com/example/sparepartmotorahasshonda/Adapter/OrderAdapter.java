package com.example.sparepartmotorahasshonda.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sparepartmotorahasshonda.Model.Order;
import com.example.sparepartmotorahasshonda.R;
import com.example.sparepartmotorahasshonda.ui.order.OrderFragment;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private OrderFragment fragment;
    private Context context;
    private List<Order> dataOrderProducts;

    public OrderAdapter(OrderFragment fragment, Context context, List<Order> dataOrderProducts) {
        this.fragment = fragment;
        this.context = context;
        this.dataOrderProducts = dataOrderProducts;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.order_list,parent,false);
        return new OrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        final Order order = dataOrderProducts.get(position);
        if (order.getImages() != null && order.getImages().length > 0) {
            String imageUrl = order.getImages()[0].getUrl(); // Assuming the first image is to be displayed
            Glide.with(context).load(imageUrl).into(holder.ProductImageOrder);
        }
        holder.tvOrderName.setText(order.getName());
        holder.tvOrderQty.setText(order.getQty()+"");
        int HargaJual = order.getHargaJual();
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id","ID"));
        formatRupiah.setCurrency(Currency.getInstance("IDR"));
        String formatedPrice = formatRupiah.format(HargaJual);
        holder.tvOrderHargaJual.setText(formatedPrice);
        int TotalOrder = order.getTotalOrder();
        String formatedTotalPrice = formatRupiah.format(TotalOrder);
        holder.tvTotalOrder.setText(formatedTotalPrice);
        holder.DeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragment !=null){
                    fragment.deleteProduct(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataOrderProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ProductImageOrder;
        TextView tvOrderName,tvOrderHargaJual,tvOrderQty,tvTotalOrder;
        ImageButton DeleteItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderName = itemView.findViewById(R.id.tvOrderName);
            tvOrderHargaJual = itemView.findViewById(R.id.tvOrderHargaJual);
            tvOrderQty = itemView.findViewById(R.id.tvOrderQty);
            tvTotalOrder = itemView.findViewById(R.id.tvTotalOrder);
            ProductImageOrder = itemView.findViewById(R.id.ProductImageOrder);
            DeleteItem = itemView.findViewById(R.id.DeleteItem);
        }
    }
}
