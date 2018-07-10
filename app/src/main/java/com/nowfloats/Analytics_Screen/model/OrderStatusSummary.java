package com.nowfloats.Analytics_Screen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nowfloats.manageinventory.models.OrderDataModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Admin on 17-04-2017.
 */


public class OrderStatusSummary implements Serializable {
    @SerializedName("Data")
    @Expose
    private List<OrderStatus> orderStatus;

    public List<OrderStatus> getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(List<OrderStatus> orderStatus) {
        this.orderStatus = orderStatus;
    }

    public class OrderStatus implements Serializable {
        @SerializedName("OrderStatus")
        @Expose
        private String orderStatus;

        @SerializedName("PaymentMethod")
        @Expose
        private String paymentMethod;

        @SerializedName("OrdersCount")
        @Expose
        private int ordersCount;


        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public int getOrdersCount() {
            return ordersCount;
        }

        public void setOrdersCount(int ordersCount) {
            this.ordersCount = ordersCount;
        }
    }

}
