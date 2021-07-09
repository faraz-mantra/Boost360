package com.nowfloats.Store.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InvoiceDetailsResult {
    @SerializedName("_id")
    @Expose
    private String orderConfirmationId;
    @SerializedName("createdOn")
    @Expose
    private String paymentDate;
    @SerializedName("netPackagePrice")
    @Expose
    private Double netPackagePrice;
    @SerializedName("_nfInternalClaimId")
    @Expose
    private String claimId;
    @SerializedName("paymentTransactionStatus")
    @Expose
    private int paymentStatus;
    @SerializedName("currencyCode")
    @Expose
    private String currencyCode;

    @SerializedName("packageDetails")
    @Expose
    public List<PackageDetails> packageDetails;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public List<PackageDetails> getPackageDetails() {
        return packageDetails;
    }

    public void setPackageDetails(List<PackageDetails> packageDetails) {
        this.packageDetails = packageDetails;
    }

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

    public Double getNetPackagePrice() {
        return netPackagePrice;
    }

    public void setNetPackagePrice(Double netPackagePrice) {
        this.netPackagePrice = netPackagePrice;
    }

    public String getClaimid() {
        return claimId;
    }

    public void setClaimid(String claimid) {
        this.claimId = claimid;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
