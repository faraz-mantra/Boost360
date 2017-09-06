package com.nowfloats.manageinventory.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by NowFloats on 31-08-2017.
 */

public class OrdersCountModel {

    @SerializedName("_id")
    @Expose
    private Id id;
    @SerializedName("count")
    @Expose
    private Integer count;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public class Id {

        @SerializedName("orderStatus")
        @Expose
        private Integer orderStatus;
        @SerializedName("deliveryStatus")
        @Expose
        private Integer deliveryStatus;

        public Integer getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(Integer orderStatus) {
            this.orderStatus = orderStatus;
        }

        public Integer getDeliveryStatus() {
            return deliveryStatus;
        }

        public void setDeliveryStatus(Integer deliveryStatus) {
            this.deliveryStatus = deliveryStatus;
        }

    }
}
