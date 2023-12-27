package com.example.sparepartmotorahasshonda.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sparepartmotorahasshonda.Model.Order;
import com.example.sparepartmotorahasshonda.Model.Product;
import com.example.sparepartmotorahasshonda.R;
import com.example.sparepartmotorahasshonda.ui.product.ProductFragment;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class AllProductAdapter extends RecyclerView.Adapter<AllProductAdapter.ViewHolder> {
    public AllProductAdapter(Context context,ProductFragment fragment,List<Product> listProduct) {
        this.context = context;
        this.listProduct = listProduct;
        this.fragment = fragment;
    }
    private ProductFragment fragment;
    Context context;
    List<Product> listProduct;

    @NonNull
    @Override
    public AllProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_allsparepart,parent,false);
        AllProductAdapter.ViewHolder holder = new AllProductAdapter.ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AllProductAdapter.ViewHolder holder, int position) {
        final Product listProduct = this.listProduct.get(position);
        holder.tvName.setText(this.listProduct.get(position).getName());
        int hargaJual = listProduct.getHargaJual();
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        formatRupiah.setCurrency(Currency.getInstance("IDR"));
        String formattedPrice = formatRupiah.format(hargaJual);

        holder.tvHargaJual.setText(formattedPrice);
        String stock = "Stok: " + this.listProduct.get(position).getStock();
        holder.tvStok.setText(stock);
        // Load image using Glide
        if (listProduct.getImages() != null && listProduct.getImages().length > 0) {
            String imageUrl = listProduct.getImages()[0].getUrl(); // Assuming the first image is to be displayed
            Glide.with(context).load(imageUrl).into(holder.ProductImage);
        }
        holder.AddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.addToCart(listProduct);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvName,tvStok,tvHargaJual;
        public CardView layoutItem;
        public ImageView ProductImage;
        public ImageButton AddToCart,DetailProduct;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvName = (TextView) itemView.findViewById(R.id.tvName);
            this.tvStok = (TextView) itemView.findViewById(R.id.tvStok);
            this.tvHargaJual = (TextView) itemView.findViewById(R.id.tvHargaJual);
            this.layoutItem = (CardView) itemView.findViewById(R.id.layoutItem);
            this.ProductImage = (ImageView) itemView.findViewById(R.id.ProductImage);
            this.AddToCart = (ImageButton) itemView.findViewById(R.id.AddToCartButton);
            this.DetailProduct = (ImageButton) itemView.findViewById(R.id.DetailProduct);
        }
    }
}
