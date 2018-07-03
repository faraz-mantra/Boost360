package com.nowfloats.Store;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by vinay on 28-06-2018.
 */

public class DiscountCoupon implements Serializable {

    @SerializedName("discountPercentage")
    @Expose
    private Double discountPercentage;

    @SerializedName("couponName")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("couponCode")
    @Expose
    private String couponCode;

    @SerializedName("toRedeem")
    @Expose
    private Boolean toRedeem;


    public DiscountCoupon(Double discountPercentage, String name, String description, String couponCode, Boolean toRedeem) {
        this.discountPercentage = discountPercentage;
        this.name = name;
        this.description = description;
        this.couponCode = couponCode;
        this.toRedeem = toRedeem;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Boolean getToRedeem() {
        return toRedeem;
    }

    public void setToRedeem(Boolean toRedeem) {
        this.toRedeem = toRedeem;
    }
}
