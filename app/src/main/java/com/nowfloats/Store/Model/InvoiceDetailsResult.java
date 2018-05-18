package com.nowfloats.Store.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class InvoiceDetailsResult {
    @SerializedName("_id")
    @Expose
    private String orderConfirmationId;
    @SerializedName("createdOn")
    @Expose
    private String paymentDate;
    @SerializedName("packageName")
    @Expose
    private String packageName;
    @SerializedName("netPackagePrice")
    @Expose
    private int netPackagePrice;
    @SerializedName("_nfInternalClaimId")
    @Expose
    private String claimid;
    @SerializedName("paymentTransactionStatus")
    @Expose
    private int paymentStatus;

    public String getOrderConfirmationId() {
        return orderConfirmationId;
    }

    public void setOrderConfirmationId(String orderConfirmationId) {
        this.orderConfirmationId = orderConfirmationId;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getNetPackagePrice() {
        return netPackagePrice;
    }

    public void setNetPackagePrice(int netPackagePrice) {
        this.netPackagePrice = netPackagePrice;
    }

    public String getClaimid() {
        return claimid;
    }

    public void setClaimid(String claimid) {
        this.claimid = claimid;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
