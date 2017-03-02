package com.nowfloats.Analytics_Screen.model;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Admin on 02-03-2017.
 */

public class SubscriberModel {

    @SerializedName("CreatedOn")
    @Expose
    private String createdOn;
    @SerializedName("SubscriptionStatus")
    @Expose
    private String subscriptionStatus;
    @SerializedName("UserCountryCode")
    @Expose
    private String userCountryCode;
    @SerializedName("UserMobile")
    @Expose
    private String userMobile;

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public String getUserCountryCode() {
        return userCountryCode;
    }

    public void setUserCountryCode(String userCountryCode) {
        this.userCountryCode = userCountryCode;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

}