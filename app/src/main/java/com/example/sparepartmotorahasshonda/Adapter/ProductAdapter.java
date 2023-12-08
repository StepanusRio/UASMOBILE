package com.example.sparepartmotorahasshonda.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import java.text.NumberFormat;
import java.util.Currency;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sparepartmotorahasshonda.Model.Order;
import com.example.sparepartmotorahasshonda.Model.Product;
import com.example.sparepartmotorahasshonda.R;
import com.example.sparepartmotorahasshonda.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    public ProductAdapter(Context context,HomeFragment fragment, List<Product> listProduct) {
        this.context = context;
        this.listProduct = listProduct;
        this.fragment = fragment;
        this.originalList = new ArrayList<>(listProduct);
    }
    private HomeFragment fragment;
    private List<Product> originalList;
    SharedPreferences sharedPreferences;
    Context context;
    List<Product> listProduct;
    List<Order> orderProducts = new ArrayList<>();

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_sparepart,parent,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
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

    public void filter(String query) {
        List<Product> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(originalList); // Revert to the original list
        } else {
            String filterPattern = query.toLowerCase().trim();
            for (Product product : originalList) {
                if (product.getName().toLowerCase().contains(filterPattern)) {
                    filteredList.add(product);
                }
            }
        }
        listProduct.clear();
        listProduct.addAll(filteredList); // Update the current list with filtered data
        notifyDataSetChanged();
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
