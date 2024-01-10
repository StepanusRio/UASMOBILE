package com.example.sparepartmotorahasshonda.Model;

import com.google.gson.annotations.SerializedName;

public class ResponseUpdateStatus {
    @SerializedName("result") String result;
    @SerializedName("message") String message;
    @SerializedName("idOrder") String idOrder;
    @SerializedName("imageProof") String imageProof;

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getImageProof() {
        return imageProof;
    }

    public void setImageProof(String imageProof) {
        this.imageProof = imageProof;
    }

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
