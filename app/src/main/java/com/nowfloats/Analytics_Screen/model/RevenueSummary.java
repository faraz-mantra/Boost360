package com.nowfloats.Analytics_Screen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


/**
 * Created by Admin on 17-04-2017.
 */


public class RevenueSummary implements Serializable {
    @SerializedName("Data")
    @Expose
    private List<RevenueData> revenueData;


    public class RevenueData implements Serializable {
        @SerializedName("DeliveryDate")
        @Expose
        private String deliveryDate;

        @SerializedName("Amount")
        @Expose
        private float amount;

        public String getDeliveryDate() {
            return deliveryDate;
        }

        public void setDeliveryDate(String deliveryDate) {
            this.deliveryDate = deliveryDate;
        }

        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }
    }

    public List<RevenueData> getRevenueData() {
        return revenueData;
    }

    public void setRevenueData(List<RevenueData> revenueData) {
        this.revenueData = revenueData;
    }
}
