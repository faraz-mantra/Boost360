package com.nowfloats.managenotification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Shimona on 19-06-2018.
 */

public class OrderModel {

    @SerializedName("BuyerName")
    @Expose
    private String buyerName;
    @SerializedName("BuyerContactNumber")
    @Expose
    private String buyerContactNumber;
    @SerializedName("BuyerCity")
    @Expose
    private String buyerCity;
    @SerializedName("BuyerState")
    @Expose
    private String buyerState;
    @SerializedName("OrderQuantity")
    @Expose
    private int orderQuantity;
    @SerializedName("OrderValue")
    @Expose
    private double orderValue;
    @SerializedName("OrderCurrency")
    @Expose
    private String orderCurrency;
    @SerializedName("CreatedOn")
    @Expose
    private String createdOn;
    @SerializedName("OrderStatus")
    @Expose
    private String orderStatus;

    @SerializedName("OrderId")
    @Expose
    private String orderId;

    @SerializedName("OrderReferenceNumber")
    @Expose
    private String orderReferenceNumber;

    public OrderModel(String orderId, String orderReferenceNumber, String buyerName, String buyerContactNumber, String buyerCity, String buyerState, int orderQuantity, double orderValue, String orderCurrency, String createdOn, String orderStatus) {
        this.buyerName = buyerName;
        this.orderId = orderId;
        this.orderReferenceNumber = orderReferenceNumber;
        this.buyerContactNumber = buyerContactNumber;
        this.buyerCity = buyerCity;
        this.buyerState = buyerState;
        this.orderQuantity = orderQuantity;
        this.orderValue = orderValue;
        this.orderCurrency = orderCurrency;
        this.createdOn = createdOn;
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderReferenceNumber() {
        return orderReferenceNumber;
    }

    public void setOrderReferenceNumber(String orderReferenceNumber) {
        this.orderReferenceNumber = orderReferenceNumber;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getOrderCurrency() {
        return orderCurrency;
    }

    public void setOrderCurrency(String orderCurrency) {
        this.orderCurrency = orderCurrency;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerContactNumber() {
        return buyerContactNumber;
    }

    public void setBuyerContactNumber(String buyerContactNumber) {
        this.buyerContactNumber = buyerContactNumber;
    }

    public String getBuyerCity() {
        return buyerCity;
    }

    public void setBuyerCity(String buyerCity) {
        this.buyerCity = buyerCity;
    }

    public String getBuyerState() {
        return buyerState;
    }

    public void setBuyerState(String buyerState) {
        this.buyerState = buyerState;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public double getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(double orderValue) {
        this.orderValue = orderValue;
    }

}
