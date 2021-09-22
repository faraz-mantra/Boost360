package com.nowfloats.Store.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by NowFloats on 05-12-2016.
 */

public class PaymentTokenModel {
    @SerializedName("PaymentMethodType")
    @Expose
    public String paymentMethodType;
    @SerializedName("AccessToken")
    @Expose
    private String accessToken;
    @SerializedName("PaymentRequestId")
    @Expose
    private String paymentRequestId;
    @SerializedName("TargetPaymentCollectionUri")
    @Expose
    private String targetPaymentCollectionUri;

    /**
     * @return The accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @param accessToken The AccessToken
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * @return The paymentRequestId
     */
    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    /**
     * @param paymentRequestId The PaymentRequestId
     */
    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }

    /**
     * @return The targetPaymentCollectionUri
     */
    public String getTargetPaymentCollectionUri() {
        return targetPaymentCollectionUri;
    }

    /**
     * @param targetPaymentCollectionUri The TargetPaymentCollectionUri
     */
    public void setTargetPaymentCollectionUri(String targetPaymentCollectionUri) {
        this.targetPaymentCollectionUri = targetPaymentCollectionUri;
    }

    public String getPaymentMethodType() {
        return paymentMethodType;
    }

    public void setPaymentMethodType(String paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }
}
