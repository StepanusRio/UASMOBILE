package com.example.sparepartmotorahasshonda.Model;

import android.net.Uri;

public class Checkout {
    public String imageProof;
    private String Username;
    private String idOrder;
    private String Alamat;
    private String Kota;
    private String Provinsi;
    private int Subtotal;
    private int Ongkir;
    private int TotalHarga;
    private String PaymentMethod;
    private String TanggalCheckout;
    private String StatusPayment;
    private Uri ImageProofURI;


    public Uri getImageProofURI() {
        return ImageProofURI;
    }

    public void setImageProofURI(Uri imageProofURI) {
        ImageProofURI = imageProofURI;
    }

    public String getImageProof() {
        return imageProof;
    }

    public void setImageProof(String imageProof) {
        this.imageProof = imageProof;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getAlamat() {
        return Alamat;
    }

    public void setAlamat(String alamat) {
        Alamat = alamat;
    }

    public String getKota() {
        return Kota;
    }

    public void setKota(String kota) {
        Kota = kota;
    }

    public String getProvinsi() {
        return Provinsi;
    }

    public void setProvinsi(String provinsi) {
        Provinsi = provinsi;
    }

    public int getSubtotal() {
        return Subtotal;
    }

    public void setSubtotal(int subtotal) {
        Subtotal = subtotal;
    }

    public int getOngkir() {
        return Ongkir;
    }

    public void setOngkir(int ongkir) {
        Ongkir = ongkir;
    }

    public int getTotalHarga() {
        return TotalHarga;
    }

    public void setTotalHarga(int totalHarga) {
        TotalHarga = totalHarga;
    }

    public String getPaymentMethod() {
        return PaymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        PaymentMethod = paymentMethod;
    }

    public String getTanggalCheckout() {
        return TanggalCheckout;
    }

    public void setTanggalCheckout(String tanggalCheckout) {
        TanggalCheckout = tanggalCheckout;
    }

    public String getStatusPayment() {
        return StatusPayment;
    }

    public void setStatusPayment(String statusPayment) {
        StatusPayment = statusPayment;
    }
}
