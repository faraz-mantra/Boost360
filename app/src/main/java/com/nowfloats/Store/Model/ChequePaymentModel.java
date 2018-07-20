package com.nowfloats.Store.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChequePaymentModel {

    @SerializedName("_id")
    @Expose
    private Object id;
    @SerializedName("alternateImage")
    @Expose
    private String alternateImage;
    @SerializedName("bankName")
    @Expose
    private String bankName;
    @SerializedName("chequeNumber")
    @Expose
    private String chequeNumber;
    @SerializedName("clientId")
    @Expose
    private String clientId;
    @SerializedName("currencyCode")
    @Expose
    private String currencyCode;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("ifscCode")
    @Expose
    private String ifscCode;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("micrCode")
    @Expose
    private String micrCode;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("fpTag")
    @Expose
    private String fpTag;
    @SerializedName("GSTNumber")
    @Expose
    private String gSTNumber;
    @SerializedName("partnerType")
    @Expose
    private Integer partnerType;
    @SerializedName("paymentDate")
    @Expose
    private String paymentDate;
    @SerializedName("activationDate")
    @Expose
    private String activationDate;
    @SerializedName("paymentFor")
    @Expose
    private String paymentFor;
    @SerializedName("paymentStatus")
    @Expose
    private int paymentStatus;
    @SerializedName("paymentTransactionChannel")
    @Expose
    private Integer paymentTransactionChannel;
    @SerializedName("products")
    @Expose
    private List<ProductPaymentModel> products = null;
    @SerializedName("referenceId")
    @Expose
    private Object referenceId;
    @SerializedName("rejectionReason")
    @Expose
    private Object rejectionReason;
    @SerializedName("rtgsId")
    @Expose
    private String rtgsId;
    @SerializedName("taxAmount")
    @Expose
    private Double taxAmount;
    @SerializedName("tdsPercentage")
    @Expose
    private Double tdsPercentage;
    @SerializedName("totalPrice")
    @Expose
    private Double totalPrice;
    @SerializedName("transactionId")
    @Expose
    private String transactionId;
    @SerializedName("username")
    @Expose
    private String username;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getAlternateImage() {
        return alternateImage;
    }

    public void setAlternateImage(String alternateImage) {
        this.alternateImage = alternateImage;
    }

    public String getBankName() {
        return bankName;
    }

    public String getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getChequeNumber() {
        return chequeNumber;
    }

    public void setChequeNumber(String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMicrCode() {
        return micrCode;
    }

    public void setMicrCode(String micrCode) {
        this.micrCode = micrCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFpTag() {
        return fpTag;
    }

    public void setFpTag(String fpTag) {
        this.fpTag = fpTag;
    }

    public String getGSTNumber() {
        return gSTNumber;
    }

    public void setGSTNumber(String gSTNumber) {
        this.gSTNumber = gSTNumber;
    }

    public Integer getPartnerType() {
        return partnerType;
    }

    public void setPartnerType(Integer partnerType) {
        this.partnerType = partnerType;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentFor() {
        return paymentFor;
    }

    public void setPaymentFor(String paymentFor) {
        this.paymentFor = paymentFor;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Integer getPaymentTransactionChannel() {
        return paymentTransactionChannel;
    }

    public void setPaymentTransactionChannel(Integer paymentTransactionChannel) {
        this.paymentTransactionChannel = paymentTransactionChannel;
    }

    public List<ProductPaymentModel> getProducts() {
        return products;
    }

    public void setProducts(List<ProductPaymentModel> products) {
        this.products = products;
    }

    public Object getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Object referenceId) {
        this.referenceId = referenceId;
    }

    public Object getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(Object rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRtgsId() {
        return rtgsId;
    }

    public void setRtgsId(String rtgsId) {
        this.rtgsId = rtgsId;
    }


    public String getgSTNumber() {
        return gSTNumber;
    }

    public void setgSTNumber(String gSTNumber) {
        this.gSTNumber = gSTNumber;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getTdsPercentage() {
        return tdsPercentage;
    }

    public void setTdsPercentage(Double tdsPercentage) {
        this.tdsPercentage = tdsPercentage;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public enum PaymentTransactionStatus {
        INITIATED,
        PENDING,
        REFUNDED,
        SUCCESSFUL,
        FAILED,
        CANCELLED_BY_USER,
        CANCELLED_BY_VERIFICATION_TEAM
    }
}
