package com.nowfloats.manageinventory.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.stmt.query.In;

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

    @SerializedName("TotalNetAmount")
    @Expose
    private Double totalNetAmount;

    @SerializedName("CurrencyCode")
    @Expose
    private String currencyCode;

    @SerializedName("TotalOrders")
    @Expose
    private Double totalOrders;

    @SerializedName("TotalOrdersCompleted")
    @Expose
    private Double totalOrdersCompleted;

    @SerializedName("TotalOrdersCancelled")
    @Expose
    private Double totalOrdersCancelled;

    @SerializedName("TotalOrdersEscalated")
    @Expose
    private Double totalOrdersEscalated;

    @SerializedName("TotalOrdersInProgress")
    @Expose
    private Double totalOrdersInProgress;

    @SerializedName("TotalOrdersAbandoned")
    @Expose
    private Double totalOrdersAbandoned;

    public Integer getTotalNetAmount() {
      if (totalNetAmount != null) return totalNetAmount.intValue();
      return 0;
    }

    public void setTotalNetAmount(Double totalNetAmount) {
      this.totalNetAmount = totalNetAmount;
    }

    public Integer getTotalRevenue() {
      if (totalRevenue != null) return totalRevenue.intValue();
      return 0;
    }

    public void setTotalRevenue(Double totalRevenue) {
      this.totalRevenue = totalRevenue;
    }

    public Integer getTotalOrders() {
      if (totalOrders != null) return totalOrders.intValue();
      return 0;
    }

    public void setTotalOrders(Double totalOrders) {
      this.totalOrders = totalOrders;
    }

    public Integer getTotalOrdersCompleted() {
      if (totalOrdersCompleted != null) return totalOrdersCompleted.intValue();
      return 0;
    }

    public void setTotalOrdersCompleted(Double totalOrdersCompleted) {
      this.totalOrdersCompleted = totalOrdersCompleted;
    }

    public Integer getTotalOrdersCancelled() {
      if (totalOrdersCancelled != null) return totalOrdersCancelled.intValue();
      return 0;
    }

    public void setTotalOrdersCancelled(Double totalOrdersCancelled) {
      this.totalOrdersCancelled = totalOrdersCancelled;
    }

    public Integer getTotalOrdersEscalated() {
      if (totalOrdersEscalated != null) return totalOrdersEscalated.intValue();
      return 0;
    }

    public void setTotalOrdersEscalated(Double totalOrdersEscalated) {
      this.totalOrdersEscalated = totalOrdersEscalated;
    }

    public Integer getTotalOrdersAbandoned() {
      if (totalOrdersAbandoned != null) return totalOrdersAbandoned.intValue();
      return 0;
    }

    public void setTotalOrdersAbandoned(Double totalOrdersAbandoned) {
      this.totalOrdersAbandoned = totalOrdersAbandoned;
    }

    public Integer getTotalOrdersInProgress() {
      if (totalOrdersInProgress != null) return totalOrdersInProgress.intValue();
      return 0;
    }

    public void setTotalOrdersInProgress(Double totalOrdersInProgress) {
      this.totalOrdersInProgress = totalOrdersInProgress;
    }

    public String getCurrencyCode() {
      return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
      this.currencyCode = currencyCode;
    }
  }

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }
}
