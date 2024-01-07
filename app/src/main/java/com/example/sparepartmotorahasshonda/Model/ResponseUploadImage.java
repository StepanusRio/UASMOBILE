package com.example.sparepartmotorahasshonda.Model;

import com.google.gson.annotations.SerializedName;

public class ResponseUploadImage {
    @SerializedName("kode") String kode;
    @SerializedName("pesan") String pesan;
    @SerializedName("imageUploaded") String imageUploaded;

    public String getImageUploaded() {
        return imageUploaded;
    }

    public void setImageUploaded(String imageUploaded) {
        this.imageUploaded = imageUploaded;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }
}
