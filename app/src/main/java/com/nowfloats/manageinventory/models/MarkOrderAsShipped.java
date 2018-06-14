package com.nowfloats.manageinventory.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by NowFloats on 17-08-2017.
 */

public class MarkOrderAsShipped {
    @SerializedName("OrderId")
    @Expose
    private String orderId;

    @SerializedName("ShippedBy")
    @Expose
    private String ShippedOn;

    @SerializedName("shippedOn")
    @Expose
    private String shippedOn;

    @SerializedName("DeliveryProvider")
    @Expose
    private String deliveryProvider;

    @SerializedName("TrackingNumber")
    @Expose
    private String trackingNumber;

    @SerializedName("TrackingURL")
    @Expose
    private String trackingURL;

    @SerializedName("DeliveryCharges")
    @Expose
    private Double deliveryCharges;

    @SerializedName("Address")
    @Expose
    private OrderDataModel.Address address;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getShippedOn() {
        return ShippedOn;
    }

    public void setShippedOn(String shippedOn) {
        ShippedOn = shippedOn;
    }

    public String getDeliveryProvider() {
        return deliveryProvider;
    }

    public void setDeliveryProvider(String deliveryProvider) {
        this.deliveryProvider = deliveryProvider;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getTrackingURL() {
        return trackingURL;
    }

    public void setTrackingURL(String trackingURL) {
        this.trackingURL = trackingURL;
    }

    public Double getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(Double deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public OrderDataModel.Address getAddress() {
        return address;
    }

    public void setAddress(OrderDataModel.Address address) {
        this.address = address;
    }
}
