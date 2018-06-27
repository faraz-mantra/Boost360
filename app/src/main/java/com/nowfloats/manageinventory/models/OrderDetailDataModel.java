package com.nowfloats.manageinventory.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by NowFloats on 28-08-2017.
 */

public class OrderDetailDataModel {

    @SerializedName("Data")
    @Expose
    private OrderDataModel.Order order;

    public OrderDataModel.Order getOrder() {
        return order;
    }

    public void setOrder(OrderDataModel.Order order) {
        this.order = order;
    }
}
