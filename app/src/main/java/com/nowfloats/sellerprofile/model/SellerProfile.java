package com.nowfloats.sellerprofile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SellerProfile {

    @SerializedName("DeliveryMethod")
    @Expose
    private String deliveryMethod;

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }
}