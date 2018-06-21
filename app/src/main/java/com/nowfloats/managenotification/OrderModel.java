package com.nowfloats.managenotification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Shimona on 19-06-2018.
 */

public class OrderModel {

    @SerializedName("buyerName")
    @Expose
    private String buyerName;
    @SerializedName("buyerContactNumber")
    @Expose
    private String buyerContactNumber;
    @SerializedName("buyerCity")
    @Expose
    private String buyerCity;
    @SerializedName("buyerState")
    @Expose
    private String buyerState;
    @SerializedName("orderQuantity")
    @Expose
    private int orderQuantity;
    @SerializedName("orderValue")
    @Expose
    private double orderValue;
    @SerializedName("orderCurrency")
    @Expose
    private String orderCurrency;
    @SerializedName("createdOn")
    @Expose
    private String createdOn;
    @SerializedName("orderStatus")
    @Expose
    private String orderStatus;

    public OrderModel(String buyerName, String buyerContactNumber, String buyerCity, String buyerState, int orderQuantity, double orderValue, String orderCurrency, String createdOn, String orderStatus) {
        this.buyerName = buyerName;
        this.buyerContactNumber = buyerContactNumber;
        this.buyerCity = buyerCity;
        this.buyerState = buyerState;
        this.orderQuantity = orderQuantity;
        this.orderValue = orderValue;
        this.orderCurrency = orderCurrency;
        this.createdOn = createdOn;
        this.orderStatus = orderStatus;
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
