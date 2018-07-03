package com.nowfloats.Store;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by vinay on 28-06-2018.
 */

public class RedeemDiscountRequestModel implements Serializable {

    @SerializedName("clientId")
    @Expose
    private String clientId;

    @SerializedName("couponCode")
    @Expose
    private String couponCode;

    @SerializedName("toRedeem")
    @Expose
    private Boolean toRedeem;

    public RedeemDiscountRequestModel(String clientId, String couponCode, Boolean toRedeem) {
        this.clientId = clientId;
        this.couponCode = couponCode;
        this.toRedeem = toRedeem;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
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
