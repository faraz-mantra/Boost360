package com.nowfloats.Analytics_Screen.model;


import android.os.Build;
import androidx.annotation.RequiresApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof SubscriberModel)) {
            return false;
        }
        SubscriberModel user = (SubscriberModel) o;
        return getUserMobile().equalsIgnoreCase(user.getUserMobile()) &&
                Objects.equals(userCountryCode, user.getUserCountryCode());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(userMobile,userCountryCode);
    }
    @Override
    public String toString() {
        return getUserMobile();
    }
}