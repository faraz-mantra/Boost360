package com.nowfloats.Store.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InitiateModel {

    @SerializedName("clientId")
    @Expose
    private String clientId;
    @SerializedName("customerName")
    @Expose
    private String customerName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("fpId")
    @Expose
    private String fpId;
    @SerializedName("fpTag")
    @Expose
    private String fpTag;
    @SerializedName("paymentTransactionChannel")
    @Expose
    private Integer paymentTransactionChannel;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("phoneNumberExtension")
    @Expose
    private String phoneNumberExtension;
    @SerializedName("_NFInternalSalesPersonId")
    @Expose
    private String salesPersonId;
    @SerializedName("paymentMode")
    @Expose
    private int paymentMode;
    @SerializedName("products")
    @Expose
    private List<ProductPaymentModel> products = null;
    @SerializedName("recurringMonths")
    @Expose
    private Integer recurringMonths;
    @SerializedName("tdsPercentage")
    @Expose
    private Double tdsPercentage;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFpId() {
        return fpId;
    }

    public void setFpId(String fpId) {
        this.fpId = fpId;
    }

    public String getFpTag() {
        return fpTag;
    }

    public void setFpTag(String fpTag) {
        this.fpTag = fpTag;
    }

    public Integer getPaymentTransactionChannel() {
        return paymentTransactionChannel;
    }

    public void setPaymentTransactionChannel(Integer paymentTransactionChannel) {
        this.paymentTransactionChannel = paymentTransactionChannel;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumberExtension() {
        return phoneNumberExtension;
    }

    public void setPhoneNumberExtension(String phoneNumberExtension) {
        this.phoneNumberExtension = phoneNumberExtension;
    }

    public List<ProductPaymentModel> getProducts() {
        return products;
    }

    public void setProducts(List<ProductPaymentModel> products) {
        this.products = products;
    }

    public Integer getRecurringMonths() {
        return recurringMonths;
    }

    public void setRecurringMonths(Integer recurringMonths) {
        this.recurringMonths = recurringMonths;
    }

    public Double getTdsPercentage() {
        return tdsPercentage;
    }

    public void setTdsPercentage(Double tdsPercentage) {
        this.tdsPercentage = tdsPercentage;
    }

    public String getSalesPersonId() {
        return salesPersonId;
    }

    public void setSalesPersonId(String salesPersonId) {
        this.salesPersonId = salesPersonId;
    }

    public int getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(int paymentMode) {
        this.paymentMode = paymentMode;
    }

    public enum PAYMENT_MODE {
        CASH,
        CHEQUE,
        PDC,
        NEFT,
        ONLINEPAYMENTGATEWAY
    }
}

