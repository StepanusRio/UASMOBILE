package com.example.sparepartmotorahasshonda.Model;

public class Checkout {
    private String Username;
    private String Id_Trx;
    private String Alamat;
    private String Kota;
    private String Provinsi;
    private int Subtotal;
    private int Ongkir;
    private int TotalHarga;
    private String PaymentMethod;
    private String TanggalCheckout;
    private String StatusPayment;

    public Checkout(String username,String id_Trx, String alamat, String kota, String provinsi, int subtotal, int ongkir, int totalHarga, String paymentMethod, String tanggalCheckout, String statusPayment) {
        Username = username;
        Id_Trx = id_Trx;
        Alamat = alamat;
        Kota = kota;
        Provinsi = provinsi;
        Subtotal = subtotal;
        Ongkir = ongkir;
        TotalHarga = totalHarga;
        PaymentMethod = paymentMethod;
        TanggalCheckout = tanggalCheckout;
        StatusPayment = statusPayment;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getId_Trx() {
        return Id_Trx;
    }

    public void setId_Trx(String id_Trx) {
        Id_Trx = id_Trx;
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
