package com.example.sparepartmotorahasshonda.Model;

import com.google.gson.annotations.SerializedName;

public class Order {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("hargaJual")
    private int hargaJual;

    @SerializedName("images")
    private Product.Image[] images;

    @SerializedName("Kategori")
    private Product.Category kategori;

    @SerializedName("stok")
    private int stock;

    @SerializedName("QtyOrder")
    private int Qty;

    @SerializedName("TotalOrder")
    private int TotalOrder;

    public int getQty() {
        return Qty;
    }

    public void setQty(int qty) {
        Qty = qty;
    }

    public int getTotalOrder() {
        return TotalOrder;
    }

    public void setTotalOrder(int totalOrder) {
        TotalOrder = totalOrder;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(int hargaJual) {
        this.hargaJual = hargaJual;
    }

    public Product.Image[] getImages() {
        return images;
    }

    public void setImages(Product.Image[] images) {
        this.images = images;
    }

    public Product.Category getKategori() {
        return kategori;
    }

    public void setKategori(Product.Category kategori) {
        this.kategori = kategori;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public class Image {
        @SerializedName("url")
        private String url;

        // Getter and Setter for url
        // ...


        public Image(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public class Category {
        @SerializedName("label")
        private String label;

        public Category(String label) {
            this.label = label;
        }
        // Getter and Setter for label
        // ...

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}
