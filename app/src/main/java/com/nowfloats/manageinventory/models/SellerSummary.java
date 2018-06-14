package com.nowfloats.manageinventory.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vinay on 13-06-2018.
 */

public class SellerSummary {


    @SerializedName("Data")
    @Expose
    private Data data;

    public class Data {

        @SerializedName("TotalRevenue")
        @Expose
        private Double totalRevenue;

        @SerializedName("TotalOrders")
        @Expose
        private Integer totalOrders;

        @SerializedName("TotalCompletedOrders")
        @Expose
        private Integer totalCompletedOrders;

        @SerializedName("TotalOrdersCancelled")
        @Expose
        private Integer totalOrdersCancelled;

        @SerializedName("TotalOrdersEscalated")
        @Expose
        private Integer totalOrdersEscalated;

        @SerializedName("TotalOrdersInProgress")
        @Expose
        private Integer totalOrdersInProgress;

        @SerializedName("TotalOrdersAbandoned")
        @Expose
        private Integer totalOrdersAbandoned;

        public Double getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(Double totalRevenue) {
            this.totalRevenue = totalRevenue;
        }

        public Integer getTotalOrders() {
            return totalOrders;
        }

        public void setTotalOrders(Integer totalOrders) {
            this.totalOrders = totalOrders;
        }

        public Integer getTotalCompletedOrders() {
            return totalCompletedOrders;
        }

        public void setTotalCompletedOrders(Integer totalCompletedOrders) {
            this.totalCompletedOrders = totalCompletedOrders;
        }

        public Integer getTotalOrdersCancelled() {
            return totalOrdersCancelled;
        }

        public void setTotalOrdersCancelled(Integer totalOrdersCancelled) {
            this.totalOrdersCancelled = totalOrdersCancelled;
        }

        public Integer getTotalOrdersEscalated() {
            return totalOrdersEscalated;
        }

        public void setTotalOrdersEscalated(Integer totalOrdersEscalated) {
            this.totalOrdersEscalated = totalOrdersEscalated;
        }

        public Integer getTotalOrdersAbandoned() {
            return totalOrdersAbandoned;
        }

        public void setTotalOrdersAbandoned(Integer totalOrdersAbandoned) {
            this.totalOrdersAbandoned = totalOrdersAbandoned;
        }

        public Integer getTotalOrdersInProgress() {
            return totalOrdersInProgress;
        }

        public void setTotalOrdersInProgress(Integer totalOrdersInProgress) {
            this.totalOrdersInProgress = totalOrdersInProgress;
        }
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
